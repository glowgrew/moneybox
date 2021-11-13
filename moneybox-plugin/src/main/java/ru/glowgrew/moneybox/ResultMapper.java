package ru.glowgrew.moneybox;

import io.r2dbc.spi.Result;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.glowgrew.moneybox.api.PlayerAccount;

import java.util.function.Function;

public interface ResultMapper<T> extends Function<Result, T> {

    ResultMapper<Mono<Long>> CREATE_BALANCE = result -> {
        return Mono.from(result.map((row, rowMetadata) -> row.get("amount", Long.class)));
    };

    ResultMapper<Flux<PlayerAccount>> CREATE_ACCOUNT = result -> Flux.from(result.map((row, rowMetadata) -> {
        return new PlayerAccount(row.get("username", String.class), row.get("amount", Long.class));
    }));

    ResultMapper<Mono<Void>> CHECK_ONE_ROW_UPDATED = result -> {
        final Mono<Void> errorMono = Mono.error(new RuntimeException("Player not found"));
        return Mono.from(result.getRowsUpdated()).flatMap(rows -> rows != 1 ? errorMono : Mono.empty());
    };
}
