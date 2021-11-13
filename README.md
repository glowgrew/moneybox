# Moneybox
An economy plugin for different Minecraft server environments powered by Reactor Core

### Techologies used

- [R2DBC](https://github.com/r2dbc/r2dbc-pool) (database connectivity)
- [adventure](https://github.com/KyoriPowered/adventure) (UI library)
- [cloud](https://github.com/Incendo/cloud) (in-game command framework)

### Available environments

- CraftBukkit (any version)
- [Crucible](https://github.com/CrucibleMC/Crucible/releases) (for 1.7.10)
- [CatServer](https://github.com/Luohuayu/CatServer) (for 1.12.2)
- [Arclight](https://github.com/IzzelAliz/Arclight/releases) (for 1.16.5 and above)

### Available connection types

- `POSTGRESQL` - Discover connection through the PostgreSQL

### Environment variables

| Name                                           | Description                      | Default value          |  
| ---------------------------------------------- | -------------------------------  | ---------------------- |  
| MONEYBOX__STARTING_BALANCE_AMOUNT              | Set a default balance amount     | 0                      |
| MONEYBOX_DATASOURCE_TYPE                       | Data source type                 | POSTGRES               |
| MONEYBOX_<MONEYBOX_DATASOURCE_TYPE>_HOST       | Data source host                 | 127.0.0.1              |  
| MONEYBOX_<MONEYBOX_DATASOURCE_TYPE>_PORT       | Data source port                 | 5432                   |  
| MONEYBOX_<MONEYBOX_DATASOURCE_TYPE>_USER       | Data source username             | root                   |  
| MONEYBOX_<MONEYBOX_DATASOURCE_TYPE>_PASSWORD   | Data source password             | root                   |  
| MONEYBOX_<MONEYBOX_DATASOURCE_TYPE>_DB         | Data source database             | database               |

### Supported by MCSTUDIO _卐_