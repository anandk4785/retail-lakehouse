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

│   └── CustomerSilverJob

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

│   └── CustomerSilverService

├── transform

│   └── CustomerTransformer

├── validator

│   └── CustomerValidator

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

│   └── CustomerTransformerTest

├── validator

│   └── CustomerValidatorTest

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
  data. See ADR-016.


US010

Product Dimension


US011

Order Fact


US012

Payment Fact
```

---

### Sprint 4

```text
US013

Hive Metastore


US014

Sales Analytics


US015

Top Customers Report
```

---

### Sprint 5

```text
US016

Airflow DAG


US017

Daily ETL Pipeline


US018

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

US010

Product Dimension


Next User Story

US011

Order Fact
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

---