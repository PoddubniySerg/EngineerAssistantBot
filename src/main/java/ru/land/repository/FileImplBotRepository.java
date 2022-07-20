package ru.land.repository;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.land.model.Brigade;
import ru.land.model.Shop;
import ru.land.service.enums.StaticStrings;
import ru.land.service.parser.brigade.BrigadeParser;
import ru.land.service.parser.customer.LentaParser;
import ru.land.service.parser.customer.PerekrestokParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FileImplBotRepository implements BotRepository {

    private static final String FILE_UPDATE_PATH = "src/main/resources/Data.txt";

    private final ConcurrentMap<Long, Brigade> brigadeByUserId;
    private final ConcurrentMap<Integer, List<Shop>> shopListByBrigade;
    private final ConcurrentMap<Integer, List<Date>> datesByShopId;

    public FileImplBotRepository() {
        this.brigadeByUserId = new ConcurrentHashMap<>();
        this.shopListByBrigade = new ConcurrentHashMap<>();
        this.datesByShopId = new ConcurrentHashMap<>();
        getUpdate();
    }

    //    обновление данных из файла при наличии
    private void getUpdate() {
//        если файл отсутствует или не читается, завершаем работу метода
        final var path = Path.of(FILE_UPDATE_PATH);
        if (!Files.exists(path) || !Files.isReadable(path)) return;
//        если список магазинов по ключу 0 равен null, то создаем пустой список и кладем по ключу 0 в мапу
        shopListByBrigade.computeIfAbsent(0, key -> new ArrayList<>());

        final var fullShopList = shopListByBrigade.get(0);
        try {
            var brigade = -1;
            var shopListForBrigade = new ArrayList<Shop>();
//            читаем из файла (почему-то читается одной строкой весь файл), затем делим на строки и обрабатываем каждую
            var rowsInFile = Files.readString(path);
            for (var row : rowsInFile.split("\r\n")) {
//                пробуем получить из строки бригаду
                final var parserBrigade = new BrigadeParser();
                if (parserBrigade.canBeParsed(row)) {
                    if (brigade > 0 && !shopListForBrigade.isEmpty())
                        shopListByBrigade.put(brigade, sortList(shopListForBrigade));
                    brigade = parserBrigade.parse();
                    shopListForBrigade = new ArrayList<>();
                }
//                пробуем получить из строки магазин
                final var parsersCustomer = List.of(new PerekrestokParser(), new LentaParser());
                for (var parser : parsersCustomer) {
                    if (parser.canBeParsed(row)) {
                        final var shop = parser.parse(row);
                        if (shop != null) {
                            if (fullShopList.stream().noneMatch(el -> el.getSpecificity().equals(shop.getSpecificity()))) {
                                if (brigade > 0) shopListForBrigade.add(shop);
                                fullShopList.add(shop);
                            }
                        }
                    }
                }
            }
            if (brigade > 0 && !shopListForBrigade.isEmpty())
                shopListByBrigade.put(brigade, sortList(shopListForBrigade));
            shopListByBrigade.put(0, sortList(fullShopList));
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }

    //    сортируем магазины в списке перед их добавлением в мапу
    private List<Shop> sortList(List<Shop> shops) {
        return shops.stream().sorted(Comparator.comparing(Shop::getSpecificity)).toList();
    }

    @Override
    public boolean isUserExist(long id) {
        return brigadeByUserId.containsKey(id);
    }

    @Override
    public void addUser(User user, int brigade) {
        brigadeByUserId.put(user.getId(), new Brigade(brigade, ""));
    }

    @Override
    public List<Integer> getBrigades() {
        return shopListByBrigade.keySet().stream().filter(brigade -> brigade > 0).toList();
    }

    @Override
    public int getBrigadeByUserId(long userId) {
        return brigadeByUserId.get(userId).getId();
    }

    @Override
    public List<Shop> shopList(Date date, User user) {
        final var shops = shopListByBrigade(getBrigadeByUserId(user.getId()));
        final var dateFormat = new SimpleDateFormat(StaticStrings.YEAR_MONTH_AND_DAY_MAPPER.toString());
        final var dateString = dateFormat.format(date);
        final var newShops = new ArrayList<Shop>();
        final var keySet = datesByShopId.keySet();
        for (var shop : shops) {
            if (keySet.contains(shop.getId())) {
                final var dates = datesByShopId.get(shop.getId());
                final var d = datesByShopId.get(shop.getId());
                if (dates != null
                        && datesByShopId.get(shop.getId()).stream().anyMatch(day -> dateFormat.format(day).equals(dateString))) {
                    newShops.add(shop);
                }
            }
        }
        return newShops;
    }

    @Override
    public List<Shop> shopListByBrigade(int brigade) {
        final var shops = shopListByBrigade.get(brigade);
        return shops == null ? new ArrayList<>() : shops;
    }

    @Override
    public void setMaintenance(Date newDate, int shopId) {
        if (!datesByShopId.containsKey(shopId)) datesByShopId.put(shopId, new ArrayList<>());
        final var dateFormat = new SimpleDateFormat(StaticStrings.YEAR_AND_MONTH_MAPPER.toString());
        final var newDateString = dateFormat.format(newDate);
        for (var i = 0; i < datesByShopId.get(shopId).size(); i++) {
            final var date = datesByShopId.get(shopId).get(i);
            final var dateString = dateFormat.format(date);
            if (newDateString.equals(dateString)) datesByShopId.get(shopId).remove(i);
        }
        datesByShopId.get(shopId).add(newDate);
    }

    @Override
    public Date getDateByShop(Shop shop) {
        final var dates = datesByShopId.getOrDefault(shop.getId(), null);
        final var dateFormat = new SimpleDateFormat(StaticStrings.YEAR_AND_MONTH_MAPPER.toString());
        if (dates != null) {
            for (var date : dates) {
                final var dateString = dateFormat.format(date);
                final var nowDate = dateFormat.format(new Date());
                if (dateString.equals(nowDate)) return date;
            }
        }
        return null;
    }

    @Override
    public Shop getShopById(int id) {
        final var shops = shopListByBrigade.get(0).stream()
                .filter(shop -> shop.getId() == id)
                .findAny();
        return shops.orElse(null);
    }
}
