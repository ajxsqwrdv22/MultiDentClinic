package com.dentalclinic.dental.config;

public final class AppConfig {
    // Edit or set environment variables:
    // DC_JDBC_URL, DC_DB_USER, DC_DB_PASSWORD
    public static final String JDBC_URL = System.getenv().getOrDefault("DC_JDBC_URL", "jdbc:mysql://localhost:3306/dentalclinic?useSSL=false&serverTimezone=UTC");
    public static final String DB_USER = System.getenv().getOrDefault("DC_DB_USER", "root");
    public static final String DB_PASSWORD = System.getenv().getOrDefault("DC_DB_PASSWORD", "");
    private AppConfig() {}
}
