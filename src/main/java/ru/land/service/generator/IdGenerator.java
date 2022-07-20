package ru.land.service.generator;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    protected static final int MIN_ID = 1;

    protected static volatile IdGenerator idGenerator = null;

    protected static AtomicInteger id = new AtomicInteger(MIN_ID);

    public static IdGenerator getInstance() {
        if (idGenerator == null) idGenerator = new IdGenerator();
        return idGenerator;
    }

    public int newId() {
        return id.getAndIncrement();
    }
}
