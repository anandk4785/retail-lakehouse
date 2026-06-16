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
│   └── dev
│       ├── curated
│       ├── raw
│       └── reports
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
~/projects/bigdata/data
```

Datasets:

```text
customers.csv

orders.csv

products.csv

payments.csv
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

Use layered architecture:

```text
CSV Files

↓

Raw Layer

Parquet

↓

Curated Layer

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

* Industry standard approach.
* Easier debugging.
* Supports incremental processing.
* Clear separation of ingestion and analytics.

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

# Revision History

| Date       | Change                             |
| ---------- | ---------------------------------- |
| 2026-06-17 | Initial Architecture Notes created |


---
