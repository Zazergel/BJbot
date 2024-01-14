package com.zazergel.bjbot.bot.service;

import com.zazergel.bjbot.blackjack.BlackJackGame;
import com.zazergel.bjbot.blackjack.repo.GamesRepo;
import com.zazergel.bjbot.blackjack.repo.StatsRepo;
import com.zazergel.bjbot.bot.config.BotConfig;
import com.zazergel.bjbot.bot.config.Constants;
import com.zazergel.bjbot.bot.factory.InlineKeyBoardFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class Bot extends TelegramLongPollingBot {

    final BotConfig config;
    final InlineKeyBoardFactory inlineKeyBoardFactory;
    final GamesRepo gamesRepo;
    final StatsRepo statsRepo;


    @Autowired
    public Bot(BotConfig config, InlineKeyBoardFactory inlineKeyBoardFactory, GamesRepo gamesRepo, StatsRepo statsRepo) {
        super();
        this.config = config;
        this.inlineKeyBoardFactory = inlineKeyBoardFactory;
        this.gamesRepo = gamesRepo;
        this.statsRepo = statsRepo;
    }


    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotKey();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            onUpdateReceivedText(update);
        } else if (update.hasCallbackQuery()) {
            onUpdateReceivedCallbackQuery(update);
        } else {
            log.error("Unsupported command");
        }
    }

    public void onUpdateReceivedText(Update update) {
        String msg = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        int messageId = update.getMessage().getMessageId();
        String username = update.getMessage().getChat().getUserName();

        if (msg.equals("/start")) {
            checkStatsSession(chatId);
            sendMessage(chatId, "Здравствуйте, " + username + ".", null);
            sendMessage(chatId, "Главное меню:", inlineKeyBoardFactory.getKeyboardToStartMessage());
        }
        deleteMessage(chatId, messageId);
    }


    private void onUpdateReceivedCallbackQuery(Update update) {
        String callBackData = update.getCallbackQuery().getData();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callBackData.equals(Constants.rulesButton)) {
            InlineKeyboardMarkup keyboard = inlineKeyBoardFactory.getKeyboardToBackMessage();
            sendEditMessage(chatId, Constants.rules, messageId, keyboard);
        }
        if (callBackData.equals(Constants.statisticButton)) {
            Map<Integer, Integer> playerStat = checkStatsSession(chatId);
            String text = " Ваша статистика:\n" +
                          "Побед: " + playerStat.get(0) + "\n" +
                          "Поражений: " + playerStat.get(1) + "\n" +
                          "Ничьих: " + playerStat.get(2);
            sendEditMessage(chatId, text, messageId, inlineKeyBoardFactory.getKeyboardToBackMessage());
        }
        if (callBackData.equals(Constants.startGameButton)) {
            BlackJackGame game = saveGameSession(chatId);
            game.dealInitialCards();
            String text = game.play();
            addStats(chatId, text);
            if (text.contains("!")) {
                sendEditMessage(chatId, text, messageId, inlineKeyBoardFactory.getKeyboardToBackMessage());
            } else {
                sendEditMessage(chatId, text, messageId, inlineKeyBoardFactory.getKeyboardToChooseCard());
            }
        }
        if (callBackData.equals(Constants.noButton)) {
            BlackJackGame game = gamesRepo.getGames().get(chatId);
            game.setNext(false);
            String text = game.play();
            addStats(chatId, text);
            sendEditMessage(chatId, text, messageId, inlineKeyBoardFactory.getKeyboardToBackMessage());
        }
        if (callBackData.equals(Constants.takeButton)) {
            BlackJackGame game = gamesRepo.getGames().get(chatId);
            game.playerTakeCard();
            String text = game.play();
            addStats(chatId, text);
            if (text.contains("!")) {
                sendEditMessage(chatId, text, messageId, inlineKeyBoardFactory.getKeyboardToBackMessage());
            } else {
                sendEditMessage(chatId, text, messageId, inlineKeyBoardFactory.getKeyboardToChooseCard());
            }
        }
        if (callBackData.equals(Constants.mainMenuButton)) {
            sendEditMessage(chatId, "Сыграем?", messageId, inlineKeyBoardFactory.getKeyboardToStartMessage());
        }
    }

    private void sendMessage(long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(keyboard)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(Constants.exceptionOccur + e.getMessage());
        }
    }

    private void sendEditMessage(long chatId, String text, int messageId, InlineKeyboardMarkup keyboard) {
        EditMessageText message = EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(text)
                .replyMarkup(keyboard)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(Constants.exceptionOccur + e.getMessage());
        }
    }

    private void deleteMessage(long chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(chatId));
        deleteMessage.setMessageId(messageId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error(Constants.exceptionOccur + e.getMessage());
        }
    }

    private BlackJackGame saveGameSession(long chatId) {
        gamesRepo.getGames().put(chatId, new BlackJackGame());
        return gamesRepo.getGames().get(chatId);
    }

    private Map<Integer, Integer> checkStatsSession(long chatId) {
        if (!statsRepo.getStatsMap().containsKey(chatId)) {
            Map<Integer, Integer> playerStat = new HashMap<>();
            playerStat.put(0, 0);
            playerStat.put(1, 0);
            playerStat.put(2, 0);
            statsRepo.getStatsMap().put(chatId, playerStat);
        }
        return statsRepo.getStatsMap().get(chatId);
    }

    private void addStats(long chatId, String text) {
        if (text.contains("выиграли!")) {
            int wins = statsRepo.getStatsMap().get(chatId).get(0);
            wins++;
            statsRepo.getStatsMap().get(chatId).put(0, wins);
        } else if (text.contains("проиграли!")) {
            int loses = statsRepo.getStatsMap().get(chatId).get(1);
            loses++;
            statsRepo.getStatsMap().get(chatId).put(1, loses);
        } else if (text.contains("Ничья!")) {
            int draws = statsRepo.getStatsMap().get(chatId).get(2);
            draws++;
            statsRepo.getStatsMap().get(chatId).put(2, draws);
        }

    }
}

