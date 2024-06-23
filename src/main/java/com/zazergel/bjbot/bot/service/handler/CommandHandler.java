package com.zazergel.bjbot.bot.service.handler;

import com.zazergel.bjbot.bot.Bot;
import com.zazergel.bjbot.bot.factory.AnswerMessageFactory;
import com.zazergel.bjbot.bot.service.command.StartCommand;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommandHandler {

    StartCommand startCommand;
    AnswerMessageFactory messageFactory;

    @Autowired
    public CommandHandler(StartCommand startCommand,
                          AnswerMessageFactory messageFactory) {
        this.startCommand = startCommand;
        this.messageFactory = messageFactory;
    }


    public BotApiMethod<?> answer(Message message, Bot bot) {
        long chatId = message.getChatId();
        String username = message.getChat().getUserName();
        if (message.getText().equals("/start")) {
            log.info("User: " + username + " was init for chatId: " + chatId);
            delPrevMessage(chatId, bot, message);
            return startCommand.sendAnswer(message);
        } else {
            log.info("Unsupported command: " + message.getText()
                     + " from user: " + message.getChat().getUserName());
            return null;
        }
    }

    private void delPrevMessage(Long chatId, Bot bot, Message message) {
        try {
            bot.execute(messageFactory.deleteMessage(chatId, message.getMessageId()));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
