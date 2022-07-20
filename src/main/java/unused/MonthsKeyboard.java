package unused;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.land.service.enums.CallbackQuerySelect;
import ru.land.service.enums.StaticStrings;
import ru.land.service.keyboard.AbstractInlineKeyboard;
import ru.land.repository.BotRepository;

import java.util.*;

public class MonthsKeyboard extends AbstractInlineKeyboard {

    protected final long oldMessageId;
    protected final String dateString;

    public MonthsKeyboard(BotRepository repository, long oldMessageId, String dateString) {
        super(repository);
        this.oldMessageId = oldMessageId;
        this.dateString = dateString;
    }

    @Override
    public InlineKeyboardMarkup getInlineKeyboard() {
        final var rows = new ArrayList<List<InlineKeyboardButton>>();
        final var calendar = Calendar.getInstance(TimeZone.getTimeZone(StaticStrings.TIME_ZONE.toString()));
        for (int month = 0; month < calendar.getActualMaximum(Calendar.MONTH); month++) {
            final var monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            final var button = new InlineKeyboardButton(monthName);
            button.setCallbackData(CallbackQuerySelect.MONTH_SET.command().getCommand() + month);
            rows.add(List.of(button));
        }
        return new InlineKeyboardMarkup(rows);
    }
}
