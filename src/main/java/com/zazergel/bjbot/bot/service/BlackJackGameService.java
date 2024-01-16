package com.zazergel.bjbot.bot.service;

import com.zazergel.bjbot.blackjack.BlackJackGame;
import com.zazergel.bjbot.blackjack.repo.GameSessionRepo;
import com.zazergel.bjbot.blackjack.repo.PlayerStatsRepo;
import com.zazergel.bjbot.bot.config.Constants;
import com.zazergel.bjbot.bot.factory.KeyboardFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class BlackJackGameService {
    final PlayerStatsRepo playerStatsRepo;
    final GameSessionRepo gameSessionRepo;

    MessageService messageService;

    @Autowired
    public BlackJackGameService(PlayerStatsRepo playerStatsRepo, GameSessionRepo gameSessionRepo) {
        this.gameSessionRepo = gameSessionRepo;
        this.playerStatsRepo = playerStatsRepo;
    }

    public void initGameSession(MessageService messageService, long chatId) {
        this.messageService = messageService;
        checkStatsSession(chatId);
    }

    public void receivedButton(Update update) {
        String callBackData = update.getCallbackQuery().getData();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callBackData.equals(Constants.BJ_RULES_BUTTON)) {
            messageService.sendEditMessage(chatId, Constants.RULES, messageId,
                    KeyboardFactory.getKeyboardToBackMessage());
        }
        if (callBackData.equals(Constants.BJ_STATISTIC_BUTTON)) {
            messageService.sendEditMessage(chatId, getStats(chatId), messageId,
                    KeyboardFactory.getKeyboardToBackMessage());
        }
        if (callBackData.equals(Constants.BJ_START_GAME_BUTTON)) {
            BlackJackGame game = saveGameSession(chatId);
            game.dealInitialCards();
            String text = game.play();
            addStats(chatId, text);
            if (text.contains("!")) {
                messageService.sendEditMessage(chatId, text, messageId,
                        KeyboardFactory.getKeyboardToBackMessage());
            } else {
                messageService.sendEditMessage(chatId, text, messageId,
                        KeyboardFactory.getKeyboardToChooseCard());
            }
        }
        if (callBackData.equals(Constants.BJ_NO_BUTTON)) {
            BlackJackGame game = gameSessionRepo.getGames().get(chatId);
            game.setNext(false);
            String text = game.play();
            addStats(chatId, text);
            messageService.sendEditMessage(chatId, text, messageId,
                    KeyboardFactory.getKeyboardToBackMessage());
        }
        if (callBackData.equals(Constants.BJ_TAKE_BUTTON)) {
            BlackJackGame game = gameSessionRepo.getGames().get(chatId);
            game.playerTakeCard();
            String text = game.play();
            addStats(chatId, text);
            if (text.contains("!")) {
                messageService.sendEditMessage(chatId, text, messageId,
                        KeyboardFactory.getKeyboardToBackMessage());
            } else {
                messageService.sendEditMessage(chatId, text, messageId,
                        KeyboardFactory.getKeyboardToChooseCard());
            }
        }
        if (callBackData.equals(Constants.BJ_MAIN_MENU_BUTTON)) {
            messageService.sendEditMessage(chatId, "Сыграем?", messageId,
                    KeyboardFactory.getKeyboardToMainMenuMessage());
        }
    }

    private BlackJackGame saveGameSession(long chatId) {
        gameSessionRepo.getGames().put(chatId, new BlackJackGame());
        return gameSessionRepo.getGames().get(chatId);
    }

    private Map<Integer, Integer> checkStatsSession(long chatId) {
        if (!playerStatsRepo.getStatsMap().containsKey(chatId)) {
            log.info("StatsSession was init for chatId: " + chatId);

            Map<Integer, Integer> playerStat = new HashMap<>();
            playerStat.put(0, 0);
            playerStat.put(1, 0);
            playerStat.put(2, 0);
            playerStatsRepo.getStatsMap().put(chatId, playerStat);
        }
        return playerStatsRepo.getStatsMap().get(chatId);
    }

    private void addStats(long chatId, String text) {
        String logText = "User from chatId: " + chatId;
        if (text.contains("выиграли!")) {
            int wins = playerStatsRepo.getStatsMap().get(chatId).get(0);
            wins++;
            playerStatsRepo.getStatsMap().get(chatId).put(0, wins);
            log.info(logText + " - win!");
        } else if (text.contains("проиграли!")) {
            int loses = playerStatsRepo.getStatsMap().get(chatId).get(1);
            loses++;
            playerStatsRepo.getStatsMap().get(chatId).put(1, loses);
            log.info(logText + " - lose!");
        } else if (text.contains("Ничья!")) {
            int draws = playerStatsRepo.getStatsMap().get(chatId).get(2);
            draws++;
            playerStatsRepo.getStatsMap().get(chatId).put(2, draws);
            log.info(logText + " - draw!");
        }

    }

    private String getStats(long chatId) {
        Map<Integer, Integer> playerStat = checkStatsSession(chatId);
        return "<b>Ваша статистика</b>:\n" +
               "Побед: " + playerStat.get(0) + "\n" +
               "Поражений: " + playerStat.get(1) + "\n" +
               "Ничьих: " + playerStat.get(2);
    }
}

