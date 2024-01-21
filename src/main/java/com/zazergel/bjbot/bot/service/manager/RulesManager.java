package com.zazergel.bjbot.bot.service.manager;

import com.zazergel.bjbot.bot.config.Constants;
import com.zazergel.bjbot.bot.factory.KeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class RulesManager extends AbstractManager {

    @Override
    public EditMessageText sendEditAnswer(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(Constants.RULES)
                .parseMode(ParseMode.HTML)
                .replyMarkup(KeyboardFactory.getKeyboardToBackMessage())
                .build();
    }

    @Override
    public SendMessage sendAnswer(CallbackQuery callbackQuery) {
        return null;
    }
}
