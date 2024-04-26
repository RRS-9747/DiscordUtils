package me.rrs.discordutils.profile.command.minecraft;

import me.rrs.discordutils.profile.ProfileCore;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetupCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length != 0) {

            switch (args[0].toLowerCase()) {

                case "add":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;

                        if (!player.hasPermission("discordutils.admin")) {
                            player.spigot().sendMessage(new TextComponent(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "You don't have permission to use this command!"));
                            return true;
                        }
                    }

                        if (args.length != 3) {
                            sender.spigot().sendMessage(new TextComponent(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Usage: /profile add <Title> <Placeholder>"));
                            return true;
                        }

                        if (!args[2].contains("%")) {
                            sender.spigot().sendMessage(new TextComponent(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "This is not a valid placeholder"));
                            return true;
                        }

                        if (ProfileCore.getDatabase().createPAPITitle(args[1], args[2])){
                            sender.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "[DiscordStats] " + ChatColor.RESET + "Title and Placeholder have been added successfully."));
                        }else sender.spigot().sendMessage(new TextComponent(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Title already exists."));


                    break;
                case "edit":
                    if (sender instanceof Player) { //For player
                        Player player = (Player) sender;
                        if (!player.hasPermission("discordutils.admin")){
                            player.spigot().sendMessage(new TextComponent(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "You don't have permission to use this command!"));
                            return true;
                        }
                        if (args.length < 4) {
                            player.spigot().sendMessage(new TextComponent(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Please provide a field to update, old value and the new value."));
                            return false;
                        }
                        String field = args[1];
                        String searchValue = args[2];
                        String newValue = args[3];
                        switch (field.toLowerCase()) {
                            case "title":
                                if (ProfileCore.getDatabase().updatePAPITitle(newValue, searchValue)){
                                    player.spigot().sendMessage(new TextComponent(String.format(ChatColor.GREEN + "[DiscordStats] " + ChatColor.RESET + "Successfully updated title %s with: %s", searchValue, newValue)));
                                }else player.spigot().sendMessage(new TextComponent(String.format(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Error updating title " + searchValue)));
                                break;
                            case "papi":
                                if (!newValue.contains("%")) {
                                    player.spigot().sendMessage(new TextComponent(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "This is not a valid placeholder"));
                                    return false;
                                }
                                if (ProfileCore.getDatabase().updatePAPIString(newValue, searchValue)){
                                    player.spigot().sendMessage(new TextComponent(String.format(ChatColor.GREEN + "[DiscordStats] " + ChatColor.RESET + "Successfully updated %s for: %s", searchValue, newValue)));
                                }else player.spigot().sendMessage(new TextComponent(String.format(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Error updating Placeholder!")));
                                break;
                            default:
                                player.spigot().sendMessage(new TextComponent(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Invalid field provided, valid fields are title & Placeholder"));
                                return false;
                        }
                    }

                    break;
                case "remove":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (!player.hasPermission("discordutils.admin")){
                            player.spigot().sendMessage(new TextComponent(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "You don't have permission to use this command!"));
                            return true;
                        }

                        if (args.length == 1) {
                            sender.spigot().sendMessage(new TextComponent(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Please provide a Title name to remove."));
                            return false;
                        }

                        if (ProfileCore.getDatabase().removePAPITitle(args[1])){
                            sender.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "[DiscordStats] " + ChatColor.RESET + args[1] +"Removed Successfully"));
                        }else sender.spigot().sendMessage(new TextComponent(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Error removing" + args[1]));
                    }
                    break;
                case "preset":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (!player.hasPermission("discordutils.admin")) {
                            player.spigot().sendMessage(new TextComponent(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "You don't have permission to use this command!"));
                            return true;
                        }
                    }
                    switch (args[1]){
                        case "1":
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "profile add Rank %vault_prefix%");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "profile add Exp %player_level%");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "profile add Balance %vault_eco_balance_formatted%");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "profile add Ping %player_ping%");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "profile add Play" + ProfileCore.getConfig().getString("Embed.Separator") + "Time %player_level%");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "profile add Deaths %statistic_deaths%");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "profile add Mob" + ProfileCore.getConfig().getString("Embed.Separator") + "Killed %statistic_mob_kills%");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "profile add Block" + ProfileCore.getConfig().getString("Embed.Separator") + "Mined %statistic_mine_block%");
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "profile add Player" + ProfileCore.getConfig().getString("Embed.Separator") + "Killed %statistic_player_kills%");
                    }
                    break;

            }

        }

        return true;
    }



    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("add", "edit", "remove", "preset");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("preset")) {
            return Arrays.asList("1");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("edit")) {
            return Arrays.asList("title", "papi");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("edit") && (args[1].equalsIgnoreCase("title") || args[1].equalsIgnoreCase("papi"))) {
            return ProfileCore.getDatabase().getPAPITitles();
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            return ProfileCore.getDatabase().getPAPITitles();
        }
        return Collections.emptyList();
    }
}
