package com.zazergel.bjbot.bot.service;

import com.zazergel.bjbot.blackjack.BlackJackGame;
import com.zazergel.bjbot.bot.Bot;
import com.zazergel.bjbot.bot.config.Buttons;
import com.zazergel.bjbot.bot.config.Constants;
import com.zazergel.bjbot.bot.factory.AnswerMessageFactory;
import com.zazergel.bjbot.bot.factory.KeyboardFactory;
import com.zazergel.bjbot.bot.service.manager.MainMenuManager;
import com.zazergel.bjbot.entity.user.User;
import com.zazergel.bjbot.entity.user.UserGameStat;
import com.zazergel.bjbot.repository.StatRepo;
import com.zazergel.bjbot.repository.UserRepo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BlackJackGameService {

    StatRepo statRepo;
    UserRepo userRepo;
    MainMenuManager mainMenuManager;
    AnswerMessageFactory messageFactory;
    BetService betService;
    Map<Long, BlackJackGame> gameSession = new HashMap<>();

    @Autowired
    public BlackJackGameService(StatRepo statRepo,
                                UserRepo userRepo,
                                MainMenuManager mainMenuManager,
                                AnswerMessageFactory messageFactory,
                                BetService betService) {
        this.statRepo = statRepo;
        this.userRepo = userRepo;
        this.mainMenuManager = mainMenuManager;
        this.messageFactory = messageFactory;
        this.betService = betService;
    }


    public BotApiMethod<?> receivedButton(CallbackQuery callbackQuery, Bot bot) {
        String callBackData = callbackQuery.getData();
        var params = MessageParam.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .callbackQuery(callbackQuery)
                .bot(bot)
                .build();
        if (!gameSession.containsKey(params.getChatId())) {
            gameSession.put(params.getChatId(), new BlackJackGame());
        }
        switch (callBackData) {
            case Buttons.BJ_START_BET_BUTTON -> {
                return startBetButton(params);
            }
            case Buttons.BJ_START_GAME_BUTTON -> {
                return startGameButton(params);
            }
            case Buttons.BJ_NO_BUTTON -> {
                return noButton(params);
            }
            case Buttons.BJ_TAKE_BUTTON -> {
                return takeButton(params);
            }
            default -> {
                return null;
            }
        }
    }

    public BotApiMethod<?> startBetButton(MessageParam params) {
        var callbackQuery = params.getCallbackQuery();
        return betService.chooseStartBet(callbackQuery);
    }

    public BotApiMethod<?> startGameButton(MessageParam params) {
        var chatId = params.getChatId();
        var messageId = params.getMessageId();
        var callbackQuery = params.getCallbackQuery();
        var bot = params.getBot();

        log.info("The game for: " + chatId + " was start! MessageId: " + messageId);
        gameSession.put(chatId, new BlackJackGame());
        BlackJackGame game = gameSession.get(chatId);
        game.dealInitialCards();
        StringBuilder sb = new StringBuilder(game.play());
        if (sb.toString().contains("!")) {
            addStats(chatId, sb.toString());
            sb.append(Constants.BALANCE).append(betService.getUserScore(chatId));
            try {
                bot.execute(messageFactory
                        .sendEditMessage(chatId, sb.toString(), messageId, null));
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
            gameSession.remove(chatId);
            return mainMenuManager.sendAnswer(callbackQuery);
        } else {
            return messageFactory.sendEditMessage(chatId, sb.toString(), messageId,
                    KeyboardFactory.getKeyboardToChoose());
        }
    }

    public BotApiMethod<?> noButton(MessageParam params) {
        var chatId = params.getChatId();
        var messageId = params.getMessageId();
        var callbackQuery = params.getCallbackQuery();
        var bot = params.getBot();

        BlackJackGame game = gameSession.get(chatId);
        game.setNext(false);
        StringBuilder sb = new StringBuilder(game.play());
        addStats(chatId, sb.toString());
        sb.append(Constants.BALANCE).append(betService.getUserScore(chatId));
        try {
            bot.execute(messageFactory
                    .sendEditMessage(chatId, sb.toString(), messageId, null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        gameSession.remove(chatId);
        return mainMenuManager.sendAnswer(callbackQuery);
    }

    public BotApiMethod<?> takeButton(MessageParam params) {
        var chatId = params.getChatId();
        var messageId = params.getMessageId();
        var callbackQuery = params.getCallbackQuery();
        var bot = params.getBot();

        BlackJackGame game = gameSession.get(chatId);
        game.playerTakeCard();
        StringBuilder sb = new StringBuilder(game.play());
        if (sb.toString().contains("!")) {
            addStats(chatId, sb.toString());
            sb.append(Constants.BALANCE).append(betService.getUserScore(chatId));
            try {
                bot.execute(messageFactory
                        .sendEditMessage(chatId, sb.toString(), messageId, null));
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
            gameSession.remove(chatId);
            return mainMenuManager.sendAnswer(callbackQuery);
        } else {
            return messageFactory.sendEditMessage(chatId, sb.toString(), messageId,
                    KeyboardFactory.getKeyboardToChoose());
        }
    }

    private void addStats(long chatId, String text) {
        String logText = "User from chatId: " + chatId;
        User user = userRepo.findById(chatId).orElseThrow();
        UserGameStat stat = user.getGameStat();
        user.getGameStat().setLastGame(LocalDateTime.now());
        if (text.contains("выиграли!")) {
            if (text.contains("Блэкджек")) {
                betService.playerBlackJack(chatId);
                stat.setBjWins(stat.getBjWins() + 1);
            }
            betService.playerWin(chatId);
            stat.setWins(stat.getWins() + 1);
            log.info(logText + " - win!");
        } else if (text.contains("проиграли!")) {
            betService.playerLose(chatId);
            stat.setLoses(stat.getLoses() + 1);
            log.info(logText + " - lose!");
        } else if (text.contains("Ничья!")) {
            stat.setDraws(stat.getDraws() + 1);
            log.info(logText + " - draw!");
        }
        statRepo.save(stat);
    }
}


