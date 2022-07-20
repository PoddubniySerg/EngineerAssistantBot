package ru.land.service.enums;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.Arrays;
import java.util.List;

public enum BotCommands {

    START(new BotCommand("/start", "Список команд:")),
    AUTHORIZATION_COMMAND(new BotCommand("/brigade", "Выберите свою бригаду")),
    CALENDAR_COMMAND(new BotCommand("/calendar", "Календарь:")),
    SHOP_LIST_COMMAND(new BotCommand("/shop_list", "Список магазинов"));

    private final BotCommand command;

    BotCommands(BotCommand command) {
        this.command = command;
    }

    public BotCommand botCommand() {
        return command;
    }

    public static List<String> commands() {
        return Arrays.stream(BotCommands.values()).map(command -> command.botCommand().getCommand()).toList();
    }
}
