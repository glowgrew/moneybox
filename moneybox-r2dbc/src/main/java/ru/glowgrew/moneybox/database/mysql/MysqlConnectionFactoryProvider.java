package ru.glowgrew.moneybox.database.mysql;

import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration;
import dev.miku.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import ru.glowgrew.moneybox.api.MoneyboxCredentials;
import ru.glowgrew.moneybox.database.ConnectionFactoryProvider;

import static java.time.Duration.ofMinutes;

public class MysqlConnectionFactoryProvider implements ConnectionFactoryProvider {

    @Override
    public ConnectionFactory provide(MoneyboxCredentials credentials) {
        return MySqlConnectionFactory.from(MySqlConnectionConfiguration.builder()
                                                                       .connectTimeout(ofMinutes(1L))
                                                                       .host(credentials.host())
                                                                       .port(credentials.port())
                                                                       .username(credentials.username())
                                                                       .password(credentials.password())
                                                                       .database(credentials.database())
                                                                       .build());
    }
}
