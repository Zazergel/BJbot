package com.zazergel.bjbot.blackjack.deck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;

@Component
public class Deck {
    private final LinkedList<Card> cards;

    @Autowired
    public Deck() {
        cards = new LinkedList<>();
        String[] suits = {"♥️", "♦️", "♣️", "♠️"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        int[] values = {2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11};
        for (int i = 0; i < 3; i++) {
            for (String suit : suits) {
                for (int j = 0; j < ranks.length; j++) {
                    cards.add(new Card(suit, ranks[j], values[j]));
                }
            }
        }
        Collections.shuffle(cards);
    }

    public Card dealCard() {
        return cards.removeFirst();
    }
}
