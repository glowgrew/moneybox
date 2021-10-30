package ru.glowgrew.moneybox.environment;

public class CraftbukkitEnvironment implements ServerEnvironment {

    @Override
    public String getName() {
        return "CraftBukkit";
    }

    @Override
    public Type getType() {
        return Type.CRAFTBUKKIT;
    }
}
