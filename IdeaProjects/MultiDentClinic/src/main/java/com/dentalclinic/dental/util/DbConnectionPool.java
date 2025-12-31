package com.dentalclinic.dental.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

/**
 * Singleton HikariCP DataSource provider.
 *
 * Configuration priority (highest -> lowest):
 *  1) System properties (e.g. -DDC_JDBC_URL=jdbc:...)
 *  2) Environment variables (DC_JDBC_URL, DC_DB_USER, DC_DB_PASSWORD, DC_MAX_POOL_SIZE)
 *  3) ./config.properties (working directory) if present
 *  4) application.properties on the classpath (src/main/resources/application.properties)
 *  5) Built-in defaults (use only for local dev)
 */
public final class DbConnectionPool {
    private static final Logger LOG = LoggerFactory.getLogger(DbConnectionPool.class);

    private static volatile HikariDataSource ds;

    // default values (suitable for local dev only)
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/dental_clinic?useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "aj 123";
    private static final int DEFAULT_MAX_POOL = 10;
    private static final String DEFAULT_POOL_NAME = "DentalClinicPool";
    private static final long DEFAULT_CONNECTION_TIMEOUT_MS = Duration.ofSeconds(30).toMillis();
    private static final long DEFAULT_IDLE_TIMEOUT_MS = Duration.ofMinutes(10).toMillis();
    private static final long DEFAULT_MAX_LIFETIME_MS = Duration.ofMinutes(30).toMillis();

    private DbConnectionPool() {}

    public static DataSource getDataSource() {
        if (ds == null) {
            synchronized (DbConnectionPool.class) {
                if (ds == null) {
                    ds = createDataSource(loadProperties());
                }
            }
        }
        return ds;
    }

    public static void close() {
        synchronized (DbConnectionPool.class) {
            if (ds != null) {
                try {
                    ds.close();
                    LOG.info("HikariDataSource closed.");
                } catch (Exception ex) {
                    LOG.warn("Error closing HikariDataSource: {}", ex.getMessage(), ex);
                } finally {
                    ds = null;
                }
            }
        }
    }

    public static boolean testConnection() {
        try (Connection c = getDataSource().getConnection()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            LOG.error("DB connection test failed: {}", e.getMessage());
            return false;
        }
    }

    /* -------------------- internal helpers -------------------- */

    private static HikariDataSource createDataSource(Properties p) {
        String jdbcUrl = firstNonEmpty(
                System.getProperty("DC_JDBC_URL"),
                env("DC_JDBC_URL"),
                p.getProperty("DC_JDBC_URL"),
                DEFAULT_URL
        );
        String user = firstNonEmpty(
                System.getProperty("DC_DB_USER"),
                env("DC_DB_USER"),
                p.getProperty("DC_DB_USER"),
                DEFAULT_USER
        );
        String pass = firstNonEmpty(
                System.getProperty("DC_DB_PASSWORD"),
                env("DC_DB_PASSWORD"),
                p.getProperty("DC_DB_PASSWORD"),
                DEFAULT_PASSWORD
        );
        int maxPool = intFrom(
                System.getProperty("DC_MAX_POOL_SIZE"),
                env("DC_MAX_POOL_SIZE"),
                p.getProperty("DC_MAX_POOL_SIZE"),
                DEFAULT_MAX_POOL
        );

        String poolName = firstNonEmpty(
                System.getProperty("DC_POOL_NAME"),
                env("DC_POOL_NAME"),
                p.getProperty("DC_POOL_NAME"),
                DEFAULT_POOL_NAME
        );

        long connectionTimeout = longFrom(
                System.getProperty("DC_CONNECTION_TIMEOUT_MS"),
                env("DC_CONNECTION_TIMEOUT_MS"),
                p.getProperty("DC_CONNECTION_TIMEOUT_MS"),
                DEFAULT_CONNECTION_TIMEOUT_MS
        );

        long idleTimeout = longFrom(
                System.getProperty("DC_IDLE_TIMEOUT_MS"),
                env("DC_IDLE_TIMEOUT_MS"),
                p.getProperty("DC_IDLE_TIMEOUT_MS"),
                DEFAULT_IDLE_TIMEOUT_MS
        );

        long maxLifetime = longFrom(
                System.getProperty("DC_MAX_LIFETIME_MS"),
                env("DC_MAX_LIFETIME_MS"),
                p.getProperty("DC_MAX_LIFETIME_MS"),
                DEFAULT_MAX_LIFETIME_MS
        );

        LOG.info("Initializing HikariCP pool '{}', url='{}', user='{}', maxPool={}", poolName, sanitizeUrl(jdbcUrl), user, maxPool);

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(user);
        cfg.setPassword(pass);
        cfg.setMaximumPoolSize(maxPool);
        cfg.setPoolName(poolName);
        cfg.setConnectionTimeout(connectionTimeout);
        cfg.setIdleTimeout(idleTimeout);
        cfg.setMaxLifetime(maxLifetime);

        // MySQL recommended settings
        cfg.addDataSourceProperty("cachePrepStmts", "true");
        cfg.addDataSourceProperty("prepStmtCacheSize", "250");
        cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        cfg.addDataSourceProperty("useServerPrepStmts", "true");

        // Optional: auto-commit default is true (common for simple apps)
        cfg.setAutoCommit(true);

        HikariDataSource hds = new HikariDataSource(cfg);
        LOG.info("HikariCP pool '{}' initialized.", poolName);
        return hds;
    }

    private static Properties loadProperties() {
        Properties p = new Properties();

        // 1) try working directory config.properties
        try (InputStream in = new FileInputStream("config.properties")) {
            p.load(in);
            LOG.info("Loaded DB properties from ./config.properties");
            return p;
        } catch (Exception ignored) {
            // no-op, try classpath
        }

        // 2) try application.properties on classpath
        try (InputStream in = DbConnectionPool.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                p.load(in);
                LOG.info("Loaded DB properties from classpath application.properties");
            }
        } catch (Exception e) {
            LOG.warn("Failed to load application.properties from classpath: {}", e.getMessage());
        }
        return p;
    }

    private static String sanitizeUrl(String url) {
        if (url == null) return null;
        int at = url.indexOf('@');
        if (at > 0) {
            int start = url.lastIndexOf("//", at);
            if (start >= 0) {
                return url.substring(0, start + 2) + "****@REDACTED";
            }
        }
        return url;
    }

    private static String env(String key) {
        try {
            return System.getenv(key);
        } catch (Exception e) {
            return null;
        }
    }

    private static String firstNonEmpty(Object... values) {
        for (Object v : values) {
            if (v == null) continue;
            String s = v.toString();
            if (!s.trim().isEmpty()) return s.trim();
        }
        return null;
    }

    /**
     * Accepts mixed types (String or Number). Tries each value in order.
     * If value is Number -> returned intValue.
     * If value is String -> parsed to int if possible.
     * Otherwise skipped.
     * If none valid -> returns DEFAULT_MAX_POOL.
     */
    private static int intFrom(Object... valuesAndDefault) {
        if (valuesAndDefault == null) return DEFAULT_MAX_POOL;
        for (Object v : valuesAndDefault) {
            if (v == null) continue;
            if (v instanceof Number) {
                return ((Number) v).intValue();
            }
            if (v instanceof String) {
                String s = ((String) v).trim();
                if (s.isEmpty()) continue;
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException ignored) {}
            }
        }
        return DEFAULT_MAX_POOL;
    }

    /**
     * Accepts mixed types (String or Number). Tries each value in order.
     * If value is Number -> returned longValue.
     * If value is String -> parsed to long if possible.
     * Otherwise skipped.
     * If none valid -> returns DEFAULT_CONNECTION_TIMEOUT_MS (sensible default).
     */
    private static long longFrom(Object... valuesAndDefault) {
        if (valuesAndDefault == null) return DEFAULT_CONNECTION_TIMEOUT_MS;
        for (Object v : valuesAndDefault) {
            if (v == null) continue;
            if (v instanceof Number) {
                return ((Number) v).longValue();
            }
            if (v instanceof String) {
                String s = ((String) v).trim();
                if (s.isEmpty()) continue;
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException ignored) {}
            }
        }
        return DEFAULT_CONNECTION_TIMEOUT_MS;
    }
}
