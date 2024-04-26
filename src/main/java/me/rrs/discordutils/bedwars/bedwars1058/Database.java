package me.rrs.discordutils.bedwars.bedwars1058;

import me.rrs.discordutils.CoreDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Database {

    public void createTable() {
        try (Connection connection = CoreDatabase.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS DiscordUtilsBedWars1058 (Player varchar(255) PRIMARY KEY, bedDestroyed int, death int, finalKills int, finalDeath int, totalPlayed int, kills int, loses int, win int, level int, winStreak int, bestWinStreak int);")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStats(String player, int bedDestroyed, int death, int finalKills, int finalDeath, int totalPlayed, int kills, int loses, int win, int level, int winStreak, int bestWinStreak) {
        try (Connection connection = CoreDatabase.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM DiscordUtilsBedWars1058 WHERE Player = ?");
            statement.setString(1, player);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // player exists, update the stats
                statement = connection.prepareStatement("UPDATE DiscordUtilsBedWars1058 SET bedDestroyed = ?, death = ?, finalKills = ?, finalDeath = ?, totalPlayed = ?, kills = ?, loses = ?, win = ?, level = ?, winStreak = ?, bestWinStreak = ? WHERE Player = ?");
                statement.setInt(1, bedDestroyed);
                statement.setInt(2, death);
                statement.setInt(3, finalKills);
                statement.setInt(4, finalDeath);
                statement.setInt(5, totalPlayed);
                statement.setInt(6, kills);
                statement.setInt(7, loses);
                statement.setInt(8, win);
                statement.setInt(9, level);
                statement.setInt(10, winStreak);
                statement.setInt(11, bestWinStreak);
                statement.setString(12, player);
                statement.executeUpdate();
            } else {
                // player does not exist, set the stats
                statement = connection.prepareStatement("INSERT INTO DiscordUtilsBedWars1058 (Player, bedDestroyed, death, finalKills, finalDeath, totalPlayed, kills, loses, win, level, winStreak, bestWinStreak) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setString(1, player);
                statement.setInt(2, bedDestroyed);
                statement.setInt(3, death);
                statement.setInt(4, finalKills);
                statement.setInt(5, finalDeath);
                statement.setInt(6, totalPlayed);
                statement.setInt(7, kills);
                statement.setInt(8, loses);
                statement.setInt(9, win);
                statement.setInt(10, level);
                statement.setInt(11, winStreak);
                statement.setInt(12, bestWinStreak);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getStats(String playerName) {
        Map<String, Integer> stats = new HashMap<>();
        try (Connection connection = CoreDatabase.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM DiscordUtilsBedWars1058 WHERE Player = ?")) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                stats.put("bedDestroyed", resultSet.getInt("bedDestroyed"));
                stats.put("death", resultSet.getInt("death"));
                stats.put("finalKills", resultSet.getInt("finalKills"));
                stats.put("finalDeath", resultSet.getInt("finalDeath"));
                stats.put("totalPlayed", resultSet.getInt("totalPlayed"));
                stats.put("kills", resultSet.getInt("kills"));
                stats.put("loses", resultSet.getInt("loses"));
                stats.put("win", resultSet.getInt("win"));
                stats.put("level", resultSet.getInt("level"));
                stats.put("winStreak", resultSet.getInt("winStreak"));
                stats.put("bestWinStreak", resultSet.getInt("bestWinStreak"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}
