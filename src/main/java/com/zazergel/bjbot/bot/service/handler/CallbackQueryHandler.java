package com.zazergel.bjbot.bot.service.handler;

import com.zazergel.bjbot.bot.Bot;
import com.zazergel.bjbot.bot.service.BlackJackGameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@Slf4j
public class CallbackQueryHandler {
    BlackJackGameService blackJackGameService;

    @Autowired
    public CallbackQueryHandler(BlackJackGameService blackJackGameService) {
        this.blackJackGameService = blackJackGameService;
    }


    public BotApiMethod<?> answer(CallbackQuery callbackQuery, Bot bot) {
        String callBackData = callbackQuery.getData();
        if (callBackData.contains("BJ")) {
            return blackJackGameService.receivedButton(callbackQuery, bot);
        }
        log.info("Unsupported callbackData: " + callBackData);
        return null;
    }
}
