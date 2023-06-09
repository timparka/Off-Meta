package com.offmeta.gg.Service;

import com.offmeta.gg.Entity.ParticipantEntity;
import com.offmeta.gg.Repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard;
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;
import no.stelar7.api.r4j.basic.constants.types.lol.TierDivisionType;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.impl.lol.builders.summoner.SummonerBuilder;
import no.stelar7.api.r4j.impl.lol.raw.LeagueAPI;
import no.stelar7.api.r4j.impl.lol.raw.MatchV5API;
import no.stelar7.api.r4j.pojo.lol.league.LeagueEntry;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant;
import no.stelar7.api.r4j.pojo.lol.staticdata.champion.StaticChampion;
import no.stelar7.api.r4j.pojo.lol.staticdata.item.Item;
import no.stelar7.api.r4j.pojo.lol.staticdata.summonerspell.StaticSummonerSpell;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private ParticipantRepository participantRepository;

    private LeagueShard region = LeagueShard.NA1;
    private RegionShard regionShard = RegionShard.AMERICAS;
    private static final int SMITE_SPELL_ID = 11;

    @Autowired
    private R4J api;

    public UserService(@Value("${api.key}") String apiKey) {
        this.api = new R4J(new APICredentials(apiKey));
    }

    public void fetchData() {
        List<String> summonerIds = getTopPlayerIds();
        Set<String> uniqueMatchIds = getUniqueMatchIds(summonerIds);

        //these maps just hold relevant information of the game as the names suggest
        Map<Integer, StaticChampion> champData = api.getDDragonAPI().getChampions();
        Map<Integer, StaticSummonerSpell> spellData = api.getDDragonAPI().getSummonerSpells();
        Map<Integer, Item> itemData = api.getDDragonAPI().getItems();

        saveParticipants(uniqueMatchIds, champData, spellData, itemData);
    }

    private List<String> getTopPlayerIds() {
        List<LeagueEntry> challengerEntries = LeagueAPI.getInstance().getLeagueByTierDivision(region, GameQueueType.RANKED_SOLO_5X5, TierDivisionType.CHALLENGER_I, 1);
        List<LeagueEntry> grandmasterEntries = LeagueAPI.getInstance().getLeagueByTierDivision(region, GameQueueType.RANKED_SOLO_5X5, TierDivisionType.GRANDMASTER_I, 1);
        List<LeagueEntry> masterEntries = LeagueAPI.getInstance().getLeagueByTierDivision(region, GameQueueType.RANKED_SOLO_5X5, TierDivisionType.MASTER_I, 1);

        List<String> summonerIds = new ArrayList<>();
        //gets players summonerId and puts into summonerIds list
        summonerIds.addAll(challengerEntries.stream().map(LeagueEntry::getSummonerId).collect(Collectors.toList()));
        summonerIds.addAll(grandmasterEntries.stream().map(LeagueEntry::getSummonerId).collect(Collectors.toList()));
        summonerIds.addAll(masterEntries.stream().map(LeagueEntry::getSummonerId).collect(Collectors.toList()));
        return summonerIds;
    }

    private Set<String> getUniqueMatchIds(List<String> summonerIds) {
        Set<String> uniqueMatchIds = new HashSet<>();

        // Getting match IDs
        for (String summonerId : summonerIds) {
            Summoner summoner = new SummonerBuilder().withPlatform(region).withSummonerId(summonerId).get();
            MatchV5API matchV5API = MatchV5API.getInstance();
            List<String> matches = matchV5API.getMatchList(regionShard, summoner.getPUUID(), GameQueueType.RANKED_SOLO_5X5, null, 0, 10, null, null);

            // Add match IDs to the HashSet
            uniqueMatchIds.addAll(matches);
        }
        return uniqueMatchIds;
    }

    private void saveParticipants(Set<String> uniqueMatchIds, Map<Integer, StaticChampion> champData, Map<Integer,
            StaticSummonerSpell> spellData, Map<Integer, Item> itemData) {
        List<ParticipantEntity> participantEntities = new ArrayList<>();
        String currentVersion = api.getDDragonAPI().getVersions().get(0);

        for (String matchId : uniqueMatchIds) {
            LOLMatch match = LOLMatch.get(regionShard, matchId);
            List<MatchParticipant> participants = match.getParticipants();

            for (MatchParticipant participant : participants) {
                StaticChampion participantChampion = champData.get(participant.getChampionId());
                String championImageUrl = String.format("http://ddragon.leagueoflegends.com/cdn/%s/img/champion/%s", currentVersion, participantChampion.getImage().getFull());
                StaticSummonerSpell summonerSpell1 = spellData.get(participant.getSummoner1Id());
                StaticSummonerSpell summonerSpell2 = spellData.get(participant.getSummoner2Id());

                if (summonerSpell1 == null) {
                    summonerSpell1 = spellData.get(SMITE_SPELL_ID);
                }
                if (summonerSpell2 == null) {
                    summonerSpell2 = spellData.get(SMITE_SPELL_ID);
                }

                ParticipantEntity participantEntity = new ParticipantEntity(
                        null,
                        participantChampion.getName(),
                        participant.getChampionId(),
                        participant.didWin(),
                        getItemNames(participant, itemData),
                        summonerSpell1.getName(),
                        summonerSpell2.getName(),
                        String.valueOf(participant.getChampionSelectLane()),
                        championImageUrl
                );

                participantEntities.add(participantEntity);
            }
        }
        participantRepository.saveAll(participantEntities);
    }

    private List<String> getItemNames(MatchParticipant participant, Map<Integer, Item> itemData) {
        List<String> itemNames = new ArrayList<>();
        List<Integer> itemIds = Arrays.asList(participant.getItem0(), participant.getItem1(), participant.getItem2(), participant.getItem3(),
                participant.getItem4(), participant.getItem5(), participant.getItem6());

        for (int itemId : itemIds) {
            if (itemId != 0) {
                Item item = itemData.get(itemId);
                if(item != null) {
                    itemNames.add(item.getName());
                }
            }
        }
        return itemNames;
    }

    public void newPatchData() {
        participantRepository.deleteAll();
    }

}

