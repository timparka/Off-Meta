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

    public void calculateWinRateAndPickRate(int totalMatches) {
        this.winRate = (double) this.wins / this.gamesPlayed;
        this.pickRate = (double) this.gamesPlayed / totalMatches;
    }

    public List<Integer> getMostCommonItemBuild() {
        Map.Entry<List<Integer>, Integer> mostCommonEntry = null;
        for (Map.Entry<List<Integer>, Integer> entry : itemBuildFrequency.entrySet()) {
            // Ensure that the item build doesn't have a 0 value
            if (!entry.getKey().contains(0)) {
                if (mostCommonEntry == null || entry.getValue().compareTo(mostCommonEntry.getValue()) > 0) {
                    mostCommonEntry = entry;
                }
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

        String result = mostCommonEntry != null ? mostCommonEntry.getKey() : null;
        if ("ignite".equalsIgnoreCase(result)) {
            return "Dot";
        } else if ("cleanse".equalsIgnoreCase(result)) {
            return "Boost";
        } else {
            return result;
        }
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

        String result = mostCommonEntry != null ? mostCommonEntry.getKey() : null;
        if ("ignite".equalsIgnoreCase(result)) {
            return "Dot";
        } else if ("cleanse".equalsIgnoreCase(result)) {
            return "Boost";
        } else {
            return result;
        }
    }

}


