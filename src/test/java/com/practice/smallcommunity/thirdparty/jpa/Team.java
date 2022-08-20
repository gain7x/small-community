package com.practice.smallcommunity.thirdparty.jpa;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Team {

    @Id
    @GeneratedValue
    public Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    private final List<Player> players = new ArrayList<>();

    public Team(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
