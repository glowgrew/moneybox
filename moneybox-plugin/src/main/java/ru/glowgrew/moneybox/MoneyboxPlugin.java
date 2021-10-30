package ru.glowgrew.moneybox;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import ru.glowgrew.moneybox.environment.ServerEnvironmentProvider;

@Plugin(name = "Moneybox", version = "1.0.0")
@Author("glowgrew")
public class MoneyboxPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getLogger()
              .info(String.format("Welcome to Moneybox! We're running on %s environment.",
                                  ServerEnvironmentProvider.get().getName()));
    }
}
