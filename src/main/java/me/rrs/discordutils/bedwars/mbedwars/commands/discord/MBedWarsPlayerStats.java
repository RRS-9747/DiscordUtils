package me.rrs.discordutils.bedwars.mbedwars.commands.discord;

import de.marcely.bedwars.api.player.DefaultPlayerStatSet;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import dev.dejvokep.boostedyaml.YamlDocument;
import github.scarsz.discordsrv.DiscordSRV;
import me.rrs.discordutils.bedwars.mbedwars.MBedWarsCore;
import me.rrs.discordutils.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.Optional;
import java.util.UUID;

public class MBedWarsPlayerStats extends ListenerAdapter {

    private final Utils utils = new Utils();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        final YamlDocument config = MBedWarsCore.getConfig();

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

        PlayerDataAPI.get().getStats(player, stats -> {
            if (config.getBoolean("Stats.Rank.Enable"))
                builder.addField(config.getString("Stats.Rank.Name"), config.getString("Stats.Rank.Value").replace("{RANK}", DefaultPlayerStatSet.RANK.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.BedDestroyed.Enable"))
                builder.addField(config.getString("Stats.BedDestroyed.Name"), config.getString("Stats.BedDestroyed.Value").replace("{BEDDESTROYED}", DefaultPlayerStatSet.BEDS_DESTROYED.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.BedsLost.Enable"))
                builder.addField(config.getString("Stats.BedsLost.Name"), config.getString("Stats.BedsLost.Value").replace("{BEDSLOST}", DefaultPlayerStatSet.BEDS_LOST.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.Kills.Enable"))
                builder.addField(config.getString("Stats.Kills.Name"), config.getString("Stats.Kills.Value").replace("{KILLS}", DefaultPlayerStatSet.KILLS.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.FinalKills.Enable"))
                builder.addField(config.getString("Stats.FinalKills.Name"), config.getString("Stats.FinalKills.Value").replace("{FINALKILLS}", DefaultPlayerStatSet.FINAL_KILLS.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.KillStreak.Enable"))
                builder.addField(config.getString("Stats.KillStreak.Name"), config.getString("Stats.KillStreak.Value").replace("{KILLSTREAK}", DefaultPlayerStatSet.KILL_STREAK.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.TopKillStreak.Enable"))
                builder.addField(config.getString("Stats.TopKillStreak.Name"), config.getString("Stats.TopKillStreak.Value").replace("{TOPKILLSTREAK}", DefaultPlayerStatSet.TOP_KILL_STREAK.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.Death.Enable"))
                builder.addField(config.getString("Stats.Death.Name"), config.getString("Stats.Death.Value").replace("{DEATH}", DefaultPlayerStatSet.DEATHS.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.FinalDeath.Enable"))
                builder.addField(config.getString("Stats.FinalDeath.Name"), config.getString("Stats.FinalDeath.Value").replace("{FINALDEATH}", DefaultPlayerStatSet.FINAL_DEATHS.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.Win.Enable"))
                builder.addField(config.getString("Stats.Win.Name"), config.getString("Stats.Win.Value").replace("{WIN}", DefaultPlayerStatSet.WINS.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.WinStreak.Enable"))
                builder.addField(config.getString("Stats.WinStreak.Name"), config.getString("Stats.WinStreak.Value").replace("{WINSTREAK}", DefaultPlayerStatSet.WIN_STREAK.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.TopWinStreak.Enable"))
                builder.addField(config.getString("Stats.TopWinStreak.Name"), config.getString("Stats.TopWinStreak.Value").replace("{TOPWINSTREAK}", DefaultPlayerStatSet.TOP_WIN_STREAK.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.Losses.Enable"))
                builder.addField(config.getString("Stats.Losses.Name"), config.getString("Stats.Losses.Value").replace("{LOSSES}", DefaultPlayerStatSet.LOSES.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.RoundsPlayed.Enable"))
                builder.addField(config.getString("Stats.RoundsPlayed.Name"), config.getString("Stats.RoundsPlayed.Value").replace("{ROUNDSPLAYED}", DefaultPlayerStatSet.ROUNDS_PLAYED.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.PlayTime.Enable"))
                builder.addField(config.getString("Stats.PlayTime.Name"), config.getString("Stats.PlayTime.Value").replace("{PLAYTIME}", DefaultPlayerStatSet.PLAY_TIME.getDisplayedValue(stats)), true);
            if (config.getBoolean("Stats.WL.Enable"))
                builder.addField(config.getString("Stats.WL.Name"), config.getString("Stats.WL.Value").replace("{WL}", DefaultPlayerStatSet.W_L.getDisplayedValue(stats)), true);
            event.getHook().editOriginalEmbeds(builder.build()).queue();
        });
    }
}
