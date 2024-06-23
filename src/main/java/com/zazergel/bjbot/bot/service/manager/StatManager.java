package com.zazergel.bjbot.bot.service.manager;

import com.zazergel.bjbot.bot.factory.KeyboardFactory;
import com.zazergel.bjbot.bot.service.AuthService;
import com.zazergel.bjbot.entity.user.User;
import com.zazergel.bjbot.entity.user.UserGameStat;
import com.zazergel.bjbot.repository.UserRepo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatManager extends AbstractManager {

    UserRepo userRepo;
    AuthService authService;

    @Autowired
    public StatManager(UserRepo userRepo, AuthService authService) {
        this.userRepo = userRepo;
        this.authService = authService;
    }


    @Override
    public EditMessageText sendEditAnswer(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        if (authService.checkUserRole(chatId)) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text(getStatsForAdmin(chatId))
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(KeyboardFactory.getKeyboardToBackMessage())
                    .build();
        } else {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text(getStatsForUser(chatId))
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(KeyboardFactory.getKeyboardToBackMessage())
                    .build();
        }
    }

    @Override
    public SendMessage sendAnswer(CallbackQuery callbackQuery) {
        return null;
    }

    private String getStatsForUser(long chatId) {
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

    private String getStatsForAdmin(long chatId) {
        User user = userRepo.findById(chatId).orElseThrow();
        UserGameStat stat = user.getGameStat();
        return "<b>Баланс:</b> " + user.getScore()
               + "\n\n" +
               "<b>Статистика по игре:</b>\n" +
               "Побед всего: " + stat.getWins() + "\n" +
               "Блэкджеком: " + stat.getBjWins() + "\n\n" +
               "Поражений: " + stat.getLoses() + "\n" +
               "Ничьих: " + stat.getDraws() + "\n\n" +
               "<b>Статистика по боту:</b>" + "\n" +
               "Пользователей всего: " + userRepo.getCountOfUsers();
    }
}
