package com.zazergel.bjbot.blackjack;

import com.zazergel.bjbot.blackjack.deck.Card;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Hand {
    private final List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        cards.add(card);
    }

    public int getScore() {
        int score = 0;
        boolean hasAce = false;
        for (Card card : cards) {
            int value = card.getValue();
            if (value == 11) {
                hasAce = true;
            }
            score += value;
        }
        if (hasAce && score > 21) {
            score -= 10; // Адаптация значения туза, если сумма очков превышает 21
        }
        return score;
    }

    public String toStringPlayerHand() {
        StringBuilder sb = new StringBuilder();
        for (Card c : cards) {
            sb.append(c.toString()).append(" ");
        }
        return sb + " (Очки: " + getScore() + ")";
    }

    public String toStringDealerHand() {
        return cards.get(0).toString() + " (Очки: " + cards.get(0).getValue() + ")";
    }

    public int getSizeOfHand() {
        return cards.size();
    }
}
