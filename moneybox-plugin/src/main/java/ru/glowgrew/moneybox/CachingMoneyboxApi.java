package ru.glowgrew.moneybox;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.glowgrew.moneybox.api.MoneyboxApi;
import ru.glowgrew.moneybox.api.PlayerAccount;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class CachingMoneyboxApi implements MoneyboxApi {

    private final @NotNull MoneyboxRepository moneyboxRepository;
    private final @NotNull Cache<Player, Long> balanceCache;

    public CachingMoneyboxApi(@NotNull Plugin plugin, @NotNull MoneyboxRepository moneyboxRepository) {
        this.moneyboxRepository = moneyboxRepository;
        this.balanceCache = CacheBuilder.newBuilder().concurrencyLevel(4).build();

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(@NotNull PlayerJoinEvent event) {
                @NotNull Player player = event.getPlayer();
                moneyboxRepository.loadBalance(player.getName())
                                  .subscribe(balance -> balanceCache.put(player, balance));
            }

            @EventHandler
            public void onQuit(@NotNull PlayerQuitEvent event) {
                @NotNull Player player = event.getPlayer();
                final long balance = getCachedBalance(player);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> balanceCache.invalidate(player), 4L);
            }
        }, plugin);
    }

    @Override
    public long getCachedBalance(@NotNull Player player) {
        Preconditions.checkArgument(player.isOnline(), "A player " + player.getName() + " is not online!");

        return Optional.ofNullable(balanceCache.getIfPresent(player))
                       .orElseThrow(() -> new IllegalStateException("A balance is not cached for player " +
                                                                    player.getName()));
    }

    @Override
    public @NotNull Mono<Long> getOfflineBalanceAsync(String username) {
        return moneyboxRepository.loadBalance(username);
    }

    @Override
    public @NotNull Mono<Long> getBalanceAsync(String username) {
        return parseOnlinePlayer(username).map(player -> Mono.just(getCachedBalance(player)))
                                          .orElseGet(() -> getOfflineBalanceAsync(username));
    }

    @Override
    public @NotNull Mono<Void> setBalanceAsync(@NotNull Player player, long amount) {
        Preconditions.checkArgument(player.isOnline(), "Player " + player.getName() + " is offline");

        balanceCache.put(player, amount);
        return moneyboxRepository.saveBalance(player.getName(), amount);
    }

    @Override
    public @NotNull Mono<Void> setOfflineBalanceAsync(String username, long amount) {
        parseOnlinePlayer(username).ifPresent(player -> balanceCache.put(player, amount));
        return moneyboxRepository.saveBalance(username, amount);
    }

    @Override
    public @NotNull Mono<Void> setBalanceAsync(String username, long amount) {
        return parseOnlinePlayer(username).map(player -> setBalanceAsync(player, amount))
                                          .orElseGet(() -> setOfflineBalanceAsync(username, amount));
    }

    @Override
    public boolean hasCachedBalance(@NotNull Player player, long amount) {
        return getCachedBalance(player) >= amount;
    }

    @Override
    public @NotNull Mono<Boolean> hasOfflineBalanceAsync(String username, long amount) {
        return moneyboxRepository.loadBalance(username).map(balance -> balance >= amount);
    }

    @Override
    public @NotNull Mono<Boolean> hasBalanceAsync(String username, long amount) {
        return parseOnlinePlayer(username).map(player -> Mono.just(hasCachedBalance(player, amount)))
                                          .orElseGet(() -> hasOfflineBalanceAsync(username, amount));
    }

    @Override
    public @NotNull Mono<Void> depositBalanceAsync(@NotNull Player player, long amount) {
        Preconditions.checkArgument(player.isOnline(), "Player " + player.getName() + " is offline");

        final long balance = getCachedBalance(player);
        balanceCache.put(player, balance + amount);
        return moneyboxRepository.saveBalance(player.getName(), balance + amount);
    }

    @Override
    public @NotNull Mono<Void> depositOfflineBalanceAsync(String username, long amount) {
        return moneyboxRepository.loadBalance(username)
                                 .flatMap(balance -> setOfflineBalanceAsync(username, balance + amount));
    }

    @Override
    public @NotNull Mono<Void> depositBalanceAsync(String username, long amount) {
        return parseOnlinePlayer(username).map(player -> depositBalanceAsync(player, amount))
                                          .orElseGet(() -> depositOfflineBalanceAsync(username, amount));
    }

    @Override
    public @NotNull Mono<Void> withdrawBalanceAsync(@NotNull Player player, long amount) {
        Preconditions.checkArgument(player.isOnline(), "Player " + player.getName() + " is offline");

        final long balance = getCachedBalance(player);
        balanceCache.put(player, balance - amount);
        return moneyboxRepository.saveBalance(player.getName(), balance - amount);
    }

    @Override
    public @NotNull Mono<Void> withdrawOfflineBalanceAsync(String username, long amount) {
        return moneyboxRepository.loadBalance(username)
                                 .flatMap(balance -> setOfflineBalanceAsync(username, balance - amount));
    }

    @Override
    public @NotNull Mono<Void> withdrawBalanceAsync(String username, long amount) {
        return parseOnlinePlayer(username).map(player -> withdrawBalanceAsync(player, amount))
                                          .orElseGet(() -> withdrawOfflineBalanceAsync(username, amount));
    }

    @Override
    public @NotNull Flux<PlayerAccount> getTopAccounts(
            Direction direction, int offset, int limit, Set<String> excludedPlayers
    ) {
        return moneyboxRepository.loadTopAccounts(direction, Math.max(offset, 0), Math.max(limit, 0));
    }

    @Override
    public @NotNull Flux<PlayerAccount> getTopAccounts(Direction direction, int offset, int limit) {
        return moneyboxRepository.loadTopAccounts(direction, Math.max(offset, 0), Math.max(limit, 0));
    }

    @Override
    public @NotNull Mono<Boolean> transferBalanceAsync(Player player, Player recipient, long amount) {
        return Mono.just(hasCachedBalance(player, amount)).flatMap(result -> {
            if (result) {
                return withdrawBalanceAsync(player, amount).then(depositBalanceAsync(recipient, amount))
                                                           .thenReturn(true);
            }
            return Mono.just(false);
        });
    }

    @Override
    public @NotNull Mono<Boolean> transferBalanceAsync(Player player, String recipient, long amount) {
        return Mono.just(hasCachedBalance(player, amount)).flatMap(result -> {
            if (result) {
                return withdrawBalanceAsync(player, amount).then(depositOfflineBalanceAsync(recipient, amount))
                                                           .thenReturn(true);
            }
            return Mono.just(false);
        });
    }

    @Override
    public @NotNull Mono<Boolean> transferOfflineBalanceAsync(String username, Player recipient, long amount) {
        return hasBalanceAsync(username, amount).flatMap(result -> {
            if (result) {
                return withdrawOfflineBalanceAsync(username, amount).then(depositBalanceAsync(recipient, amount))
                                                                    .thenReturn(true);
            }
            return Mono.just(false);
        });
    }

    @Override
    public @NotNull Mono<Boolean> transferOfflineBalanceAsync(String username, String recipient, long amount) {
        return hasBalanceAsync(username, amount).flatMap(result -> {
            if (result) {
                return withdrawOfflineBalanceAsync(username, amount).then(depositOfflineBalanceAsync(recipient, amount))
                                                                    .thenReturn(true);
            }
            return Mono.just(false);
        });
    }

    @Override
    public @NotNull Mono<Boolean> transferBalanceAsync(String username, String recipient, long amount) {
        final Optional<Player> playerOpt = parseOnlinePlayer(username);
        final Optional<Player> recipientOpt = parseOnlinePlayer(recipient);
        if (Stream.of(playerOpt, recipientOpt).allMatch(Optional::isPresent)) {
            return transferBalanceAsync(playerOpt.get(), recipientOpt.get(), amount);
        }
        if (Stream.of(playerOpt, recipientOpt).noneMatch(Optional::isPresent)) {
            return transferOfflineBalanceAsync(username, recipient, amount);
        }
        if (!playerOpt.isPresent() && recipientOpt.isPresent()) {
            return transferOfflineBalanceAsync(username, recipientOpt.get(), amount);
        }
        if (playerOpt.isPresent() && !recipientOpt.isPresent()) {
            return transferBalanceAsync(playerOpt.get(), recipient, amount);
        }
        // not possible to reach here
        return Mono.just(false);
    }

    private Optional<Player> parseOnlinePlayer(@Nullable String username) {
        if (username == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(Bukkit.getPlayerExact(username));
    }

}