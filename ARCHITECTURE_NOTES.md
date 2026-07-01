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

---