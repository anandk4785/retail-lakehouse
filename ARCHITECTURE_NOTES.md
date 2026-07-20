<h1 align="center">Architecture Notes</h1>

---

## Document Purpose

This document records the important architectural and design decisions made during the development of the **Retail Lakehouse** project.

For each decision we record:

* Context
* Available options
* Decision taken
* Reasoning
* Consequences

This helps:

* Understand why choices were made
* Keep the project consistent
* Prepare for interviews
* Mimic real-world Architecture Decision Records (ADR)

---

# ADR-001 : Technology Stack

## Status

Accepted

## Context

The goal of this project is to simulate a production-style Big Data Engineering project that demonstrates:

* ETL pipelines
* Data lakehouse concepts
* Batch processing
* Data warehousing
* Workflow orchestration
* Agile development practices

## Decision

Use:

| Component          | Technology     |
| ------------------ | -------------- |
| Language           | Java 17        |
| Build Tool         | Gradle         |
| Processing Engine  | Spark 3.5.x    |
| Storage Format     | Parquet        |
| SQL Engine         | Spark SQL      |
| Metadata Store     | Hive Metastore |
| Workflow Scheduler | Airflow        |
| Version Control    | Git + GitHub   |
| OS                 | Ubuntu 24.04   |

## Reason

* Java is widely used in Big Data Engineering.
* Gradle is modern and flexible.
* Spark is the industry standard for distributed data processing.
* Parquet provides efficient columnar storage.
* Hive Metastore enables table management.
* Airflow is the most popular orchestration tool.

## Consequences

* Strong alignment with industry practices.
* Project becomes resume-worthy.
* More initial setup effort compared to lightweight stacks.

---

# ADR-002 : Repository Structure

## Status

Accepted

## Context

The project requires separation of code, datasets, SQL scripts and generated outputs.

## Decision

Repository structure:

```text
retail-lakehouse

├── src
│   ├── main
│   │   ├── java
│   │   └── resources
│   │
│   └── test
│       ├── java
│       └── resources
├── data
│   └── dev
│       ├── bronze
│       ├── silver
│       ├── gold
│       └── warehouse
|
├── sql
|
├── logs
|
├── README.md
|
├── PROJECT_SUMMARY.md
|
└── ARCHITECTURE_NOTES.md
```

## Reason

* Separation of concerns.
* Easier navigation.
* Mimics production repositories.
* Bronze/Silver/Gold directories reflect actual lakehouse layer structure.

## Consequences

* Cleaner codebase.
* Easier onboarding.
* Consistent data organization.

---

# ADR-003 : Dataset Storage Location

## Status

Accepted

## Context

CSV datasets can be large and may vary between environments.

## Decision

Store datasets outside Git repository.

Location:

```text
~/projects/bigdata/data/olist_ecom_dataset
```

Datasets:

```text
olist_customers_dataset.csv

olist_orders_dataset.csv

olist_products_dataset.csv

olist_order_payments_dataset.csv
```

## Reason

* Repository remains lightweight.
* Avoid committing large files.
* Eases migration between environments.

## Consequences

* External paths must be configured.
* Additional setup step required.

---

# ADR-004 : Data Lakehouse Layers

## Status

Accepted

## Context

Raw datasets should be separated from transformed and analytical data.

## Decision

Use layered Bronze / Silver / Gold architecture:

```text
CSV Files

↓

Bronze Layer

Raw Parquet
(no transformations)

↓

Silver Layer

Cleaned Data
Standardized Types
Validated Records

↓

Gold Layer

Business Metrics
Dimension Tables
Fact Tables

↓

Spark SQL

↓

Hive Metastore

↓

Analytics Reports

↓

Airflow DAG
```

## Reason

* Industry standard medallion architecture.
* Easier debugging at each layer.
* Supports incremental processing.
* Clear separation of ingestion, transformation, and analytics.

## Consequences

* Additional storage requirements.
* More transformation steps.

---

# ADR-005 : Build Tool

## Status

Accepted

## Context

The project requires dependency management and build automation.

## Decision

Use Gradle.

Version:

```text
Gradle 8.14
```

## Reason

* Faster incremental builds.
* Version catalogs.
* Excellent Java ecosystem support.
* Modern DSL.

## Alternatives Considered

### Maven

Pros

* Extremely popular.
* Mature ecosystem.

Cons

* More verbose.
* Less flexible.

## Consequences

* Easier dependency management.
* Better scalability as project grows.

---

# ADR-006 : Spring Boot

## Status

Rejected

## Context

Spring Boot is often used in Java projects.

## Decision

Do not use Spring Boot initially.

## Reason

The project focuses on:

* Spark ETL
* Spark SQL
* Parquet
* Hive
* Airflow

Spring Boot adds unnecessary complexity at this stage.

## Consequences

* Simpler project.
* Faster learning curve.
* Easier focus on Big Data concepts.

---

# ADR-007 : Centralized SparkSession Creation

## Status: Accepted ✅

## Decision

All Spark jobs will obtain SparkSession through:

``` Java
SparkSession spark =
SparkSessionFactory.getSparkSession();
```

## Rejected Alternatives
```
new SparkSessionFactory()
.createSparkSession();
```

## Reason:

- Unnecessary object creation
- SparkSession behaves like a singleton
- More verbose

## Benefits
- Centralized Spark configurations
- Single place to enable Hive
- Easy to add configs later
- Consistent Spark initialization
- Similar to enterprise Spark projects

---

# ADR-008 : Centralized Warehouse Storage Inside Environment Directory

## Status: Accepted ✅

## Decision
Instead of
```bash
spark-warehouse/
```
at project root, we will use:
```
data

└── dev

    ├── bronze

    ├── silver

    ├── gold

    └── warehouse
```

## Reason

- Keeps all generated data together
- Easy cleanup
- Mimics dev/qa/prod environments
- Similar to lakehouse storage hierarchy

---

# ADR-009 : Separate Dataset Root and Dataset Filenames

## Status: Accepted ✅

## Decision
Instead of
```properties
customers.csv=/home/anand/projects/bigdata/data/olist_ecom_dataset/olist_customers_dataset.csv
```
we will use:
```properties
dataset.root=/home/anand/projects/bigdata/data/olist_ecom_dataset

customers.csv=olist_customers_dataset.csv

orders.csv=olist_orders_dataset.csv

products.csv=olist_products_dataset.csv

payments.csv=olist_order_payments_dataset.csv
```

## Benefits

- Cleaner configuration
- Easier environment switch
- Less duplication
- Easy to move datasets

---

# ADR-010 : Java Version Compatibility

## Status: Accepted ✅

## Decision:

**Java 17** will be used for the project.

Apache Spark 3.5.x requires JVM
`--add-opens` arguments for **Java 17**.

These arguments are configured
centrally in build.gradle and
applied to:

- application runs

- unit tests

## Reason:

- Modern Java LTS

- Industry standard

- Long term compatibility

---

# ADR-011 : Externalized Configuration

## Status: Accepted ✅

## Decision

All environment-specific and runtime configuration is stored in:

```text
src/main/resources/application.properties
```

This covers:

```properties
# Application
app.name=RetailLakehouse

# Environment
environment=dev

# Spark
spark.master=local[*]
spark.sql.shuffle.partitions=4
spark.sql.warehouse.dir=./data/dev/warehouse

# Storage
data.root=./data/dev
bronze.path=./data/dev/bronze
silver.path=./data/dev/silver
gold.path=./data/dev/gold

# Dataset Source
dataset.root=/home/anand/projects/bigdata/data/olist_ecom_dataset
customers.csv=olist_customers_dataset.csv
orders.csv=olist_orders_dataset.csv
products.csv=olist_products_dataset.csv
payments.csv=olist_order_payments_dataset.csv
```

Configuration is loaded at startup through `ConfigLoader.get(key)`.

## Reason

- Avoid hardcoded values in Java source
- Easier environment switch (dev / qa / prod)
- Production-style config management
- Single place to change paths or Spark settings

## Consequences

- All jobs remain environment-agnostic
- Path changes require only a properties update
- Supports future environment-specific property files

---

# ADR-012 : Explicit Spark Schemas Instead of inferSchema

## Status: Accepted ✅

## Decision

All CSV readers will define schemas explicitly using dedicated schema classes:

```
CustomerSchema.getSchema();

OrderSchema.getSchema();

ProductSchema.getSchema();

PaymentSchema.getSchema();
```

Instead of:

```
.option("inferSchema", "true")
```

One schema class per entity:

```text
com.anand.retail.schema

├── CustomerSchema

├── OrderSchema

├── ProductSchema

└── PaymentSchema
```

## Reason

- `inferSchema` requires a full CSV scan before reading, which is slow
- Explicit schemas guarantee predictable column types
- Prevents silent type inference errors (e.g. timestamps read as strings)
- Common production practice for large-scale pipelines
- Better data quality from the first layer

## Consequences

- Schema must be kept in sync with the CSV source
- Slightly more upfront code per entity
- Faster job startup

## Addendum (US-013, 2026-07-19): Column-Name Constants

Each Schema class now also exposes its column names as
`public static final String` constants (e.g.
`OrderSchema.ORDER_ID = "order_id"`), which `getSchema()` itself
references when building the `StructType`. Job-wiring code (e.g.
`NullPkDedupValidator`'s required/dedup column arrays in
`CustomerSilverJob`/`ProductSilverJob`/`OrderSilverJob`) uses these
constants instead of raw string literals.

Reason: a misspelled string literal used to fail silently at Spark
runtime (an `AnalysisException` when the job actually ran against real
data); a misspelled constant reference now fails at compile time. This
also guarantees the exact same value is used everywhere a column name is
referenced — the schema definition and every consumer of it share one
source of truth, with no second definition that could drift.

Consequence: `Test` classes for `NullPkDedupValidator` and `SilverService`
were updated to reference these constants too (e.g.
`CustomerSchema.CUSTOMER_ID`), rather than repeating literals — a test
asserting against a stale string would otherwise keep passing even after
a real schema rename, defeating the purpose of the constant.
`*Transformer` classes still reference raw column-name strings
internally; migrating them to the same constants is a reasonable future
follow-up, not required by this change.

---

# ADR-013 : Centralized Bronze Writer

## Status: Accepted ✅

## Decision

All Bronze layer writes are routed through a single class:

```
BronzeWriter.writeTable(df, LakehouseTable.CUSTOMERS)

BronzeWriter.writeTable(df, LakehouseTable.ORDERS)

BronzeWriter.writeTable(df, LakehouseTable.PRODUCTS)

BronzeWriter.writeTable(df, LakehouseTable.PAYMENTS)
```

The destination path is resolved automatically using the `LakehouseTable` enum:

```
Paths.get(bronzePath, table.getDirectoryName())
```

## Reason

- Single place for all Bronze storage logic
- Consistent `SaveMode.Overwrite` behaviour across all entities
- New tables require zero changes to `BronzeWriter`
- Path construction is not duplicated across job classes
- Easy to extend (add partitioning, compression, metadata) in one place

## Consequences

- All Bronze jobs delegate writing to `BronzeWriter`
- Consistent output paths enforced automatically
- Clean separation between job orchestration and storage concerns

---

# ADR-014 : Dependency Injection and Service Layer

## Status: Accepted ✅

## Decision

Shifted from a static Helper pattern (`CustomerJobHelper.loadAndShowCustomers`) to a true Service Layer with Dependency Injection (`OrderService`, `ProductService`).

```java
// Dependency Injection: Service demands the Reader
OrderReader reader = new OrderReader();
OrderService service = new OrderService(reader);
```

## Reason

- Follows standard Java/Spring enterprise patterns natively
- Enforces the Single Responsibility Principle
- Separates orchestration (`Main`), I/O logic (`Reader/Writer`), and business preview/count logic (`Service`)
- Highly testable — the `Reader` can be mocked when unit testing the `Service`

## Consequences

- Slightly more boilerplate in `main` methods to wire dependencies together (Assembly Line pattern)
- Highly modular and testable code

---

# ADR-015 : Serializable Service Classes

## Status: Accepted ✅

## Decision

All Spark Service classes must explicitly implement `java.io.Serializable`.

```java
public class OrderService implements Serializable { ... }

public class ProductService implements Serializable { ... }
```
## Reason

- When Spark executes actions/transformations using class methods, the JVM attempts to serialize the entire enclosing object to send it to executor nodes
- If the class is not `Serializable`, Spark throws a fatal `NotSerializableException` (Serialization Leak)
- Explicit implementation guarantees cluster compatibility from day one

## Consequences

- All instance variables within the Service (e.g. Loggers, Readers) must be either `static`, `transient`, or themselves `Serializable`

---

# ADR-016 : Universal Parquet Readers and Writers

## Status: Accepted ✅

## Decision

Silver/Gold layers will use generic, stateless IO utilities (`BronzeReader`, `SilverWriter`)
that read and write directly to Parquet using the `LakehouseTable` enum.

## Reason

- Parquet files embed their own schema, eliminating the need for entity-specific readers (like `CustomerReader` for CSVs).

- Drastically reduces boilerplate code in transformation layers.

- Forces all tables to comply strictly with the central `LakehouseTable` registry.

---

# ADR-017 : Validator Pattern (Data Quality Separation)

# Status: Accepted ✅

# Decision

Data cleansing (null checks, deduplication) is explicitly separated from data formatting (casting, string manipulation) using a dedicated `Validator` class.

```Plaintext
Raw Data → Validator (drops invalid rows) → Transformer (formats rows)
```

# Reason

- Strict adherence to the Single Responsibility Principle (SRP).

- Enhances observability: Services can count records before and after validation to log exactly how many dirty records were dropped.

- Makes unit tests highly focused.

---

# ADR-018 : Intentional Duplication Across Silver Dimensions (US-010 to US-012), Deferred to US-013

## Status: Accepted ✅

## Context

With US-009 (Customer Silver Dimension) complete, the pattern
`BronzeReader` → `Validator` → `Transformer` → `SilverWriter`, orchestrated
by an entity-specific, DI-based `Service` class, is proven for one entity.
US-010 (Product), US-011 (Order Fact), and US-012 (Payment Fact) each need
the same shape.

Two options were considered:

1. Generalize immediately — introduce `DataValidator` / `DataTransformer`
   interfaces and a single configurable generic Service class before
   writing Product/Order/Payment code, inferring the abstraction from one
   real example (Customer).
2. Duplicate the Customer pattern per entity (`ProductValidator`,
   `ProductTransformer`, `ProductSilverService`, `ProductSilverJob`, etc.),
   accept the resulting near-identical classes, and generalize later once
   multiple real examples exist.

## Decision

Option 2. `ProductValidator`, `ProductTransformer`, `ProductSilverService`,
and `ProductSilverJob` (US-010) intentionally duplicate the structure of
their Customer counterparts, rather than introducing shared interfaces or
a generic service at this point. The same approach will be used for US-011
(Order Fact) and US-012 (Payment Fact).

A dedicated **US-013 : Technical Debt / Refactoring** story is scheduled
once Customer, Product, Order, and Payment Silver pipelines all exist, to
consolidate the now-proven duplication behind `DataValidator` /
`DataTransformer` interfaces and a single generic, DI-driven Silver
service.

## Reason

- Generalizing from a single example (Customer alone) risks guessing at an
  abstraction that doesn't actually fit once a second or third real case
  (Product's non-PK null-fill transform, Order/Payment's fact-table shape)
  is written — premature abstraction is itself a design smell.
- Deliberately allowing duplication to accumulate across three more
  entities, then refactoring against four real, tested examples, produces
  a more defensible generalization and a clear, demonstrable before/after
  story — closer to how real teams manage technical debt against sprint
  deadlines.
- Framing the consolidation as its own user story (US-013) keeps it
  visible and prioritized on the sprint tracker rather than being silently
  deferred indefinitely.

## Consequences

- `ProductValidator` / `OrderValidator` / `PaymentValidator` and their
  `Transformer` counterparts intentionally contained near-identical
  null-PK/dedup logic until US-013 gave the project enough real Silver
  pipelines to generalize from.
- `ProductSilverService` (and its Order/Payment equivalents) intentionally
  duplicated the orchestration shape of `CustomerSilverService` almost line
  for line until US-013.
- US-013 introduces `DataValidator` / `DataTransformer` interfaces,
  `NullPkDedupValidator` for configurable required-column filtering and
  deduplication, and a single generic `SilverService` that accepts
  `BronzeReader`, `DataValidator`, `DataTransformer`, `SilverWriter`, and
  `LakehouseTable` through constructor injection.
- Customer, Product, and Order Silver jobs now wire `NullPkDedupValidator`
  directly with their table-specific required and dedup columns. Payment
  Silver keeps `PaymentValidator`, because its composite key and business
  filters (`payment_type != "not_defined"`, `payment_value >= 0`) are
  domain-specific and should not be forced into the generic null/dedup
  validator.
- Entity-specific Transformer classes remain by design. They implement
  `DataTransformer`, but their transformation logic differs meaningfully
  by table type and is not a generalization candidate (see ADR-017,
  ADR-019, and ADR-020).
- The old entity-specific Silver service classes
  (`CustomerSilverService`, `ProductSilverService`, `OrderSilverService`,
  `PaymentSilverService`) and the generic-replaced Customer/Product/Order
  validator classes (`CustomerValidator`, `ProductValidator`,
  `OrderValidator`, and their tests) have been deleted as the closing
  step of US-013. `PaymentValidator` and `PaymentValidatorTest` remain,
  per the decision above.
- US-013 is reserved for this Technical Debt / Refactoring story. Since
  the Kanban board only has issues created through US-012, the previously
  planned US-013 (Hive Metastore) and later Sprint 4/5 stories shift up by
  one: Hive Metastore → US-014, Sales Analytics → US-015, Top Customers
  Report → US-016, Airflow DAG → US-017, Daily ETL Pipeline → US-018,
  Monitoring → US-019. See `PROJECT_SUMMARY.md` Sprint 3–5 for the
  current numbering.

---

# ADR-019 : Order Silver Fact — Transformer Scope for Facts vs. Dimensions

## Status: Accepted ✅

## Context

US-011 (Order Silver Fact) needed the same `BronzeReader` → `Validator` →
`Transformer` → `SilverWriter` shape as US-009 (Customer) and US-010
(Product), but Order is a **Fact** table, not a **Dimension**. Facts
typically carry measures and derived metrics; dimensions carry descriptive
attributes. Two related design questions came up while implementing it:

1. How much should `OrderTransformer` do? `CustomerTransformer` and
   `ProductTransformer` are narrowly scoped to standardization (casing,
   null-fill on non-PK fields). Order's Silver value, however, is largely
   about derived business metrics: purchase date parts, delivery duration,
   delay versus estimate, approval and carrier-transit time, and an
   `is_delivered` flag.
2. How much should `OrderValidator` null-check? Every one of those derived
   metrics depends on `order_purchase_timestamp` being present, in addition
   to the usual primary key.

## Decision

- `OrderTransformer`'s scope is intentionally wider than `CustomerTransformer`/
  `ProductTransformer`: it computes derived date-dimension and delivery/
  processing-duration columns (via `datediff`, `year`, `month`, `dayofmonth`),
  in addition to renaming and reshaping the final column set. This does not
  violate ADR-017's Validator/Transformer split — validation (null/dedup)
  still lives solely in `OrderValidator` — it simply means a Fact table's
  Transformer naturally does more than a Dimension table's.
- `OrderValidator` null-checks `order_id`, `customer_id`, and
  `order_purchase_timestamp` (not just the primary key), because every
  derived metric in `OrderTransformer` depends on `order_purchase_timestamp`
  being non-null. It deliberately does **not** null-check
  `order_approved_at`, `order_delivered_carrier_date`,
  `order_delivered_customer_date`, or `order_estimated_delivery_date` —
  those are legitimately null for orders still in flight (e.g. `shipped`
  but not yet `delivered`), and forcing them non-null would incorrectly
  drop valid, in-progress orders.

## Reason

- A Validator's null-check scope should match what the pipeline stage
  immediately downstream of it actually requires to run correctly — not a
  fixed "PK only" rule copied mechanically from the Customer/Product
  precedent. Doing this while still being intentional (see ADR-018) about
  not over-engineering.
- Keeping derived-metric logic inside the Transformer (rather than, say, a
  new pipeline stage) preserves the two-stage Validator/Transformer
  contract from ADR-017 without adding a third abstraction before there's
  evidence a third one is needed.

## Consequences

- Future Fact-table Silver pipelines (e.g. a Payment Fact in US-012) should
  expect their Transformer to carry real business-metric logic, not just
  standardization — this is now a documented, table-type-appropriate
  pattern rather than a one-off for Order.
- US-013's eventual generalization (ADR-018) needs to keep this in mind:
  a single generic Transformer interface is still viable (both Dimension
  and Fact Transformers return `Dataset<Row> transform(Dataset<Row>)`), but
  a generic *implementation* of Transformer (unlike the generic
  null-PK/dedup Validator) is unlikely to be worthwhile — Transformer logic
  is expected to stay entity-specific.

## Lessons Learned (recorded for interview prep)

- **Correction (2026-07-05):** an earlier version of this ADR incorrectly
  stated that `org.apache.spark.sql.functions.day(Column)` does not exist
  in Spark 3.5.x's Java API. That was wrong — `day(Column e)` is
  documented in the official Spark 3.5.6 Java API
  (`spark.apache.org/docs/3.5.6/api/java/org/apache/spark/sql/functions.html`)
  as an alias for `dayofmonth(Column e)`, both returning the day of the
  month as an integer. `OrderTransformer` was changed from `day(...)` to
  `dayofmonth(...)` during review based on this incorrect claim; the
  change is harmless (both are equivalent and correct) but was not
  actually necessary. Left as `dayofmonth(...)` since it's already merged
  and functionally identical — no urgency to change it back.
- **`Row.getAs(String)` combined with JUnit 5's overloaded `assertTrue`/
  `assertFalse`** (which accept both `boolean` and `BooleanSupplier`) can
  fail to compile due to generic type-inference ambiguity, since
  `getAs`'s type parameter has nothing forcing it to `Boolean` without an
  explicit cast. Fixed with `(boolean) result.getAs(...)`, consistent with
  the `(int)` casts already used for numeric columns in the same test
  files. This one holds — verified independently of the `day()` mistake
  above.

---

# ADR-020 : Payment Silver Fact — Composite Natural Key, Verified Business Rules, and Rejected Mock-Based Service Test

## Status: Accepted ✅

## Context

US-012 (Payment Silver Fact) surfaced a structural difference from every
prior Silver entity: Customer, Product, and Order each have a
single-column primary key (`customer_id`, `product_id`, `order_id`).
Payment does not. Per the Olist data dictionary, a customer may pay a
single order using more than one payment method (e.g. part voucher, part
credit card); each method is stored as its own row, distinguished by
`payment_sequential`.

Three design questions came up while implementing it:

1. What is Payment's natural key for deduplication?
2. Which `payment_value`/`payment_type` values are genuinely invalid vs.
   legitimate business cases?
3. Should `PaymentSilverService` get a dedicated test, and if so, in what
   style?

## Decision

**1. Composite key.** `PaymentValidator` deduplicates on
`(order_id, payment_sequential)`, not `order_id` alone, and null-checks
both columns before deduplication (`dropDuplicates` treats `null == null`
as a match, so an unchecked `payment_sequential` could silently collapse
two genuinely distinct payment rows). This was independently validated
against the real dataset profile: `order_id` has 99,440 unique values
against ~104k total rows, and `payment_sequential` ranges up to 29 —
confirming the single-column approach used elsewhere would have caused
real data loss here.

An earlier test draft mislabeled this scenario as "multiple installment
rows." That conflates two distinct Olist concepts: `payment_sequential`
(multiple payment *methods* split across one order, each its own row) vs.
`payment_installments` (a deferred repayment schedule *within* a single
row, which does not create additional rows). Caught and corrected before
merge — see `PaymentValidatorTest.shouldKeepMultipleSplitPaymentRowsForTheSameOrder`.

**2. Verified, not assumed, business-rule filters.** `PaymentValidator`
filters `payment_value >= 0` (not strictly `> 0`) and unconditionally
excludes `payment_type == "not_defined"`. Both were set only after
querying the real Bronze CSV directly: 9 rows have `payment_value = 0.0`
— 6 `voucher` (a voucher can legitimately cover a split payment in full,
leaving that row's amount at zero) and 3 `not_defined` (an inherently
unknown/invalid payment method, treated as invalid regardless of its
value). Filtering `payment_value > 0` would have silently dropped the 6
legitimate voucher rows; filtering only on value without also excluding
`not_defined` would have kept 0 of those 3 invalid rows out only by
coincidence of their value being zero, not because the filter targeted
the actual invalid condition.

**3. Rejected: a Mockito-based `PaymentSilverServiceTest`.** A test
mocking `BronzeReader`/`SilverWriter` and asserting via `ArgumentCaptor`/
`verify(...)` was written and reviewed. It was not adopted. No other
entity (Customer excepted — see below) has a service-level test, and
Mockito is not otherwise a dependency of this project. `SilverServiceTest`
does exist, but it is a classical, state-based integration test (seeds
real Bronze Parquet via `BronzeWriter`, runs the real service end-to-end,
reads real Silver Parquet back) with no mocking involved — a different
category of test, not a precedent for a mockist one. Adopting Mockito for
Payment alone, with no other entity following suit, would be an
inconsistency without a documented reason. Payment's test coverage is
`PaymentValidatorTest` and `PaymentTransformerTest` only, matching
Product's and Order's pattern.

## Reason

- A Validator's key and filter logic should be derived from the actual
  domain and the actual data, not copied mechanically from the previous
  entity's precedent — the composite-key decision and the verified-filter
  decisions are both instances of this same principle, already established
  for Order's expanded null-checks in ADR-019.
- Confirming assumptions against real data before writing the tests that
  lock them in (rather than after) avoids shipping a green test suite that
  quietly encodes a wrong assumption.
- Test-style consistency across entities is itself a design decision worth
  making deliberately rather than by accident — introducing a new test
  framework dependency for one entity, with no stated reason, would read
  as inconsistency rather than intentional variation to a future reviewer.

## Consequences

- `PaymentValidator` is the most complex Validator in the project so far —
  a composite key, two business-rule filters, and three null-checks,
  versus generic null-check + dedup handling for Customer/Product/Order.
  US-013's `NullPkDedupValidator` intentionally does not absorb Payment;
  `PaymentValidator` remains a dedicated `DataValidator` implementation.
- Mockito remains outside the project's dependencies. If a future story
  has a genuine reason to need interaction-based testing (verifying call
  counts/arguments rather than state), that will be its own deliberate
  decision, not inherited from this one.
- The distinction between `payment_sequential` and `payment_installments`
  is recorded here as a durable domain note, since it's easy to
  re-confuse and cheap to get wrong in a future Gold-layer aggregation.

---

# Future Architecture Decisions

The following decisions are expected later:

* Hive warehouse location
* Partitioning strategy
* Spark configuration tuning
* Logging framework
* Airflow deployment strategy
* Table naming conventions
* Data quality checks
* Monitoring and alerting

---

## Retail Lakehouse Layer Summary


### Bronze

```
CSV

↓

Specific Reader (Explicit Schema)

↓

Dataset<Row>

↓

Parquet
(SaveMode.Overwrite)
```


### Silver

```
Bronze Parquet

↓ 

Universal Reader

↓

Validator

↓

Transformer

↓

Universal Parquet Writer
```

### Gold

```
Silver Data

↓

Business Metrics

↓

Reports

↓

Dashboards
```

---

# Revision History

| Date       | Change                                                    |
| ---------- | --------------------------------------------------------- |
| 2026-06-17 | Initial Architecture Notes created                        |
| 2026-06-23 | Updated ADR-002 directory structure to Bronze/Silver/Gold |
| 2026-06-23 | Updated ADR-003 dataset path to olist_ecom_dataset        |
| 2026-06-23 | Updated ADR-004 to reflect medallion architecture         |
| 2026-06-23 | Added ADR-011 : Externalized Configuration                |
| 2026-06-23 | Added ADR-012 : Explicit Spark Schemas                    |
| 2026-06-23 | Added ADR-013 : Centralized Bronze Writer                 |
| 2026-06-27 | Added ADR-014 : Dependency Injection & Service Layer      |
| 2026-06-27 | Added ADR-015 : Serializable Service Classes              |
| 2026-06-28 | Updated ADR-012 schema list to include ProductSchema and PaymentSchema |
| 2026-06-28 | Updated ADR-013 writeTable examples to include PRODUCTS and PAYMENTS  |
| 2026-07-01 | Added ADR-016 : Universal Parquet Readers and Writers     |
| 2026-07-01 | Added ADR-017 : Validator Pattern (Data Quality Separation) |
| 2026-07-02 | Added ADR-018 : Intentional Duplication Across Silver Dimensions, Deferred to US-013 |
| 2026-07-04 | Added ADR-019 : Order Silver Fact — Transformer Scope for Facts vs. Dimensions, plus Lessons Learned (Spark day() API gotcha, Row.getAs/assertTrue generics gotcha) |
| 2026-07-05 | Corrected ADR-019 Lessons Learned: functions.day() DOES exist in Spark 3.5.6 (confirmed against official Javadoc) — earlier claim that it didn't was wrong |
| 2026-07-06 | Added ADR-020 : Payment Silver Fact — Composite Natural Key, Verified Business Rules, and Rejected Mock-Based Service Test |
| 2026-07-06 | Restored missing "Future Architecture Decisions" section header |
| 2026-07-19 | Added ADR-012 Addendum : Schema column-name constants (typo-safety at compile time), referenced from Silver job wiring and validator/service tests |
| 2026-07-19 | Updated ADR-018 Consequences: retired entity-specific Silver services and Customer/Product/Order validators (+ tests) confirmed deleted, closing US-013 |

---