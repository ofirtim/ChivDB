# 🧩 ChivDB — The Multipurpose Database Abstraction Layer

**ChivDB** (short for *Chivalrous Database*) is a **multi-engine database unification layer** for Java.  
It provides a **single, type-safe API** across both **relational** and **document** databases, empowering developers to write expressive, vendor-neutral data access logic.

> Think of ChivDB as a Swiss-army knife for database integration — one consistent model, multiple back-ends, no dialect headaches.

### ⚙️ Installation (Gradle + Kotlin DSL)
Currently, ChivDB is still in experimental phases.
as of now, the only way to actually have ChivDB, is by compiling it in your own IDE/IDEA and work with its flow.
We have no estimation on when this will be officially published.

---

## ✨ Key Features

- **Unified API** — Query SQL or NoSQL engines with one fluent syntax.
- **Type Safety** — Full generic typing from field definitions to result mapping.
- **Automatic Objectification** — Convert rows or documents directly into POJOs or records.
- **Multi-Engine Architecture** — Plug in jOOQ (Postgres, MySQL, SQLite), MongoDB, RethinkDB, and more.
- **Clean Encapsulation** — No driver leakage; engine specifics are completely hidden.
- **Declarative Conditions** — Build expressive, composable predicates (`and`, `or`, `in`, `regex`, `arrayContains`, etc.).
- **ServiceLoader Bootstrapping** — Engines self-register through the JVM’s SPI mechanism.

---

## 🧠 Concept Overview

ChivDB abstracts the notion of *data access* rather than *query dialects*.

Every engine implements two canonical interfaces:

- `RelationalApi` — for table-like operations (`SELECT`, `INSERT`, `UPDATE`, `DELETE`).
- `DocumentApi` — for JSON/BSON document stores (e.g. MongoDB).

These are opened through a single `DbHandle` that is produced by the static entry point `UnifiedDB.init(cfg)`.

```java
var cfg = DbConfig.builder()
    .kind(EngineKind.POSTGRES)
    .uri("jdbc:postgresql://localhost:5432/game")
    .username("admin")
    .password("secret")
    .build();

try (var db = UnifiedDB.init(cfg)) {
    db.relational().ifPresent(r -> {
        var id = UUID.fromString("a6bbd99a-0a8a-48bb-84cb-8df1b9a76420");
        var player = r.fetch("players")
            .where(Conditions.eq(Fields.uuid("id"), id))
            .oneAs(Player.class);
        System.out.println(player);
    });
}
```

## 🏗️ Architecture
core/
 ├── ChivField --------------------// Typed field abstraction
 ├── ChivCondition --------------// AST root (Atom, Junction, Not)
 ├── Conditions ------------------// Static builders (eq, gt, regex, in, etc.)
 ├── TypeCoercion --------------// Scalar conversion across drivers
 ├── PojoMapper ----------------// Reflection & record-based object mapping
 ├── DbConfig, DbHandle ------// Connection metadata & unified handle
 ├── RelationalApi, DocumentApi
 └── UnifiedDB (bootstraps engines via ServiceLoader)

engines/
 ├── jooq/
 │    ├── JooqRelationalApi
 │    ├── JooqSelectQuery
 │    ├── JooqConditionEmitter
 │    └── support/ (JooqSets, JooqStrings, JooqTypes, etc.)
 ├── mongo/ (planned)
 ├── rethink/ (planned)
 └── sqlite/ (planned)

## 🧩 Fluent Conditions
ChivDB’s condition model is composable, engine-agnostic, and convertible into SQL or BSON as needed.

```java
import static com.millenialsoftwares.chivdb.core.Conditions.*;
import static com.millenialsoftwares.chivdb.core.Fields.*;

var c = and(
    or(eq(string("username"), "Steve"), like(string("username"), "%Alex%")),
    gte(int32("level"), 10),
    arrayContains(string("roles"), "builder")
);
```
This can be rendered to:

-   `WHERE (username = 'Steve' OR username LIKE '%Alex%') AND level >= 10 AND roles @> ARRAY['builder']`  
    _(Postgres)_
    
-   or a corresponding MongoDB `$and`/$`or`/$`regex` pipeline expression.


## 🧬 Type Coercion & Objectification
ChivDB ships with a reflection-based `PojoMapper` that can map:

-   **Plain records**
    
-   **POJOs with setters**
    
-   **Single-argument constructors** (for scalar wrappers)
    

It integrates deeply with the shared `TypeCoercion` layer to support conversions between Java primitives, numerics, UUIDs, dates, and enums.
```java
var mapper = PojoMapper.builder()
    .ignoreUnknown(true)
    .failOnMissing(false)
    .build();

Player p = mapper.map(Map.of("id", id, "username", "Steve"), Player.class);
```

## 🪶 About the Name

> **Chiv** — derived from the Hebrew word _אולר_ (_Ular_), meaning “pocket knife.”  
> The project embodies that spirit: a compact, versatile tool for every data workload.
