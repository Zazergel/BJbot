package com.zazergel.bjbot.bot.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@Slf4j
public class MessageHandler {

    public BotApiMethod<?> answer(Message message) {
        log.info(message.getChat().getUserName() + " send: " + message.getText());
        return null;
    }
}
