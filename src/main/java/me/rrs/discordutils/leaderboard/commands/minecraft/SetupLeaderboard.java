package me.rrs.discordutils.leaderboard.commands.minecraft;

import me.rrs.discordutils.leaderboard.LeaderboardCore;
import org.bukkit.Bukkit;
import us.ajg0702.leaderboards.LeaderboardPlugin;
import us.ajg0702.leaderboards.boards.TopManager;

public class SetupLeaderboard {


    public void setup(String newBoard, String name){

        TopManager topManager = LeaderboardCore.getajLeaderboards().getTopManager();
        if (!topManager.getBoards().contains(newBoard)) {
            topManager.getBoards().add(newBoard); //Add board if don't exist
        }

    }


}
