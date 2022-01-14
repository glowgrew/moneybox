package ru.glowgrew.moneybox.database.pool;

public enum SimpleConnectionTypeResolverStrategy implements ConnectionTypeResolverStrategy {

    INSTANCE;

    private static final ConnectionType DEFAULT_MODE = ConnectionType.POSTGRESQL;

    @Override
    public ConnectionType resolve(String type) {
        try {
            return ConnectionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DEFAULT_MODE;
        }
    }
}
