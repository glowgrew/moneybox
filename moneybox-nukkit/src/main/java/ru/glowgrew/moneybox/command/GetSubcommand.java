package ru.glowgrew.moneybox.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import ru.glowgrew.moneybox.api.MoneyboxApi;

public class GetSubcommand implements CommandExecutor {

    private final MoneyboxApi moneyboxApi;

    public GetSubcommand(final MoneyboxApi moneyboxApi) {
        this.moneyboxApi = moneyboxApi;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label,
                             final String[] args) {
        if (args.length != 1) {
            return true;
        }
        moneyboxApi.getBalanceAsync(args[0]).subscribe(balance -> {
            sender.sendMessage("Balance of " + args[0] + " is " + balance);
        });
        return false;
    }
}
