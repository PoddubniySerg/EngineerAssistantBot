package ru.land.model;

public class Brigade {

    private final int id;
    private final String password;

    public Brigade(int id, String password) {
        this.id = id;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    @Override
    public int hashCode() {
        return id + password.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof final Brigade brigade)) return false;
        return id == brigade.id && password.equals(brigade.password);
    }
}
