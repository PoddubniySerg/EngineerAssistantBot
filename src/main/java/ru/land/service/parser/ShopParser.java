package ru.land.service.parser;

import ru.land.model.Shop;

public interface ShopParser {

    Shop parse(String string);

    boolean canBeParsed(String string);
}
