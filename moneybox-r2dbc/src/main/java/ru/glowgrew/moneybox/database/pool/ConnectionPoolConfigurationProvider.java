package ru.glowgrew.moneybox.database.pool;

import io.r2dbc.pool.ConnectionPoolConfiguration;

@FunctionalInterface
public interface ConnectionPoolConfigurationProvider {

    ConnectionPoolConfiguration create();
}
