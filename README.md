# Moneybox

An economy plugin for different Minecraft server environments powered by Reactor Core with a _wide-range_ of supported
platforms and versions.

### Techologies used

- [R2DBC](https://github.com/r2dbc/r2dbc-pool) (database connectivity)
- [adventure](https://github.com/KyoriPowered/adventure) (UI library)
- [cloud](https://github.com/Incendo/cloud) (in-game command framework)
- [caffeine](https://github.com/ben-manes/caffeine) (high-performance caching library)

### Available environments

- Bukkit for Java Edition:
  - [Crucible](https://github.com/CrucibleMC/Crucible/releases) (for 1.7.10)
  - [CatServer](https://github.com/Luohuayu/CatServer) (for 1.12.2)
  - [Arclight](https://github.com/IzzelAliz/Arclight/releases) (for 1.16.5 and above)
  - _or any fork that runs on >1.7.10_

- Nukkit for Bedrock:
  - [PowerNukkit](https://github.com/PowerNukkit/PowerNukkit) (for 1.18 and above)
  - _or any fork that runs on >1.0.0_

### Available connection types

- `POSTGRESQL` - Discover connection through the PostgreSQL
- `MYSQL` - Discover connection through the MySQL

### Environment variables

| Name                           |      Description       | Default value |
|:-------------------------------|:----------------------:|--------------:|
| `MONEYBOX_STARTING_BALANCE`    | Default balance amount |           `0` |
| `MONEYBOX_DATASOURCE`          |    Data source type    |  `POSTGRESQL` |
| `MONEYBOX_DATASOURCE_HOST`     |    Data source host    |   `127.0.0.1` |
| `MONEYBOX_DATASOURCE_PORT`     |    Data source port    |        `5432` |
| `MONEYBOX_DATASOURCE_USER`     |  Data source username  |         `app` |
| `MONEYBOX_DATASOURCE_PASSWORD` |  Data source password  |       `gbplf` |
| `MONEYBOX_DATASOURCE_DB`       |  Data source database  |         `app` |

### Examples

- You can retrieve an instance of Moneybox API via platform's Service Manager API
  - Bukkit:
      ```java
      MoneyboxApi api = getServer().getServicesManager().getRegistration(MoneyboxApi.class).getProvider();
      ``` 
  - Nukkit:
    ```java
    MoneyboxApi api = getServer().getServiceManager().getProvider(MoneyboxApi.class).getProvider();
    ``` 

- Deposit player an amount then send a message asynchronously.

   ```java
   api.depositBalanceAsync(username, amount).doOnSuccess($ -> {
           sender.sendMessage("You have deposit %d for player %s".formatted(amount, username));
   }).subscribe();
   ```

- Retrieve a balance amount of a player then send it asynchronously.
   ```java
   api.getBalanceAsync(username)
           .subscribe(balance -> sender.sendMessage("Balance amount of %s is %d".formatted(username, balance));
   ```

- Retrieve a balance amount of an online player then send it.
   ```java
   long balance = api.getCachedBalance(MoneyboxPlayer.of(username));
   sender.sendMessage("Balance amount of %s is %d".formatted(username, balance));
   ```

### Supported by MCSTUDIO