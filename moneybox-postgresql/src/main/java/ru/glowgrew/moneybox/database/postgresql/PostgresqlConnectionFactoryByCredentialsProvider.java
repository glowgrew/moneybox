package ru.glowgrew.moneybox.database.postgresql;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import ru.glowgrew.moneybox.api.MoneyboxCredentials;
import ru.glowgrew.moneybox.database.ConnectionFactoryByCredentialsProvider;

import java.util.HashMap;
import java.util.Map;

import static java.time.Duration.ofMinutes;

public class PostgresqlConnectionFactoryByCredentialsProvider implements ConnectionFactoryByCredentialsProvider {

    @Override
    public ConnectionFactory provide(MoneyboxCredentials credentials) {
        Map<String, String> options = new HashMap<>();
        options.put("lock_timeout", "10s");

        return new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
                                                                                .connectTimeout(ofMinutes(1L))
                                                                                .host(credentials.host())
                                                                                .port(credentials.port())
                                                                                .username(credentials.username())
                                                                                .password(credentials.password())
                                                                                .database(credentials.database())
                                                                                .options(options)
                                                                                .build());
    }
}
