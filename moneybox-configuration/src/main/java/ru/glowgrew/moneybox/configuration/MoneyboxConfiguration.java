package ru.glowgrew.moneybox.configuration;

import ru.glowgrew.moneybox.api.MoneyboxCredentials;

public interface MoneyboxConfiguration {

    MoneyboxCredentials getCredentials();

    long getStartingBalanceAmount();

    String getDatasourceType();

    void reload();
}
