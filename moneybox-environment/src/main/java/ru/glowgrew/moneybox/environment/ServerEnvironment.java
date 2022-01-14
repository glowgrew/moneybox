package ru.glowgrew.moneybox.environment;

public interface ServerEnvironment {

    String getName();

    enum Type {

        CRUCIBLE(CrucibleEnvironment.class, "io.github.crucible.Crucible"),
        CATSERVER(CatServerEnvironment.class, "catserver.server.CatServer"),
        ARCLIGHT(ArclightEnvironment.class, "io.izzel.arclight.common.mod.util.remapper.resource.RemapSourceHandler"),
        CRAFTBUKKIT(CraftbukkitEnvironment.class, "org.bukkit.craftbukkit.Main"),
        NUKKIT(NukkitEnvironment.class, "cn.nukkit.Nukkit"),

        UNKNOWN(UnknownEnvironment.class, ""),
        ;

        private final Class<?> environmentInstanceClass;
        private final String validationClass;

        Type(Class<?> environmentInstanceClass, String validationClass) {
            this.environmentInstanceClass = environmentInstanceClass;
            this.validationClass = validationClass;
        }

        public Class<?> getEnvironmentInstanceClass() {
            return environmentInstanceClass;
        }

        public String getValidationClass() {
            return validationClass;
        }

    }
}
