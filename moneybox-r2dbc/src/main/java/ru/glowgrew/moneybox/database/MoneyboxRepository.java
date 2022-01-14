package ru.glowgrew.moneybox.database;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.glowgrew.moneybox.api.MoneyboxApi;
import ru.glowgrew.moneybox.api.PlayerAccount;

public interface MoneyboxRepository {

    Mono<Long> loadBalance(String username);

    Mono<Void> saveBalance(String username, long amount);

    Flux<PlayerAccount> loadTopAccounts(MoneyboxApi.Direction direction, int offset, int limit);
}