package me.rrs.discordutils.utils;

import github.scarsz.discordsrv.DiscordSRV;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.essentialsx.api.v2.services.discordlink.DiscordLinkService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Utils {

    public OfflinePlayer getPlayerFromDiscord(String discordId){
        if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
            UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(discordId);
            return (uuid != null) ? Bukkit.getOfflinePlayer(uuid) : null;
        } else if (Bukkit.getPluginManager().isPluginEnabled("EssentialsDiscordLink")) {
            final DiscordLinkService linkApi = Bukkit.getServicesManager().load(DiscordLinkService.class);
            return Bukkit.getOfflinePlayer(linkApi.getUUID(discordId));
        }
        return null;
    }

    public OfflinePlayer getRightPlayer(SlashCommandInteractionEvent event) {
        OfflinePlayer player;

        if (event.getOption("user") != null) {
            User mentionedUser = event.getOption("user").getAsUser();
            if (mentionedUser.isBot()) {
                event.reply("``" + mentionedUser.getName() + "``" + " is a bot!").setEphemeral(true).queue();
                return null;
            }

            String discordId = mentionedUser.getId();
            player = getPlayerFromDiscord(discordId);

            if (player == null) {
                event.reply("``" + mentionedUser.getName() + "``" + " is not linked to a player").setEphemeral(true).queue();
                return null;
            }
        } else if (event.getOption("name") != null) {
            String name = event.getOption("name").getAsString();
            player = Bukkit.getOfflinePlayer(name);
        } else {
            String discordId = event.getUser().getId();
            player = getPlayerFromDiscord(discordId);
        }

        if (player == null) {
            event.reply("Link your account!").setEphemeral(true).queue();
            return null;
        }
        return player;
    }
}
