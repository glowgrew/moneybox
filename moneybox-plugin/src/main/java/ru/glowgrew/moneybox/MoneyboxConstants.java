package ru.glowgrew.moneybox;

import ru.glowgrew.moneybox.database.util.EnvironmentUtils;

public final class MoneyboxConstants {

    public static final String ENVIRONMENT_PREFIX = "MONEYBOX";

    public static final long STARTING_BALANCE_AMOUNT =
            EnvironmentUtils.getLong(ENVIRONMENT_PREFIX + "_STARTING_BALANCE_AMOUNT").orElse(0L);

    private MoneyboxConstants() {
    }
}
