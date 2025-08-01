package me.rrs.discordutils.profile.command.discord;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.clip.placeholderapi.PlaceholderAPI;
import me.rrs.discordutils.profile.ProfileCore;
import me.rrs.discordutils.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.time.Instant;
import java.util.Map;

public class StatsCommand extends ListenerAdapter {

    private final YamlDocument config = ProfileCore.getConfig();
    private final Utils utils = new Utils();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase(config.getString("Command"))) return;

        OfflinePlayer player = resolvePlayer(event);
        if (player == null) return;

        if (!player.hasPlayedBefore()) {
            event.reply("``" + player.getName() + "`` has not played on the server before.")
                    .setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();
        EmbedBuilder embed = buildEmbed(player, event.getUser().getName());
        event.getHook().editOriginalEmbeds(embed.build()).queue();
    }

    private OfflinePlayer resolvePlayer(SlashCommandInteractionEvent event) {
        OptionMapping userOption = event.getOption("user");
        OptionMapping nameOption = event.getOption("name");

        if (userOption != null) {
            User mentionedUser = userOption.getAsUser();
            if (mentionedUser.isBot()) {
                event.reply("``" + mentionedUser.getName() + "`` is a bot!")
                        .setEphemeral(true).queue();
                return null;
            }

            OfflinePlayer linked = utils.getPlayerFromDiscord(mentionedUser.getId());
            if (linked == null) {
                event.reply("``" + mentionedUser.getName() + "`` is not linked to a player.")
                        .setEphemeral(true).queue();
            }
            return linked;
        }

        if (nameOption != null) {
            return Bukkit.getOfflinePlayer(nameOption.getAsString());
        }

        // Default to invoking user
        OfflinePlayer linked = utils.getPlayerFromDiscord(event.getUser().getId());
        if (linked == null) {
            event.reply("Link your account!").setEphemeral(true).queue();
        }

        return linked;
    }

    private EmbedBuilder buildEmbed(OfflinePlayer player, String authorName) {
        EmbedBuilder builder = new EmbedBuilder();

        String playerName = player.getName();
        String uuid = player.getUniqueId().toString();

        // Strip colors from templates to prevent reintroducing color codes
        String title = stripColors(config.getString("Embed.Title").replace("{PLAYER}", playerName));
        String thumbnail = stripColors(config.getString("Embed.Thumbnail")
                .replace("{PLAYER}", playerName)
                .replace("{UUID}", uuid));
        builder.setTitle(title);
        builder.setThumbnail(thumbnail);
        builder.setAuthor(player.isOnline() ? "🟢 Online" : "🔴 Offline", null, null);
        builder.setColor(player.isOnline() ? Color.GREEN : Color.RED);

        Map<String, String> papiTitles = ProfileCore.getDatabase().getAllPAPITitles();
        String separator = config.getString("Embed.Separator");
        String emptyField = config.getString("Embed.Empty-Field");
        boolean inline = config.getBoolean("Embed.Field.InLine");
        String titleTemplate = stripColors(config.getString("Embed.Field.Title"));
        String valueTemplate = stripColors(config.getString("Embed.Field.Value"));

        for (Map.Entry<String, String> entry : papiTitles.entrySet()) {
            String titleKey = entry.getKey().replace(separator, " ");
            String rawValue = entry.getValue();
            String placeholder = PlaceholderAPI.setPlaceholders(player, rawValue);

            String stripped = stripColors(placeholder);
            String finalValue = stripped;
            if (finalValue.isEmpty() || finalValue.matches("^%.*%$")) {
                finalValue = emptyField;
            }

            String finalTitle = titleTemplate.replace("{TITLE}", titleKey);
            String finalFieldValue = valueTemplate.replace("{VALUE}", finalValue);
            builder.addField(finalTitle, finalFieldValue, inline);
        }

        if (config.getBoolean("Embed.Footer.Timestamp")) {
            builder.setTimestamp(Instant.now());
        }

        builder.setFooter(config.getString("Embed.Footer.Text").replace("{AUTHOR}", authorName), null);
        return builder;
    }

    public static String stripColors(String input) {
        if (input == null) return null;
        return input.replaceAll("[\\u0026\\u00A7][0-9a-fA-Fk-or]|[\\u0026\\u00A7]#[0-9a-fA-F]{6}", "");
    }
}