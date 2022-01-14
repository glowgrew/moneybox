package ru.glowgrew.moneybox.database.pool;

import io.r2dbc.pool.ConnectionPool;
import ru.glowgrew.moneybox.api.MoneyboxCredentials;
import ru.glowgrew.moneybox.configuration.MoneyboxConfiguration;
import ru.glowgrew.moneybox.database.ConnectionFactoryByCredentialsProvider;
import ru.glowgrew.moneybox.database.MoneyboxRepository;

public interface ConnectionPoolService {

    ConnectionFactoryByCredentialsProvider getConnectionFactoryProvider(ConnectionType connectionType);

    ConnectionPoolConfigurationProvider getConnectionPoolConfigurationProvider(
            ConnectionFactoryByCredentialsProvider connectionFactoryProvider, MoneyboxCredentials credentials
    );

    ConnectionPool createConnectionPool(ConnectionPoolConfigurationProvider configurationProvider);

    MoneyboxRepository createRepository(
            ConnectionType connectionType, ConnectionPool pool, MoneyboxConfiguration configuration
    );
}
