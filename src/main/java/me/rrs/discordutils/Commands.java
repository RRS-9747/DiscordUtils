package me.rrs.discordutils;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {


        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', DiscordUtils.getInstance().getName() + " by RRS"));

        } else {
            switch (args[0].toLowerCase()) {
                case "reload":
                    if (sender instanceof Player){
                        Player player = (Player) sender;
                        if (!player.hasPermission("discordutils.admin")){
                            player.spigot().sendMessage(new TextComponent(ChatColor.RED + "[DiscordUtils] " + ChatColor.RESET + "You don't have permission to use this command!"));
                            return true;
                        }
                    }
                    for (YamlDocument config : DiscordUtils.getInstance().getReloadList()){
                        try {
                            config.reload();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    sender.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "[DiscordUtils]" + ChatColor.RESET + "Plugin Reloaded!"));

                    break;
                case "help":
                    sender.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "[DiscordUtils] " + ChatColor.RESET + "/discordstats help -> You already discovered this!"));
                    sender.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "[DiscordUtils] " + ChatColor.RESET + "/profile add <Title> <Placeholder>"));
                    sender.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "[DiscordUtils] " + ChatColor.RESET + "/profile edit title|papi <Title> <New Value>"));
                    sender.spigot().sendMessage(new TextComponent(ChatColor.GREEN + "[DiscordUtils] " + ChatColor.RESET + "/profile remove <Title>"));

                    break;
                case "debug":

            }
        }
        return true;
    }



    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

        if (cmd.getName().equals("discordutils") && args.length == 1) {
            return Arrays.asList("reload", "help");
        }
        return Collections.emptyList();
    }
}
