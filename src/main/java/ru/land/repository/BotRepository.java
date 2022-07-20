package ru.land.repository;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.land.model.Shop;

import java.util.Date;
import java.util.List;

public interface BotRepository {

    boolean isUserExist(long id);

    void addUser(User user, int brigade);

    List<Integer> getBrigades();

    int getBrigadeByUserId(long userId);

    List<Shop> shopList(Date date, User user);

    List<Shop> shopListByBrigade(int brigade);

    void setMaintenance(Date date, int shopId);

    Date getDateByShop(Shop shop);

    Shop getShopById(int id);
}
