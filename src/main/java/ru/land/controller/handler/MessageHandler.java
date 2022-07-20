package ru.land.controller.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.land.service.enums.BotCommands;
import ru.land.service.enums.StaticStrings;
import ru.land.service.keyboard.BrigadesKeyboard;
import ru.land.service.keyboard.CalendarKeyboard;
import ru.land.service.keyboard.ShopListKeyboard;
import ru.land.repository.BotRepository;

import java.util.Arrays;
import java.util.List;

public class MessageHandler extends ContentHandler {

    public MessageHandler(BotRepository repository) {
        super(repository);
    }

    @Override
    public void handle(Object content, AbsSender sender) throws TelegramApiException {
        if (!(content instanceof final Message message)) return;
        if (message.hasText()) {
            final var id = message.getChat().getId();
            final var sendMessage = new SendMessage();
            sendMessage.setChatId(id);
            final var user = message.getFrom();
            final long userId = user.getId();
            final boolean userExist = userIsExist(user);
            final var text = message.getText();
            if (!userExist
                    && !BotCommands.AUTHORIZATION_COMMAND.botCommand().getCommand().equals(text)
                    && !BotCommands.START.botCommand().getCommand().equals(text)) {
                sendMessage.setText(
                        StaticStrings.NONE_BRIGADE.toString() + BotCommands.AUTHORIZATION_COMMAND.botCommand().getCommand()
                );
                sender.execute(sendMessage).getMessageId();
                return;
            }
            if (BotCommands.CALENDAR_COMMAND.botCommand().getCommand().equalsIgnoreCase(text)) {
                sendMessage.setText(BotCommands.CALENDAR_COMMAND.botCommand().getDescription());
                sendMessage.setReplyMarkup(new CalendarKeyboard(repository, user).getInlineKeyboard());
            } else if (!userExist
                    && BotCommands.AUTHORIZATION_COMMAND.botCommand().getCommand().equalsIgnoreCase(text)) {
                sendMessage.setText(BotCommands.AUTHORIZATION_COMMAND.botCommand().getDescription());
                sendMessage.setReplyMarkup(new BrigadesKeyboard(repository).getInlineKeyboard());
            } else if (userExist
                    && BotCommands.AUTHORIZATION_COMMAND.botCommand().getCommand().equals(text)) {
                final int brigade = repository.getBrigadeByUserId(userId);
                sendMessage.setText(brigade == 0 ? "admin" : "Бригада №" + brigade);
            } else if (BotCommands.START.botCommand().getCommand().equalsIgnoreCase(text)) {
                sendMessage.setText(listToString(
                        Arrays.stream(BotCommands.values()).map(el -> el.botCommand().getCommand()).toList()
                ));
            } else if (BotCommands.SHOP_LIST_COMMAND.botCommand().getCommand().equalsIgnoreCase(text)) {
                sendMessage.setText(BotCommands.SHOP_LIST_COMMAND.botCommand().getDescription());
                sendMessage.setReplyMarkup(new ShopListKeyboard(repository, userId).getInlineKeyboard());
            } else {
                sendMessage.setText(StaticStrings.UNKNOWN_COMMAND + "\n" + listToString(
                        Arrays.stream(BotCommands.values()).map(el -> el.botCommand().getCommand()).toList()
                ));
            }
            sender.execute(sendMessage).getMessageId();
        }
    }

    private String listToString(List<String> list) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(BotCommands.START.botCommand().getDescription());
        for (String command : list) {
            stringBuilder.append("\n").append(command);
        }
        return stringBuilder.toString();
    }
}
