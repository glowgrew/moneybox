package ru.glowgrew.moneybox.api;

import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Represents an API that performs operations on players' economy accounts.
 */
@SuppressWarnings("unused")
public interface MoneyboxApi {

    // -=-=-=-=-=-=--=-=-=-=-=-=- RETRIEVE -=-=-=-=-=-=--=-=-=-=-=-=- //

    /**
     * Retrieve a player's cached balance amount.
     *
     * @param player the player
     * @return the player's balance amount
     * @throws IllegalStateException if the player's balance amount is not cached
     */
    long getCachedBalance(MoneyboxPlayer player);

    /**
     * Retrieve a player's offline balance amount asynchronously.
     *
     * @param username the username
     * @return a Mono with the player's balance amount
     */
    @NotNull Mono<Long> getOfflineBalanceAsync(String username);

    /**
     * Retrieve a player's offline balance amount asynchronously or a cached one, if available.
     *
     * @param username the username
     * @return a Mono with the player's balance amount
     * @apiNote use this method if you unsure which method to choose from the above, as it
     * combines them by checking whether the player is online or offline.
     */
    @NotNull Mono<Long> getBalanceAsync(String username);

    // -=-=-=-=-=-=--=-=-=-=-=-=- SET -=-=-=-=-=-=--=-=-=-=-=-=- //

    /**
     * Set a player's offline balance amount to the given amount and updating the cached one.
     *
     * @param player the player
     * @param amount the amount to set the player's balance to
     * @return an empty Mono
     * @throws IllegalStateException if the player is not online
     */
    @NotNull Mono<Void> setBalanceAsync(MoneyboxPlayer player, long amount);

    /**
     * Set a player's offline balance amount to the given amount.
     *
     * @param username the username
     * @param amount   the amount to set the player's balance to
     * @return an empty Mono
     */
    @NotNull Mono<Void> setOfflineBalanceAsync(String username, long amount);

    /**
     * Set a player's offline balance amount to the given amount and updating the cached one, if available.
     *
     * @param username the username
     * @param amount   the amount to set the player's balance to
     * @return an empty Mono
     * @apiNote use this method if you unsure which method to choose from the above, as it
     * combines them by checking whether the player is online or offline.
     */
    @NotNull Mono<Void> setBalanceAsync(String username, long amount);

    // -=-=-=-=-=-=--=-=-=-=-=-=- HAS -=-=-=-=-=-=--=-=-=-=-=-=- //

    /**
     * Perform a check on the presence of a given amount on the player's cached balance.
     *
     * @param player the player
     * @param amount the amount to check if the player's balance has
     * @return the result of the check
     * @throws IllegalStateException if the player is not online
     */
    boolean hasCachedBalance(MoneyboxPlayer player, long amount);

    /**
     * Perform a check on the presence of a given amount on the player's offline balance.
     *
     * @param username the username
     * @param amount   the amount to check if the player's balance has
     * @return a Mono with the result of the check
     */
    @NotNull Mono<Boolean> hasOfflineBalanceAsync(String username, long amount);

    /**
     * Perform a check on the presence of a given amount on the player's offline balance
     * or in the cache, if available.
     *
     * @param username the username
     * @param amount   the amount to check if the player's balance has
     * @return a Mono with the result of the check
     * @apiNote use this method if you unsure which method to choose from the above, as it
     * combines them by checking whether the player is online or offline.
     */
    @NotNull Mono<Boolean> hasBalanceAsync(String username, long amount);

    // -=-=-=-=-=-=--=-=-=-=-=-=- DEPOSIT -=-=-=-=-=-=--=-=-=-=-=-=- //

    /**
     * Deposit a given amount to the player's offline balance and updating the cached one.
     *
     * @param player the username
     * @param amount the amount to deposit
     * @return an empty Mono
     * @throws IllegalStateException if the player is not online
     */
    @NotNull Mono<Void> depositBalanceAsync(MoneyboxPlayer player, long amount);

    /**
     * Deposit a given amount to the player's offline balance.
     *
     * @param username the username
     * @param amount   the amount to deposit
     * @return an empty Mono
     */
    @NotNull Mono<Void> depositOfflineBalanceAsync(String username, long amount);

    /**
     * Deposit a given amount to the player's offline balance and updating the cached one, if available.
     *
     * @param username the username
     * @param amount   the amount to deposit
     * @return an empty Mono
     * @apiNote use this method if you unsure which method to choose from the above, as it
     * combines them by checking whether the player is online or offline.
     */
    @NotNull Mono<Void> depositBalanceAsync(String username, long amount);

    // -=-=-=-=-=-=--=-=-=-=-=-=- WITHDRAW -=-=-=-=-=-=--=-=-=-=-=-=- //

    /**
     * Withdraw a given amount to the player's offline balance and updating the cached one.
     *
     * @param player the username
     * @param amount the amount to withdraw
     * @return an empty Mono
     * @throws IllegalStateException if the player is not online
     */
    @NotNull Mono<Void> withdrawBalanceAsync(MoneyboxPlayer player, long amount);

    /**
     * Withdraw a given amount to the player's offline balance.
     *
     * @param username the username
     * @param amount   the amount to withdraw
     * @return an empty Mono
     */
    @NotNull Mono<Void> withdrawOfflineBalanceAsync(String username, long amount);

    /**
     * Withdraw a given amount from the player's offline balance and updating the cached one, if available.
     *
     * @param username the username
     * @param amount   the amount to withdraw
     * @return an empty Mono
     * @apiNote use this method if you unsure which method to choose from the above, as it
     * combines them by checking whether the player is online or offline.
     */
    @NotNull Mono<Void> withdrawBalanceAsync(String username, long amount);

    // -=-=-=-=-=-=--=-=-=-=-=-=- TOP -=-=-=-=-=-=--=-=-=-=-=-=- //


    /**
     * Retrieve an ordered player's offline balance leaderboard with a given offset and limit.
     *
     * @param direction the direction of the leaderboard
     * @param offset    the offset of the leaderboard
     * @param limit     the maximum amount of included entries
     * @return an ordered Flux with retrieved entries
     */
    @NotNull Flux<PlayerAccount> getTopAccounts(Direction direction, int offset, int limit);

    // -=-=-=-=-=-=--=-=-=-=-=-=- TRANSFER -=-=-=-=-=-=--=-=-=-=-=-=- //

    /**
     * Transfer a given balance amount between two accounts updating the cached ones.
     *
     * @param player    the player whose balance amount is being transferred
     * @param recipient the player who will receive the transferred balance amount
     * @param amount    the amount to transfer
     * @return a Mono with the result of the transaction; {@code true} if the transfer
     * was successful, {@code false} otherwise
     * @throws IllegalStateException if any of the accounts is not online
     */
    @NotNull Mono<Boolean> transferBalanceAsync(MoneyboxPlayer player, MoneyboxPlayer recipient, long amount);

    /**
     * Transfer a given balance amount between two accounts updating the cached ones, if available.
     *
     * @param player    the player whose balance amount is being transferred
     * @param recipient the player who will receive the transferred balance amount
     * @param amount    the amount to transfer
     * @return a Mono with the result of the transaction; {@code true} if the transfer
     * was successful, {@code false} otherwise
     * @throws IllegalStateException if the player is not online
     */
    @NotNull Mono<Boolean> transferBalanceAsync(MoneyboxPlayer player, String recipient, long amount);

    /**
     * Transfer a given balance amount between two accounts updating the cached ones, if available.
     *
     * @param username  the player whose balance amount is being transferred
     * @param recipient the player who will receive the transferred balance amount
     * @param amount    the amount to transfer
     * @return a Mono with the result of the transaction; {@code true} if the transfer
     * was successful, {@code false} otherwise
     * @throws IllegalStateException if the recipient is not online
     */
    @NotNull Mono<Boolean> transferOfflineBalanceAsync(String username, MoneyboxPlayer recipient, long amount);

    /**
     * Transfer a given balance amount between two accounts updating the cached ones, if available.
     *
     * @param username  the player whose balance amount is being transferred
     * @param recipient the player who will receive the transferred balance amount
     * @param amount    the amount to transfer
     * @return a Mono with the result of the transaction; {@code true} if the transfer
     * was successful, {@code false} otherwise
     */
    @NotNull Mono<Boolean> transferOfflineBalanceAsync(String username, String recipient, long amount);

    /**
     * Transfer a given balance amount between two accounts updating the cached ones, if available.
     *
     * @param username  the player whose balance amount is being transferred
     * @param recipient the player who will receive the transferred balance amount
     * @param amount    the amount to transfer
     * @return a Mono with the result of the transaction; {@code true} if the transfer
     * was successful, {@code false} otherwise
     * @apiNote use this method if you unsure which method to choose from the above, as it
     * combines them by checking whether the player or recipient is online or offline.
     */
    @NotNull Mono<Boolean> transferBalanceAsync(String username, String recipient, long amount);

    /**
     * A leaderboard sorting direction.
     */
    enum Direction {
        DESCENDING,
        ASCENDING
    }
}
