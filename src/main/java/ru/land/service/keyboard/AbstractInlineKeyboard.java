package ru.land.service.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.land.repository.BotRepository;

public abstract class AbstractInlineKeyboard {

    protected final BotRepository repository;

    public AbstractInlineKeyboard(BotRepository repository) {
        this.repository = repository;
    }

    public abstract InlineKeyboardMarkup getInlineKeyboard();
}
