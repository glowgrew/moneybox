package ru.glowgrew.moneybox.database.pool;

public interface ConnectionTypeResolverStrategy {

    ConnectionType resolve(String type);
}
