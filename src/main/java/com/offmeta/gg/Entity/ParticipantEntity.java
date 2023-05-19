package com.offmeta.gg.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.awt.event.ItemEvent;
import java.util.List;

@Document(value = "Participant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantEntity {
    @Id
    private String id;
    private String championName;
    private int championId;
    private boolean win;
    private List<String> items;
    private String summonerSpell1;
    private String summonerSpell2;
    private String role;

    public void addItem(String item) {
        items.add(item);
    }
}
