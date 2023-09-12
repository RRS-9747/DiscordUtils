package me.rrs.discordutils.profile.command;

import me.rrs.discordutils.CoreDatabase;

import java.sql.*;
import java.util.*;

public class Database {

    public void createTable() {
        String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS DiscordStats (PAPITitle varchar(255) PRIMARY KEY, PAPIString varchar(255))";
        try (Connection connection = CoreDatabase.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_QUERY)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    public boolean createPAPITitle(String title, String papiString) {
        String INSERT_PAPI_TITLE_QUERY = "INSERT INTO DiscordStats (PAPITitle, PAPIString) VALUES (?, ?)";
        try (Connection connection = CoreDatabase.getDataSource().getConnection();
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
        try (Connection connection = CoreDatabase.getDataSource().getConnection();
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
        try (Connection connection = CoreDatabase.getDataSource().getConnection();
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
        try (Connection connection = CoreDatabase.getDataSource().getConnection();
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
        try (Connection connection = CoreDatabase.getDataSource().getConnection();
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
        try (Connection connection = CoreDatabase.getDataSource().getConnection();
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

    private void handleSQLException(SQLException e) {
        // Handle or log the SQLException appropriately
        e.printStackTrace();
    }
}

