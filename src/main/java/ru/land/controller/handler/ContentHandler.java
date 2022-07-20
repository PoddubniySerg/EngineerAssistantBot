package ru.land.controller.handler;

import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.land.repository.BotRepository;

import java.text.ParseException;

public abstract class ContentHandler {

    protected final BotRepository repository;

    public ContentHandler(BotRepository repository) {
        this.repository = repository;
    }

    protected boolean userIsExist(User user) {
        if (user == null) return false;
        final long id = user.getId();
        return id > 0 && repository.isUserExist(id);
    }

    public abstract void handle(Object content, AbsSender sender) throws TelegramApiException, ParseException;
}
