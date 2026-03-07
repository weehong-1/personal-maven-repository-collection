package com.weehong.bootstrap.database.enums;

import static com.weehong.bootstrap.database.constants.DatabaseConstants.JDBC_H2;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.JDBC_MYSQL;
import static com.weehong.bootstrap.database.constants.DatabaseConstants.JDBC_POSTGRESQL;

public enum DatabaseType {

    H2(JDBC_H2, "org.h2.Driver"),
    POSTGRESQL(JDBC_POSTGRESQL, "org.postgresql.Driver"),
    MYSQL(JDBC_MYSQL, "com.mysql.cj.jdbc.Driver");

    private final String prefix;
    private final String driverClassName;

    DatabaseType(String prefix, String driverClassName) {
        this.prefix = prefix;
        this.driverClassName = driverClassName;
    }

    public static DatabaseType fromJdbcUrl(String jdbcUrl) {
        if (jdbcUrl == null) {
            return null;
        }

        for (DatabaseType type : values()) {
            if (jdbcUrl.startsWith(type.prefix)) {
                return type;
            }
        }

        return null;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

}
