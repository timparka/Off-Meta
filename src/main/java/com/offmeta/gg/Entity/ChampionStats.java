package com.offmeta.gg.Entity;

import com.offmeta.gg.Service.RiotApiService;
import lombok.Getter;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.staticdata.item.Item;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ChampionStats {
    private String championName;
    private int gamesPlayed;
    private int wins;
    private double winRate;
    private double pickRate;
    private Map<List<Integer>, Integer> itemBuildFrequency;
    private Map<String, Integer> summonerSpellFrequency;
    private String championImageUrl;

    public ChampionStats(String championName, String championImageUrl) {
        this.championName = championName;
        this.championImageUrl = championImageUrl;
        this.gamesPlayed = 0;
        this.wins = 0;
        this.itemBuildFrequency = new HashMap<>();
        this.summonerSpellFrequency = new HashMap<>();
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    public void incrementWins() {
        this.wins++;
    }

    public void calculateWinRateAndPickRate(int totalMatches) {
        this.winRate = (double) this.wins / this.gamesPlayed;
        this.pickRate = (double) this.gamesPlayed / totalMatches;
    }

    public void addItemBuild(List<Integer> items) {
        itemBuildFrequency.putIfAbsent(items, 0);
        itemBuildFrequency.put(items, itemBuildFrequency.get(items) + 1);
    }

    public void addSummonerSpells(String summonerSpell1, String summonerSpell2) {
        List<String> spells = Arrays.asList(summonerSpell1, summonerSpell2);
        for (String spell : spells) {
            summonerSpellFrequency.putIfAbsent(spell, 0);
            summonerSpellFrequency.put(spell, summonerSpellFrequency.get(spell) + 1);
        }
    }

    public Map<List<Integer>, Integer> getItemBuildFrequency() {
        return this.itemBuildFrequency;
    }

    public Map<String, Integer> getSummonerSpellFrequency() {
        return this.summonerSpellFrequency;
    }
}




