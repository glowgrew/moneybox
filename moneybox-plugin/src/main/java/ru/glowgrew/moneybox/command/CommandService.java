package ru.glowgrew.moneybox.command;

import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import ru.glowgrew.moneybox.api.MoneyboxApi;
import ru.glowgrew.moneybox.configuration.MoneyboxConfiguration;
import ru.glowgrew.moneybox.localization.LocalizationService;

import static cloud.commandframework.execution.CommandExecutionCoordinator.simpleCoordinator;
import static cloud.commandframework.minecraft.extras.MinecraftExceptionHandler.ExceptionType.*;
import static java.util.function.Function.identity;

public class CommandService {

    private final Plugin plugin;
    private final MoneyboxApi moneyboxApi;
    private final BukkitAudiences adventureApi;
    private final MoneyboxConfiguration configuration;
    private final LocalizationService localizationService;

    private final BukkitCommandManager<CommandSender> commandManager;

    private CommandService(
            Plugin plugin,
            MoneyboxApi moneyboxApi,
            BukkitAudiences adventureApi,
            MoneyboxConfiguration configuration,
            LocalizationService localizationService
    ) {
        this.plugin = plugin;
        this.moneyboxApi = moneyboxApi;
        this.adventureApi = adventureApi;
        this.configuration = configuration;
        this.localizationService = localizationService;
        try {
            commandManager = new BukkitCommandManager<>(plugin, simpleCoordinator(), identity(), identity());

            if (commandManager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
                plugin.getLogger().info("Found Brigadier, using it now");
                commandManager.registerBrigadier();
            }

            new MinecraftExceptionHandler<CommandSender>().withHandler(NO_PERMISSION, $ -> Component.translatable(
                                                                               "command.no_permission",
                                                                               NamedTextColor.RED))
                                                          .withHandler(INVALID_SYNTAX,
                                                                       $ -> Component.translatable(
                                                                               "command.invalid_syntax",
                                                                               NamedTextColor.RED))
                                                          .withHandler(ARGUMENT_PARSING,
                                                                       $ -> Component.translatable(
                                                                               "command.invalid_argument_type",
                                                                               NamedTextColor.RED))
                                                          .withArgumentParsingHandler()
                                                          .apply(commandManager, adventureApi::sender);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize the command manager", e);
        }
    }

    public static CommandService create(
            Plugin plugin,
            MoneyboxApi moneyboxApi,
            BukkitAudiences adventureApi,
            MoneyboxConfiguration configuration,
            LocalizationService localizationService
    ) {
        return new CommandService(plugin, moneyboxApi, adventureApi, configuration, localizationService);
    }

    public void register() {
        plugin.getLogger().info("Successfully registered all plugin commands.");
        new MoneyboxCommand(moneyboxApi, adventureApi, configuration, localizationService).register(commandManager);
    }
}
