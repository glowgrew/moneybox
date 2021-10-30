package ru.glowgrew.moneybox.environment;

public class CatServerEnvironment implements ServerEnvironment {

    @Override
    public String getName() {
        return "CatServer";
    }

    @Override
    public Type getType() {
        return Type.CATSERVER;
    }
}
