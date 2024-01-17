package com.zazergel.bjbot.blackjack.repo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Getter
public class PlayerStatsRepo {

    private final Map<Long, Map<Integer, Integer>> statsMap;

    @Autowired
    public PlayerStatsRepo() {
        this.statsMap = new HashMap<>();
    }
}
