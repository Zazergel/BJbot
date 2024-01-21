package com.zazergel.bjbot.bot;

import com.zazergel.bjbot.bot.config.BotConfig;
import com.zazergel.bjbot.bot.service.UpdateDispatcher;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class Bot extends TelegramWebhookBot {

    BotConfig config;
    UpdateDispatcher updateDispatcher;


    @Autowired
    public Bot(BotConfig config, UpdateDispatcher updateDispatcher) {
        super(config.getBotKey());
        this.config = config;
        this.updateDispatcher = updateDispatcher;

    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }


    @Override
    public String getBotPath() {
        return config.getBotPath();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return updateDispatcher.distribute(update, this);
    }

}

