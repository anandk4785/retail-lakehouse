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

Merge to main
```

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

│   └── OrderBronzeJob

├── reader

│   ├── CustomerReader

│   └── OrderReader

├── schema

│   ├── CustomerSchema

│   └── OrderSchema

├── service

│   ├── CustomerService

│   └── OrderService

└── writer

    └── BronzeWriter
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
IN PROGRESS
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

Status : IN PROGRESS

Implemented:

- OrderSchema

- OrderReader

- OrderService

- OrderBronzeJob

- BronzeWriter.writeTable(ORDERS)


US007

Product Bronze Ingestion

Status : TODO


US008

Payment Bronze Ingestion

Status : TODO
```

---

### Sprint 3

```text
US009

Customer Dimension


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
| Externalized configuration    | Accepted | No hardcoded values             |
| Explicit Spark schemas        | Accepted | Faster startup, predictable types |
| Centralized BronzeWriter      | Accepted | Reusable, single storage point  |

---

## Current Status

```text
Current Branch

main


Build Status

BUILD SUCCESSFUL


Current Sprint

Sprint 2


Current User Story

US006

Order Bronze Ingestion


Next User Story

US007

Product Bronze Ingestion
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
| 2026-06-23 | Updated Current Status to Sprint 2 / US006          |
| 2026-06-23 | Added new design decisions to decision table        |


---