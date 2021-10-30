package ru.glowgrew.moneybox.environment;

public class CrucibleEnvironment implements ServerEnvironment {

    @Override
    public String getName() {
        return "Crucible";
    }

    @Override
    public Type getType() {
        return Type.CRUCIBLE;
    }
}
