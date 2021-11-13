package ru.glowgrew.moneybox.database;

import io.r2dbc.pool.ConnectionPool;
import ru.glowgrew.moneybox.database.pool.ConnectionPoolConfigurationProvider;
import ru.glowgrew.moneybox.database.pool.SimpleConnectionPoolConfigurationProvider;
import ru.glowgrew.moneybox.database.postgresql.PostgresqlConnectionFactoryProvider;

public class ConnectionPoolServiceImpl implements ConnectionPoolService {

    @Override
    public ConnectionFactoryProvider getConnectionFactoryProvider(ConnectionType connectionType) {
        if (connectionType == ConnectionType.POSTGRESQL) {
            return new PostgresqlConnectionFactoryProvider();
        }
        throw new IllegalArgumentException();
    }

    @Override
    public ReactorCredentialsFactory createDefaultCredentialsFactory(ConnectionType connectionType, String prefix) {
        return new PrefixedEnvironmentReactorCredentialsFactory(connectionType, prefix);
    }

    @Override
    public ConnectionPoolConfigurationProvider getConnectionPoolConfigurationProvider(
            ConnectionFactoryProvider connectionFactoryProvider, ReactorCredentialsFactory credentialsFactory
    ) {
        return new SimpleConnectionPoolConfigurationProvider(connectionFactoryProvider, credentialsFactory.create());
    }

    @Override
    public ConnectionPool createConnectionPool(ConnectionPoolConfigurationProvider configurationProvider) {
        return new ConnectionPool(configurationProvider.create());
    }
}
