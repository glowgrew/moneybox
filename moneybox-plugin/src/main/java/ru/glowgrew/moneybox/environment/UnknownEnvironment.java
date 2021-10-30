package ru.glowgrew.moneybox.environment;

public class UnknownEnvironment implements ServerEnvironment {

    @Override
    public String getName() {
        return "Unknown";
    }

    @Override
    public Type getType() {
        return Type.UNKNOWN;
    }
}
