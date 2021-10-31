package ru.glowgrew.moneybox.command;

import cloud.commandframework.Command.Builder;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class MoneyboxCommand implements RegistrableCommand {

    @Override
    public void register(final BukkitCommandManager<CommandSender> manager) {
        Builder<CommandSender> builder = manager.commandBuilder("moneybox", "mbox", "mb", "balance", "bal", "ebal");
        manager.command(builder.handler(this::handleBalanceSelf));
    }

    private void handleBalanceSelf(@NonNull CommandContext<CommandSender> context) {
        context.getSender().sendMessage("ANAL");
    }
}
