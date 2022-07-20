package ru.land.service.parser.customer;

import ru.land.model.Shop;
import ru.land.service.generator.IdGenerator;
import ru.land.service.parser.ShopParser;

import java.util.Arrays;

public class PerekrestokParser implements ShopParser {

    private static final int SIZE_PERECRESTOK_NUMBER = 4;
    private static final String PERECRESTOK = "Перекресток";

    private int index = -1;


    @Override
    public Shop parse(String string) {
        final var shopNumber = string.substring(0, SIZE_PERECRESTOK_NUMBER);
        final var array = string.substring(SIZE_PERECRESTOK_NUMBER).split(" ");
        final var finalString =
                Arrays.toString(Arrays.copyOfRange(array, index + 1, array.length))
                        .substring(1);
        String shopName = null;
        String address;
        if (finalString.contains("(") && finalString.contains(")")) {
            address = finalString.substring(0, finalString.indexOf("(") - 1);
            shopName = finalString.substring(finalString.indexOf("(") + 1, finalString.lastIndexOf(")"));
        } else {
            address = finalString;
        }
        return new Shop(IdGenerator.getInstance().newId(), shopNumber, PERECRESTOK, address, shopName);
    }

    @Override
    public boolean canBeParsed(String string) {
        if (string.length() < SIZE_PERECRESTOK_NUMBER) return false;
        final var array = string.substring(SIZE_PERECRESTOK_NUMBER).split(" ");
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(PERECRESTOK)) {
                index = i;
                return true;
            }
        }
        return false;
    }
}
