package com.zazergel.bjbot.bot.service.handler;

import com.zazergel.bjbot.bot.Bot;
import com.zazergel.bjbot.bot.service.BetService;
import com.zazergel.bjbot.bot.service.BlackJackGameService;
import com.zazergel.bjbot.bot.service.manager.MainMenuManager;
import com.zazergel.bjbot.bot.service.manager.RulesManager;
import com.zazergel.bjbot.bot.service.manager.StatManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@Slf4j
public class CallbackQueryHandler {
    BlackJackGameService blackJackGameService;
    RulesManager rulesManager;
    StatManager statManager;
    MainMenuManager mainMenuManager;
    BetService betService;

    @Autowired
    public CallbackQueryHandler(BlackJackGameService blackJackGameService,
                                RulesManager rulesManager,
                                MainMenuManager mainMenuManager,
                                StatManager statManager,
                                BetService betService) {
        this.blackJackGameService = blackJackGameService;
        this.rulesManager = rulesManager;
        this.mainMenuManager = mainMenuManager;
        this.statManager = statManager;
        this.betService = betService;
    }


    public BotApiMethod<?> sendAnswer(CallbackQuery callbackQuery, Bot bot) {
        String callBackData = callbackQuery.getData();

        if (callBackData.contains("MAIN_MENU")) {
            return mainMenuManager.sendEditAnswer(callbackQuery);
        }
        if (callBackData.contains("BET_UP")) {
            return betService.userBetDouble(callbackQuery);
        }
        if (callBackData.contains("10") || callBackData.contains("50") || callBackData.contains("100")) {
            return betService.takeUserBet(callbackQuery, bot);
        }
        if (callBackData.contains("BJ")) {
            return blackJackGameService.receivedButton(callbackQuery, bot);
        }
        if (callBackData.contains("RULES")) {
            return rulesManager.sendEditAnswer(callbackQuery);
        }
        if (callBackData.contains("STAT")) {
            return statManager.sendEditAnswer(callbackQuery);
        } else {
            log.info("Unsupported callbackData: " + callBackData);
            return null;
        }
    }
}
