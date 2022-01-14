package ru.glowgrew.moneybox.database.pool;

import io.r2dbc.pool.ConnectionPoolConfiguration;
import ru.glowgrew.moneybox.api.MoneyboxCredentials;
import ru.glowgrew.moneybox.database.ConnectionFactoryByCredentialsProvider;

import static java.time.Duration.ofSeconds;

public class SimpleConnectionPoolConfigurationProvider implements ConnectionPoolConfigurationProvider {

    private final ConnectionFactoryByCredentialsProvider connectionFactoryProvider;
    private final MoneyboxCredentials credentials;

    public SimpleConnectionPoolConfigurationProvider(
            ConnectionFactoryByCredentialsProvider connectionFactoryProvider, MoneyboxCredentials reactorCredentials
    ) {
        this.connectionFactoryProvider = connectionFactoryProvider;
        this.credentials = reactorCredentials;
    }

    @Override
    public ConnectionPoolConfiguration create() {
        return ConnectionPoolConfiguration.builder(connectionFactoryProvider.provide(credentials))
                                          .maxIdleTime(ofSeconds(1L))
                                          .maxSize(20)
                                          .build();
    }
}
