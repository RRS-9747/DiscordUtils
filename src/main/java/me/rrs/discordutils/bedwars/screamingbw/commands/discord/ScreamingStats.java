package me.rrs.discordutils.bedwars.screamingbw.commands.discord;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.rrs.discordutils.bedwars.screamingbw.ScreamingCore;
import me.rrs.discordutils.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;
import org.screamingsandals.bedwars.api.statistics.PlayerStatisticsManager;

import java.awt.*;

public class ScreamingStats extends ListenerAdapter {

    private final Utils utils = new Utils();

    PlayerStatisticsManager manager = BedwarsAPI.getInstance().getStatisticsManager();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        final YamlDocument config = ScreamingCore.getConfig();

        if (event.getName().equals(config.getString("Command"))){

            OfflinePlayer player;

            if (event.getOption("user") != null) {
                User mentionedUser = event.getOption("user").getAsUser();
                if (mentionedUser.isBot()){
                    event.reply("``" + mentionedUser.getName() + "``" + " is a bot!").setEphemeral(true).queue();
                    return;
                }

                String discordId = mentionedUser.getId();
                player = utils.getPlayerFromDiscord(discordId);

                if (player == null) {
                    event.reply("``" + mentionedUser.getName() + "``" + " is not linked to a player").setEphemeral(true).queue();
                    return;
                }
            } else if (event.getOption("name") != null) {
                String name = event.getOption("name").getAsString();
                player = Bukkit.getOfflinePlayer(name);
            } else {
                String discordId = event.getUser().getId();
                player = utils.getPlayerFromDiscord(discordId);

                if (player == null) {
                    event.reply("Link your account!").setEphemeral(true).queue();
                    return;
                }
            }

            if (!player.hasPlayedBefore()) {
                event.reply("`" + player.getName() + "` has not played on the server before.").setEphemeral(true).queue();
                return;
            }
            event.deferReply().queue();
            displayPlayerStats(event, player, config);
        }
    }

    private void displayPlayerStats(SlashCommandInteractionEvent event, OfflinePlayer player, YamlDocument config) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(config.getString("Embed.Title").replace("{PLAYER}", player.getName()));
        builder.setThumbnail(config.getString("Embed.Thumbnail")
                .replace("{PLAYER}", player.getName())
                .replace("{UUID}", player.getUniqueId().toString()));
        builder.setColor(Color.getColor(config.getString("Embed.Color")));
        PlayerStatistic statistic = manager.loadStatistic(player.getUniqueId());
            if (config.getBoolean("Stats.Kills.Enable"))
                builder.addField(config.getString("Stats.Kills.Name"), config.getString("Stats.Kills.Value").replace("{KILLS}", String.valueOf(statistic.getKills())), true);
            if (config.getBoolean("Stats.Death.Enable"))
                builder.addField(config.getString("Stats.Death.Name"), config.getString("Stats.Death.Value").replace("{DEATH}", String.valueOf(statistic.getDeaths())), true);
            if (config.getBoolean("Stats.KD.Enable"))
                builder.addField(config.getString("Stats.KD.Name"), config.getString("Stats.KD.Value").replace("{KD}", String.valueOf(statistic.getKD())), true);
            if (config.getBoolean("Stats.Win.Enable"))
                builder.addField(config.getString("Stats.Win.Name"), config.getString("Stats.Win.Value").replace("{WIN}", String.valueOf(statistic.getWins())), true);
            if (config.getBoolean("Stats.Losses.Enable"))
                builder.addField(config.getString("Stats.Losses.Name"), config.getString("Stats.Losses.Value").replace("{LOSSES}", String.valueOf(statistic.getLoses())), true);
            if (config.getBoolean("Stats.RoundsPlayed.Enable"))
                builder.addField(config.getString("Stats.RoundsPlayed.Name"), config.getString("Stats.RoundsPlayed.Value").replace("{ROUNDSPLAYED}", String.valueOf(statistic.getGames())), true);
            if (config.getBoolean("Stats.BedDestroyed.Enable"))
                builder.addField(config.getString("Stats.BedDestroyed.Name"), config.getString("Stats.BedDestroyed.Value").replace("{BEDDESTROYED}", String.valueOf(statistic.getDestroyedBeds())), true);
            if (config.getBoolean("Stats.Score.Enable"))
                builder.addField(config.getString("Stats.Score.Name"), config.getString("Stats.Score.Value").replace("{SCORE}", String.valueOf(statistic.getScore())), true);
            event.getHook().editOriginalEmbeds(builder.build()).queue();
    }



}
