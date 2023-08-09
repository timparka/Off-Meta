package com.offmeta.gg.Entity;

import lombok.Getter;

import java.util.*;

@Getter
public class ChampionStats {
    private String championName;
    private int gamesPlayed;
    private int wins;
    private double winRate;
    private double pickRate;
    private Map<List<String>, Integer> itemBuildFrequency;
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

    public void addItemBuild(List<String> items) {
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

    public void calculateWinRateAndPickRate(int totalMatches) {
        this.winRate = (double) this.wins / this.gamesPlayed;
        this.pickRate = (double) this.gamesPlayed / totalMatches;
    }

    public List<String> getMostCommonItemBuild() {
        Map.Entry<List<String>, Integer> mostCommonEntry = null;
        for(Map.Entry<List<String>, Integer> entry : itemBuildFrequency.entrySet()) {
            if (mostCommonEntry == null || entry.getValue().compareTo(mostCommonEntry.getValue()) > 0) {
                mostCommonEntry = entry;
            }
        }

        return mostCommonEntry != null ? mostCommonEntry.getKey() : Collections.emptyList();
    }

    public String getMostCommonSummonerSpell1() {
        Map.Entry<String, Integer> mostCommonEntry = null;
        for(Map.Entry<String, Integer> entry : summonerSpellFrequency.entrySet()) {
            if (mostCommonEntry == null || entry.getValue().compareTo(mostCommonEntry.getValue()) > 0) {
                mostCommonEntry = entry;
            }
        }

        return mostCommonEntry != null ? mostCommonEntry.getKey() : null;
    }

    public String getMostCommonSummonerSpell2() {
        String mostCommonSpell1 = getMostCommonSummonerSpell1();

        Map.Entry<String, Integer> mostCommonEntry = null;
        for(Map.Entry<String, Integer> entry : summonerSpellFrequency.entrySet()) {
            if (!entry.getKey().equals(mostCommonSpell1)) {
                if (mostCommonEntry == null || entry.getValue().compareTo(mostCommonEntry.getValue()) > 0) {
                    mostCommonEntry = entry;
                }
            }
        }

        return mostCommonEntry != null ? mostCommonEntry.getKey() : null;
    }
}


