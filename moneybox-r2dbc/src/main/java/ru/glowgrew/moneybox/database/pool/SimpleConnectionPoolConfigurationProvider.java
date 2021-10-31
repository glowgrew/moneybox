package ru.glowgrew.moneybox.database.pool;

import io.r2dbc.pool.ConnectionPoolConfiguration;
import ru.glowgrew.moneybox.database.ConnectionFactoryProvider;
import ru.glowgrew.moneybox.database.ReactorCredentials;

import static java.time.Duration.ofSeconds;

public class SimpleConnectionPoolConfigurationProvider implements ConnectionPoolConfigurationProvider {

    private final ConnectionFactoryProvider connectionFactoryProvider;
    private final ReactorCredentials reactorCredentials;

    public SimpleConnectionPoolConfigurationProvider(
            ConnectionFactoryProvider connectionFactoryProvider, ReactorCredentials reactorCredentials
    ) {
        this.connectionFactoryProvider = connectionFactoryProvider;
        this.reactorCredentials = reactorCredentials;
    }

    @Override
    public ConnectionPoolConfiguration create() {
        return ConnectionPoolConfiguration.builder(connectionFactoryProvider.provide(reactorCredentials))
                                          .maxIdleTime(ofSeconds(1L))
                                          .maxSize(20)
                                          .build();
    }
}
