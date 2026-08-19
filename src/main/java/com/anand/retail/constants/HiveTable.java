package com.anand.retail.constants;

/**
 * Centralizes the mapping from a physical LakehouseTable (directory name,
 * used by BronzeReader/SilverWriter) to its Hive catalog identity: which
 * Hive database it's registered in, and its fully qualified name for
 * Spark SQL queries.
 *
 * Composes LakehouseTable rather than re-declaring table names, so
 * "customers"/"products"/etc. continue to have exactly one source of
 * truth (see ADR-013/ADR-016).
 */
public enum HiveTable {

    SILVER_CUSTOMERS(LakehouseTable.CUSTOMERS, "silver"),
    SILVER_PRODUCTS(LakehouseTable.PRODUCTS, "silver"),
    SILVER_ORDERS(LakehouseTable.ORDERS, "silver"),
    SILVER_PAYMENTS(LakehouseTable.PAYMENTS, "silver"),
    SILVER_ORDER_ITEMS(LakehouseTable.ORDER_ITEMS, "silver");

    private final LakehouseTable lakehouseTable;
    private final String hiveDatabase;

    HiveTable(LakehouseTable lakehouseTable, String hiveDatabase) {
        this.lakehouseTable = lakehouseTable;
        this.hiveDatabase = hiveDatabase;
    }

    public LakehouseTable getLakehouseTable() {
        return lakehouseTable;
    }

    /**
     * The Hive database this table is registered in (e.g. "silver").
     * Also used to resolve the base storage path via
     * ConfigLoader.get(hiveDatabase + ".path") — e.g. "silver.path" —
     * reusing the same *.path keys HiveDatabaseService already uses to
     * create the databases themselves.
     */
    public String getHiveDatabase() {
        return hiveDatabase;
    }

    public String getHiveTableName() {
        return lakehouseTable.getDirectoryName();
    }

    public String getFullQualifiedName() {
        return hiveDatabase + "." + getHiveTableName();
    }
}