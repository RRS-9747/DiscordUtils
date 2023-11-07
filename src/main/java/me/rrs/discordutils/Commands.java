package me.rrs.discordutils;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {


        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', DiscordUtils.getInstance().getName() + " by RRS"));

        } else {
            switch (args[0].toLowerCase()) {
                case "reload":
                    for (YamlDocument config : DiscordUtils.getInstance().getReloadList()){
                        try {
                            config.reload();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    sender.sendMessage("[DiscordUtils] Plugin Reloaded!");

                case "help":
                case "debug":

            }
        }
        return true;
    }


    final List< String > results = new ArrayList< >();
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String alias, @NotNull String[] args) {

        if (cmd.getName().equals("discordutils") && args.length ==1){
            results.clear();
            results.add("reload");
        }
        return sortedResults(args[0]);
    }

    public List < String > sortedResults(String arg) {
        final List < String > completions = new ArrayList < > ();
        StringUtil.copyPartialMatches(arg, results, completions);
        Collections.sort(completions);
        results.clear();
        results.addAll(completions);
        return results;
    }
}
