package me.rrs.discordutils.profile.command.discord;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.clip.placeholderapi.PlaceholderAPI;
import me.rrs.discordutils.profile.ProfileCore;
import me.rrs.discordutils.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
                event.reply("``" + player.getName() + "``" + " has not played on the server before.").setEphemeral(true).queue();
                return;
            }
            event.deferReply().queue();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(config.getString("Embed.Title").replace("{PLAYER}", player.getName()));
            builder.setThumbnail(config.getString("Embed.Thumbnail")
                    .replace("{PLAYER}", player.getName())
                    .replace("{UUID}", player.getUniqueId().toString()));
            builder.setAuthor((player.isOnline() ? "🟢 Online" : "🔴 Offline"), null);
            builder.setColor(player.isOnline() ? Color.GREEN : Color.RED);

            Map<String, String> allPAPITitles = ProfileCore.getDatabase().getAllPAPITitles();
            for (Map.Entry<String, String> entry : allPAPITitles.entrySet()) {
                String PAPITitle = entry.getKey();
                String PAPIString = entry.getValue();
                PAPIString = PAPIString.replaceAll("(?:#([0-9A-Fa-f]{6})|([&§][0-9A-Fa-fK-Ok-oRr]))", "");
                PAPIString = PlaceholderAPI.setPlaceholders(player, PAPIString);
                if (PAPIString.isEmpty() || PAPIString.startsWith("%") && PAPIString.endsWith("%")) {
                    PAPIString = config.getString("Embed.Empty-Field");
                }
                if (PAPITitle.contains(config.getString("Embed.Separator"))) {
                    PAPITitle = PAPITitle.replace(config.getString("Embed.Separator"), " ");
                }

                builder.addField(config.getString("Embed.Field.Title").replace("{TITLE}", PAPITitle),
                        config.getString("Embed.Field.Value").replace("{VALUE}", PAPIString),
                        config.getBoolean("Embed.Field.InLine"));
            }


            if (config.getBoolean("Embed.Footer.Timestamp")){
                builder.setTimestamp(Instant.now());
            }
            String author = event.getUser().getName();
            builder.setFooter(config.getString("Embed.Footer.Text").replace("{AUTHOR}", author), null);
            event.getHook().editOriginalEmbeds(builder.build()).queue();
        }
    }

}
