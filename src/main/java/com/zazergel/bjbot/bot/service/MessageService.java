package com.zazergel.bjbot.bot.service;

import com.zazergel.bjbot.bot.Bot;
import com.zazergel.bjbot.bot.config.Constants;
import com.zazergel.bjbot.bot.factory.KeyboardFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class MessageService {

    Bot bot;
    final BlackJackGameService blackJackGameService;
    boolean isAuthorized;


    @Autowired
    public MessageService(BlackJackGameService blackJackGameService) {
        this.blackJackGameService = blackJackGameService;
        this.isAuthorized = false;
    }

    public void registerBot(Bot bot) {
        this.bot = bot;
    }

    public void onUpdateReceivedText(Update update) {
        String msg = update.getMessage().getText();
        int messageId = update.getMessage().getMessageId();
        long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getChat().getUserName();

        if (msg.equals("/start")) {
            blackJackGameService.initGameSession(this, chatId);
            log.info("GameSession was init for chatId: " + chatId);

            isAuthorized = true;
            String text = "Здравствуй, " + username + ", добро пожаловать.\n\n <b>Главное меню:</b>";
            sendMessage(chatId, text, KeyboardFactory.getKeyboardToMainMenuMessage());
        }
        deleteMessage(chatId, messageId);

    }

    public void onUpdateReceivedCallbackQuery(Update update) {
        String callBackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        if (!isAuthorized) {
            deleteMessage(chatId, messageId);
            sendMessage(chatId, Constants.REBOOT_PLEASE, null);
        } else if (callBackData.contains("BJ")) {
            blackJackGameService.receivedButton(update);
        } else {
            log.error("Unsupported query");
        }

    }

    private void sendMessage(long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard)
                .build();
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error(Constants.EXCEPTION_OCCURRED + e.getMessage());
        }
    }

    protected void sendEditMessage(long chatId, String text, int messageId, InlineKeyboardMarkup keyboard) {
        EditMessageText message = EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard)
                .build();
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error(Constants.EXCEPTION_OCCURRED + e.getMessage());
        }
    }

    private void deleteMessage(long chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(chatId));
        deleteMessage.setMessageId(messageId);
        try {
            bot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error(Constants.EXCEPTION_OCCURRED + e.getMessage());
        }
    }
}
