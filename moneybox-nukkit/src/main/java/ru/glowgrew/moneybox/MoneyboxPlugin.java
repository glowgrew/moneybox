package ru.glowgrew.moneybox;

import cn.nukkit.command.PluginCommand;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.service.ServicePriority;
import com.google.common.base.Stopwatch;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ValidationDepth;
import net.kyori.adventure.key.Key;
import reactor.core.publisher.Mono;
import ru.glowgrew.moneybox.api.MoneyboxApi;
import ru.glowgrew.moneybox.command.MoneyboxCommand;
import ru.glowgrew.moneybox.configuration.MoneyboxConfiguration;
import ru.glowgrew.moneybox.configuration.MoneyboxConfigurationProvider;
import ru.glowgrew.moneybox.database.ConnectionFactoryByCredentialsProvider;
import ru.glowgrew.moneybox.database.MoneyboxRepository;
import ru.glowgrew.moneybox.database.pool.*;
import ru.glowgrew.moneybox.environment.ServerEnvironmentProvider;
import ru.glowgrew.moneybox.localization.MoneyboxLocalizationService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class MoneyboxPlugin extends PluginBase {

    private ConnectionPool connectionPool;
    private MoneyboxLocalizationService localizationService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getLogger().info(String.format("Welcome to Moneybox! We're running on %s environment.",
                                       ServerEnvironmentProvider.get().getName()));

        MoneyboxConfiguration configuration = new YamlNukkitMoneyboxConfiguration(() -> {
            reloadConfig();
            return getConfig();
        });

        MoneyboxConfigurationProvider.set(configuration);
        final ConnectionType type =
                SimpleConnectionTypeResolverStrategy.INSTANCE.resolve(configuration.getDatasourceType());

        ConnectionPoolService connectionPoolService = new ConnectionPoolServiceImpl();

        final ConnectionFactoryByCredentialsProvider connectionFactoryProvider =
                connectionPoolService.getConnectionFactoryProvider(type);

        final ConnectionPoolConfigurationProvider configurationProvider =
                connectionPoolService.getConnectionPoolConfigurationProvider(connectionFactoryProvider,
                                                                             configuration.getCredentials());

        connectionPool = connectionPoolService.createConnectionPool(configurationProvider);
        discoverConnection(connectionPool);

        MoneyboxRepository moneyboxRepository =
                connectionPoolService.createRepository(type, connectionPool, configuration);
        MoneyboxApi moneyboxApi = new CachingMoneyboxApi(this, moneyboxRepository);

        getServer().getServiceManager().register(MoneyboxApi.class, moneyboxApi, this, ServicePriority.NORMAL);

        localizationService = MoneyboxLocalizationService.create(Key.key("moneybox", "localization"),
                                                                 new Locale("ru", "ru"),
                                                                 this::getLocalizationFile);
        localizationService.register();

        ((PluginCommand<?>) getCommand("moneybox")).setExecutor(new MoneyboxCommand(this, moneyboxApi));
    }

    @Override
    public void onDisable() {
        if (localizationService != null) {
            localizationService.unregister();
        }
        getServer().getServiceManager().cancel(this);
        if (connectionPool != null) {
            connectionPool.close();
        }
    }

    private InputStream getLocalizationFile(String filename) {
        final Path dataFolder = getDataFolder().toPath().resolve("localization");
        boolean notExistsDataFolder = Files.notExists(dataFolder);
        if (notExistsDataFolder) {
            try {
                Files.createDirectories(dataFolder);
            } catch (IOException e) {
                throw new RuntimeException("Exception due to create directory", e);
            }
        }
        Path path = dataFolder.resolve(filename);
        if (notExistsDataFolder || Files.notExists(path)) {
            try {
                Files.copy(getResource("localization/" + filename), path);
            } catch (IOException e) {
                throw new RuntimeException("Exception due to copy resource", e);
            }
        }
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException("Exception due to load resource", e);
        }
    }

    private void discoverConnection(ConnectionPool connectionPool) {
        final Stopwatch stopwatch = Stopwatch.createStarted();

        getLogger().info("Discovering a database connection...");

        final Connection connection = connectionPool.create().block();
        if (connection == null) {
            getLogger().error("A provided connection pool is invalid!");
            return;
        }
        Mono.from(connection.validate(ValidationDepth.LOCAL)).doOnSuccess(success -> {
            if (success) {
                getLogger().info("Successfully connected to database in " +
                                 stopwatch.elapsed(TimeUnit.MILLISECONDS) +
                                 " ms!");
            } else {
                getLogger().error("Cannot connect to database.");
            }
        }).then(connectionPool.warmup()).subscribe(rows -> {
            if (rows == 0) {
                getLogger().info("Successfully warmed up the database.");
            } else {
                getLogger().error("Cannot warmup the database.");
            }
        });
    }
}
