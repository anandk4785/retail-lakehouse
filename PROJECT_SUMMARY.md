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

│   └── LakehouseTable

├── factory

│   └── SparkSessionFactory

├── main

│   ├── HelloSparkJob

│   ├── CustomerBronzeJob

│   ├── OrderBronzeJob

│   ├── ProductBronzeJob

│   ├── PaymentBronzeJob

│   ├── CustomerSilverJob

│   ├── ProductSilverJob

│   ├── OrderSilverJob

│   └── PaymentSilverJob

├── reader

│   ├── CustomerReader

│   ├── OrderReader

│   ├── ProductReader

│   ├── PaymentReader

│   └── BronzeReader

├── schema

│   ├── CustomerSchema

│   ├── OrderSchema

│   ├── ProductSchema

│   └── PaymentSchema

├── service

│   ├── CustomerService

│   ├── OrderService

│   ├── ProductService

│   ├── PaymentService

│   ├── CustomerSilverService

│   ├── ProductSilverService

│   ├── OrderSilverService

│   └── PaymentSilverService

├── transform

│   ├── CustomerTransformer

│   ├── ProductTransformer

│   ├── OrderTransformer

│   └── PaymentTransformer

├── validator

│   ├── CustomerValidator

│   ├── ProductValidator

│   ├── OrderValidator

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

│   └── BronzeReaderTest

├── service

│   ├── CustomerServiceTest

│   ├── OrderServiceTest

│   ├── ProductServiceTest

│   ├── PaymentServiceTest

│   └── CustomerSilverServiceTest

├── transform

│   ├── CustomerTransformerTest

│   ├── ProductTransformerTest

│   ├── OrderTransformerTest

│   └── PaymentTransformerTest

├── validator

│   ├── CustomerValidatorTest

│   ├── ProductValidatorTest

│   ├── OrderValidatorTest

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

Consolidates the intentional duplication introduced across US-010
(Product), US-011 (Order Fact), and US-012 (Payment Fact) — see ADR-018.

Planned scope:

- Introduce DataValidator / DataTransformer interfaces

- Introduce a generic, configurable validator for null-PK/dedup handling
  for the entities where a single-column PK applies (CustomerValidator,
  ProductValidator, OrderValidator). PaymentValidator's composite-key
  and business-rule logic (not_defined exclusion, value >= 0) is
  expected to remain a dedicated class rather than fit the generic
  single-PK validator — to be confirmed during the refactor itself.

- Introduce a single generic, DI-driven Silver service (replacing
  CustomerSilverService, ProductSilverService, OrderSilverService,
  PaymentSilverService)

- Retain entity-specific Transformer classes (standardization logic
  differs meaningfully per entity — not a generalization candidate)

- Update all *SilverJob classes to wire the generic service

- Full regression pass: all existing Silver-layer tests must continue to
  pass unchanged in behavior after the refactor
```

Matches the Kanban board 1:1 — issues are currently tracked through
US-012 only; Sprint 4 and Sprint 5 stories below exist in this document
as forward planning and have not yet been created as board issues.

---

### Sprint 4

```text
US014

Hive Metastore


US015

Sales Analytics


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

---

## Current Status

```text
Current Branch

main


Build Status

BUILD SUCCESSFUL


Current Sprint

Sprint 3


Current User Story

US013

Technical Debt / Refactoring


Next User Story

None — Sprint 3 (Silver Layer) complete pending US-013. Sprint 4 (Hive
Metastore, US-014) follows.
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
| 2026-07-03 | Resolved US-013 numbering: US-013 is now Technical Debt / Refactoring; Sprint 4 renumbered US013→US014, US014→US015, US015→US016; Sprint 5 renumbered US016→US017, US017→US018, US018→US019 |
| 2026-07-03 | Added US-013 Technical Debt / Refactoring story detail (planned scope) to Sprint 3 |
| 2026-07-03 | Noted Kanban board currently only has issues through US-012; Sprint 4/5 remain document-only forward planning |
| 2026-07-04 | US011 Order Silver Fact marked DONE                         |
| 2026-07-04 | Added OrderSilverJob/OrderSilverService/OrderTransformer/OrderValidator and their tests to Current Folder Structure |
| 2026-07-04 | Added Fact-vs-Dimension Transformer scope design decision to decision table |
| 2026-07-04 | Advanced Current Status to Sprint 3 / US012, Next US013     |
| 2026-07-06 | US012 Payment Silver Fact marked DONE                        |
| 2026-07-06 | Added PaymentSilverJob/PaymentSilverService/PaymentTransformer/PaymentValidator and their tests to Current Folder Structure |
| 2026-07-06 | Added composite-key, verified-filter, and rejected-Mockito-test design decisions to decision table |
| 2026-07-06 | Noted PaymentSilverServiceTest reviewed but not merged; Payment tests limited to PaymentValidatorTest/PaymentTransformerTest, consistent with Product/Order |
| 2026-07-06 | Advanced Current Status to Sprint 3 / US013 (Technical Debt / Refactoring) |

---