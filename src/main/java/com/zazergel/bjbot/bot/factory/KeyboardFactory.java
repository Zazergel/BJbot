package com.zazergel.bjbot.bot.factory;

import com.zazergel.bjbot.bot.config.Constants;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeyboardFactory {
    public static InlineKeyboardMarkup getKeyboardToMainMenuMessage() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

        InlineKeyboardButton startGameButton = new InlineKeyboardButton();
        startGameButton.setText("Раздавай!");
        startGameButton.setCallbackData(Constants.BJ_START_GAME_BUTTON);

        InlineKeyboardButton rulesButton = new InlineKeyboardButton();
        rulesButton.setText("Правила");
        rulesButton.setCallbackData(Constants.BJ_RULES_BUTTON);

        InlineKeyboardButton statisticButton = new InlineKeyboardButton();
        statisticButton.setText("Моя статистика");
        statisticButton.setCallbackData(Constants.BJ_STATISTIC_BUTTON);

        List<InlineKeyboardButton> rowInLine = List.of(startGameButton, rulesButton, statisticButton);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInLine);
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public static InlineKeyboardMarkup getKeyboardToBackMessage() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton mainMenuButton = new InlineKeyboardButton();
        mainMenuButton.setText("В главное меню");
        mainMenuButton.setCallbackData(Constants.BJ_MAIN_MENU_BUTTON);

        rowInLine.add(mainMenuButton);
        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public static InlineKeyboardMarkup getKeyboardToChooseCard() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

        InlineKeyboardButton takeButton = new InlineKeyboardButton();
        takeButton.setText("Беру");
        takeButton.setCallbackData(Constants.BJ_TAKE_BUTTON);
        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("Пас");
        noButton.setCallbackData(Constants.BJ_NO_BUTTON);

        List<InlineKeyboardButton> rowInLine = List.of(takeButton, noButton);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInLine);
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    private KeyboardFactory() {
    }
}
