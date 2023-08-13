package me.rrs.discordstats;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.*;
import java.util.*;

public class Database {

    private HikariDataSource dataSource;

    public void createTable() {
        String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS DiscordStats (PAPITitle varchar(255) PRIMARY KEY, PAPIString varchar(255))";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_QUERY)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    public boolean createPAPITitle(String title, String papiString) {
        String INSERT_PAPI_TITLE_QUERY = "INSERT INTO DiscordStats (PAPITitle, PAPIString) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_PAPI_TITLE_QUERY)) {
            statement.setString(1, title);
            statement.setString(2, papiString);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            handleSQLException(e);
            return false;
        }
    }

    public Map<String, String> getAllPAPITitles() {
        Map<String, String> titles = new LinkedHashMap<>();
        String SELECT_ALL_PAPI_TITLES_QUERY = "SELECT PAPITitle, PAPIString FROM DiscordStats";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_PAPI_TITLES_QUERY);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                titles.put(resultSet.getString("PAPITitle"), resultSet.getString("PAPIString"));
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return titles;
    }

    public boolean updatePAPITitle(String newTitle, String oldTitle) {
        String UPDATE_PAPI_TITLE_QUERY = "UPDATE DiscordStats SET PAPITitle = ? WHERE PAPITitle = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PAPI_TITLE_QUERY)) {
            connection.setAutoCommit(false); // Start a transaction

            statement.setString(1, newTitle);
            statement.setString(2, oldTitle);
            statement.addBatch();

            int[] result = statement.executeBatch();
            connection.commit(); // Commit the transaction
            return result.length > 0;
        } catch (SQLException e) {
            handleSQLException(e);
            return false;
        }
    }

    public boolean updatePAPIString(String newString, String title) {
        String UPDATE_PAPI_STRING_QUERY = "UPDATE DiscordStats SET PAPIString = ? WHERE PAPITitle = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PAPI_STRING_QUERY)) {
            statement.setString(1, newString);
            statement.setString(2, title);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            handleSQLException(e);
            return false;
        }
    }

    public boolean removePAPITitle(String title) {
        String DELETE_PAPI_TITLE_QUERY = "DELETE FROM DiscordStats WHERE PAPITitle = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_PAPI_TITLE_QUERY)) {
            statement.setString(1, title);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            handleSQLException(e);
            return false;
        }
    }

    public List<String> getPAPITitles() {
        List<String> titles = new ArrayList<>();
        String SELECT_PAPI_TITLES_QUERY = "SELECT PAPITitle FROM DiscordStats";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_PAPI_TITLES_QUERY);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String title = resultSet.getString("PAPITitle");
                titles.add(title);
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return titles;
    }

    public void setupDataSource() {
        String connectionString = DiscordStats.getConfiguration().getString("Database.URL");
        String username = DiscordStats.getConfiguration().getString("Database.User");
        String password = DiscordStats.getConfiguration().getString("Database.Password");
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
                File dataFolder = DiscordStats.getInstance().getDataFolder();
                File dbFile = new File(dataFolder, databaseName.replace("jdbc:sqlite:", ""));
                String absolutePath = dbFile.getAbsolutePath();
                config.setJdbcUrl("jdbc:sqlite:" + absolutePath);
                break;
        }

        this.dataSource = new HikariDataSource(config);
    }

    private void handleSQLException(SQLException e) {
        // Handle or log the SQLException appropriately
        e.printStackTrace();
    }
}

