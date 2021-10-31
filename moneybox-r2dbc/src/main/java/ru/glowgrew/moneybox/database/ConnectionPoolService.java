package ru.glowgrew.moneybox.database;

import io.r2dbc.pool.ConnectionPool;
import ru.glowgrew.moneybox.database.pool.ConnectionPoolConfigurationProvider;

public interface ConnectionPoolService {

    ConnectionFactoryProvider getConnectionFactoryProvider(ConnectionType connectionType);

    ReactorCredentialsFactory createDefaultCredentialsFactory(ConnectionType connectionType, String prefix);

    ConnectionPoolConfigurationProvider getConnectionPoolConfigurationProvider(
            ConnectionFactoryProvider connectionFactoryProvider, ReactorCredentialsFactory credentialsFactory
    );

    ConnectionPool createConnectionPool(ConnectionPoolConfigurationProvider configurationProvider);
}
