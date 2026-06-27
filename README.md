<h1 align="center">Retail Lakehouse</h1>

<p align="center">
  A pseudo-production Big Data pipeline built with Java 17 and Apache Spark 3.5.x,
  implementing the Medallion Architecture (Bronze → Silver → Gold) for e-commerce analytics.
</p>

---

## 📖 Project Overview

The **Retail Lakehouse** is a pseudo-production big data pipeline designed to ingest, process, and analyze e-commerce data. It adheres strictly to industry standards, utilizing a decoupled, test-driven architecture and the **Medallion Data Architecture** (Bronze, Silver, Gold layers) pattern.

**Key Features:**

- **Decoupled Architecture** — Utilizes Dependency Injection for modular, 100% testable Spark services.
- **Type Safety** — Domain constants managed via Java Enums (`LakehouseTable`) for robust Parquet writing.
- **Orchestrator-Ready** — `main` methods are built with explicit OS exit codes (`System.exit(1)`) to trap failures for Apache Airflow.
- **Test Pyramid** — Thorough unit testing for Readers, Writers, and Services, leaving `main` classes strictly as dependency assembly lines.

---

## 🏗️ Architecture

The pipeline transforms raw CSV data into curated analytics tables using the following flow:

```
External CSV Files (Landing)
         ↓
Bronze Layer     (Raw Parquet Data          →  data/dev/bronze)
         ↓
Silver Layer     (Curated Dimension/Facts   →  data/dev/silver)
         ↓
Spark SQL / Hive Metastore
         ↓
Gold Layer       (Analytics & Reports       →  data/dev/gold)
         ↓
Apache Airflow DAG (Orchestration)
```

---

## 🛠️ Technology Stack

| Component       | Technology                                   |
|-----------------|----------------------------------------------|
| Language        | Java 17                                      |
| Build Tool      | Gradle 8.14                                  |
| Data Processing | Apache Spark 3.5.x (Spark SQL / DataFrame API) |
| Storage Format  | Apache Parquet                               |
| Metadata Store  | Hive Metastore                               |
| Orchestration   | Apache Airflow *(Planned)*                   |

---

## 📂 Project Structure

```
retail-lakehouse/
├── src/
│   ├── main/java/com/anand/retail/
│   │   ├── config/       # Configuration loaders
│   │   ├── constants/    # Enums and file constants
│   │   ├── factory/      # SparkSessionFactory
│   │   ├── main/         # Assembly line jobs (CustomerBronzeJob, etc.)
│   │   ├── reader/       # Spark CSV Readers
│   │   ├── service/      # Business logic and transformation services
│   │   └── writer/       # Universal Bronze/Silver/Gold Parquet writers
│   └── test/             # JUnit 5 tests mirroring the main structure
├── data/
│   └── dev/
│       ├── bronze/       # Raw Parquet outputs
│       ├── silver/       # Cleansed and joined tables
│       ├── gold/         # Aggregated reports
│       └── warehouse/    # Hive managed warehouse
├── sql/                  # DDL and query scripts
├── logs/                 # Application execution logs
├── ARCHITECTURE_NOTES.md # Architecture Decision Records (ADR)
├── PROJECT_SUMMARY.md    # High-level project tracker
└── TESTING_STRATEGY.md   # Rules around testing Spark jobs
```

---

## ⚙️ Getting Started

### 1. Prerequisites

Ensure the following are installed and available on your `PATH`:

- **Java 17** — `java -version`
- **Gradle 8.x** — `gradle -v`
- **Apache Spark 3.5.x** — configured in `$SPARK_HOME`
- **Ubuntu / Linux** — recommended for local Airflow integration

---

### 2. Dataset Setup & Pre-Ingestion Validation

Datasets are stored **outside** the Git repository to keep the codebase lightweight. By default, `application.properties` points to:

```
~/projects/bigdata/data/olist_ecom_dataset
```

Before running any Spark job, validate that the dataset is accessible and correctly formatted:

```bash
# Inspect headers and the first few rows
head -5 ~/projects/bigdata/data/olist_ecom_dataset/olist_orders_dataset.csv

# Verify the total record count (~99k rows expected)
wc -l ~/projects/bigdata/data/olist_ecom_dataset/olist_orders_dataset.csv
```

> Ensure `dataset.root` in `application.properties` reflects the correct path for your environment.

---

### 3. Build the Project

Clone the repository and run the Gradle build to download Spark dependencies and execute the test suite:

```bash
./gradlew clean build
```

---

### 4. Run an Ingestion Job

**Option A — Gradle run task:**

```bash
./gradlew run
# Ensure mainClass in build.gradle is set to the desired job,
# e.g. com.anand.retail.main.CustomerBronzeJob
```

**Option B — spark-submit:**

```bash
# Build the JAR first
./gradlew shadowJar

# Submit the job
spark-submit \
  --class com.anand.retail.main.CustomerBronzeJob \
  --master local[*] \
  build/libs/retail-lakehouse-1.0.0.jar
```

---

## 🧪 Testing

This project uses **JUnit 5**. Run the full test suite with:

```bash
./gradlew test
```

> **Note:** Due to JVM constraints and the `System.exit(1)` triggers used for orchestrator safety, `main` classes are purposefully excluded from unit testing. See [`TESTING_STRATEGY.md`](TESTING_STRATEGY.md) for details.

---

## 📝 Documentation

| Document                  | Purpose                                              |
|---------------------------|------------------------------------------------------|
| [`ARCHITECTURE_NOTES.md`](ARCHITECTURE_NOTES.md) | Architecture Decision Records (ADR) — why tools and patterns were chosen |
| [`PROJECT_SUMMARY.md`](PROJECT_SUMMARY.md)       | Sprint tracker, current status, and design decisions |
| [`TESTING_STRATEGY.md`](TESTING_STRATEGY.md)     | Testing rules and approach for Spark jobs            |

---