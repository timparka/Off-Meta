package com.offmeta.gg.Service;

import com.offmeta.gg.DTO.OffMetaDTO;
import com.offmeta.gg.Entity.ChampionStats;
import com.offmeta.gg.Entity.ParticipantEntity;
import com.offmeta.gg.Repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OffMetaService {
    @Autowired
    private ParticipantRepository participantRepository;

    public OffMetaDTO getOffMetaPick(String lane) {
        List<ParticipantEntity> participants = participantRepository.findAll();
        List<ParticipantEntity> laneParticipants = filterParticipantsByLane(participants, lane);
        int totalMatches = calculateTotalMatches(laneParticipants);
        Map<String, ChampionStats> championStatsMap = generateChampionStats(laneParticipants);
        calculateRatesForAllChampions(championStatsMap, totalMatches);
        List<ChampionStats> championStatsList = sortChampionStatsList(championStatsMap);

        ChampionStats bestChampion = championStatsList.get(0);

        return buildOffMetaDTO(bestChampion, lane);
    }

    private List<ParticipantEntity> filterParticipantsByLane(List<ParticipantEntity> participants, String lane) {
        return participants.stream()
                .filter(participant -> lane.equalsIgnoreCase(participant.getRole()))
                .collect(Collectors.toList());
    }

    private int calculateTotalMatches(List<ParticipantEntity> laneParticipants) {
        return laneParticipants.size() / 2;
    }

    private Map<String, ChampionStats> generateChampionStats(List<ParticipantEntity> laneParticipants) {
        Map<String, ChampionStats> championStatsMap = new HashMap<>();
        for (ParticipantEntity participant : laneParticipants) {
            championStatsMap.putIfAbsent(participant.getChampionName(), new ChampionStats(participant.getChampionName()));
            ChampionStats stats = championStatsMap.get(participant.getChampionName());
            stats.incrementGamesPlayed();
            if (participant.isWin()) {
                stats.incrementWins();
            }
            stats.addItemBuild(participant.getItems());
            stats.addSummonerSpells(participant.getSummonerSpell1(), participant.getSummonerSpell2());
        }
        return championStatsMap;
    }

    private void calculateRatesForAllChampions(Map<String, ChampionStats> championStatsMap, int totalMatches) {
        for (ChampionStats stats : championStatsMap.values()) {
            stats.calculateWinRateAndPickRate(totalMatches);
        }
    }

    private List<ChampionStats> sortChampionStatsList(Map<String, ChampionStats> championStatsMap) {
        List<ChampionStats> championStatsList = new ArrayList<>(championStatsMap.values());
        championStatsList.sort(Comparator.comparingDouble(ChampionStats::getPickRate)
                .thenComparing(ChampionStats::getWinRate, Comparator.reverseOrder()));
        return championStatsList;
    }

    private OffMetaDTO buildOffMetaDTO(ChampionStats bestChampion, String lane) {
        OffMetaDTO offMetaPick = new OffMetaDTO();
        offMetaPick.setChampionName(bestChampion.getChampionName());
        offMetaPick.setItems(bestChampion.getMostCommonItemBuild());
        offMetaPick.setSummonerSpell1(bestChampion.getMostCommonSummonerSpell1());
        offMetaPick.setSummonerSpell2(bestChampion.getMostCommonSummonerSpell2());
        offMetaPick.setRole(lane);
        return offMetaPick;
    }
}
