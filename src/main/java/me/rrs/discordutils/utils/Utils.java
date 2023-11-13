package me.rrs.discordutils.utils;

import github.scarsz.discordsrv.DiscordSRV;
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
}
