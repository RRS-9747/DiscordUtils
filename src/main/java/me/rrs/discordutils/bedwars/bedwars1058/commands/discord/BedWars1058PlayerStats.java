package me.rrs.discordutils.bedwars.bedwars1058.commands.discord;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.levels.Level;
import dev.dejvokep.boostedyaml.YamlDocument;
import github.scarsz.discordsrv.DiscordSRV;
import me.clip.placeholderapi.PlaceholderAPI;
import me.rrs.discordutils.bedwars.bedwars1058.BedWars1058Core;
import me.rrs.discordutils.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Map;
import java.util.UUID;

public class BedWars1058PlayerStats extends ListenerAdapter {

    private final BedWars bedwarsAPI =
            Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

    private final BedWars.IStats statUtil = bedwarsAPI.getStatsUtil();
    private final Level levelUtil = bedwarsAPI.getLevelsUtil();
    private final YamlDocument config = BedWars1058Core.getConfig();
    private final Utils utils = new Utils();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (!event.getName().equalsIgnoreCase(config.getString("Command"))) {
            return;
        }

        // Resolve player via Utils (user / name / invoking user)
        OfflinePlayer player = utils.resolvePlayer(event);
        if (player == null) {
            return; // Utils already replied
        }

        if (!player.hasPlayedBefore()) {
            event.reply("``" + player.getName() + "`` has not played on the server before.")
                    .setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        String level, bedDestroyed, death, finalKills, finalDeath,
                totalPlayed, kills, loses, win, winStreak, bestWinStreak;

        if (player.isOnline()) {
            Player online = (Player) player;

            level = String.valueOf(levelUtil.getPlayerLevel(online));
            bedDestroyed = String.valueOf(statUtil.getPlayerBedsDestroyed(player.getUniqueId()));
            death = String.valueOf(statUtil.getPlayerDeaths(player.getUniqueId()));
            finalKills = String.valueOf(statUtil.getPlayerFinalKills(player.getUniqueId()));
            finalDeath = String.valueOf(statUtil.getPlayerFinalDeaths(player.getUniqueId()));
            totalPlayed = String.valueOf(statUtil.getPlayerGamesPlayed(player.getUniqueId()));
            kills = String.valueOf(statUtil.getPlayerKills(player.getUniqueId()));
            loses = String.valueOf(statUtil.getPlayerLoses(player.getUniqueId()));
            win = String.valueOf(statUtil.getPlayerWins(player.getUniqueId()));
            winStreak = PlaceholderAPI.setPlaceholders(player, "%winstreak_streak%");
            bestWinStreak = PlaceholderAPI.setPlaceholders(player, "%winstreak_best_streak%");
        } else {
            Map<String, Integer> stats =
                    BedWars1058Core.getDatabase().getStats(player.getName());

            level = String.valueOf(stats.getOrDefault("level", 0));
            bedDestroyed = String.valueOf(stats.getOrDefault("bedDestroyed", 0));
            death = String.valueOf(stats.getOrDefault("death", 0));
            finalKills = String.valueOf(stats.getOrDefault("finalKills", 0));
            finalDeath = String.valueOf(stats.getOrDefault("finalDeath", 0));
            totalPlayed = String.valueOf(stats.getOrDefault("totalPlayed", 0));
            kills = String.valueOf(stats.getOrDefault("kills", 0));
            loses = String.valueOf(stats.getOrDefault("loses", 0));
            win = String.valueOf(stats.getOrDefault("win", 0));
            winStreak = String.valueOf(stats.getOrDefault("winStreak", 0));
            bestWinStreak = String.valueOf(stats.getOrDefault("bestWinStreak", 0));
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(config.getString("Embed.Title")
                        .replace("{PLAYER}", player.getName()))
                .setThumbnail(config.getString("Embed.Thumbnail")
                        .replace("{PLAYER}", player.getName())
                        .replace("{UUID}", player.getUniqueId().toString()))
                .setColor(Color.getColor(config.getString("Embed.Color")));

        if (config.getBoolean("Stats.Level.Enable"))
            builder.addField(config.getString("Stats.Level.Name"),
                    config.getString("Stats.Level.Value").replace("{LEVEL}", level), true);

        if (config.getBoolean("Stats.BedDestroyed.Enable"))
            builder.addField(config.getString("Stats.BedDestroyed.Name"),
                    config.getString("Stats.BedDestroyed.Value")
                            .replace("{BEDDESTROYED}", bedDestroyed), true);

        if (config.getBoolean("Stats.GamePlayed.Enable"))
            builder.addField(config.getString("Stats.GamePlayed.Name"),
                    config.getString("Stats.GamePlayed.Value")
                            .replace("{GAMEPLAYED}", totalPlayed), true);

        if (config.getBoolean("Stats.Kills.Enable"))
            builder.addField(config.getString("Stats.Kills.Name"),
                    config.getString("Stats.Kills.Value").replace("{KILLS}", kills), true);

        if (config.getBoolean("Stats.Death.Enable"))
            builder.addField(config.getString("Stats.Death.Name"),
                    config.getString("Stats.Death.Value").replace("{DEATH}", death), true);

        if (config.getBoolean("Stats.Win.Enable"))
            builder.addField(config.getString("Stats.Win.Name"),
                    config.getString("Stats.Win.Value").replace("{WIN}", win), true);

        if (config.getBoolean("Stats.FinalKills.Enable"))
            builder.addField(config.getString("Stats.FinalKills.Name"),
                    config.getString("Stats.FinalKills.Value")
                            .replace("{FINALKILLS}", finalKills), true);

        if (config.getBoolean("Stats.FinalDeath.Enable"))
            builder.addField(config.getString("Stats.FinalDeath.Name"),
                    config.getString("Stats.FinalDeath.Value")
                            .replace("{FINALDEATH}", finalDeath), true);

        if (config.getBoolean("Stats.Losses.Enable"))
            builder.addField(config.getString("Stats.Losses.Name"),
                    config.getString("Stats.Losses.Value").replace("{LOSSES}", loses), true);

        if (config.getBoolean("Stats.WinStreak.Enable")
                && Bukkit.getPluginManager().isPluginEnabled("BedWars1058-WinStreak"))
            builder.addField(config.getString("Stats.WinStreak.Name"),
                    config.getString("Stats.WinStreak.Value")
                            .replace("{WINSTREAK}", winStreak), true);

        if (config.getBoolean("Stats.BestWinStreak.Enable")
                && Bukkit.getPluginManager().isPluginEnabled("BedWars1058-WinStreak"))
            builder.addField(config.getString("Stats.BestWinStreak.Name"),
                    config.getString("Stats.BestWinStreak.Value")
                            .replace("{BESTWINSTREAK}", bestWinStreak), true);

        event.getHook().editOriginalEmbeds(builder.build()).queue();
    }
}
