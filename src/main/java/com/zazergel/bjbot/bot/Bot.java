package com.zazergel.bjbot.bot;

import com.zazergel.bjbot.bot.config.BotConfig;
import com.zazergel.bjbot.bot.controller.UpdateController;
import com.zazergel.bjbot.bot.service.MessageService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class Bot extends TelegramLongPollingBot {

    BotConfig config;
    UpdateController updateController;
    MessageService messageService;


    @Autowired
    public Bot(BotConfig config, UpdateController updateController, MessageService messageService) {
        this.config = config;
        this.updateController = updateController;
        this.messageService = messageService;
    }

    @PostConstruct
    public void init() {
        messageService.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotKey();
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateController.checkUpdate(update);

    }
}

