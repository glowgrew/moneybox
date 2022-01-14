package ru.glowgrew.moneybox.api;

import org.jetbrains.annotations.NotNull;

public class MoneyboxPlayer {

    @NotNull private final String username;

    private MoneyboxPlayer(@NotNull String username) {
        this.username = username;
    }

    public static @NotNull MoneyboxPlayer of(@NotNull String username) {
        return new MoneyboxPlayer(username);
    }

    public @NotNull String getUsername() {
        return username;
    }

    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoneyboxPlayer)) return false;

        @NotNull MoneyboxPlayer that = (MoneyboxPlayer) o;

        return getUsername().equals(that.getUsername());
    }

    @Override
    public @NotNull String toString() {
        return "MoneyboxPlayer{" + "username='" + username + '\'' + '}';
    }
}
