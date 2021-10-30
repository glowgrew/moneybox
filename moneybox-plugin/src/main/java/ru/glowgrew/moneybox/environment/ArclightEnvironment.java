package ru.glowgrew.moneybox.environment;

public class ArclightEnvironment implements ServerEnvironment {

    @Override
    public String getName() {
        return "Arclight";
    }

    @Override
    public Type getType() {
        return Type.ARCLIGHT;
    }
}
