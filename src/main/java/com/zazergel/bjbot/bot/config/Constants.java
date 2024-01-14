package com.zazergel.bjbot.bot.config;

import org.springframework.stereotype.Component;

@Component
public class Constants {

    public static final String startGameButton = "START_GAME_BUTTON";
    public static final String rulesButton = "RULES_BUTTON";
    public static final String statisticButton = "STATISTIC_BUTTON";
    public static final String mainMenuButton = "MAIN_MENU_BUTTON";
    public static final String takeButton = "TAKE_BUTTON";
    public static final String noButton = "NO_BUTTON";
    public static final String exceptionOccur = "Exception occurred: ";

    public static String rules = """
            • В игре участвует 4 колоды по 52 карты. В начале игры я раздам вам две карты в открытую, а себе одну открытую и одну закрытую карту.
            • После раздачи, сравниваются значения наших рук. Пока у вас на руке меньше 17 очков, вы можете взять еще карту, если захотите.
            • Ваша цель - набрать больше очков, но не более 21, иначе вы проигрываете.
            • Победит тот, у кого наберется больше очков. Если суммы очков равны (кроме Блэкджека), то это ничья.                                                                                 \s
            • Если первые две карты на руке после раздачи дают 21 очко - это Блэкджек. Тот, кто первый его собрал - побеждает. Если мы оба его собрали - ничья.\s""";

    private Constants() {
    }
}
