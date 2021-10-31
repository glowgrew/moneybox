package ru.glowgrew.moneybox.command;

import cloud.commandframework.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;

public interface RegistrableCommand {

    void register(final BukkitCommandManager<CommandSender> manager);
}
