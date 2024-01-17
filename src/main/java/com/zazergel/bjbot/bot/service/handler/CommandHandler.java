package com.zazergel.bjbot.bot.service.handler;

import com.zazergel.bjbot.bot.Bot;
import com.zazergel.bjbot.bot.factory.KeyboardFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
public class CommandHandler {

    public BotApiMethod<?> answer(Message message, Bot bot) {
        long chatId = message.getChatId();
        if (message.getText().equals("/start")) {
            log.info("User: " + message.getChat().getUserName() + " was init for chatId: " + chatId);
            try {
                bot.execute(DeleteMessage.builder().chatId(chatId).messageId(message.getMessageId()).build());
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
            return start(message);
        } else {
            log.info("Unsupported command: " + message.getText()
                     + " from user: " + message.getChat().getUserName());
            return null;
        }
    }

    private SendMessage start(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("Здравствуйте, добро пожаловать.\n\n <b>Главное меню:</b>")
                .parseMode(ParseMode.HTML)
                .replyMarkup(KeyboardFactory.getKeyboardToMainMenuMessage())
                .build();
    }
}
