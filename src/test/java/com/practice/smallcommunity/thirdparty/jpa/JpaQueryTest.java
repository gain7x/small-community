package com.practice.smallcommunity.thirdparty.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class JpaQueryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Test
    void 팀_하나에_속하는_플레이어() {
        Team teamA = new Team(null, "팀A");
        Player playerA = new Player(null, "playerA", 1, teamA);
        Player playerB = new Player(null, "playerB", 1, teamA);
        Player playerC = new Player(null, "playerC", 1, teamA);
        Player playerD = new Player(null, "playerD", 1, teamA);

        teamRepository.save(teamA);
        playerRepository.saveAll(List.of(playerA, playerB, playerC, playerD));
        em.flush();
        em.clear();

        Team findTeam = teamRepository.findById(teamA.getId()).orElseThrow();
        List<Player> players = findTeam.getPlayers();
        for (Player player : players) {
            System.out.println(player.getName());
        }
    }

    @Test
    void 각각의_팀에_속하는_플레이어() {
        Team teamA = new Team(null, "팀A");
        Team teamB = new Team(null, "팀B");
        Player playerA = new Player(null, "playerA", 1, teamA);
        Player playerB = new Player(null, "playerB", 1, teamA);
        Player playerC = new Player(null, "playerC", 1, teamB);
        Player playerD = new Player(null, "playerD", 1, teamB);

        teamRepository.saveAll(List.of(teamA, teamB));
        playerRepository.saveAll(List.of(playerA, playerB, playerC, playerD));
        em.flush();
        em.clear();

        List<Team> teams = teamRepository.findAll();
        for (Team team : teams) {
            System.out.println(team.getPlayers().size());
        }
    }

    @Test
    void 플레이어가_속한_각각의_팀() {
        Team teamA = new Team(null, "팀A");
        Team teamB = new Team(null, "팀B");
        Player playerA = new Player(null, "playerA", 1, teamA);
        Player playerB = new Player(null, "playerB", 1, teamA);
        Player playerC = new Player(null, "playerC", 1, teamB);
        Player playerD = new Player(null, "playerD", 1, teamB);

        teamRepository.saveAll(List.of(teamA, teamB));
        playerRepository.saveAll(List.of(playerA, playerB, playerC, playerD));
        em.flush();
        em.clear();

        List<Player> players = playerRepository.findAll();
        for (Player player : players) {
            System.out.println(player.getTeam().getName());
        }
    }
}
