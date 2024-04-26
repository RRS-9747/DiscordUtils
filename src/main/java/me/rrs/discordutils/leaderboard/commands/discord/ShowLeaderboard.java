package me.rrs.discordutils.leaderboard.commands.discord;

import com.google.gson.JsonObject;
import me.rrs.discordutils.leaderboard.LeaderboardCore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import us.ajg0702.leaderboards.boards.TimedType;

import java.awt.*;

public class ShowLeaderboard extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("test") && event.getOption("board") != null) {
            String board = event.getOption("board").getAsString();

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Leaderboard - " + board)
                    .setColor(Color.GREEN); // You can change the color as per your preference

            StringBuilder leaderboardDescription = new StringBuilder();
            for (int i = 1; i <= 10; i++) {
                JsonObject json = LeaderboardCore.getajLeaderboards().getTopManager().getStat(i, board, TimedType.ALLTIME).toJsonObject();
                String playerName = LeaderboardCore.getajLeaderboards().getTopManager().getStat(i, board, TimedType.ALLTIME).getPlayerName();
                String score = LeaderboardCore.getajLeaderboards().getTopManager().getStat(i, board, TimedType.ALLTIME).getScoreFormatted();
                leaderboardDescription.append(":crown: ").append("**").append(playerName).append("**").append(" - ").append(score).append("\n");
            }

            embedBuilder.setDescription(leaderboardDescription.toString())
                    .setFooter("Top 10 players", null);

            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }





}
