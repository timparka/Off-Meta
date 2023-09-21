package com.offmeta.gg.Service;

import com.offmeta.gg.Entity.ChampionStats;
import no.stelar7.api.r4j.basic.utils.Pair;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.staticdata.item.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChampionStatsService {

    @Autowired
    private RiotApiService riotApiService;

    public Map<Integer, Item> getItemsData() {
        R4J api = riotApiService.getApi();
        return api.getDDragonAPI().getItems();
    }

    public HashMap<Integer, Integer> getItemFrequencyMap(Map<List<Integer>, Integer> itemBuildFrequency) {
        HashMap<Integer, Integer> itemFreqMap = new HashMap<>();
        for (Map.Entry<List<Integer>, Integer> entry : itemBuildFrequency.entrySet()) {
            for (int itemId : entry.getKey()) {
                itemFreqMap.put(itemId, itemFreqMap.getOrDefault(itemId, 0) + entry.getValue());
            }
        }
        return itemFreqMap;
    }

    public Pair<Integer, List<String>> getMostFrequentItemAndTags(List<Map.Entry<Integer, Integer>> entryList, Map<Integer, Item> itemData) {
        Map.Entry<Integer, Integer> mostFrequentEntry = entryList.get(0);
        int mostFrequentItemId = mostFrequentEntry.getKey();
        Item mostFrequentItem = itemData.get(mostFrequentItemId);
        return new Pair<>(mostFrequentItemId, mostFrequentItem.getTags());
    }

    public boolean getMythicItem(List<Map.Entry<Integer, Integer>> entryList, List<Integer> top6Keys, Map<Integer, Item> itemData) {
        boolean mythicAdded = false;
        Iterator<Map.Entry<Integer, Integer>> iterator = entryList.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            int itemId = entry.getKey();
            Item item = itemData.get(itemId);
            if (item != null && item.getDescription().contains("Mythic Passive:")) {
                top6Keys.add(itemId);
                mythicAdded = true;
                iterator.remove();
                break;
            }
        }
        return mythicAdded;
    }

    public void getItemsByTags(List<Map.Entry<Integer, Integer>> entryList, List<String> dominantTags, List<Integer> top6Keys, Map<Integer, Item> itemData, boolean mythicAdded, boolean bootsAdded) {
        for (Map.Entry<Integer, Integer> entry : entryList) {
            int itemId = entry.getKey();
            Item item = itemData.get(itemId);

            if (item != null) {
                boolean tagMatch = false;

                for (String tag : item.getTags()) {
                    if (dominantTags.contains(tag)) {
                        tagMatch = true;
                        break;
                    }
                }

                if (tagMatch) {
                    if (mythicAdded && item.getDescription().contains("Mythic Passive:")) continue;
                    if (bootsAdded && item.getTags().contains("Boots")) continue;
                    if (!top6Keys.contains(itemId)) {
                        top6Keys.add(itemId);
                    }
                }
            }

            if (top6Keys.size() >= 6) {
                break;
            }
        }
    }

    public void fillRemainingItems(List<Map.Entry<Integer, Integer>> entryList, List<Integer> top6Keys, Map<Integer, Item> itemData, boolean mythicAdded, boolean bootsAdded) {
        for (Map.Entry<Integer, Integer> entry : entryList) {
            int itemId = entry.getKey();
            Item item = itemData.get(itemId);
            if (item != null && (item.getTags().contains("Damage") || item.getTags().contains("SpellDamage") || item.getTags().contains("Defense"))) {
                if (mythicAdded && item.getDescription().contains("Mythic Passive:")) continue;
                if (bootsAdded && item.getTags().contains("Boots")) continue;
                if (!top6Keys.contains(itemId)) {
                    top6Keys.add(itemId);
                }
            }
            if (top6Keys.size() >= 6) {
                break;
            }
        }
    }

    public boolean getBoots(List<Map.Entry<Integer, Integer>> entryList, List<Integer> top6Keys, Map<Integer, Item> itemData) {
        boolean bootsAdded = false;
        Iterator<Map.Entry<Integer, Integer>> iterator = entryList.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            int itemId = entry.getKey();
            Item item = itemData.get(itemId);
            if (item != null && item.getTags().contains("Boots")) {
                top6Keys.add(itemId);
                bootsAdded = true;
                iterator.remove();
                break;
            }
        }
        return bootsAdded;
    }

    public List<Integer> getMostCommonItemBuild(ChampionStats championStats) {
        Map<Integer, Item> itemData = getItemsData();
        Map<List<Integer>, Integer> itemBuildFrequency = championStats.getItemBuildFrequency();
        HashMap<Integer, Integer> itemFreqMap = getItemFrequencyMap(itemBuildFrequency);

        // Sort entryList by frequency in descending order
        List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(itemFreqMap.entrySet());
        entryList.sort(Map.Entry.<Integer, Integer>comparingByValue().reversed());

        // Identify the most frequent item and its dominant tags
        Pair<Integer, List<String>> mostFrequentItemAndTags = getMostFrequentItemAndTags(entryList, itemData);
        List<String> dominantTags = mostFrequentItemAndTags.getValue();

        List<Integer> top6Keys = new ArrayList<>();

        boolean mythicAdded = getMythicItem(entryList, top6Keys, itemData);
        boolean bootsAdded = getBoots(entryList, top6Keys, itemData);

        // First loop to add items based on dominant tags
        getItemsByTags(entryList, dominantTags, top6Keys, itemData, mythicAdded, bootsAdded);


        // Second loop to fill up top6Keys if it has fewer than 6 items
        if (top6Keys.size() < 6) {
            fillRemainingItems(entryList, top6Keys, itemData, mythicAdded, bootsAdded);
        }

        return top6Keys;
    }

    public String getMostCommonSummonerSpell1(ChampionStats championStats) {
        Map.Entry<String, Integer> mostCommonEntry = null;
        Map<String, Integer> summonerSpellFrequency = championStats.getSummonerSpellFrequency();
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
        } else if ("ghost".equalsIgnoreCase(result)) {
            return "Haste";
        } else {
            return result;
        }
    }

    public String getMostCommonSummonerSpell2(ChampionStats championStats) {
        Map<String, Integer> summonerSpellFrequency = championStats.getSummonerSpellFrequency();
        String mostCommonSpell1 = getMostCommonSummonerSpell1(championStats);

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
        } else if ("ghost".equalsIgnoreCase(result)) {
            return "Haste";
        } else {
            return result;
        }
    }
}
