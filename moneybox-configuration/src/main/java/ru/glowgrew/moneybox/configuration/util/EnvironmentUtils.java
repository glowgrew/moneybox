package ru.glowgrew.moneybox.configuration.util;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

public final class EnvironmentUtils {

    private EnvironmentUtils() {
    }

    public static Optional<Boolean> getBoolean(String path) {
        return getString(path).map(Boolean::parseBoolean);
    }

    public static Optional<String> getString(String path) {
        return Optional.ofNullable(System.getenv(path));
    }

    public static OptionalInt getInt(String path) {
        return NumberUtils.parseInt(System.getenv(path));
    }

    public static OptionalLong getLong(String path) {
        return NumberUtils.parseLong(System.getenv(path));
    }
}
