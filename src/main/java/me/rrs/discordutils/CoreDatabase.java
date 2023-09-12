package me.rrs.discordutils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;

public class CoreDatabase {

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    private static HikariDataSource dataSource;

    public void setupDataSource() {
        String connectionString = DiscordUtils.getInstance().getConfiguration().getString("Database.URL");
        String username = DiscordUtils.getInstance().getConfiguration().getString("Database.User");
        String password = DiscordUtils.getInstance().getConfiguration().getString("Database.Password");
        HikariConfig config = new HikariConfig();

        String driverClassName;
        if (connectionString.contains("mysql")) {
            driverClassName = "com.mysql.cj.jdbc.Driver";
        } else if (connectionString.contains("postgresql")) {
            driverClassName = "org.postgresql.Driver";
        } else if (connectionString.contains("sqlite")) {
            driverClassName = "org.sqlite.JDBC";
        } else {
            throw new IllegalArgumentException("Unsupported database type");
        }

        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(connectionString);
        config.setUsername(username);
        config.setPassword(password);

        switch (driverClassName) {
            case "com.mysql.cj.jdbc.Driver":
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                break;
            case "org.postgresql.Driver":
                config.addDataSourceProperty("cachePreparedStatements", "true");
                config.addDataSourceProperty("prepareThreshold", "3");
                break;
            case "org.sqlite.JDBC":
                String databaseName = connectionString.substring(connectionString.lastIndexOf("/") + 1);
                File dataFolder = DiscordUtils.getInstance().getDataFolder();
                File dbFile = new File(dataFolder, databaseName.replace("jdbc:sqlite:", ""));
                String absolutePath = dbFile.getAbsolutePath();
                config.setJdbcUrl("jdbc:sqlite:" + absolutePath);
                break;
        }

        dataSource = new HikariDataSource(config);
    }
}
