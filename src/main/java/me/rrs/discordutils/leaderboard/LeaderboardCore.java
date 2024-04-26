package me.rrs.discordutils.leaderboard;

import me.rrs.discordutils.DiscordUtils;
import me.rrs.discordutils.bedwars.screamingbw.commands.discord.ScreamingStats;
import me.rrs.discordutils.leaderboard.commands.discord.ShowLeaderboard;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;
import us.ajg0702.leaderboards.LeaderboardPlugin;

public class LeaderboardCore {

    public static LeaderboardPlugin getajLeaderboards() {
        return leaderboardPlugin;
    }

    private static final LeaderboardPlugin leaderboardPlugin = (LeaderboardPlugin) Bukkit.getPluginManager().getPlugin("ajLeaderboards");



    private static void loadConfig(){

    }
    private static void loadCommands(){

    }
    private static void loadEvent(){
        DiscordUtils.getInstance().getJda().addEventListener(new ShowLeaderboard());
    }
    private static void registerSlashCommand(){
        DiscordUtils.getInstance().getCommands().add(
                Commands.slash("test", "Show leaderboard")
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "board", "Give a board name", true));
    }


    public static void loadModule(){
        loadConfig();
        loadCommands();
        loadEvent();
        registerSlashCommand();

    }


}
