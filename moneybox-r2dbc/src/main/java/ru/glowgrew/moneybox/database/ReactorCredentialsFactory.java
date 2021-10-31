package ru.glowgrew.moneybox.database;

@FunctionalInterface
public interface ReactorCredentialsFactory {

    ReactorCredentials create();
}
