package com.zazergel.bjbot.bot.service.handler;

import com.zazergel.bjbot.bot.Bot;
import com.zazergel.bjbot.bot.config.Buttons;
import com.zazergel.bjbot.bot.service.BetService;
import com.zazergel.bjbot.bot.service.BlackJackGameService;
import com.zazergel.bjbot.bot.service.manager.ErrorManager;
import com.zazergel.bjbot.bot.service.manager.MainMenuManager;
import com.zazergel.bjbot.bot.service.manager.RulesManager;
import com.zazergel.bjbot.bot.service.manager.StatManager;
import com.zazergel.bjbot.repository.UserRepo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CallbackQueryHandler {
    BlackJackGameService blackJackGameService;
    RulesManager rulesManager;
    StatManager statManager;
    MainMenuManager mainMenuManager;
    ErrorManager errorManager;
    BetService betService;

    UserRepo userRepo;

    @Autowired
    public CallbackQueryHandler(BlackJackGameService blackJackGameService,
                                RulesManager rulesManager,
                                MainMenuManager mainMenuManager,
                                StatManager statManager,
                                ErrorManager errorManager,
                                BetService betService,
                                UserRepo userRepo) {
        this.blackJackGameService = blackJackGameService;
        this.rulesManager = rulesManager;
        this.mainMenuManager = mainMenuManager;
        this.statManager = statManager;
        this.errorManager = errorManager;
        this.betService = betService;
        this.userRepo = userRepo;
    }


    public BotApiMethod<?> sendAnswer(CallbackQuery callbackQuery, Bot bot) {

        if (!userRepo.existsById(callbackQuery.getMessage().getChatId())) {
            return errorManager.sendEditAnswer(callbackQuery);
        } else {
            String callBackData = callbackQuery.getData();

            if (callBackData.contains(Buttons.MAIN_MENU_BUTTON)) {
                return mainMenuManager.sendEditAnswer(callbackQuery);
            }
            if (callBackData.contains(Buttons.BET_UP_BUTTON)) {
                return betService.userBetDouble(callbackQuery);
            }
            if (callBackData.contains(Buttons.BJ_BET_10) || callBackData.contains(Buttons.BJ_BET_50) || callBackData.contains(Buttons.BJ_BET_100)) {
                return betService.takeUserBet(callbackQuery, bot);
            }
            if (callBackData.contains("BJ")) {
                return blackJackGameService.receivedButton(callbackQuery, bot);
            }
            if (callBackData.contains(Buttons.RULES_BUTTON)) {
                return rulesManager.sendEditAnswer(callbackQuery);
            }
            if (callBackData.contains(Buttons.STAT_BUTTON)) {
                return statManager.sendEditAnswer(callbackQuery);
            } else {
                log.info("Unsupported callbackData: " + callBackData);
                return null;
            }
        }
    }
}
