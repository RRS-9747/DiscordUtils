package me.rrs.discordutils.profile.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.rrs.discordutils.profile.command.ProfileCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EditPAPICommand implements CommandExecutor, TabCompleter {

    private final List<String> list = Arrays.asList("title", "papi");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("discordstats.admin")){
                player.sendMessage(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "You don't have permission to use this command!");
                return true;
            }
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Please provide a field to update, old value and the new value.");
                return false;
            }
            String field = args[0];
            String searchValue = args[1];
            String newValue = args[2];
            switch (field.toLowerCase()) {
                case "title":
                    if (ProfileCore.getDatabase().updatePAPITitle(newValue, searchValue)){
                        player.sendMessage(String.format(ChatColor.GREEN + "[DiscordStats] " + ChatColor.RESET + "Successfully updated title %s with: %s", searchValue, newValue));
                    }else player.sendMessage(String.format(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Error updating title " + searchValue));
                    break;
                case "papi":
                    if (!(newValue.startsWith("%") && newValue.endsWith("%"))) {
                        player.sendMessage(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Placeholder must start and end with '%' sign");
                        return false;
                    }
                    if (ProfileCore.getDatabase().updatePAPIString(newValue, searchValue)){
                        player.sendMessage(String.format(ChatColor.GREEN + "[DiscordStats] " + ChatColor.RESET + "Successfully updated %s for: %s", searchValue, newValue));
                    }else player.sendMessage(String.format(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Error updating Placeholder!"));
                    break;
                default:
                    player.sendMessage(ChatColor.RED + "[DiscordStats] " + ChatColor.RESET + "Invalid field provided, valid fields are title, papi");
                    return false;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> tabCompletions = new ArrayList<>();

        if (args.length == 1) {
            for (String option : list) {
                if (option.toLowerCase().startsWith(args[0].toLowerCase())) {
                    tabCompletions.add(option);
                }
            }
        } else if (args.length == 2 && ("papi".equalsIgnoreCase(args[0]) || "title".equalsIgnoreCase(args[0]))) {
            List<String> papiTitles = ProfileCore.getDatabase().getPAPITitles();
            tabCompletions.addAll(papiTitles);
        }
        return tabCompletions;
    }

}
