package ru.land.service.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.land.service.enums.CallbackQuerySelect;
import ru.land.repository.BotRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BrigadesKeyboard extends AbstractInlineKeyboard {

    public BrigadesKeyboard(BotRepository repository) {
        super(repository);
    }

    @Override
    public InlineKeyboardMarkup getInlineKeyboard() {
        final var rows = new ArrayList<List<InlineKeyboardButton>>();
        for (var brigade : repository.getBrigades().stream().sorted().toList()) {
            final var textButton = String.valueOf(brigade);
            final var button = new InlineKeyboardButton(textButton);
            button.setCallbackData(CallbackQuerySelect.BRIGADE.command().getCommand() + textButton);
            rows.add(List.of(button));
        }
        if (rows.isEmpty()) {
            final var button = new InlineKeyboardButton(CallbackQuerySelect.LIST_IS_EMPTY.command().getDescription());
            button.setCallbackData(CallbackQuerySelect.LIST_IS_EMPTY.command().getCommand());
            rows.add(List.of(button));
        } else {
//        Добавляем кнопку "отмена"
            final var cancelButton = new InlineKeyboardButton();
            cancelButton.setText(CallbackQuerySelect.CANCEL.command().getDescription());
            cancelButton.setCallbackData(CallbackQuerySelect.CANCEL.command().getCommand());
            rows.add(List.of(cancelButton));
        }
        return new InlineKeyboardMarkup(rows);
    }
}
