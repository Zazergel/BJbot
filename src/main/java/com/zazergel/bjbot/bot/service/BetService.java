package com.zazergel.bjbot.bot.service;

import com.zazergel.bjbot.bot.Bot;
import com.zazergel.bjbot.bot.config.Constants;
import com.zazergel.bjbot.bot.factory.AnswerMessageFactory;
import com.zazergel.bjbot.bot.factory.KeyboardFactory;
import com.zazergel.bjbot.entity.user.User;
import com.zazergel.bjbot.repository.UserRepo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BetService {

    UserRepo userRepo;
    AnswerMessageFactory messageFactory;

    Map<Long, Long> userBets = new HashMap<>();

    @Autowired
    public BetService(UserRepo userRepo, AnswerMessageFactory messageFactory) {
        this.userRepo = userRepo;
        this.messageFactory = messageFactory;
    }

    public Long getUserScore(long chatId) {
        return userRepo.findById(chatId).orElseThrow().getScore();
    }

    public BotApiMethod<?> chooseStartBet(CallbackQuery callbackQuery) {
        int messageId = callbackQuery.getMessage().getMessageId();
        long chatId = callbackQuery.getMessage().getChatId();
        checkUserBalance(chatId);
        Long userScore = userRepo.findById(chatId).orElseThrow().getScore();
        String text = "<b>Ваш баланс:</b> " + userScore + "\n\nУкажите вашу начальную ставку:";
        return messageFactory.sendEditMessage(chatId, text, messageId, KeyboardFactory.getKeyboardToBet());
    }

    public BotApiMethod<?> takeUserBet(CallbackQuery callbackQuery, Bot bot) {
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        Long currentBet = Long.parseLong(callbackQuery.getData());
        log.info(chatId + " was bet: " + currentBet);
        Long userScore = userRepo.findById(chatId).orElseThrow().getScore();
        if (currentBet > userScore) {
            try {
                bot.execute(messageFactory.deleteMessage(chatId, messageId));
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
            String text = "У вас недостаточно средств, попробуйте снизить ставку. Или подождите 24 часа.";
            return messageFactory.sendMessage(chatId, text, KeyboardFactory.getKeyboardToBet());
        }
        userBets.put(chatId, currentBet);
        try {
            String text = "<b>Ваша ставка:</b> " + userBets.get(chatId) + "\n\nCтавки сделаны, ставок больше нет.";
            bot.execute(messageFactory.sendMessage(chatId, text, KeyboardFactory.getKeyboardToStartGame()));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return messageFactory.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
    }

    public BotApiMethod<?> userBetDouble(CallbackQuery callbackQuery) {
        String textFromQuery = callbackQuery.getMessage().getText();
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        Long lastBet = userBets.getOrDefault(chatId, 0L);
        if (lastBet * 2 > userRepo.findById(chatId).orElseThrow().getScore()) {
            String text = textFromQuery + "\n\nУ вас <b>недостаточно средств</b> этого.\n<b>Ваша ставка:</b> " + userBets.get(chatId);
            return messageFactory.sendEditMessage(chatId, text, messageId, KeyboardFactory.getKeyboardToChoose());
        } else {
            log.info(chatId + " DOUBLE bet: " + lastBet * 2);
            userBets.put(chatId, lastBet * 2);
            String text = textFromQuery + "\n\n<b>Ваша ставка:</b> " + userBets.get(chatId);
            return messageFactory.sendEditMessage(chatId, text, messageId, KeyboardFactory.getKeyboardToChoose());
        }
    }

    private void checkUserBalance(long chatId) {
        User user = userRepo.findById(chatId).orElseThrow();
        long userBalance = user.getScore();
        long daysBetween = ChronoUnit.DAYS.between(user.getUserDetails().getLastGame(), LocalDateTime.now());
        if (daysBetween >= 1) {
            log.info(chatId + " get everyDay bonus: " + Constants.EVERYDAY_BONUS);
            user.setScore(userBalance + Constants.EVERYDAY_BONUS);
            userRepo.save(user);
        }
    }

    public void playerWin(long chatId) {
        Long bet = userBets.get(chatId);
        User user = userRepo.findById(chatId).orElseThrow();
        Long playerScore = user.getScore();
        userRepo.findById(chatId).orElseThrow().setScore(playerScore + bet * 2);
        userBets.remove(chatId);
    }

    public void playerLose(long chatId) {
        Long bet = userBets.get(chatId);
        Long playerScore = userRepo.findById(chatId).orElseThrow().getScore();
        userRepo.findById(chatId).orElseThrow().setScore(playerScore - bet);
        userBets.remove(chatId);
    }

    public void playerBlackJack(long chatId) {
        Long bet = userBets.get(chatId);
        Long playerScore = userRepo.findById(chatId).orElseThrow().getScore();
        userRepo.findById(chatId).orElseThrow().setScore(playerScore + bet);
    }
}
