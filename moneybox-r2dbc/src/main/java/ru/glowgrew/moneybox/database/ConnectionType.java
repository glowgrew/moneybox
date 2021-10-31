package ru.glowgrew.moneybox.database;

import ru.glowgrew.moneybox.database.postgres.PostgresConnectionFactoryProvider;

public enum ConnectionType {

    POSTGRES(PostgresConnectionFactoryProvider.class, 5432);

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
