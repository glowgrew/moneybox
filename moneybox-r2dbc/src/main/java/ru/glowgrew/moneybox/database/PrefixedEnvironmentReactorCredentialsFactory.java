package ru.glowgrew.moneybox.database;

import static ru.glowgrew.moneybox.database.util.EnvironmentUtils.getInt;
import static ru.glowgrew.moneybox.database.util.EnvironmentUtils.getString;

public class PrefixedEnvironmentReactorCredentialsFactory implements ReactorCredentialsFactory {

    private final ConnectionType connectionType;
    private final String prefix;

    public PrefixedEnvironmentReactorCredentialsFactory(
            ConnectionType connectionType, String prefix
    ) {
        this.connectionType = connectionType;
        this.prefix = prefix;
    }

    @Override
    public ReactorCredentials create() {
        return ReactorCredentials.builder()
                                 .setHost(getString(format("HOST")).orElse("127.0.0.1"))
                                 .setPort(getInt(format("PORT")).orElse(connectionType.getDefaultPort()))
                                 .setUsername(getString(format("USER")).orElse("root"))
                                 .setPassword(getString(format("PASSWORD")).orElse("root"))
                                 .setDatabase(getString(format("DB")).orElse("database"))
                                 .build();
    }

    private String format(String type) {
        return String.join("_", prefix, connectionType.name(), type);
    }
}
