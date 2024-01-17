package com.zazergel.bjbot.blackjack.repo;

import com.zazergel.bjbot.blackjack.BlackJackGame;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class GameSessionRepo {
    Map<Long, BlackJackGame> games;

    @Autowired
    public GameSessionRepo() {
        this.games = new HashMap<>();
    }
}
