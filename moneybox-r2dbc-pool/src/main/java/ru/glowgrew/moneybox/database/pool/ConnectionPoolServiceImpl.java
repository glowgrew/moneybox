package ru.glowgrew.moneybox.database.pool;

import io.r2dbc.pool.ConnectionPool;
import ru.glowgrew.moneybox.api.MoneyboxCredentials;
import ru.glowgrew.moneybox.configuration.MoneyboxConfiguration;
import ru.glowgrew.moneybox.database.ConnectionFactoryByCredentialsProvider;
import ru.glowgrew.moneybox.database.MoneyboxRepository;
import ru.glowgrew.moneybox.database.mysql.MysqlConnectionFactoryByCredentialsProvider;
import ru.glowgrew.moneybox.database.mysql.MysqlMoneyboxRepository;
import ru.glowgrew.moneybox.database.postgresql.PostgresqlConnectionFactoryByCredentialsProvider;
import ru.glowgrew.moneybox.database.postgresql.PostgresqlMoneyboxRepository;

public class ConnectionPoolServiceImpl implements ConnectionPoolService {

    @Override
    public ConnectionFactoryByCredentialsProvider getConnectionFactoryProvider(ConnectionType connectionType) {
        switch (connectionType) {
            case POSTGRESQL:
                return new PostgresqlConnectionFactoryByCredentialsProvider();
            case MYSQL:
                return new MysqlConnectionFactoryByCredentialsProvider();
        }
        throw new IllegalArgumentException();
    }

    @Override
    public ConnectionPoolConfigurationProvider getConnectionPoolConfigurationProvider(
            ConnectionFactoryByCredentialsProvider connectionFactoryProvider, MoneyboxCredentials credentials
    ) {
        return new SimpleConnectionPoolConfigurationProvider(connectionFactoryProvider, credentials);
    }

    @Override
    public ConnectionPool createConnectionPool(ConnectionPoolConfigurationProvider configurationProvider) {
        return new ConnectionPool(configurationProvider.create());
    }

    @Override
    public MoneyboxRepository createRepository(
            ConnectionType connectionType, ConnectionPool pool, MoneyboxConfiguration configuration
    ) {
        switch (connectionType) {
            case POSTGRESQL:
                return new PostgresqlMoneyboxRepository(pool, configuration);
            case MYSQL:
                return new MysqlMoneyboxRepository(pool, configuration);
        }
        throw new IllegalArgumentException();
    }
}
