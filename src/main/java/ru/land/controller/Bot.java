package ru.land.controller;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.land.service.enums.StaticStrings;
import ru.land.controller.handler.CallbackQueryHandler;
import ru.land.controller.handler.MessageHandler;
import ru.land.repository.BotRepository;

import java.text.ParseException;

public class Bot extends TelegramLongPollingBot {

    private final BotRepository repositoty;

    public Bot(BotRepository repositoty) {
        super();
        this.repositoty = repositoty;
    }

    @Override
    public String getBotUsername() {
        return StaticStrings.BOT_USERNAME.toString();
    }

    @Override
    public String getBotToken() {
        return StaticStrings.BOT_TOKEN.toString();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                new MessageHandler(repositoty).handle(update.getMessage(), this);
            }
            if (update.hasCallbackQuery()) {
                final var callbackQuery = update.getCallbackQuery();
                new CallbackQueryHandler(repositoty).handle(callbackQuery, this);
            }
        } catch (TelegramApiException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
