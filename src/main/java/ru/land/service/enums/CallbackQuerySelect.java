
package ru.land.service.enums;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.Arrays;
import java.util.List;

public enum CallbackQuerySelect {

    BRIGADE(new BotCommand("/BRIGADE/", "/BRIGADE/")),
    CALENDAR(new BotCommand("/CALENDAR/", "Выберите дату")),
    CANCEL(new BotCommand("/CANCEL/", "ОТМЕНА")),
    LIST_IS_EMPTY(new BotCommand("/LIST_IS_EMPTY/", "Не найдено")),
    MONTH_PRE(new BotCommand("/MONTH_PRE/", "<<")),
    MONTH_POST(new BotCommand("/MONTH_POST/", ">>")),
    MONTH_SET(new BotCommand("/MONTH_SET/", "/MONTH_SET/")),
    SET_DATE(new BotCommand("/SET_DATE/", "Укажите дату:")),
    SET_DATE_TO_SHOP(new BotCommand("/SET_DATE_TO_SHOP/", "Выберите магазин:")),
    SHOP(new BotCommand("/SHOP/", "/SHOP/")),
    YEAR_PRE(new BotCommand("/YEAR_PRE/", "<<")),
    YEAR_POST(new BotCommand("/YEAR_POST/", ">>"));

    private final BotCommand command;

    CallbackQuerySelect(BotCommand command) {
        this.command = command;
    }

    public BotCommand command() {
        return command;
    }

    public static List<String> commands() {
        return Arrays.stream(CallbackQuerySelect.values()).map(command -> command.command.getCommand()).toList();
    }
}
