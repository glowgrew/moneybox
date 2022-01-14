package ru.glowgrew.moneybox.command;

import cloud.commandframework.Command.Builder;
import cloud.commandframework.arguments.standard.LongArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import ru.glowgrew.moneybox.api.MoneyboxApi;
import ru.glowgrew.moneybox.api.MoneyboxPlayer;
import ru.glowgrew.moneybox.configuration.MoneyboxConfiguration;
import ru.glowgrew.moneybox.localization.LocalizationService;

import java.util.concurrent.atomic.AtomicInteger;

public final class MoneyboxCommand implements RegistrableCommand {

    private final MoneyboxApi moneyboxApi;
    private final BukkitAudiences adventureApi;
    private final MoneyboxConfiguration configuration;
    private final LocalizationService localizationService;

    public MoneyboxCommand(
            MoneyboxApi moneyboxApi,
            BukkitAudiences adventureApi,
            MoneyboxConfiguration configuration,
            LocalizationService localizationService
    ) {
        this.moneyboxApi = moneyboxApi;
        this.adventureApi = adventureApi;
        this.configuration = configuration;
        this.localizationService = localizationService;
    }

    @Override
    public void register(final BukkitCommandManager<CommandSender> manager) {
        Builder<CommandSender> builder = manager.commandBuilder("moneybox", "mbox", "mb", "balance", "bal", "ebal");

        manager.command(builder.senderType(Player.class)
                               .permission("moneybox.command.balance.self")
                               .handler(this::handleBalanceSelf));
        manager.command(builder.literal("get")
                               .permission("moneybox.command.balance.other")
                               .argument(StringArgument.of("target"))
                               .handler(this::handleBalanceOther));
        manager.command(builder.literal("set")
                               .permission("moneybox.command.set")
                               .argument(StringArgument.of("target"))
                               .argument(LongArgument.of("amount"))
                               .handler(this::handleBalanceSet));
        manager.command(builder.literal("give")
                               .permission("moneybox.command.give")
                               .argument(StringArgument.of("target"))
                               .argument(LongArgument.of("amount"))
                               .handler(this::handleBalanceGive));
        manager.command(builder.literal("take")
                               .permission("moneybox.command.take")
                               .argument(StringArgument.of("target"))
                               .argument(LongArgument.of("amount"))
                               .handler(this::handleBalanceTake));
        manager.command(builder.literal("purge")
                               .permission("moneybox.command.purge")
                               .argument(StringArgument.of("target"))
                               .handler(this::handleBalancePurge));
        manager.command(builder.literal("pay")
                               .senderType(Player.class)
                               .permission("moneybox.command.pay")
                               .argument(StringArgument.of("target"))
                               .argument(LongArgument.of("amount"))
                               .handler(this::handleBalancePay));
        manager.command(builder.literal("top").permission("moneybox.command.top").handler(this::handleBalanceTop));
    }

    private void handleBalancePay(@NonNull CommandContext<CommandSender> context) {
        final Player player = ((Player) context.getSender());
        final String target = context.get("target");
        final long amount = context.get("amount");

        moneyboxApi.transferBalanceAsync(MoneyboxPlayer.of(player.getName()), target, amount).subscribe(result -> {
            final Audience audience = adventureApi.player(player);
            if (!result) {
                audience.sendMessage(Component.translatable("command.insufficient_funds", NamedTextColor.RED));
                return;
            }
            audience.sendMessage(Component.translatable("command.pay.success", NamedTextColor.GREEN)
                                          .args(Component.text(target), Component.text(amount)));
        });
    }

    private void handleBalancePurge(@NonNull CommandContext<CommandSender> context) {
        final CommandSender sender = context.getSender();
        final String target = context.get("target");

        moneyboxApi.setBalanceAsync(target, configuration.getStartingBalanceAmount()).doOnSuccess(unused -> {
            adventureApi.sender(sender)
                        .sendMessage(Component.translatable("command.purge.success", NamedTextColor.GREEN)
                                              .args(Component.text(target)));
        }).subscribe();
    }

    private void handleBalanceTake(@NonNull CommandContext<CommandSender> context) {
        final CommandSender sender = context.getSender();
        final String target = context.get("target");
        final long amount = context.get("amount");

        moneyboxApi.withdrawBalanceAsync(target, amount).doOnSuccess(unused -> {
            adventureApi.sender(sender)
                        .sendMessage(Component.translatable("command.take.success", NamedTextColor.GREEN)
                                              .args(Component.text(target), Component.text(amount)));
        }).subscribe();
    }

    private void handleBalanceGive(@NonNull CommandContext<CommandSender> context) {
        final CommandSender sender = context.getSender();
        final String target = context.get("target");
        final long amount = context.get("amount");

        moneyboxApi.depositBalanceAsync(target, amount).doOnSuccess(unused -> {
            adventureApi.sender(sender)
                        .sendMessage(Component.translatable("command.give.success", NamedTextColor.GREEN)
                                              .args(Component.text(target), Component.text(amount)));
        }).subscribe();
    }

    private void handleBalanceSet(@NonNull CommandContext<CommandSender> context) {
        final CommandSender sender = context.getSender();
        final String target = context.get("target");
        final long amount = context.get("amount");

        moneyboxApi.setBalanceAsync(target, amount).doOnSuccess(unused -> {
            adventureApi.sender(sender)
                        .sendMessage(Component.translatable("command.set.success", NamedTextColor.GREEN)
                                              .args(Component.text(target), Component.text(amount)));
        }).subscribe();
    }

    private void handleBalanceTop(@NonNull CommandContext<CommandSender> context) {
        final Audience audience = adventureApi.sender(context.getSender());
        audience.sendMessage(Component.translatable("command.top.header", NamedTextColor.GREEN));

        AtomicInteger position = new AtomicInteger(0);
        moneyboxApi.getTopAccounts(MoneyboxApi.Direction.DESCENDING, 0, 10).subscribe(account -> {
            audience.sendMessage(Component.translatable("command.top.entry", NamedTextColor.GRAY)
                                          .args(Component.text(position.incrementAndGet()).color(NamedTextColor.AQUA),
                                                Component.text(account.username()).color(NamedTextColor.GREEN),
                                                Component.text(account.amount()).color(NamedTextColor.GOLD)));
        });
    }

    private void handleBalanceOther(@NonNull CommandContext<CommandSender> context) {
        final CommandSender sender = context.getSender();
        final String target = context.get("target");

        moneyboxApi.getBalanceAsync(target)
                   .subscribe(balance -> adventureApi.sender(sender)
                                                     .sendMessage(Component.translatable("command.balance.other.success",
                                                                                         NamedTextColor.GREEN)
                                                                           .args(Component.text(target),
                                                                                 Component.text(balance)
                                                                                          .color(NamedTextColor.GOLD))));
    }

    private void handleBalanceSelf(@NonNull CommandContext<CommandSender> context) {
        Player player = ((Player) context.getSender());
        final long balance = moneyboxApi.getCachedBalance(MoneyboxPlayer.of(player.getName()));
        adventureApi.player(player)
                    .sendMessage(Component.translatable("command.balance.self.success", NamedTextColor.GREEN)
                                          .args(Component.text(balance).color(NamedTextColor.GOLD)));
    }
}
