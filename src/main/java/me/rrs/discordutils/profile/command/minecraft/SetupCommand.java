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

    private static final String PREFIX = ChatColor.RED + "[DiscordStats] " + ChatColor.RESET;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "Usage: /profile <add|edit|remove|preset>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "add":
                return handleAdd(sender, args);

            case "edit":
                return handleEdit(sender, args);

            case "remove":
                return handleRemove(sender, args);

            case "preset":
                return handlePreset(sender, args);

            default:
                sendMessage(sender, "Unknown subcommand. Use add, edit, remove, or preset.");
                return true;
        }
    }

    private boolean handleAdd(CommandSender sender, String[] args) {
        if (!hasAdminPermission(sender)) return true;

        if (args.length != 3) {
            sendMessage(sender, "Usage: /profile add <Title> <Placeholder>");
            return true;
        }

        String title = args[1];
        String placeholder = args[2];

        if (!placeholder.contains("%")) {
            sendMessage(sender, "This is not a valid placeholder.");
            return true;
        }

        if (ProfileCore.getDatabase().createPAPITitle(title, placeholder)) {
            sendSuccess(sender, "Title and Placeholder have been added successfully.");
        } else {
            sendMessage(sender, "Title already exists.");
        }
        return true;
    }

    private boolean handleEdit(CommandSender sender, String[] args) {
        if (!hasAdminPermission(sender)) return true;

        if (args.length < 4) {
            sendMessage(sender, "Usage: /profile edit <title|papi> <oldValue> <newValue>");
            return true;
        }

        String field = args[1].toLowerCase();
        String searchValue = args[2];
        String newValue = args[3];

        switch (field) {
            case "title":
                if (ProfileCore.getDatabase().updatePAPITitle(newValue, searchValue)) {
                    sendSuccess(sender, String.format("Successfully updated title %s to %s.", searchValue, newValue));
                } else {
                    sendMessage(sender, String.format("Error updating title %s.", searchValue));
                }
                break;

            case "papi":
                if (!newValue.contains("%")) {
                    sendMessage(sender, "This is not a valid placeholder.");
                    return true;
                }
                if (ProfileCore.getDatabase().updatePAPIString(newValue, searchValue)) {
                    sendSuccess(sender, String.format("Successfully updated placeholder for %s to %s.", searchValue, newValue));
                } else {
                    sendMessage(sender, "Error updating placeholder.");
                }
                break;

            default:
                sendMessage(sender, "Invalid field. Valid fields are 'title' and 'papi'.");
        }
        return true;
    }

    private boolean handleRemove(CommandSender sender, String[] args) {
        if (!hasAdminPermission(sender)) return true;

        if (args.length < 2) {
            sendMessage(sender, "Please provide a Title name to remove.");
            return true;
        }

        String title = args[1];
        if (ProfileCore.getDatabase().removePAPITitle(title)) {
            sendSuccess(sender, title + " removed successfully.");
        } else {
            sendMessage(sender, "Error removing " + title + ".");
        }
        return true;
    }

    private boolean handlePreset(CommandSender sender, String[] args) {
        if (!hasAdminPermission(sender)) return true;

        if (args.length < 2 || !args[1].equals("1")) {
            sendMessage(sender, "Usage: /profile preset 1");
            return true;
        }

        String sep = ProfileCore.getConfig().getString("Embed.Separator");

        List<String> presets = Arrays.asList(
                "Rank %vault_prefix%",
                "Exp %player_level%",
                "Balance %vault_eco_balance_formatted%",
                "Ping %player_ping%",
                "Play" + sep + "Time %player_level%",
                "Deaths %statistic_deaths%",
                "Mob" + sep + "Killed %statistic_mob_kills%",
                "Block" + sep + "Mined %statistic_mine_block%",
                "Player" + sep + "Killed %statistic_player_kills%"
        );

        for (String preset : presets) {
            String[] split = preset.split(" ", 2);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "profile add " + split[0] + " " + split[1]);
        }

        sendSuccess(sender, "Preset 1 added.");
        return true;
    }

    private boolean hasAdminPermission(CommandSender sender) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (!player.hasPermission("discordutils.admin")) {
            sendMessage(sender, "You don't have permission to use this command!");
            return false;
        }
        return true;
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.spigot().sendMessage(new TextComponent(PREFIX + message));
    }

    private void sendSuccess(CommandSender sender, String message) {
        sender.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "[DiscordStats] " + ChatColor.RESET + message));
    }

    // -------------------- TAB COMPLETER --------------------
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("add", "edit", "remove", "preset");
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "preset":
                    return Collections.singletonList("1");
                case "edit":
                    return Arrays.asList("title", "papi");
                case "remove":
                    return ProfileCore.getDatabase().getPAPITitles();
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("edit") &&
                (args[1].equalsIgnoreCase("title") || args[1].equalsIgnoreCase("papi"))) {
            return ProfileCore.getDatabase().getPAPITitles();
        }

        return Collections.emptyList();
    }
}
