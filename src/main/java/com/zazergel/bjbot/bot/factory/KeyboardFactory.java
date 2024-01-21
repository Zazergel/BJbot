package com.zazergel.bjbot.bot.factory;

import com.zazergel.bjbot.bot.config.Buttons;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardFactory {
    public static InlineKeyboardMarkup getKeyboardToMainMenuMessage() {
        var markupInLine = new InlineKeyboardMarkup();

        var startGameButton = new InlineKeyboardButton();
        startGameButton.setText("💵Ставка");
        startGameButton.setCallbackData(Buttons.BJ_START_BET_BUTTON);

        var rulesButton = new InlineKeyboardButton();
        rulesButton.setText("📃Правила");
        rulesButton.setCallbackData(Buttons.RULES_BUTTON);

        var statisticButton = new InlineKeyboardButton();
        statisticButton.setText("📈Статистика");
        statisticButton.setCallbackData(Buttons.STAT_BUTTON);

        List<InlineKeyboardButton> rowInLine = List.of(rulesButton, startGameButton, statisticButton);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInLine);
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public static InlineKeyboardMarkup getKeyboardToBackMessage() {
        var markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        var mainMenuButton = new InlineKeyboardButton();
        mainMenuButton.setText("В главное меню↩️");
        mainMenuButton.setCallbackData(Buttons.MAIN_MENU_BUTTON);

        rowInLine.add(mainMenuButton);
        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public static InlineKeyboardMarkup getKeyboardToStartGame() {
        var markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        var startGameButton = new InlineKeyboardButton();
        startGameButton.setText("🃏Раздавай!");
        startGameButton.setCallbackData(Buttons.BJ_START_GAME_BUTTON);

        rowInLine.add(startGameButton);
        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public static InlineKeyboardMarkup getKeyboardToChoose() {
        var markupInLine = new InlineKeyboardMarkup();

        var takeButton = new InlineKeyboardButton();
        takeButton.setText("✅Беру");
        takeButton.setCallbackData(Buttons.BJ_TAKE_BUTTON);

        var noButton = new InlineKeyboardButton();
        noButton.setText("❌Пас");
        noButton.setCallbackData(Buttons.BJ_NO_BUTTON);

        var changeBetButton = new InlineKeyboardButton();
        changeBetButton.setText("⏫Удвоить ставку");
        changeBetButton.setCallbackData(Buttons.BET_UP_BUTTON);


        List<InlineKeyboardButton> rowInLine = List.of(takeButton, changeBetButton, noButton);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInLine);
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }


    public static InlineKeyboardMarkup getKeyboardToBet() {
        var markupInLine = new InlineKeyboardMarkup();

        var tenButton = new InlineKeyboardButton();
        tenButton.setText("10");
        tenButton.setCallbackData(Buttons.BJ_BET_10);

        var fiftyButton = new InlineKeyboardButton();
        fiftyButton.setText("50");
        fiftyButton.setCallbackData(Buttons.BJ_BET_50);

        var hundredButton = new InlineKeyboardButton();
        hundredButton.setText("100");
        hundredButton.setCallbackData(Buttons.BJ_BET_100);

        List<InlineKeyboardButton> rowInLine = List.of(tenButton, fiftyButton, hundredButton);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInLine);
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    private KeyboardFactory() {
    }
}
