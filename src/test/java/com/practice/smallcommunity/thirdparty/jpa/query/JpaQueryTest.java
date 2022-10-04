package com.practice.smallcommunity.thirdparty.jpa.query;

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
        //given
        Team teamA = new Team("팀A");
        Player playerA = new Player("playerA", 1);
        Player playerB = new Player("playerB", 1);
        Player playerC = new Player("playerC", 1);
        Player playerD = new Player("playerD", 1);

        playerA.changeTeam(teamA);
        playerB.changeTeam(teamA);
        playerC.changeTeam(teamA);
        playerD.changeTeam(teamA);

        //when
        teamRepository.save(teamA);
        playerRepository.saveAll(List.of(playerA, playerB, playerC, playerD));
        em.flush();
        em.clear();

        //then
        Team findTeam = teamRepository.findById(teamA.getId()).orElseThrow();
        List<Player> players = findTeam.getPlayers();
        for (Player player : players) {
            System.out.println(player.getName());
        }
    }

    @Test
    void 각각의_팀에_속하는_플레이어() {
        //given
        Team teamA = new Team("팀A");
        Team teamB = new Team("팀B");
        Player playerA = new Player("playerA", 1);
        Player playerB = new Player("playerB", 1);
        Player playerC = new Player("playerC", 1);
        Player playerD = new Player("playerD", 1);

        playerA.changeTeam(teamA);
        playerB.changeTeam(teamA);
        playerC.changeTeam(teamB);
        playerD.changeTeam(teamB);

        //when
        teamRepository.saveAll(List.of(teamA, teamB));
        playerRepository.saveAll(List.of(playerA, playerB, playerC, playerD));
        em.flush();
        em.clear();

        //then
        List<Team> teams = teamRepository.findAll();
        for (Team team : teams) {
            System.out.println(team.getPlayers().size());
        }
    }

    @Test
    void 플레이어가_속한_각각의_팀() {
        //given
        Team teamA = new Team("팀A");
        Team teamB = new Team("팀B");
        Player playerA = new Player("playerA", 1);
        Player playerB = new Player("playerB", 1);
        Player playerC = new Player("playerC", 1);
        Player playerD = new Player("playerD", 1);

        playerA.changeTeam(teamA);
        playerB.changeTeam(teamA);
        playerC.changeTeam(teamB);
        playerD.changeTeam(teamB);

        //when
        teamRepository.saveAll(List.of(teamA, teamB));
        playerRepository.saveAll(List.of(playerA, playerB, playerC, playerD));
        em.flush();
        em.clear();

        //then
        List<Player> players = playerRepository.findAll();
        for (Player player : players) {
            System.out.println(player.getTeam().getName());
        }
    }
}
