package com.offmeta.gg.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OffMetaDTO {
    private String id;
    @JsonProperty("championName")
    private String championName;
    @JsonProperty("items")
    private List<String> items;
    @JsonProperty("summonerSpell1")
    private String summonerSpell1;
    @JsonProperty("summonerSpell2")
    private String summonerSpell2;
    @JsonProperty("role")
    private String role;
    @JsonProperty("winRate")
    private double winRate;
    @JsonProperty("pickRate")
    private double pickRate;
    @JsonProperty("gamesPlayed")
    private int gamesPlayed;
}
