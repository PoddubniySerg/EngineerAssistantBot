package ru.land.model;

public class Shop {

    private final int id;
    private final String specificity;
    private final String owner;
    private final String address;
    private final String shopName;

    public Shop(int id, String specificity, String owner, String address, String shopName) {
        this.id = id;
        this.specificity = specificity == null ? "" : specificity;
        this.owner = owner;
        this.address = address;
        this.shopName = shopName;
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getSpecificity() {
        return specificity;
    }

    @Override
    public int hashCode() {
        return id + specificity.hashCode() + owner.hashCode() + address.hashCode() + shopName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof final Shop shop)) return false;
        return id == shop.id
                && specificity.equals(shop.specificity)
                && owner.equals(shop.owner)
                && address.equals(shop.address)
                && shopName.equals(shop.shopName);
    }

    @Override
    public String toString() {
        final var printShopName = shopName == null ? "" : " (" + shopName + ")";
        final var printSpecificity = specificity == null ? "" : specificity;
        return printSpecificity + " " + owner + " " + address + printShopName;
    }
}
