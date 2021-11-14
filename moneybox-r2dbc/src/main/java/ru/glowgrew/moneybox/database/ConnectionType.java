package ru.glowgrew.moneybox.database;

import ru.glowgrew.moneybox.database.mysql.MysqlConnectionFactoryProvider;
import ru.glowgrew.moneybox.database.postgresql.PostgresqlConnectionFactoryProvider;

public enum ConnectionType {

    POSTGRESQL(PostgresqlConnectionFactoryProvider.class, 5432),
    MYSQL(MysqlConnectionFactoryProvider.class, 3306);

    private final Class<? extends ConnectionFactoryProvider> connectionFactoryProviderClass;
    private final int defaultPort;

    ConnectionType(Class<? extends ConnectionFactoryProvider> connectionFactoryProviderClass, int defaultPort) {
        this.connectionFactoryProviderClass = connectionFactoryProviderClass;
        this.defaultPort = defaultPort;
    }

    public Class<? extends ConnectionFactoryProvider> getConnectionFactoryProviderClass() {
        return connectionFactoryProviderClass;
    }

    public int getDefaultPort() {
        return defaultPort;
    }
}
