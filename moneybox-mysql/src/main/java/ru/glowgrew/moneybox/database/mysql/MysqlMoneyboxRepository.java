package ru.glowgrew.moneybox.database.mysql;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Statement;
import org.intellij.lang.annotations.Language;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;
import ru.glowgrew.moneybox.api.MoneyboxApi;
import ru.glowgrew.moneybox.api.PlayerAccount;
import ru.glowgrew.moneybox.configuration.MoneyboxConfiguration;
import ru.glowgrew.moneybox.database.MoneyboxRepository;
import ru.glowgrew.moneybox.database.ResultMapper;

import java.time.Duration;
import java.util.logging.Level;

@SuppressWarnings("CallingSubscribeInNonBlockingScope") // that's ok i think?
public class MysqlMoneyboxRepository implements MoneyboxRepository {

    @Language("MySQL") private static final String CREATE_TABLE_QUERY =
            "create table if not exists economy (username varchar(16) not null primary key, amount bigint not null default ?)\n";

    @Language("MySQL") private static final String LOAD_BALANCE_QUERY =
            "select amount from economy where username = ?\n";

    @Language("MySQL") private static final String UPDATE_BALANCE_QUERY =
            "insert into economy (username, amount) values (?, ?) on duplicate key update amount = ?\n";

    @Language("MySQL") private static final String FETCH_TOP_ENTRIES_DESC_QUERY =
            "select * from economy order by amount desc limit ? offset ?\n";

    @Language("MySQL") private static final String FETCH_TOP_ENTRIES_ASC_QUERY =
            "select * from economy order by amount limit ? offset ?\n";

    private final ConnectionPool connectionPool;
    private final MoneyboxConfiguration configuration;

    public MysqlMoneyboxRepository(ConnectionPool connectionPool, MoneyboxConfiguration configuration) {
        this.connectionPool = connectionPool;
        this.configuration = configuration;

        connectionPool.create().flatMap(connection -> {
            final Statement statement = connection.createStatement(CREATE_TABLE_QUERY);
            statement.bind(0, configuration.getStartingBalanceAmount());
            return Mono.from(statement.execute())
                       .doOnTerminate(() -> Mono.from(connection.close())
                                                .publishOn(Schedulers.boundedElastic())
                                                .subscribe());
        }).subscribe();
    }

    @Override
    public Mono<Long> loadBalance(String username) {
        return connectionPool.create().flatMap(connection -> {
            final Statement statement = connection.createStatement(LOAD_BALANCE_QUERY);
            statement.bind(0, username);
            return Mono.from(statement.execute())
                       .checkpoint("query-run")
                       .flatMap(ResultMapper.CREATE_BALANCE)
                       .defaultIfEmpty(configuration.getStartingBalanceAmount())
                       .checkpoint("result-map")
                       .timeout(Duration.ofSeconds(3L))
                       .doOnError(Throwable::printStackTrace)
                       .doOnTerminate(() -> Mono.from(connection.close())
                                                .publishOn(Schedulers.boundedElastic())
                                                .subscribe())
                       .log("load", Level.INFO, SignalType.ON_ERROR);
        });
    }

    @Override
    public Mono<Void> saveBalance(String username, long amount) {
        return connectionPool.create().flatMap(connection -> {
            final Statement statement = connection.createStatement(UPDATE_BALANCE_QUERY);
            statement.bind(0, username);
            statement.bind(1, amount);
            return Mono.from(statement.execute())
                       .checkpoint("query-run")
                       .flatMap(ResultMapper.CHECK_ONE_ROW_UPDATED)
                       .checkpoint("result-map")
                       .timeout(Duration.ofSeconds(1))
                       .doOnError(Throwable::printStackTrace)
                       .doOnTerminate(() -> Mono.from(connection.close())
                                                .publishOn(Schedulers.boundedElastic())
                                                .subscribe())
                       .log("save", Level.INFO, SignalType.ON_ERROR);
        });
    }

    @Override
    public Flux<PlayerAccount> loadTopAccounts(
            MoneyboxApi.Direction direction, int offset, int limit
    ) {
        return connectionPool.create().flux().flatMap(connection -> {
            String topQuery = resolveTopQuery(direction);
            final Statement statement = connection.createStatement(topQuery);
            statement.bind(0, limit);
            statement.bind(1, offset);
            return Flux.from(statement.execute())
                       .checkpoint("query-run")
                       .flatMap(ResultMapper.CREATE_ACCOUNT)
                       .checkpoint("result-map")
                       .timeout(Duration.ofSeconds(3))
                       .doOnError(Throwable::printStackTrace)
                       .doOnTerminate(() -> Mono.from(connection.close())
                                                .publishOn(Schedulers.boundedElastic())
                                                .subscribe())
                       .log("top", Level.INFO, SignalType.ON_ERROR);
        });
    }

    private String resolveTopQuery(MoneyboxApi.Direction direction) {
        return direction == MoneyboxApi.Direction.DESCENDING ?
               FETCH_TOP_ENTRIES_DESC_QUERY :
               FETCH_TOP_ENTRIES_ASC_QUERY;
    }
}
