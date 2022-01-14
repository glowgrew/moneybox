package ru.glowgrew.moneybox.configuration;

import org.jetbrains.annotations.NotNull;

public final class MoneyboxConfigurationProvider {

    private static MoneyboxConfiguration configuration;

    private MoneyboxConfigurationProvider() {
    }

    public static MoneyboxConfiguration get() {
        return checkNotNull(configuration, "A plugin is not initialized yet");
    }

    public static void set(@NotNull MoneyboxConfiguration configuration) {
        checkNotNull(configuration, "configuration");
        checkState(MoneyboxConfigurationProvider.configuration == null,
                   "A configuration reassignment is not supported");

        MoneyboxConfigurationProvider.configuration = configuration;
    }

    private static <T> T checkNotNull(T value, String message) {
        if (null == value) {
            throw new NullPointerException(message);
        } else {
            return value;
        }
    }

    private static void checkState(boolean state, String message) {
        if (!state) {
            throw new IllegalStateException(message);
        }
    }
}
