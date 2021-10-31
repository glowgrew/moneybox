package ru.glowgrew.moneybox.database;

import io.r2dbc.spi.ConnectionFactory;

@FunctionalInterface
public interface ConnectionFactoryProvider {

    ConnectionFactory provide(ReactorCredentials credentials);
}
