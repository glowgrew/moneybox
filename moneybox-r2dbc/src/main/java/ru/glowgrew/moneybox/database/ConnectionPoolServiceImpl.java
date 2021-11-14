package ru.glowgrew.moneybox.database;

import io.r2dbc.pool.ConnectionPool;
import ru.glowgrew.moneybox.api.MoneyboxCredentials;
import ru.glowgrew.moneybox.database.mysql.MysqlConnectionFactoryProvider;
import ru.glowgrew.moneybox.database.pool.ConnectionPoolConfigurationProvider;
import ru.glowgrew.moneybox.database.pool.SimpleConnectionPoolConfigurationProvider;
import ru.glowgrew.moneybox.database.postgresql.PostgresqlConnectionFactoryProvider;

public class ConnectionPoolServiceImpl implements ConnectionPoolService {

    @Override
    public ConnectionFactoryProvider getConnectionFactoryProvider(ConnectionType connectionType) {
        switch (connectionType) {
            case POSTGRESQL:
                return new PostgresqlConnectionFactoryProvider();
            case MYSQL:
                return new MysqlConnectionFactoryProvider();
        }
        throw new IllegalArgumentException();
    }

    @Override
    public ConnectionPoolConfigurationProvider getConnectionPoolConfigurationProvider(
            ConnectionFactoryProvider connectionFactoryProvider, MoneyboxCredentials credentials
    ) {
        return new SimpleConnectionPoolConfigurationProvider(connectionFactoryProvider, credentials);
    }

    @Override
    public ConnectionPool createConnectionPool(ConnectionPoolConfigurationProvider configurationProvider) {
        return new ConnectionPool(configurationProvider.create());
    }
}
