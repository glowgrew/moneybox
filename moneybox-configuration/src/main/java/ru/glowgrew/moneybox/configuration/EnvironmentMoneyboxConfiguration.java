package ru.glowgrew.moneybox.configuration;

import ru.glowgrew.moneybox.api.MoneyboxCredentials;
import ru.glowgrew.moneybox.configuration.util.EnvironmentUtils;

// MONEYBOX_DATASOURCE=POSTGRES
// MONEYBOX_DATASOURCE_HOST=127.0.0.1
// MONEYBOX_DATASOURCE_PORT=5432
// MONEYBOX_DATASOURCE_USER=app
// MONEYBOX_DATASOURCE_PASSWORD=gbplf
// MONEYBOX_DATASOURCE_DB=app
public class EnvironmentMoneyboxConfiguration implements MoneyboxConfiguration {

    @Override
    public MoneyboxCredentials getCredentials() {
        return MoneyboxCredentials.builder()
                                  .setHost(EnvironmentUtils.getString(format("HOST")).orElse("127.0.0.1"))
                                  .setPort(EnvironmentUtils.getInt(format("PORT")).orElse(5432))
                                  .setUsername(EnvironmentUtils.getString(format("USER")).orElse("root"))
                                  .setPassword(EnvironmentUtils.getString(format("PASSWORD")).orElse("root"))
                                  .setDatabase(EnvironmentUtils.getString(format("DB")).orElse("database"))
                                  .build();
    }

    @Override
    public long getStartingBalanceAmount() {
        return EnvironmentUtils.getLong("MONEYBOX_STARTING_BALANCE").orElse(0);
    }

    @Override
    public String getDatasourceType() {
        return EnvironmentUtils.getString("MONEYBOX_DATASOURCE")
                               .orElseThrow(() -> new IllegalArgumentException("Please specify data source"));
    }

    @Override
    public void reload() {

    }

    private String format(String type) {
        return String.join("_", "MONEYBOX", "DATASOURCE", type);
    }

}
