package ru.glowgrew.moneybox.command;

import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import static cloud.commandframework.execution.CommandExecutionCoordinator.simpleCoordinator;
import static java.util.function.Function.identity;

public class CommandService {

    private final Plugin plugin;
    private final BukkitCommandManager<CommandSender> commandManager;

    private CommandService(Plugin plugin) {
        this.plugin = plugin;
        try {
            commandManager = new BukkitCommandManager<>(plugin, simpleCoordinator(), identity(), identity());

            if (commandManager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
                plugin.getLogger().info("Found Brigadier, using it now");
                commandManager.registerBrigadier();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize the command manager", e);
        }
    }

    public static CommandService create(Plugin plugin) {
        return new CommandService(plugin);
    }

    public void register() {
        plugin.getLogger().info("Successfully registered all plugin commands.");
        new MoneyboxCommand().register(commandManager);
    }
}
