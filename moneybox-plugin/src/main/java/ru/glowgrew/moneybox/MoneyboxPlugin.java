package ru.glowgrew.moneybox;

import com.google.common.base.Stopwatch;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ValidationDepth;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import reactor.core.publisher.Mono;
import ru.glowgrew.moneybox.api.MoneyboxApi;
import ru.glowgrew.moneybox.command.CommandService;
import ru.glowgrew.moneybox.database.*;
import ru.glowgrew.moneybox.database.pool.ConnectionPoolConfigurationProvider;
import ru.glowgrew.moneybox.database.util.EnvironmentUtils;
import ru.glowgrew.moneybox.environment.ServerEnvironmentProvider;
import ru.glowgrew.moneybox.localization.MoneyboxLocalizationService;
import ru.glowgrew.moneybox.postgresql.DatabaseMoneyboxRepository;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Plugin(name = "Moneybox", version = "1.0.0")
@Description("An economy plugin for different Minecraft server environments powered by Reactor Core")
@Author("glowgrew")
public final class MoneyboxPlugin extends JavaPlugin {

    private ConnectionPool connectionPool;
    private MoneyboxLocalizationService localizationService;
    private BukkitAudiences adventureApi;

    @Override
    public void onDisable() {
        if (localizationService != null) {
            localizationService.unregister();
        }
        if (adventureApi != null) {
            adventureApi.close();
        }
        Bukkit.getServicesManager().unregisterAll(this);
        if (connectionPool != null) {
            connectionPool.close();
        }
    }

    @Override
    public void onEnable() {
        getLogger().info(String.format("Welcome to Moneybox! We're running on %s environment.",
                                       ServerEnvironmentProvider.get().getName()));

        connectionPool = constructConnectionPool();
        discoverConnection(connectionPool);

        MoneyboxRepository moneyboxRepository = new DatabaseMoneyboxRepository(connectionPool);
        MoneyboxApi moneyboxApi = new CachingMoneyboxApi(this, moneyboxRepository);

        Bukkit.getServicesManager().register(MoneyboxApi.class, moneyboxApi, this, ServicePriority.Normal);

        adventureApi = BukkitAudiences.create(this);

        localizationService = MoneyboxLocalizationService.create(Key.key("moneybox", "localization"),
                                                                 new Locale("ru", "ru"),
                                                                 this::getResource);
        localizationService.register();

        final CommandService commandService = CommandService.create(this, moneyboxApi, adventureApi);
        commandService.register();
    }

    private ConnectionPool constructConnectionPool() {
        ConnectionPoolService connectionPoolService = new ConnectionPoolServiceImpl();

        final String dataSourceType = MoneyboxConstants.ENVIRONMENT_PREFIX + "_DATASOURCE_TYPE";
        final ConnectionType connectionType = ConnectionType.valueOf(EnvironmentUtils.getString(dataSourceType)
                                                                                     .orElseThrow(() -> new IllegalArgumentException(
                                                                                             "Please specify data source type using environment variable " +
                                                                                             dataSourceType)));

        final ConnectionFactoryProvider connectionFactoryProvider =
                connectionPoolService.getConnectionFactoryProvider(connectionType);

        final ReactorCredentialsFactory credentialsFactory = connectionPoolService.createDefaultCredentialsFactory(
                connectionType,
                MoneyboxConstants.ENVIRONMENT_PREFIX);

        final ConnectionPoolConfigurationProvider configurationProvider =
                connectionPoolService.getConnectionPoolConfigurationProvider(connectionFactoryProvider,
                                                                             credentialsFactory);

        return connectionPoolService.createConnectionPool(configurationProvider);
    }

    private void discoverConnection(ConnectionPool connectionPool) {
        final Stopwatch stopwatch = Stopwatch.createStarted();

        getLogger().info("Discovering a database connection...");

        final Connection connection = connectionPool.create().block();
        if (connection == null) {
            getLogger().severe("A provided connection pool is invalid!");
            return;
        }
        Mono.from(connection.validate(ValidationDepth.LOCAL)).doOnSuccess(success -> {
            if (success) {
                getLogger().info("Successfully connected to database in " +
                                 stopwatch.elapsed(TimeUnit.MILLISECONDS) +
                                 " ms!");
            } else {
                getLogger().severe("Cannot connect to database.");
            }
        }).then(connectionPool.warmup()).subscribe(rows -> {
            if (rows == 0) {
                getLogger().info("Successfully warmed up the database.");
            } else {
                getLogger().severe("Cannot warmup the database.");
            }
        });
    }
}
