package me.rrs.discordutils.level;

import me.rrs.discordutils.CoreDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Database {
    public void createLevelTable() {
        String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS UserLevels (UserId bigint PRIMARY KEY, Level int, Experience int)";
        try (Connection connection = CoreDatabase.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_QUERY)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    public CompletableFuture<Map<Long, Integer>> getAllUserLevels() {
        return CompletableFuture.supplyAsync(() -> {
            Map<Long, Integer> userLevels = new HashMap<>();
            String SELECT_ALL_USER_LEVELS_QUERY = "SELECT UserId, Level FROM UserLevels";
            try (Connection connection = CoreDatabase.getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement(SELECT_ALL_USER_LEVELS_QUERY);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long userId = resultSet.getLong("UserId");
                    int level = resultSet.getInt("Level");
                    userLevels.put(userId, level);
                }
            } catch (SQLException e) {
                handleSQLException(e);
            }
            return userLevels;
        });
    }


    public void updateUserLevel(long userId, int level, int experience) {
        CompletableFuture.runAsync(() -> {
            String UPSERT_USER_LEVEL_QUERY =
                    "INSERT INTO UserLevels (UserId, Level, Experience) VALUES (?, ?, ?) " +
                            "ON CONFLICT(UserId) DO UPDATE SET Level = EXCLUDED.Level, Experience = EXCLUDED.Experience ";

            try (Connection connection = CoreDatabase.getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement(UPSERT_USER_LEVEL_QUERY)) {

                statement.setLong(1, userId);
                statement.setInt(2, level);
                statement.setInt(3, experience);

                statement.executeUpdate();

            } catch (SQLException e) {
                handleSQLException(e);
            }
        });
    }

    public boolean deleteUserLevel(long userId) {
        String DELETE_USER_LEVEL_QUERY = "DELETE FROM UserLevels WHERE UserId = ?";
        try (Connection connection = CoreDatabase.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER_LEVEL_QUERY)) {
            statement.setLong(1, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            handleSQLException(e);
            return false;
        }
    }

    public int getUserLevel(long userId) {
        String SELECT_USER_LEVEL_QUERY = "SELECT Level FROM UserLevels WHERE UserId = ?";
        try (Connection connection = CoreDatabase.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USER_LEVEL_QUERY)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("Level");
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return 0; // Default level if not found
    }

    public int getUserExperience(long userId) {
        String SELECT_USER_EXPERIENCE_QUERY = "SELECT Experience FROM UserLevels WHERE UserId = ?";
        try (Connection connection = CoreDatabase.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USER_EXPERIENCE_QUERY)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("Experience");
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return 0; // Default experience if not found
    }

    private void handleSQLException(SQLException e) {
        // Handle or log the SQLException appropriately
        e.printStackTrace();
    }
}
