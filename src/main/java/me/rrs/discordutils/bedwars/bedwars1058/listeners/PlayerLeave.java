package me.rrs.discordutils.bedwars.bedwars1058.listeners;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.levels.Level;
import me.clip.placeholderapi.PlaceholderAPI;
import me.rrs.discordutils.bedwars.bedwars1058.BedwarsCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {

    final BedWars bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();
    final BedWars.IStats statUtil = bedwarsAPI.getStatsUtil();
    final Level levelUtil = bedwarsAPI.getLevelsUtil();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        int level = levelUtil.getPlayerLevel(player);
        int bedDestroyed = statUtil.getPlayerBedsDestroyed(player.getUniqueId());
        int death = statUtil.getPlayerDeaths(player.getUniqueId());
        int finalKills = statUtil.getPlayerFinalKills(player.getUniqueId());
        int finalDeath =  statUtil.getPlayerFinalDeaths(player.getUniqueId());
        int totalPlayed = statUtil.getPlayerGamesPlayed(player.getUniqueId());
        int kills =  statUtil.getPlayerKills(player.getUniqueId());
        int loses = statUtil.getPlayerLoses(player.getUniqueId());
        int win = statUtil.getPlayerWins(player.getUniqueId());
        int winStreak = Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%winstreak_streak%"));
        int bestWinStreak = Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%winstreak_best_streak%"));
        BedwarsCore.getDatabase().updateStats(player.getName(), bedDestroyed, death, finalKills, finalDeath, totalPlayed, kills, loses, win, level, winStreak, bestWinStreak);
    }

}
