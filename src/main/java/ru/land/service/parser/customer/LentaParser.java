package ru.land.service.parser.customer;

import ru.land.model.Shop;
import ru.land.service.parser.ShopParser;

public class LentaParser implements ShopParser {
    @Override
    public Shop parse(String string) {
        return null;
    }

    @Override
    public boolean canBeParsed(String string) {
        return false;
    }
}
