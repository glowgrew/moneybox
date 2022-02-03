package ru.glowgrew.moneybox.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import ru.glowgrew.moneybox.api.MoneyboxApi;

public class GiveSubcommand implements CommandExecutor {

    private final MoneyboxApi moneyboxApi;

    public GiveSubcommand(final MoneyboxApi moneyboxApi) {
        this.moneyboxApi = moneyboxApi;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label,
                             final String[] args) {
        if (args.length != 2) {
            return true;
        }
        moneyboxApi.depositBalanceAsync(args[0], Long.parseLong(args[1])).doOnSuccess(unused -> {
            sender.sendMessage("Given " + args[1] + " to " + args[0]);
        }).subscribe();
        return false;
    }
}
