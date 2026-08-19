---

<h1 align="center">PROJECT SUMMARY</h1>


---

## 1. Project Overview

```text
Project Name

Retail Sales Lakehouse


Goal

Build an end-to-end Big Data Engineering project

demonstrating:

- Spark Java ETL

- Spark SQL

- Parquet

- Hive Metastore

- Airflow

- Agile methodology

- Git workflows

- Production-like architecture
```

---

## 2. System Environment

```text
OS

Ubuntu 24.04.4 LTS


IDE

IntelliJ Community


Java

OpenJDK 17


Build Tool

Gradle 8.14


Spark

Spark 3.5.6


Version Control

Git + GitHub
```

---

## 3. Repository Structure

```text
retail-lakehouse

├── build.gradle

├── settings.gradle

├── PROJECT_SUMMARY.md

├── README.md

├── gradle

├── logs

├── sql

├── data

    └── dev

        ├── bronze

        ├── silver

        ├── gold

        └── warehouse

└── src

    ├── main

    └── test
```

---

## 4. External Dataset

```text
Outside Repository


~/projects/bigdata/data/olist_ecom_dataset


Contains:

olist_customers_dataset.csv

olist_orders_dataset.csv

olist_products_dataset.csv

olist_order_payments_dataset.csv
```

---

## 5. Architecture

```text
CSV Dataset

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

---

## 6. Git Workflow

### Main Branch

```text
main
```

Always stable.

---

### Feature Branch

Naming:

```text
feature/us001-project-bootstrap

feature/us002-spark-session-factory

feature/us003-config-loader

feature/us004-hello-spark-job

feature/us005-customer-ingestion

feature/us009-customer-silver-dimension

feature/us010-product-silver-dimension

feature/us011-order-silver-fact

feature/us012-payment-silver-fact

feature/us013-technical-debt-refactoring

feature/us014-hive-metastore-integration
```

---

### Pull Requests

```text
feature branch

↓

PR

↓

Self Review

↓

Squash & Merge to main
```

Each story is developed on its own feature branch with multiple small,
scoped local commits (implementation, DI refactor, tests, docs). The PR is
squash-merged into `main`, so `main` history stays one clean commit per
story, while the full commit-by-commit history remains traceable on the
feature branch and inside the PR itself.

---

## 7. Current Folder Structure

```text
src/main/java/com/anand/retail

├── config

│   └── ConfigLoader

├── constants

│   ├── DatasetConstants

│   ├── LakehouseTable

│   └── HiveTable

├── factory

│   └── SparkSessionFactory

├── main

│   ├── HelloSparkJob

│   ├── CustomerBronzeJob

│   ├── OrderBronzeJob

│   ├── ProductBronzeJob

│   ├── PaymentBronzeJob

│   ├── OrderItemBronzeJob

│   ├── CustomerSilverJob

│   ├── ProductSilverJob

│   ├── OrderSilverJob

│   ├── PaymentSilverJob

│   ├── OrderItemSilverJob

│   ├── HiveSetupJob

│   └── HiveVerificationJob

├── reader

│   ├── CustomerReader

│   ├── OrderReader

│   ├── ProductReader

│   ├── PaymentReader

│   ├── OrderItemReader

│   └── BronzeReader

├── schema

│   ├── CustomerSchema

│   ├── OrderSchema

│   ├── ProductSchema

│   ├── PaymentSchema

│   └── OrderItemSchema

├── service

│   ├── CustomerService

│   ├── OrderService

│   ├── ProductService

│   ├── PaymentService

│   ├── OrderItemService

│   ├── SilverService

│   ├── HiveDatabaseService

│   └── HiveRegistrar

├── transform

│   ├── DataTransformer

│   ├── CustomerTransformer

│   ├── ProductTransformer

│   ├── OrderTransformer

│   ├── PaymentTransformer

│   └── OrderItemTransformer

├── validator

│   ├── DataValidator

│   ├── NullPkDedupValidator

│   └── PaymentValidator

└── writer

    ├── BronzeWriter

    └── SilverWriter


src/test/java/com/anand/retail

├── config

│   └── ConfigLoaderTest

├── factory

│   └── SparkSessionFactoryTest

├── reader

│   ├── CustomerReaderTest

│   ├── OrderReaderTest

│   ├── ProductReaderTest

│   ├── PaymentReaderTest

│   ├── OrderItemReaderTest

│   └── BronzeReaderTest

├── service

│   ├── CustomerServiceTest

│   ├── OrderServiceTest

│   ├── ProductServiceTest

│   ├── PaymentServiceTest

│   ├── OrderItemServiceTest

│   ├── SilverServiceTest

│   └── HiveRegistrarTest

├── transform

│   ├── CustomerTransformerTest

│   ├── ProductTransformerTest

│   ├── OrderTransformerTest

│   ├── PaymentTransformerTest

│   └── OrderItemTransformerTest

├── validator

│   ├── NullPkDedupValidatorTest

│   └── PaymentValidatorTest

└── writer

    ├── BronzeWriterTest

    └── SilverWriterTest
```

---

## 8. Sprint Tracker

### Sprint 0

Status:

```text
DONE
```

Stories:

```text
US000

Environment Setup

DONE
```

---

### Sprint 1

**Goal:**

```
Establish project foundation:

- Gradle Build
- Spark Dependencies
- Logging
- Config Management
- SparkSessionFactory
- HelloSparkJob
```
**Status:**

```text
DONE
```

**Stories:**

```text
US001

Project Bootstrap

DONE


US002

Spark Foundation

Implemented:

- SparkSessionFactory

- ConfigLoader

- application.properties

DONE


US003

Merged into US002

CLOSED


US004

Hello Spark Job

Implemented:

- DatasetConstants

- CustomerReader

- HelloSparkJob

- CustomerReaderTest

Features:

- Read customer CSV

- Print Schema

- Show sample records

- Count records

- Logging

DONE
```

---

### Sprint 2

**Goal:**

```
Bronze Layer Ingestion
```

**Status:**

```text
DONE
```

**Stories:**

```text
US005

Customer Bronze Ingestion

Status : DONE

Implemented:

- CustomerSchema

- CustomerReader

- CustomerReaderTest

- CustomerBronzeJob

- BronzeWriter.writeTable(CUSTOMERS)

- External dataset config


US006

Order Bronze Ingestion

Status : DONE

Implemented:

- OrderSchema

- OrderReader

- OrderReaderTest

- OrderService

- OrderBronzeJob

- BronzeWriter.writeTable(ORDERS)


US007

Product Bronze Ingestion

Status : DONE

Implemented:

- ProductSchema

- ProductReader

- ProductReaderTest

- ProductService

- ProductBronzeJob

- BronzeWriter.writeTable(PRODUCTS)


US008

Payment Bronze Ingestion

Status : DONE

Implemented:

- PaymentSchema

- PaymentReader

- PaymentReaderTest

- PaymentService

- PaymentServiceTest

- PaymentBronzeJob

- BronzeWriter.writeTable(PAYMENTS)
```

---

### Sprint 3

```text
US009

Customer Silver Dimension

Status : DONE

Implemented:

- BronzeReader (generic Bronze reader, resolves path via LakehouseTable)

- CustomerValidator (null PK removal + dedup on customer_id)

- CustomerTransformer (standardizes customer_city / customer_state)

- SilverWriter (generic Silver writer, resolves path via LakehouseTable)

- CustomerSilverService (DI-based orchestration, Serializable)

- CustomerSilverJob (assembly-line main class)

- BronzeReaderTest, CustomerValidatorTest, CustomerTransformerTest,
  SilverWriterTest, CustomerSilverServiceTest

- BronzeWriterTest updated to resolve paths via ConfigLoader (consistency
  with ADR-016)

Design notes:

- CustomerValidator and CustomerTransformer responsibilities were split
  cleanly: validation (null PK / dedup) is now solely owned by
  CustomerValidator; CustomerTransformer only standardizes already-valid
  data. See ADR-017.


US010

Product Silver Dimension

Status : DONE

Implemented:

- ProductValidator (null PK removal + dedup on product_id — mirrors
  CustomerValidator)

- ProductTransformer (fills null product_category_name with "Unknown")

- ProductSilverService (DI-based orchestration, Serializable)

- ProductSilverJob (assembly-line main class)

- ProductValidatorTest, ProductTransformerTest

Design notes:

- Intentionally duplicates the US-009 Customer pipeline structure rather
  than generalizing behind shared interfaces at this point. See ADR-018.
  Consolidation is scheduled as its own story, US-013 (Technical Debt /
  Refactoring), once Order and Payment Silver pipelines also exist.

- Code review caught two inconsistencies with established ADRs during
  implementation, both corrected before merge: ProductSilverService was
  missing `implements Serializable` (required by ADR-015), and a local
  variable was renamed from `valiDf` to `validDf` for consistency with
  CustomerSilverService.


US011

Order Silver Fact

Status : DONE

Implemented:

- OrderValidator (null-check on order_id, customer_id, AND
  order_purchase_timestamp — broader than Customer/Product's PK-only
  check, since every derived metric in OrderTransformer depends on
  order_purchase_timestamp being present; dedup on order_id)

- OrderTransformer (date dimensions: purchase_date/purchase_year/
  purchase_month/purchase_day; delivery metrics: delivery_days,
  delivery_delay_days, approval_time_days, carrier_dispatch_days,
  carrier_transit_days; is_delivered flag; renames *_timestamp/status
  columns and drops original order_-prefixed names)

- OrderSilverService (DI-based orchestration, Serializable, mirrors
  CustomerSilverService/ProductSilverService exactly)

- OrderSilverJob (assembly-line main class)

- OrderValidatorTest, OrderTransformerTest (delivered-order case,
  undelivered-order case verifying null downstream metrics, column
  rename verification, row-count-preserved case)

Design notes:

- OrderTransformer's scope is intentionally broader than
  CustomerTransformer/ProductTransformer: it derives business metrics
  (date parts, day-count differences) rather than only standardizing
  existing fields. This reflects Order being a Fact table (carries
  measures) rather than a Dimension (carries descriptive attributes) —
  same Validator/Transformer architectural split from ADR-017, but the
  Transformer's natural scope differs by table type. See ADR-019.

- OrderValidator's null-checks are scoped to every column the
  Transformer's derived metrics actually depend on (order_purchase_
  timestamp), not just the primary key — while intentionally leaving
  order_approved_at / order_delivered_carrier_date / order_delivered_
  customer_date / order_estimated_delivery_date unchecked, since a
  legitimately in-flight (not yet delivered) order should keep nulls
  there rather than being dropped.

- Still intentionally duplicates the per-entity Validator/Service
  pattern rather than generalizing — see ADR-018; consolidation remains
  scoped to US-013.

Review findings, fixed before merge:

- `OrderTransformer` was changed from `functions.day(...)` to
  `functions.dayofmonth(...)` during review, based on an incorrect claim
  that `day()` doesn't exist in Spark 3.5.x's Java API. That claim was
  wrong — `day(Column e)` is documented in the official Spark 3.5.6
  Javadoc as a valid alias for `dayofmonth(Column e)`. The change is
  harmless (both are equivalent) but wasn't actually necessary. See the
  correction in ADR-019.

- Two JUnit assertions (`assertTrue`/`assertFalse` on a Boolean column
  read via `Row.getAs(String)`) failed to compile due to generic type
  inference ambiguity between JUnit 5's `assertTrue(boolean)` and
  `assertTrue(BooleanSupplier)` overloads. Fixed with an explicit
  `(boolean)` cast, consistent with the `(int)` casts already used for
  numeric columns elsewhere in the same test file. This finding holds.


US012

Payment Silver Fact

Status : DONE

Implemented:

- PaymentValidator (null-checks on order_id, payment_sequential, AND
  payment_type; excludes payment_type = "not_defined" unconditionally;
  keeps payment_value >= 0 — not strictly > 0 — since 0.0 is a verified
  legitimate value for "voucher" rows; dedup on the COMPOSITE key
  (order_id, payment_sequential), not order_id alone)

- PaymentTransformer (derived flags is_installment_payment and
  is_credit_card; renames payment_value to payment_amount; logged as
  "Payment Fact", consistent with Order)

- PaymentSilverService (DI-based orchestration, Serializable — same
  gap caught and fixed here as on ProductSilverService in US-010)

- PaymentSilverJob (assembly-line main class)

- PaymentValidatorTest, PaymentTransformerTest

Design notes:

- Payment has no single-column natural key, unlike Customer/Product/
  Order. Per the Olist data dictionary, a customer may pay one order
  using more than one payment method (e.g. part voucher, part credit
  card); each method gets its own row, distinguished by
  payment_sequential — NOT to be confused with payment_installments,
  which describes a deferred repayment schedule *within* a single row
  and does not create additional rows. Confused these two concepts in
  an early test draft; caught and corrected before merge. The composite-
  key design was independently validated against the real dataset
  profile: order_id has 99,440 unique values against ~104k total rows,
  and payment_sequential ranges up to 29 — confirming dropDuplicates on
  order_id alone would have silently discarded real, distinct payment
  rows. See ADR-020.

- payment_value >= 0 and payment_type != "not_defined" were both
  verified against the real Bronze CSV before being locked into tests,
  rather than assumed: 9 rows have payment_value = 0.0 (6 voucher,
  3 not_defined). Voucher-zero rows are legitimate (a voucher can fully
  cover a split payment); not_defined rows are excluded regardless of
  value, since "not_defined" itself represents unknown/invalid payment
  method data. See ADR-020.

- A Mockito-based, interaction-style PaymentSilverServiceTest was
  written and reviewed (mocking BronzeReader/SilverWriter, asserting via
  ArgumentCaptor and verify(...)) but was deliberately NOT adopted.
  Customer/Product/Order have no service-level test — only Validator/
  Transformer tests — and Mockito is not otherwise a dependency of this
  project. Introducing it for one entity would be an inconsistency
  without a documented reason to justify it. PaymentSilverServiceTest is
  not part of the merged test suite. See ADR-020.


US013

Technical Debt / Refactoring

Status : DONE

Consolidates the intentional duplication introduced across US-010
(Product), US-011 (Order Fact), and US-012 (Payment Fact) — see ADR-018.

Implemented:

- Introduced DataValidator / DataTransformer interfaces

- Introduced NullPkDedupValidator as the generic, configurable validator
  for required-column filtering plus deduplication

- Wired Customer, Product, and Order Silver jobs with NullPkDedupValidator
  using table-specific required and dedup columns

- Kept PaymentValidator as a dedicated DataValidator implementation
  because Payment needs composite-key deduplication plus business-rule
  filters (payment_type != "not_defined", payment_value >= 0)

- Introduced a single generic, DI-driven SilverService that orchestrates
  BronzeReader -> DataValidator -> DataTransformer -> SilverWriter for a
  provided LakehouseTable

- Updated all *SilverJob classes to instantiate the generic SilverService

- Retained entity-specific Transformer classes and made them conform to
  the DataTransformer contract

- Replaced the CustomerSilverServiceTest coverage with SilverServiceTest
  and added NullPkDedupValidatorTest for Customer/Product/Order-style
  validator configurations

- Added column-name constants (e.g. CustomerSchema.CUSTOMER_ID) to every
  Schema class, referenced from Silver job wiring and from
  NullPkDedupValidatorTest/SilverServiceTest, replacing raw string
  literals with compile-time-checked references (see ADR-012 Addendum)

Verification:

- ./gradlew test --tests com.anand.retail.validator.NullPkDedupValidatorTest --tests com.anand.retail.service.SilverServiceTest

- ./gradlew test

Cleanup completed:

- Deleted retired entity-specific Silver service classes:
  CustomerSilverService, ProductSilverService, OrderSilverService,
  PaymentSilverService

- Deleted generic-replaced validator classes and tests:
  CustomerValidator, ProductValidator, OrderValidator,
  CustomerValidatorTest, ProductValidatorTest, OrderValidatorTest

- Full regression suite re-run after deletion; all tests green
```

Matches the Kanban board 1:1 — issues are currently tracked through
US-012 only; Sprint 4 and Sprint 5 stories below exist in this document
as forward planning and have not yet been created as board issues.

---

### Sprint 4

```text
US014

Hive Metastore Integration

Status : DONE

Implemented incrementally, in 7 small tasks rather than one large change:

- SparkSessionFactory: re-enabled enableHiveSupport() (previously
  commented out due to earlier setup issues), with
  javax.jdo.option.ConnectionURL pointed at a dedicated
  hive.metastore.path config key (kept SEPARATE from
  spark.sql.warehouse.dir — the two control genuinely different things;
  leaving the metastore path unset would silently default it to the
  JVM's working directory, violating ADR-008), plus
  datanucleus.schema.autoCreateAll=true for a fresh embedded Derby
  metastore, and derby.log relocated under ./logs/ via
  derby.stream.error.file

- HiveDatabaseService: creates the bronze/silver/gold Hive databases
  (CREATE DATABASE IF NOT EXISTS ... LOCATION ...), reusing the
  existing bronze.path/silver.path/gold.path config keys

- HiveTable (enum): centralizes table-to-Hive-database metadata,
  composing LakehouseTable rather than redeclaring table names —
  SILVER_CUSTOMERS, SILVER_PRODUCTS, SILVER_ORDERS, SILVER_PAYMENTS

- HiveRegistrar: registers existing Silver Parquet as external/
  unmanaged Hive tables (CREATE TABLE IF NOT EXISTS ... USING PARQUET
  LOCATION '...' — schema auto-discovered from Parquet, no manual
  column declaration needed)

- All four *SilverJob classes call HiveRegistrar.register(spark,
  HiveTable.SILVER_*) immediately after SilverService.run(spark)

- HiveSetupJob: one-time job that creates the three databases

- HiveVerificationJob: ad-hoc diagnostic job (SHOW DATABASES, SHOW
  TABLES, DESCRIBE TABLE EXTENDED, SELECT), run through the project's
  own SparkSessionFactory rather than a separately-configured
  spark-shell session

- HiveRegistrarTest: classical, state-based integration test (real
  SparkSessionFactory session, real database, real SilverWriter output,
  real Spark SQL queries against the result) — covers both the
  register-then-query path and idempotent re-registration

Design notes:

- Embedded Derby metastore chosen deliberately for this stage, over a
  standalone Metastore + Postgres — zero extra infrastructure, and every
  concept demonstrated (databases, external tables, SHOW/DESCRIBE
  semantics) carries over unchanged to a standalone Metastore later.
  Embedded Derby's single-JVM-lock limitation (only one process can hold
  the metastore at a time) is accepted as a known constraint for this
  stage, not a hidden gap — it's the concrete reason a Docker-based
  standalone Metastore + Postgres is the planned future migration. See
  ADR-021.

- HiveRegistrar creates EXTERNAL/unmanaged tables (LOCATION specified
  explicitly, pointing at Silver's existing storage path outside
  spark.sql.warehouse.dir) rather than Spark-managed tables. This
  respects SilverWriter's existing ownership of the Parquet files —
  DROP TABLE removes only the catalog entry, never the underlying data.
  See ADR-021.

- A midway detour considered renaming the databases to
  retail_bronze/retail_silver/retail_gold, and building a HiveWriter
  utility for writing Spark-managed tables for future Gold jobs
  (including a managed-table verification round-trip). Both were
  explicitly discarded: the rename was unnecessary (plain bronze/
  silver/gold kept), and HiveWriter was premature — no Gold job exists
  yet to build it against, and doing so now would repeat the same
  premature-abstraction risk already reasoned through for validators in
  ADR-018, just for a writer. See ADR-021's "Rejected Direction"
  section — this is recorded deliberately, not silently dropped, so the
  codebase doesn't carry dead, unverified code.

Review findings, fixed before merge:

- spark.sql.warehouse.dir and javax.jdo.option.ConnectionURL are
  separate settings that are easy to conflate — the first controls
  managed table DATA location, the second controls the metastore
  CATALOG's own location. Setting only the first still leaves
  metastore_db/ landing at the JVM's working directory by default.

- spark-shell does not automatically use this project's Hive config —
  it creates its own independent default SparkSession. Verification is
  done through HiveVerificationJob (going through the project's own
  SparkSessionFactory) rather than a hand-configured shell session.

- SparkSessionFactory's singleton guard only checks for null, not for
  "stopped" — HiveRegistrarTest deliberately omits the @AfterAll
  spark.stop() every other test class includes, since stopping the
  shared factory session would leave any later test's
  getSparkSession() call returning a dead, unrecoverable session. See
  ADR-021 Lessons Learned.


US015

Sales Analytics

Tracked as one parent GitHub issue with two sub-issues, rather than as
two separate top-level stories (US-015/US-016) — avoids the Sprint 4/5
renumbering churn the project went through during the US-013 saga; the
existing US015 -> US016 -> ... numbering stays untouched. Each phase
still gets its own branch and its own squash-merged PR. See ADR-022.

--- Phase 1: Order Items Bronze + Silver Ingestion ---

Status : DONE

Implemented:

- DatasetConstants.ORDER_ITEMS, LakehouseTable.ORDER_ITEMS,
  HiveTable.SILVER_ORDER_ITEMS

- OrderItemSchema (column constants from day one)

- OrderItemReader, OrderItemService, OrderItemBronzeJob (Bronze,
  mirrors Customer/Product/Order/Payment exactly)

- OrderItemTransformer (intentionally a no-op — order_items has no
  string-casing fields to standardize, and business metrics like
  total_item_value belong in Gold, not Silver; still implements
  DataTransformer, Serializable and logs explicitly, consistent with
  every other Transformer)

- OrderItemSilverJob: wires the existing NullPkDedupValidator with
  requiredColumns=[ORDER_ID, ORDER_ITEM_ID, PRODUCT_ID],
  deduplicateColumns=[ORDER_ID, ORDER_ITEM_ID] — a composite dedup key,
  handled with ZERO changes to NullPkDedupValidator itself

- OrderItemReaderTest, OrderItemServiceTest, OrderItemTransformerTest

- NullPkDedupValidatorTest: added
  shouldValidateOrderItemRowsUsingCompositeDeduplicateKey, proving
  multiple line items per order survive while true
  (order_id, order_item_id) duplicates collapse

Design notes:

- No new Validator class was needed for a 5th entity — the clearest,
  most concrete payoff yet of the US-013 consolidation:
  NullPkDedupValidator's asymmetric required/dedup column design
  (proven on Order's 3-required/1-dedup case) generalized cleanly to
  order_items' 3-required/2-dedup composite-key case without
  modification. See ADR-022.

- PRODUCT_ID is required (non-dedup) even though it isn't part of the
  composite key, because Phase 2's Gold aggregation needs to join
  order_items to products by product_id — a null there would silently
  break that join later. Same "validate what downstream actually
  needs" principle as ADR-019's OrderValidator reasoning. See ADR-022.

Review findings, fixed before merge:

- OrderItemTransformer was initially missing implements Serializable
  and had no logging at all (not even for the no-op case) — both
  brought in line with every other Transformer's established
  convention.

- OrderItemBronzeJob had a typo ("Sucessfully") in its completion log
  message, corrected.

- The new NullPkDedupValidatorTest case initially used raw string
  literals instead of OrderItemSchema constants, inconsistent with the
  other three tests in the same file — corrected to use the constants.

--- Phase 2: Sales Analytics Gold Layer ---

Status : NOT STARTED

Planned scope:

- SalesAnalyticsService: aggregation logic, querying already-registered
  Hive tables (silver.order_items JOIN silver.orders JOIN
  silver.products) via Spark SQL rather than manually resolving Parquet
  paths — the actual payoff of US-014's registration work

- gold.monthly_product_sales: revenue/freight/item-count/avg-price
  grouped by month + product category (one well-scoped Gold table,
  not a sprawling star schema)

- HiveWriter: revisited from the US-014 "Rejected Direction" (ADR-021)
  now that a real Gold job exists to design it against, rather than
  guessing ahead of need

- SalesAnalyticsGoldJob (assembly-line main class)

- SalesAnalyticsServiceTest (aggregation math against synthetic
  Silver-shaped data) + an integration test proving the managed-table
  round-trip through HiveWriter

- Verification via SHOW TABLES IN gold / DESCRIBE TABLE EXTENDED /
  SELECT, same pattern as US-014


US016

Top Customers Report
```

---

### Sprint 5

```text
US017

Airflow DAG


US018

Daily ETL Pipeline


US019

Monitoring
```

---

## 9. Important Design Decisions

| Decision                      |   Status | Reason                          |
| ----------------------------- | -------: | ------------------------------- |
| Datasets outside repo         | Accepted | Keep repo lightweight           |
| Environment-based storage     | Accepted | Mimics dev/qa/prod              |
| Gradle build                  | Accepted | Industry usage                  |
| Spark Java                    | Accepted | Matches BDE role                |
| Spring Boot                   | Rejected | Not needed for ETL              |
| Hive later                    | Accepted | Incremental development         |
| Impala optional               | Accepted | Not necessary locally           |
| Externalized configuration    | Accepted | No hardcoded values               |
| Explicit Spark schemas        | Accepted | Faster startup, predictable types |
| Centralized BronzeWriter      | Accepted | Reusable, single storage point    |
| Dependency Injection / Service Layer | Accepted | Testable, SRP-compliant    |
| Serializable Service classes  | Accepted | Prevents Spark serialization leaks |
| Validator/Transformer responsibility split | Accepted | Single owner for null/dedup handling, clearer pipeline contract |
| Test paths resolved via ConfigLoader | Accepted | Tests and production code share one source of truth for paths |
| Intentional per-entity duplication (US-010 to US-012), refactor in US-013 (Technical Debt / Refactoring) | Accepted | Avoids premature abstraction from a single example; generalize against proven, tested cases |
| Sprint 4/5 stories renumbered (US-013 reserved for Technical Debt) | Accepted | Kanban board only has issues through US-012; renumbering forward-planned stories in docs is cheap and keeps board/document numbering aligned before those issues are created |
| Fact-table Transformers derive metrics; Dimension Transformers only standardize | Accepted | Order Fact needs computed measures (date parts, day-count deltas); Customer/Product Dimensions only need field standardization — same Validator/Transformer split, table-type-appropriate Transformer scope |
| Payment Validator dedups on composite key (order_id, payment_sequential), not a single-column PK | Accepted | Verified against real data: order_id has 99,440 unique values vs. ~104k total payment rows; a customer may split one order's payment across multiple methods, each its own row |
| Validator business-rule filters (payment_value >= 0, payment_type != "not_defined") set from verified real-data findings, not assumptions | Accepted | Confirmed 9 zero-value rows exist (6 legitimate voucher, 3 invalid not_defined) before writing the filters or the tests that lock them in |
| Mockito-based PaymentSilverServiceTest reviewed but not adopted | Rejected | No other entity has a service-level test, and Mockito is not otherwise a project dependency; adopting it for one entity only would be an undocumented inconsistency |
| US-013 generic SilverService and NullPkDedupValidator | Accepted | Four Silver pipelines proved the shared orchestration shape; Customer/Product/Order share configurable null-filter/dedup behavior while Payment keeps a dedicated validator for domain-specific rules |
| Schema classes expose column-name constants, referenced by job wiring and tests instead of string literals | Accepted | A misspelled literal fails silently at Spark runtime; a misspelled constant fails at compile time — one source of truth per column name |
| Embedded Derby Hive metastore for now, standalone Metastore + Postgres deferred | Accepted | Zero extra infrastructure needed to demonstrate real Hive concepts now; single-JVM-lock limitation is the concrete, felt reason to migrate later rather than a hidden gap |
| HiveRegistrar creates external/unmanaged tables (explicit LOCATION), not managed tables | Accepted | Respects SilverWriter's existing ownership of Parquet files; DROP TABLE should not be able to delete Silver data out from under the writer that owns it |
| retail_-prefixed database rename and HiveWriter managed-table utility | Rejected | Rename was unnecessary; HiveWriter would be built against no real Gold job to prove it against — same premature-abstraction risk as ADR-018, deferred until a real Gold job exists |
| US-015 tracked as one parent issue with Phase 1/Phase 2 sub-issues, not two top-level stories | Accepted | Avoids the Sprint 4/5 renumbering churn from the US-013 saga; each phase still gets its own branch and squash-merged PR |
| NullPkDedupValidator wired for order_items' composite key with zero code changes | Accepted | Proves the US-013 generalization holds on a 5th entity it wasn't explicitly designed around — asymmetric 3-required/2-dedup configuration |

---

## Current Status

```text
Current Branch

main


Build Status

BUILD SUCCESSFUL


Current Sprint

Sprint 4


Current User Story

US015 — Phase 1 (Order Items Bronze + Silver Ingestion) DONE; all five
Silver tables (customers, products, orders, payments, order_items) are
now registered in the Hive Metastore and verified queryable via Spark
SQL. Build, tests, and manual run all confirmed green.


Next User Story

US015 — Phase 2 (Sales Analytics Gold Layer)
```

---

# Revision History

| Date       | Change                                              |
| ---------- | --------------------------------------------------- |
| 2026-06-17 | Initial Project Summary created                     |
| 2026-06-23 | Updated repository structure to Bronze/Silver/Gold  |
| 2026-06-23 | Updated external dataset path to olist_ecom_dataset |
| 2026-06-23 | Updated architecture section to medallion layers    |
| 2026-06-23 | Sprint 1 marked DONE                                |
| 2026-06-23 | Sprint 2 user stories added with current statuses   |
| 2026-06-23 | Added Current Folder Structure section              |
| 2026-06-23 | Updated Current Status to Sprint 2 / US006                  |
| 2026-06-23 | Added new design decisions to decision table                |
| 2026-06-27 | US006 Order Bronze Ingestion marked DONE                    |
| 2026-06-27 | US007 Product Bronze Ingestion marked DONE                  |
| 2026-06-27 | Added Product* classes to Current Folder Structure          |
| 2026-06-27 | Updated Current Status to US008 / Next US009                |
| 2026-06-27 | Added DI & Serializable decisions to design decisions table |
| 2026-06-28 | US008 Payment Bronze Ingestion marked DONE                  |
| 2026-06-28 | Sprint 2 marked DONE                                        |
| 2026-06-28 | Added Payment* classes to Current Folder Structure          |
| 2026-06-28 | Advanced Current Status to Sprint 3 / US009                 |
| 2026-07-01 | US009 Customer Silver Dimension marked DONE                 |
| 2026-07-01 | Added Silver-layer classes (reader/validator/transform/writer/service/main) to Current Folder Structure |
| 2026-07-01 | Added full src/test folder tree to Current Folder Structure |
| 2026-07-01 | Added Validator/Transformer split and test-path decisions to design decisions table |
| 2026-07-01 | Advanced Current Status to Sprint 3 / US010, Next US011     |
| 2026-07-01 | Documented squash-and-merge PR workflow in Git Workflow section |
| 2026-07-02 | US010 Product Silver Dimension marked DONE                  |
| 2026-07-02 | Added ProductValidator/ProductTransformer/ProductSilverService/ProductSilverJob and their tests to Current Folder Structure |
| 2026-07-02 | Added intentional-duplication design decision to decision table (ADR-018) |
| 2026-07-02 | Flagged Sprint 4 US-013 numbering collision with new Technical Debt story |
| 2026-07-02 | Advanced Current Status to Sprint 3 / US011, Next US012     |
| 2026-07-04 | US011 Order Silver Fact marked DONE                         |
| 2026-07-04 | Added OrderSilverJob/OrderSilverService/OrderTransformer/OrderValidator and their tests to Current Folder Structure |
| 2026-07-04 | Added Fact-vs-Dimension Transformer scope design decision to decision table |
| 2026-07-04 | Advanced Current Status to Sprint 3 / US012, Next US013     |
| 2026-07-06 | US012 Payment Silver Fact marked DONE                        |
| 2026-07-06 | Added PaymentSilverJob/PaymentSilverService/PaymentTransformer/PaymentValidator and their tests to Current Folder Structure |
| 2026-07-06 | Added composite-key, verified-filter, and rejected-Mockito-test design decisions to decision table |
| 2026-07-06 | Noted PaymentSilverServiceTest reviewed but not merged; Payment tests limited to PaymentValidatorTest/PaymentTransformerTest, consistent with Product/Order |
| 2026-07-06 | Advanced Current Status to Sprint 3 / US013 (Technical Debt / Refactoring) |
| 2026-07-19 | Documented US-013 generic DataValidator/DataTransformer, NullPkDedupValidator, SilverService, Silver job rewiring, and focused regression tests |
| 2026-07-19 | Marked retired entity-specific Silver services and Customer/Product/Order validators/tests as pending deletion before closing US-013 |
| 2026-07-20 | Confirmed deletion of CustomerSilverService/ProductSilverService/OrderSilverService/PaymentSilverService and CustomerValidator/ProductValidator/OrderValidator (+ tests); removed "(pending US-013 deletion)" markers from Current Folder Structure |
| 2026-07-20 | US013 Technical Debt / Refactoring marked DONE; Sprint 3 marked COMPLETE |
| 2026-07-20 | Added schema column-name constants design decision to decision table |
| 2026-07-20 | Advanced Current Status: no current story (Sprint 3 complete), Next US014 (Hive Metastore, Sprint 4) |
| 2026-07-27 | US014 Hive Metastore Integration marked DONE                |
| 2026-07-27 | Added HiveDatabaseService/HiveRegistrar/HiveTable/HiveSetupJob/HiveVerificationJob and HiveRegistrarTest to Current Folder Structure |
| 2026-07-27 | Added embedded-Derby, external-table, and rejected retail_/HiveWriter design decisions to decision table |
| 2026-07-27 | Advanced Current Status to Sprint 4, no current story, Next US015 (Sales Analytics) |
| 2026-08-02 | US015 Phase 1 (Order Items Bronze + Silver Ingestion) marked DONE; documented Phase 1/Phase 2 sub-issue structure (ADR-022) |
| 2026-08-02 | Added OrderItemSchema/Reader/Service/Transformer/BronzeJob/SilverJob and their tests to Current Folder Structure |
| 2026-08-02 | Added sub-issue-structure and composite-key-generalization design decisions to decision table |
| 2026-08-02 | Advanced Current Status: US015 Phase 1 DONE, Next US015 Phase 2 (Sales Analytics Gold Layer) |

---