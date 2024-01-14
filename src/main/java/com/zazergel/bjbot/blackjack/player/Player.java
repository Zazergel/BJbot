package com.zazergel.bjbot.blackjack.player;

import com.zazergel.bjbot.blackjack.Hand;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Player {
    private final Hand hand = new Hand();
}
