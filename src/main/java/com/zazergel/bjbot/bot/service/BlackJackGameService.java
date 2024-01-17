package com.zazergel.bjbot.bot.service;

import com.zazergel.bjbot.blackjack.BlackJackGame;
import com.zazergel.bjbot.blackjack.repo.GameSessionRepo;
import com.zazergel.bjbot.blackjack.repo.PlayerStatsRepo;
import com.zazergel.bjbot.bot.Bot;
import com.zazergel.bjbot.bot.config.Buttons;
import com.zazergel.bjbot.bot.config.Constants;
import com.zazergel.bjbot.bot.factory.KeyboardFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class BlackJackGameService {
    final PlayerStatsRepo playerStatsRepo;
    final GameSessionRepo gameSessionRepo;


    @Autowired
    public BlackJackGameService(PlayerStatsRepo playerStatsRepo, GameSessionRepo gameSessionRepo) {
        this.gameSessionRepo = gameSessionRepo;
        this.playerStatsRepo = playerStatsRepo;
    }

    public BotApiMethod<?> receivedButton(CallbackQuery callbackQuery, Bot bot) {
        String callBackData = callbackQuery.getData();
        int messageId = callbackQuery.getMessage().getMessageId();
        long chatId = callbackQuery.getMessage().getChatId();

        checkStatsSession(chatId);

        if (callBackData.equals(Buttons.BJ_RULES_BUTTON)) {
            return sendEditMessage(chatId, Constants.RULES, messageId,
                    KeyboardFactory.getKeyboardToBackMessage());
        }
        if (callBackData.equals(Buttons.BJ_STATISTIC_BUTTON)) {
            return sendEditMessage(chatId, getStats(chatId), messageId,
                    KeyboardFactory.getKeyboardToBackMessage());
        }
        if (callBackData.equals(Buttons.BJ_START_GAME_BUTTON)) {
            BlackJackGame game = saveGameSession(chatId);
            game.dealInitialCards();
            String text = game.play();
            addStats(chatId, text);
            if (text.contains("!")) {
                try {
                    bot.execute(sendEditMessage(chatId, text, messageId, null));
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
                return sendMainMenuMessage(chatId);
            } else {
                return sendEditMessage(chatId, text, messageId,
                        KeyboardFactory.getKeyboardToChooseCard());
            }
        }
        if (callBackData.equals(Buttons.BJ_NO_BUTTON)) {
            BlackJackGame game = gameSessionRepo.getGames().get(chatId);
            game.setNext(false);
            String text = game.play();
            addStats(chatId, text);
            try {
                bot.execute(sendEditMessage(chatId, text, messageId, null));
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
            return sendMainMenuMessage(chatId);
        }
        if (callBackData.equals(Buttons.BJ_TAKE_BUTTON)) {
            BlackJackGame game = gameSessionRepo.getGames().get(chatId);
            game.playerTakeCard();
            String text = game.play();
            addStats(chatId, text);
            if (text.contains("!")) {
                try {
                    bot.execute(sendEditMessage(chatId, text, messageId, null));
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
                return sendMainMenuMessage(chatId);
            } else {
                return sendEditMessage(chatId, text, messageId, KeyboardFactory.getKeyboardToChooseCard());
            }
        }
        if (callBackData.equals(Buttons.BJ_MAIN_MENU_BUTTON)) {
            return sendEditMessage(chatId, "Играем?", messageId,
                    KeyboardFactory.getKeyboardToMainMenuMessage());
        }
        return null;
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
            playerStat.put(3, 0);
            playerStatsRepo.getStatsMap().put(chatId, playerStat);
        }
        return playerStatsRepo.getStatsMap().get(chatId);
    }

    private void addStats(long chatId, String text) {
        String logText = "User from chatId: " + chatId;
        if (text.contains("выиграли!")) {
            if (text.contains("Блекджек")) {
                int winsBj = playerStatsRepo.getStatsMap().get(chatId).get(1);
                winsBj++;
                playerStatsRepo.getStatsMap().get(chatId).put(1, winsBj);
            }
            int wins = playerStatsRepo.getStatsMap().get(chatId).get(0);
            wins++;
            playerStatsRepo.getStatsMap().get(chatId).put(0, wins);
            log.info(logText + " - win!");
        } else if (text.contains("проиграли!")) {
            int loses = playerStatsRepo.getStatsMap().get(chatId).get(2);
            loses++;
            playerStatsRepo.getStatsMap().get(chatId).put(2, loses);
            log.info(logText + " - lose!");
        } else if (text.contains("Ничья!")) {
            int draws = playerStatsRepo.getStatsMap().get(chatId).get(3);
            draws++;
            playerStatsRepo.getStatsMap().get(chatId).put(3, draws);
            log.info(logText + " - draw!");
        }

    }

    private String getStats(long chatId) {
        Map<Integer, Integer> playerStat = checkStatsSession(chatId);
        return "<b>Ваша статистика</b>:\n" +
               "Побед всего: " + playerStat.get(0) + "\n" +
               "Блекджеком: " + playerStat.get(1) + "\n\n" +
               "Поражений: " + playerStat.get(2) + "\n" +
               "Ничьих: " + playerStat.get(3);
    }


    private EditMessageText sendEditMessage(long chatId, String text, int messageId,
                                            InlineKeyboardMarkup keyboard) {
        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard)
                .build();
    }

    private SendMessage sendMainMenuMessage(long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text("Сыграем?")
                .parseMode(ParseMode.HTML)
                .replyMarkup(KeyboardFactory.getKeyboardToMainMenuMessage())
                .build();
    }
}


