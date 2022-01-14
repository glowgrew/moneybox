package ru.glowgrew.moneybox;

import cn.nukkit.utils.Config;
import ru.glowgrew.moneybox.api.MoneyboxCredentials;
import ru.glowgrew.moneybox.configuration.MoneyboxConfiguration;

import java.util.function.Supplier;

public class YamlNukkitMoneyboxConfiguration implements MoneyboxConfiguration {

    private final Supplier<Config> configurationSupplier;

    private MoneyboxCredentials credentials;
    private long startingBalanceAmount;
    private String datasourceType;

    public YamlNukkitMoneyboxConfiguration(Supplier<Config> configurationSupplier) {
        this.configurationSupplier = configurationSupplier;
        reload();
    }

    @Override
    public MoneyboxCredentials getCredentials() {
        return credentials;
    }

    @Override
    public long getStartingBalanceAmount() {
        return startingBalanceAmount;
    }

    @Override
    public String getDatasourceType() {
        return datasourceType;
    }

    @Override
    public void reload() {
        final Config configuration = configurationSupplier.get();
        this.credentials = MoneyboxCredentials.builder()
                                              .setHost(configuration.getString(format("hostname"), "127.0.0.1"))
                                              .setPort(configuration.getInt(format("port"), 3306))
                                              .setUsername(configuration.getString(format("username"), "root"))
                                              .setPassword(configuration.getString(format("password"), "root"))
                                              .setDatabase(configuration.getString(format("database"), "test"))
                                              .build();
        this.startingBalanceAmount = configuration.getLong("options.starting-balance", 0);
        this.datasourceType = configuration.getString("selected-datasource", "mysql");
    }

    private String format(String type) {
        return String.join(".", "datasource", type);
    }

}
