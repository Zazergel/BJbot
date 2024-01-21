package com.zazergel.bjbot.bot.service.manager;

import com.zazergel.bjbot.bot.factory.KeyboardFactory;
import com.zazergel.bjbot.entity.user.User;
import com.zazergel.bjbot.entity.user.UserGameStat;
import com.zazergel.bjbot.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class StatManager extends AbstractManager {

    UserRepo userRepo;

    @Autowired
    public StatManager(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public EditMessageText sendEditAnswer(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(getStats(chatId))
                .parseMode(ParseMode.HTML)
                .replyMarkup(KeyboardFactory.getKeyboardToBackMessage())
                .build();
    }

    @Override
    public SendMessage sendAnswer(CallbackQuery callbackQuery) {
        return null;
    }

    private String getStats(long chatId) {
        User user = userRepo.findById(chatId).orElseThrow();
        UserGameStat stat = user.getGameStat();
        return "<b>Баланс:</b> " + user.getScore()
               + "\n\n" +
               "<b>Статистика:</b>\n" +
               "Побед всего: " + stat.getWins() + "\n" +
               "Блэкджеком: " + stat.getBjWins() + "\n\n" +
               "Поражений: " + stat.getLoses() + "\n" +
               "Ничьих: " + stat.getDraws();
    }
}
