package me.rrs.discordutils.profile.command.minecraft;

import me.rrs.discordutils.profile.command.ProfileCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetPAPICommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("discordstats.admin")) {
                player.sendMessage(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "You don't have permission to use this command!");
                return true;
            }
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Usage: /setpapi <PAPITitle> <PAPIString>");
                return true;
            }
            if (!args[1].startsWith("%") && !args[1].endsWith("%")) {
                player.sendMessage(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "The Placeholder must start and end with '%' sign.");
                return true;
            }

            if (ProfileCore.getDatabase().createPAPITitle(args[0], args[1])){
                player.sendMessage(ChatColor.GREEN + "[DiscordStats] " + ChatColor.RESET + "Title and Placeholder have been added successfully.");
            }else player.sendMessage(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Title already exists.");
        }


        return true;
    }
}
