
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
    
        ├── raw
    
        ├── curated
    
        └── reports

└── src

    ├── main

    └── test
```

---

## 4. External Dataset

```text
Outside Repository


~/projects/bigdata/data


Contains:

customers.csv

orders.csv

products.csv

payments.csv
```

---

## 5. Architecture

```text
CSV Files

↓

Raw Layer

Parquet

↓

Curated Layer

Dimension + Fact Tables

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

## 7. Sprint Tracker

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
IN PROGRESS
```

**Stories:**

```text
US001

Project Bootstrap

DONE


US002

SparkSessionFactory

TODO


US003

Config Loader

TODO


US004

HelloSparkJob

TODO
```

---

### Sprint 2

```text
US005

Customer Ingestion


US006

Order Ingestion


US007

Product Ingestion


US008

Payment Ingestion
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

## 8. Important Design Decisions

| Decision                  |   Status | Reason                  |
| ------------------------- | -------: | ----------------------- |
| Datasets outside repo     | Accepted | Keep repo lightweight   |
| Environment-based storage | Accepted | Mimics dev/qa/prod      |
| Gradle build              | Accepted | Industry usage          |
| Spark Java                | Accepted | Matches BDE role        |
| Spring Boot               | Rejected | Not needed for ETL      |
| Hive later                | Accepted | Incremental development |
| Impala optional           | Accepted | Not necessary locally   |

---

# Next Immediate Step

Before writing the first Java class, I think the next order of work should be:

### Step 1

Create GitHub repository

---

### Step 2

Create:

```text
PROJECT_SUMMARY.md
```

and

```text
README.md
```

---

### Step 3

Push:

```text
feature/us001-project-bootstrap
```

to GitHub.

---

### Step 4

Create:

```text
US002

SparkSessionFactory
```

and start actual coding.

This keeps the project aligned with the same process from beginning to end and ensures we always have a clear roadmap, current status, and rationale for every design choice.


## Current Sprint

Sprint 1

**Goal**

Establish project foundation:

- Gradle Build
- Spark Dependencies
- Logging
- Config Management
- SparkSessionFactory
- HelloSparkJob


## Current Branch

main

Latest Release

Not Released

Build Status

BUILD SUCCESSFUL

Next User Story

US002 : SparkSessionFactory

---

## Sprint 1

### US001

Project Bootstrap

Status : DONE


### US002

Spark Foundation

Implemented:

- SparkSessionFactory

- ConfigLoader

- application.properties

Status : DONE


### US003

Merged into US002

Status : CLOSED


### US004

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

Status : DONE

# Revision History

| Date       | Change                             |
| ---------- | ---------------------------------- |
| 2026-06-17 | Initial Project Summary created    |


---