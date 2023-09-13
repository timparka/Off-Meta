package com.offmeta.gg.Service;

import com.offmeta.gg.Entity.ChampionStats;
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

    public List<Integer> getMostCommonItemBuild(ChampionStats championStats) {
        //using injected service
        R4J api = riotApiService.getApi();
        Map<Integer, Item> itemData = api.getDDragonAPI().getItems();

        Map<List<Integer>, Integer> itemBuildFrequency = championStats.getItemBuildFrequency();

        //map to count each itemId's seen
        HashMap<Integer, Integer> itemFreqMap = new HashMap<>();
        for (Map.Entry<List<Integer>, Integer> entry : itemBuildFrequency.entrySet()) {
            for (int itemId : entry.getKey()) {
                itemFreqMap.put(itemId, itemFreqMap.getOrDefault(itemId, 0) + 1);
            }
        }

        List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(itemFreqMap.entrySet());
        entryList.sort(Map.Entry.comparingByValue(Collections.reverseOrder()));

        List<Integer> top6Keys = new ArrayList<>();
        boolean mythicAdded = false;

        for (Map.Entry<Integer, Integer> entry : entryList) {
            int itemId = entry.getKey();
            Item item = itemData.get(itemId);

            if (item == null) continue;

            // Check if the item has the "Mythic" tag
            boolean isMythic = item.getDescription().contains("Mythic Passive:");

            if (isMythic) {
                if (!mythicAdded) {
                    top6Keys.add(itemId);
                    mythicAdded = true;
                }
            } else {
                top6Keys.add(itemId);
            }

            if (top6Keys.size() >= 6) {
                break;
            }
        }

        // Ensure at least one Mythic item is in the list if not added yet
        if (!mythicAdded) {
            for (Map.Entry<Integer, Integer> entry : entryList) {
                int itemId = entry.getKey();
                Item item = itemData.get(itemId);
                if (item == null) continue;  // Item not found in the data

                boolean isMythic = item.getDescription().contains("Mythic Passive:");

                if (isMythic) {
                    top6Keys.set(5, itemId);  // Replace the last item with a Mythic item
                    break;
                }
            }
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
