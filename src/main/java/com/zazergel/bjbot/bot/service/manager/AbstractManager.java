package com.zazergel.bjbot.bot.service.manager;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public abstract class AbstractManager {

    public abstract EditMessageText sendEditAnswer(CallbackQuery callbackQuery);

    public abstract SendMessage sendAnswer(CallbackQuery callbackQuery);

}
