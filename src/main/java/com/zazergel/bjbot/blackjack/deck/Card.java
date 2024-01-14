package com.zazergel.bjbot.blackjack.deck;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Card {
    String suit;
    String rank;
    int value;

    @Override
    public String toString() {
        return rank + " " + suit;
    }
}