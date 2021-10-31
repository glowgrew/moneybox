package ru.glowgrew.moneybox.environment;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

public final class ServerEnvironmentProvider {

    private static final ServerEnvironment ENVIRONMENT;

    static {
        ENVIRONMENT = Arrays.stream(ServerEnvironment.Type.values())
                            .map(type -> discover(type).map(ServerEnvironmentProvider::construct))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .findFirst()
                            .orElse(new UnknownEnvironment());
    }

    private ServerEnvironmentProvider() {
    }

    private static Optional<Class<?>> discover(ServerEnvironment.Type type) {
        try {
            // if some environment class exists, then we're running on this environment
            Class.forName(type.getValidationClass());
            return Optional.of(type.getEnvironmentInstanceClass());
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    private static ServerEnvironment construct(Class<?> cls) {
        try {
            return (ServerEnvironment) cls.getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Cannot instantiate environment class " + cls.getSimpleName(), e);
        }
    }

    /**
     * Retrieves a current server environment instance.
     *
     * @return the environment instance
     */
    public static ServerEnvironment get() {
        return ENVIRONMENT;
    }
}
