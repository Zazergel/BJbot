package com.zazergel.bjbot.bot.service;

import com.zazergel.bjbot.bot.Bot;
import com.zazergel.bjbot.bot.service.handler.CallbackQueryHandler;
import com.zazergel.bjbot.bot.service.handler.CommandHandler;
import com.zazergel.bjbot.bot.service.handler.MessageHandler;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UpdateDispatcher {

    MessageHandler messageHandler;
    CallbackQueryHandler callbackQueryHandler;
    CommandHandler commandHandler;


    @Autowired
    public UpdateDispatcher(MessageHandler messageHandler,
                            CallbackQueryHandler callbackQueryHandler,
                            CommandHandler commandHandler) {
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.commandHandler = commandHandler;
    }

    public BotApiMethod<?> distribute(Update update, Bot bot) {
        if (update.hasCallbackQuery()) {
            return callbackQueryHandler.sendAnswer(update.getCallbackQuery(), bot);
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText() && (message.getText().startsWith("/"))) {
                return commandHandler.answer(update.getMessage(), bot);
            } else {
                return messageHandler.answer(message);
            }
        }
        log.info("Unsupported update: " + update);
        return null;
    }
}
