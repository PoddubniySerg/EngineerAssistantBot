package ru.land.controller.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.land.service.enums.CallbackQuerySelect;
import ru.land.service.enums.StaticStrings;
import ru.land.service.keyboard.CalendarKeyboard;
import ru.land.service.keyboard.ShopListKeyboard;
import ru.land.repository.BotRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;

public class CallbackQueryHandler extends ContentHandler {

    public CallbackQueryHandler(BotRepository repository) {
        super(repository);
    }

    @Override
    public void handle(Object content, AbsSender sender) throws TelegramApiException, ParseException {
        if (!(content instanceof final CallbackQuery callbackQuery)) return;
        final var callbackData = callbackQuery.getData();
        if (callbackData != null && !callbackData.isEmpty()) {
//            если callbackData нет в списке команд, то завершаем выполнение
            if (CallbackQuerySelect.commands().stream().noneMatch(callbackData::startsWith)) return;
//            новый пользователь
            final var user = callbackQuery.getFrom();
            if (callbackData.startsWith(CallbackQuerySelect.BRIGADE.command().getCommand())) {
                if (userIsExist(user)) return;
                repository.addUser(user, integerFromCallbackData(callbackData));
            }
//            подготовка сообщения для отправки
            final var sendMessage = new SendMessage();
            sendMessage.setChatId(callbackQuery.getMessage().getChatId());
//            обработка ответа от списка объектов
            if (callbackData.startsWith(CallbackQuerySelect.SHOP.command().getCommand())) {
                shopCommandHandle(sendMessage, callbackData, user);
            }
            if (callbackData.startsWith(CallbackQuerySelect.CALENDAR.command().getCommand())) {
                calendarCommandHandle(sendMessage, callbackData, user);
            }
            if (callbackData.startsWith(CallbackQuerySelect.MONTH_PRE.command().getCommand())) {
                prePostCommandHandle(sendMessage, callbackData, user, callbackQuery.getMessage(), Calendar.MONTH);
            }
            if (callbackData.startsWith(CallbackQuerySelect.MONTH_POST.command().getCommand())) {
                prePostCommandHandle(sendMessage, callbackData, user, callbackQuery.getMessage(), Calendar.MONTH);
            }
            if (callbackData.startsWith(CallbackQuerySelect.SET_DATE_TO_SHOP.command().getCommand())) {
                setDateToShop(sendMessage, callbackData);
            }
            if (callbackData.startsWith(CallbackQuerySelect.YEAR_PRE.command().getCommand())) {
                prePostCommandHandle(sendMessage, callbackData, user, callbackQuery.getMessage(), Calendar.YEAR);
            }
            if (callbackData.startsWith(CallbackQuerySelect.YEAR_POST.command().getCommand())) {
                prePostCommandHandle(sendMessage, callbackData, user, callbackQuery.getMessage(), Calendar.YEAR);
            }
//            удаление предыдущей клавиатуры из чата
            final var deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(callbackQuery.getMessage().getChatId());
            deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
            sender.execute(deleteMessage);
//            отправка ответа
            sender.execute(sendMessage);
        }
    }

    private int integerFromCallbackData(String callbackData) {
        return Integer.parseInt(callbackData.substring(callbackData.lastIndexOf("/") + 1));
    }

    protected void shopCommandHandle(SendMessage sendMessage, String callbackData, User user) {
//        TODO дописать с учетом того, что репозиторий хранит список дат
        final var date = repository.getDateByShop(repository.getShopById(integerFromCallbackData(callbackData)));
        final var text = date == null ?
                CallbackQuerySelect.CALENDAR.command().getDescription() :
                "ТО запланировано на: " + new SimpleDateFormat(StaticStrings.YEAR_MONTH_AND_DAY_MAPPER.toString()).format(date) + "\n" +
                        "Чтобы изменить выберите новую дату или 'ОТМЕНА' для отмены действия";
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(new CalendarKeyboard(repository, user, true).getInlineKeyboard());
    }

    protected void calendarCommandHandle(SendMessage sendMessage, String callbackData, User user) throws ParseException {
        final var dateString = callbackData.substring(callbackData.lastIndexOf("/") + 1);
        final var date = new SimpleDateFormat(StaticStrings.YEAR_MONTH_AND_DAY_MAPPER.toString()).parse(dateString);
        var shopList = repository.shopList(date, user);
        if (shopList == null) shopList = Collections.emptyList();
        final var stringBuilder = new StringBuilder();
        if (!shopList.isEmpty()) {
            stringBuilder.append("На ").append(dateString).append(" запланировано ТО:").append("\n\n");
            var rowsCounter = 1;
            for (var shop : shopList) {
                stringBuilder.append(rowsCounter).append(". ").append(shop).append("\n");
                rowsCounter++;
            }
        }
        stringBuilder.append("\n").append(CallbackQuerySelect.SET_DATE_TO_SHOP.command().getDescription());
        sendMessage.setText(stringBuilder.toString());
        sendMessage.setReplyMarkup(new ShopListKeyboard(repository, user.getId(), shopList, dateString).getInlineKeyboard());
    }

    protected void prePostCommandHandle(SendMessage sendMessage, String callbackData, User user, Message message, int calendarField) throws ParseException {
        final var text = message.hasText() ? message.getText() : CallbackQuerySelect.CALENDAR.command().getDescription();
        final var calendar = Calendar.getInstance(TimeZone.getTimeZone(StaticStrings.TIME_ZONE.toString()));
        final var dateString = callbackData.substring(callbackData.lastIndexOf("/") + 1);
        final var date = new SimpleDateFormat(StaticStrings.YEAR_AND_MONTH_MAPPER.toString()).parse(dateString);
        calendar.setTime(date);
        if (callbackData.startsWith(CallbackQuerySelect.MONTH_PRE.command().getCommand())
                || callbackData.startsWith(CallbackQuerySelect.YEAR_PRE.command().getCommand())) {
            calendar.set(calendarField, calendar.get(calendarField) - 1);
        }
        if (callbackData.startsWith(CallbackQuerySelect.MONTH_POST.command().getCommand())
                || callbackData.startsWith(CallbackQuerySelect.YEAR_POST.command().getCommand())) {
            calendar.set(calendarField, calendar.get(calendarField) + 1);
        }
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(new CalendarKeyboard(repository, calendar, user).getInlineKeyboard());
    }

    protected void setDateToShop(SendMessage sendMessage, String callbackData) throws ParseException {
        final var params = callbackData.substring(callbackData.lastIndexOf("/") + 1).split(";");
        final var date = new SimpleDateFormat(StaticStrings.YEAR_MONTH_AND_DAY_MAPPER.toString()).parse(params[0]);
        repository.setMaintenance(date, Integer.parseInt(params[1]));
        sendMessage.setText(StaticStrings.DATE_IS_SET.toString());
    }
}
