package ru.glowgrew.moneybox.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.Plugin;
import ru.glowgrew.moneybox.api.MoneyboxApi;
import ru.glowgrew.moneybox.api.MoneyboxPlayer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MoneyboxCommand implements CommandExecutor {

    private final Map<String, CommandExecutor> subcommands = new HashMap<>();
    private final MoneyboxApi moneyboxApi;

    public MoneyboxCommand(Plugin plugin, MoneyboxApi moneyboxApi) {
        this.moneyboxApi = moneyboxApi;
        registerSubcommand("give", new GiveSubcommand(moneyboxApi));
        registerSubcommand("take", new TakeSubcommand(moneyboxApi));
        registerSubcommand("set", new SetSubcommand(moneyboxApi));
        registerSubcommand("get", new GetSubcommand(moneyboxApi));
/*        registerSubcommand("top", new TopSubcommand(moneyboxApi));
        registerSubcommand("pay", new StatsSubcommand(moneyboxApi));
        registerSubcommand("purge", new StatsSubcommand(moneyboxApi));*/
    }

    private void registerSubcommand(String label, CommandExecutor subcommand, String... aliases) {
        if (aliases.length > 0) {
            for (String alias : aliases) {
                registerSubcommand(alias, subcommand);
            }
        }
        subcommands.put(label.toLowerCase(), subcommand);
    }

    @Override
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args
    ) {
        if (!sender.hasPermission("moneybox.admin")) {
            sender.sendMessage("§cУ вас недостаточно прав.");
            return false;
        }
        if (args.length == 0) {
            handleBalance(sender);
            return true;
        }
        if (dispatch(sender, command, label, args)) {
            sendHelp(sender);
            return false;
        }
        return true;
    }

    private void handleBalance(final CommandSender sender) {
        final long balance = moneyboxApi.getCachedBalance(MoneyboxPlayer.of(sender.getName()));
        sender.sendMessage("§aВаш баланс: " + balance);
    }

    private boolean dispatch(CommandSender sender, Command command, String label, String[] args) {
        CommandExecutor subcommand;
        if ((subcommand = subcommands.get(args[0].toLowerCase())) != null) {
            // we provide our subcommand with original arguments without the first entry
            // in case if we need to send the help message inside a subcommand we should return true
            return subcommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6/mb get <игрок> - получить баланс игрока");
        sender.sendMessage("§6/mb give <игрок> <сумма> - добавить баланс игрока");
        sender.sendMessage("§6/mb take <игрок> <сумма> - забрать баланс игрока");
        sender.sendMessage("§6/mb set <игрок> <сумма> - изменить баланс игрока");
    }
}
