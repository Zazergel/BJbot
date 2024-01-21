package com.zazergel.bjbot.blackjack;

import com.zazergel.bjbot.blackjack.deck.Deck;
import com.zazergel.bjbot.blackjack.player.Player;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class BlackJackGame {
    final Deck deck;
    final Player player;
    final Player dealer;
    @Setter
    boolean next;

    public BlackJackGame() {
        deck = new Deck();
        player = new Player();
        dealer = new Player();
        this.next = true;
    }

    public void dealInitialCards() {
        player.getHand().addCard(deck.dealCard());
        dealer.getHand().addCard(deck.dealCard());
        player.getHand().addCard(deck.dealCard());
        dealer.getHand().addCard(deck.dealCard());
    }

    public void playerTakeCard() {
        player.getHand().addCard(deck.dealCard());
    }

    public String play() {
        //Проверка на выпадение Блекджека вначале партии
        if (player.getHand().getScore() == 21 && player.getHand().getSizeOfHand() == 2) {
            return showHandsOnEndGame() + "\n\nУ вас <b>Блэкджек!</b> Вы выиграли!";
        } else if (dealer.getHand().getScore() == 21 && dealer.getHand().getSizeOfHand() == 2) {
            return showHandsOnEndGame() + "\n\nУ меня <b>Блэкджек!</b> Вы проиграли!";
        }
        // Игрок может взять карту, если у него меньше 21 очков
        if (player.getHand().getScore() < 21 && next) {
            return showHands() + "\n\nБерете еще карту?";
        }
        // Дилер берет карту, если у него меньше, чем у игрока, но не более 17
        if (player.getHand().getScore() < 21) {
            while (dealer.getHand().getScore() < player.getHand().getScore() && dealer.getHand().getScore() < 18) {
                dealer.getHand().addCard(deck.dealCard());
            }
        }
        return checkWinner();
    }

    private String checkWinner() {
        int playerScore = player.getHand().getScore();
        int dealerScore = dealer.getHand().getScore();

        if (playerScore <= 21 && (playerScore > dealerScore || dealerScore > 21)) {
            return showHandsOnEndGame() + "\n\nВы выиграли!";
        } else if (playerScore <= 21 && (playerScore == dealerScore)) {
            return showHandsOnEndGame() + "\n\nНичья!";
        } else {
            return showHandsOnEndGame() + "\n\nВы проиграли!";
        }
    }

    private String showHands() {
        return "Моя рука: " + dealer.getHand().toStringDealerHand() +
               "\nВаша рука: " + player.getHand().toStringPlayerHand();
    }

    private String showHandsOnEndGame() {
        return "Моя рука: " + dealer.getHand().toStringPlayerHand() +
               "\nВаша рука: " + player.getHand().toStringPlayerHand();
    }
}
