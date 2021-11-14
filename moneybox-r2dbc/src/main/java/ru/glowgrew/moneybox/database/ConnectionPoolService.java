package ru.glowgrew.moneybox.database;

import io.r2dbc.pool.ConnectionPool;
import ru.glowgrew.moneybox.api.MoneyboxCredentials;
import ru.glowgrew.moneybox.database.pool.ConnectionPoolConfigurationProvider;

public interface ConnectionPoolService {

    ConnectionFactoryProvider getConnectionFactoryProvider(ConnectionType connectionType);

    ConnectionPoolConfigurationProvider getConnectionPoolConfigurationProvider(
            ConnectionFactoryProvider connectionFactoryProvider, MoneyboxCredentials credentials
    );

    ConnectionPool createConnectionPool(ConnectionPoolConfigurationProvider configurationProvider);
}
