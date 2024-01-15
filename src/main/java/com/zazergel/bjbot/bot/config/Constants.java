package com.zazergel.bjbot.bot.config;

import org.springframework.stereotype.Component;

@Component
public class Constants {

    public static final String START_GAME_BUTTON = "START_GAME_BUTTON";
    public static final String RULES_BUTTON = "RULES_BUTTON";
    public static final String STATISTIC_BUTTON = "STATISTIC_BUTTON";
    public static final String MAIN_MENU_BUTTON = "MAIN_MENU_BUTTON";
    public static final String TAKE_BUTTON = "TAKE_BUTTON";
    public static final String NO_BUTTON = "NO_BUTTON";
    public static final String EXCEPTION_OCCURRED = "Exception occurred: ";

    public static final String RULES = """
            <b>Упрощенные правила</b>
            • В игре участвует 4 колоды по 52 карты. В начале игры я раздам вам две карты в открытую, а себе одну открытую и одну закрытую карту.
            • После раздачи, сравниваются значения наших рук. Пока у вас на руке меньше 21 очка, вы можете взять еще карту, если захотите.
            • Ваша цель - набрать больше очков, но не более 21, иначе вы проигрываете.
            • Победит тот, у кого наберется больше очков. Если суммы очков равны <i>(кроме Блэкджека)</i>, то это ничья.
            • Если первые две карты на руке после раздачи дают 21 очко - это <b>Блэкджек</b>. Тот, кто первый его собрал - побеждает. Если мы оба его собрали - ничья.
            """;

    public static final String REBOOT_PLEASE = "Похоже, что-то пошло не так, пожалуйста, используйте <i>/start</i> для перезапуска игровой сессии.";

    private Constants() {
    }
}
