package com.zazergel.bjbot.blackjack.player;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Player {
    private final Hand hand = new Hand();
}
