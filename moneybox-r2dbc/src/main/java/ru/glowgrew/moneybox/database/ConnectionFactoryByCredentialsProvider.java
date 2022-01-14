package ru.glowgrew.moneybox.database;

import io.r2dbc.spi.ConnectionFactory;
import ru.glowgrew.moneybox.api.MoneyboxCredentials;

@FunctionalInterface
public interface ConnectionFactoryByCredentialsProvider {

    ConnectionFactory provide(MoneyboxCredentials credentials);
}
