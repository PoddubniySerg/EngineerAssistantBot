package ru.land.service.keyboard;


import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.land.service.enums.CallbackQuerySelect;
import ru.land.service.enums.StaticStrings;
import ru.land.repository.BotRepository;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarKeyboard extends AbstractInlineKeyboard {

    protected final Calendar calendar;
    protected final User user;
    protected final boolean setDate;

    public CalendarKeyboard(BotRepository repository, User user) {
        super(repository);
        this.user = user;
        this.setDate = false;
        this.calendar = Calendar.getInstance(TimeZone.getTimeZone(StaticStrings.TIME_ZONE.toString()));
        setFirstDay();
    }

    public CalendarKeyboard(BotRepository repository, User user, boolean setDate) {
        super(repository);
        this.user = user;
        this.setDate = setDate;
        this.calendar = Calendar.getInstance(TimeZone.getTimeZone(StaticStrings.TIME_ZONE.toString()));
        setFirstDay();
    }

    public CalendarKeyboard(BotRepository repository, Calendar calendar, User user) {
        super(repository);
        this.calendar = calendar;
        this.user = user;
        this.setDate = false;
        setFirstDay();
    }

    private void setFirstDay() {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
    }

    @Override
    public InlineKeyboardMarkup getInlineKeyboard() {
        final var rows = new ArrayList<List<InlineKeyboardButton>>();
//        первые три строки в клавиатуре всегда год, месяц и дни недели
        fillFirstRows(rows, new SimpleDateFormat(StaticStrings.YEAR_AND_MONTH_MAPPER.toString()).format(calendar.getTime()));
//        Пустые кнопки клавиатуры до 1го числа заданного месяца
        final var keys = new ArrayList<Map.Entry<String, String>>();
        addEmptyButtonsPre(keys);
//        заполняем числа месяца
        final var callbackData = setDate ?
                CallbackQuerySelect.SET_DATE.command().getCommand()
                : CallbackQuerySelect.CALENDAR.command().getCommand();
        for (int day = 1; day <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            final var date = calendar.getTime();
            final var maintenancesToday = repository.shopList(date, user).size();
            final var text = maintenancesToday == 0 ? "" : "(" + maintenancesToday + " то)";
            final var dateString = new SimpleDateFormat(StaticStrings.YEAR_MONTH_AND_DAY_MAPPER.toString()).format(date);
            keys.add(Map.entry(day + text, (callbackData + dateString)));
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                rows.add(getRow(keys));
                keys.clear();
            }
        }
//        Пустые кнопки клавиатуры после последнего числа заданного месяца
        addEmptyButtonsPost(keys);
        rows.add(getRow(keys));
//        Добавляем кнопку "отмена"
        addCancelButton(rows);
//        собираем и отдаем собранную клавиатуру
        return new InlineKeyboardMarkup(rows);
    }

    private void fillFirstRows(List<List<InlineKeyboardButton>> rows, String date) {
        final var year = String.valueOf(calendar.get(Calendar.YEAR));
        rows.add(
                getRow(
                        List.of(Map.entry(CallbackQuerySelect.YEAR_PRE.command().getDescription(),
                                        CallbackQuerySelect.YEAR_PRE.command().getCommand() + date),
                                Map.entry(year, date),
                                Map.entry(CallbackQuerySelect.YEAR_POST.command().getDescription(),
                                        CallbackQuerySelect.YEAR_POST.command().getCommand() + date))));
        final var monthString = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        rows.add(
                getRow(
                        List.of(Map.entry(CallbackQuerySelect.MONTH_PRE.command().getDescription(),
                                        CallbackQuerySelect.MONTH_PRE.command().getCommand() + date),
                                Map.entry(monthString, date),
                                Map.entry(CallbackQuerySelect.MONTH_POST.command().getDescription(),
                                        CallbackQuerySelect.MONTH_POST.command().getCommand() + date))));
        rows.add(
                getRow(calendar.getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                        .entrySet()
                        .stream()
                        .sorted(Comparator.comparingInt(Map.Entry::getValue))
                        .map(entry -> Map.entry(entry.getKey(), entry.getKey()))
                        .collect(Collectors.toList())
                ));
    }

    private void addEmptyButtonsPre(List<Map.Entry<String, String>> keys) {
        var firstDayOfMonthInWeek = calendar.get(Calendar.DAY_OF_WEEK);
        while (firstDayOfMonthInWeek > Calendar.SUNDAY) {
            keys.add(Map.entry(" ", " "));
            firstDayOfMonthInWeek--;
        }
    }

    private void addEmptyButtonsPost(List<Map.Entry<String, String>> keys) {
        var lastDayOfMonthInWeek = calendar.get(Calendar.DAY_OF_WEEK);
        while (lastDayOfMonthInWeek < Calendar.SATURDAY) {
            keys.add(Map.entry(" ", " "));
            lastDayOfMonthInWeek++;
        }
    }

    private void addCancelButton(List<List<InlineKeyboardButton>> rows) {
        rows.add(
                getRow(
                        List.of(
                                Map.entry(CallbackQuerySelect.CANCEL.command().getDescription(),
                                        CallbackQuerySelect.CANCEL.command().getCommand()))));
    }

    private List<InlineKeyboardButton> getRow(List<Map.Entry<String, String>> keys) {
        final var buttons = new ArrayList<InlineKeyboardButton>();
        for (var key : keys) {
            final var button = new InlineKeyboardButton();
            button.setText(key.getKey());
            button.setCallbackData(key.getValue());
            buttons.add(button);
        }
        return buttons;
    }
}
