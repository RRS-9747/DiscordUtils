package me.rrs.discordutils.utils;

import github.scarsz.discordsrv.DiscordSRV;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.essentialsx.api.v2.services.discordlink.DiscordLinkService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Utils {

    private OfflinePlayer getPlayerFromDiscord(String discordId){
        if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
            UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(discordId);
            return (uuid != null) ? Bukkit.getOfflinePlayer(uuid) : null;
        } else if (Bukkit.getPluginManager().isPluginEnabled("EssentialsDiscordLink")) {
            final DiscordLinkService linkApi = Bukkit.getServicesManager().load(DiscordLinkService.class);
            return Bukkit.getOfflinePlayer(linkApi.getUUID(discordId));
        }
        return null;
    }

    public OfflinePlayer resolvePlayer(SlashCommandInteractionEvent event) {
        OptionMapping userOption = event.getOption("user");
        OptionMapping nameOption = event.getOption("name");

        if (userOption != null) {
            User mentionedUser = userOption.getAsUser();
            if (mentionedUser.isBot()) {
                event.reply("``" + mentionedUser.getName() + "`` is a bot!")
                        .setEphemeral(true).queue();
                return null;
            }

            OfflinePlayer linked = getPlayerFromDiscord(mentionedUser.getId());
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
        OfflinePlayer linked = getPlayerFromDiscord(event.getUser().getId());
        if (linked == null) {
            event.reply("Link your account!").setEphemeral(true).queue();
        }

        return linked;
    }
}
