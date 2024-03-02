package com.zazergel.bjbot.bot.service.manager;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class ErrorManager extends AbstractManager {
    @Override
    public EditMessageText sendEditAnswer(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("<b>Ошибка авторизации</b>. Для продолжения используйте команду: /start")
                .parseMode(ParseMode.HTML)
                .replyMarkup(null)
                .build();
    }

    @Override
    public SendMessage sendAnswer(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("<b>Ошибка авторизации</b>. Для продолжения используйте команду: /start")
                .parseMode(ParseMode.HTML)
                .replyMarkup(null)
                .build();
    }
}
