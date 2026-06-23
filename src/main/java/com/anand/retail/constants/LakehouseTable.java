package com.anand.retail.constants;

public enum LakehouseTable {
    CUSTOMERS("customers"),
    ORDERS("orders"),
    PRODUCTS("products"),
    PAYMENTS("payments");

    private final String directoryName;

    LakehouseTable(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryName() {
        return directoryName;
    }
}
