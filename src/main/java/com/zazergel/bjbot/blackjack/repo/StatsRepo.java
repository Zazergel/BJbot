package com.zazergel.bjbot.blackjack.repo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Getter
public class StatsRepo {

    private final Map<Long, Map<Integer, Integer>> statsMap;

    @Autowired
    public StatsRepo() {
        this.statsMap = new HashMap<>();
    }
}
