package com.zazergel.bjbot.bot.controller;

import com.zazergel.bjbot.bot.service.MessageService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UpdateController {

    MessageService messageService;

    @Autowired
    public UpdateController(MessageService messageService) {
        this.messageService = messageService;
    }

    public void checkUpdate(Update update) {
        if (update.hasMessage()) {
            log.info("User: " + update.getMessage().getChat().getUserName() +
                     " in chat with id: " + update.getMessage().getChat().getId()
                     + " send: " + update.getMessage().getText());

            messageService.onUpdateReceivedText(update);

        } else if (update.hasCallbackQuery()) {
            log.info("User press button: " + update.getCallbackQuery().getData()
                     + " in chat with id: " + update.getCallbackQuery().getMessage().getChat().getId());

            messageService.onUpdateReceivedCallbackQuery(update);

        } else {
            log.error("Unsupported update: " + update);
        }
    }

}
