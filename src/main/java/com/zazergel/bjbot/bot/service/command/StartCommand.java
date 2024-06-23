package com.zazergel.bjbot.bot.service.command;

import com.zazergel.bjbot.bot.factory.KeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class StartCommand {

    public SendMessage sendAnswer(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("Здравствуйте, добро пожаловать.\n\n <b>Главное меню:</b>")
                .parseMode(ParseMode.HTML)
                .replyMarkup(KeyboardFactory.getKeyboardToMainMenuMessage())
                .build();
    }
}
