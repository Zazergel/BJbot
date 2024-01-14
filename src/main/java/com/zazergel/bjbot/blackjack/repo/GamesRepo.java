package com.zazergel.bjbot.blackjack.repo;

import com.zazergel.bjbot.blackjack.BlackJackGame;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Getter
public class GamesRepo {
    private final Map<Long, BlackJackGame> games;

    @Autowired
    public GamesRepo() {
        this.games = new HashMap<>();
    }
}
