package ru.land.service.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.land.model.Shop;
import ru.land.service.enums.CallbackQuerySelect;
import ru.land.service.enums.StaticStrings;
import ru.land.repository.BotRepository;

import java.text.SimpleDateFormat;
import java.util.*;

public class ShopListKeyboard extends AbstractInlineKeyboard {

    protected final long brigade;
    protected final List<Shop> shops;
    protected final String date;

    public ShopListKeyboard(BotRepository repository, long userId) {
        super(repository);
        this.brigade = repository.getBrigadeByUserId(userId);
        this.shops = null;
        this.date = "";
    }

    public ShopListKeyboard(BotRepository repository, long userId, List<Shop> shops, String date) {
        super(repository);
        this.brigade = repository.getBrigadeByUserId(userId);
        this.shops = shops;
        this.date = date;
    }

    @Override
    public InlineKeyboardMarkup getInlineKeyboard() {
        final var rows = new ArrayList<List<InlineKeyboardButton>>();
//        формируем список магазинов за вычетом уже имеющихся на указанную дату (переданы в конструкторе)
        var shopList = repository.shopListByBrigade((int) brigade);
//        если список пуст
        if (shopList.isEmpty()) {
            final var button = new InlineKeyboardButton();
            button.setText(CallbackQuerySelect.LIST_IS_EMPTY.command().getDescription());
            button.setCallbackData(CallbackQuerySelect.LIST_IS_EMPTY.command().getCommand());
            rows.add(List.of(button));
            return new InlineKeyboardMarkup(rows);
        }
//        выбираем команду для callbackData и заполняем списки кнопок
        var callbackData = shops == null || date == null ?
                CallbackQuerySelect.SHOP.command().getCommand()
                : CallbackQuerySelect.SET_DATE_TO_SHOP.command().getCommand() + date;
        for (var shop : shopList) {
            if (shops != null && !shops.contains(shop)) {
                final var date = repository.getDateByShop(shop);
                var printDate = "";
                if (date != null) {
                    final var calendar =
                            Calendar.getInstance(TimeZone.getTimeZone(StaticStrings.TIME_ZONE.toString()));
                    calendar.setTime(date);
                    final var dayOfWeek =
                            calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
                    final var dateString =
                            new SimpleDateFormat(StaticStrings.YEAR_MONTH_AND_DAY_MAPPER.toString()).format(date);
                    printDate = " (" + dateString + ": " + dayOfWeek + ")";
                }
                final var button = new InlineKeyboardButton(shop.getAddress() + printDate);
                button.setCallbackData(callbackData + ";" + shop.getId());
                rows.add(List.of(button));
            }
        }
//        Добавляем кнопку "отмена"
        final var cancelButton = new InlineKeyboardButton();
        cancelButton.setText(CallbackQuerySelect.CANCEL.command().getDescription());
        cancelButton.setCallbackData(CallbackQuerySelect.CANCEL.command().getCommand());
        rows.add(List.of(cancelButton));

        return new InlineKeyboardMarkup(rows);
    }
}
