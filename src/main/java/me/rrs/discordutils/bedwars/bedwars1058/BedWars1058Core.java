package me.rrs.discordutils.bedwars.bedwars1058;


import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.segment.Segment;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.rrs.discordutils.DiscordUtils;
import me.rrs.discordutils.bedwars.bedwars1058.commands.discord.BedWars1058PlayerStats;
import me.rrs.discordutils.bedwars.bedwars1058.listeners.PlayerLeave;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class BedWars1058Core {
    private static Database database;
    private static YamlDocument config;

    public static Database getDatabase() {
        return database;
    }
    public static YamlDocument getConfig() {
        return config;
    }


    private static void loadConfig(){
        try {
            config = YamlDocument.create(new File(DiscordUtils.getInstance().getModulesFolder(), "bedwars.yml"), DiscordUtils.getInstance().getResource("bedwars1058.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setAutoSave(true).setVersioning(new Pattern(Segment.range(1, Integer.MAX_VALUE),
                            Segment.literal("."), Segment.range(0, 100)), "Version").build());
            DiscordUtils.getInstance().getReloadList().add(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadDatabase(){
        database = new Database();
        database.createTable();
    }

    private static void registerEvent(){
        Bukkit.getPluginManager().registerEvents(new PlayerLeave(), DiscordUtils.getInstance());
        DiscordUtils.getInstance().getJda().addEventListener(new BedWars1058PlayerStats());
    }

    private static void registerSlashCommand(){

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("DiscordSRV")){
            DiscordUtils.getInstance().getCommands().add(
                    Commands.slash(config.getString("Command"), "Show player stats")
                            .setGuildOnly(true)
                            .addOption(OptionType.USER, "user", "Give a user", false)
                            .addOption(OptionType.STRING, "name", "Give a user name", false));
        }else {
            DiscordUtils.getInstance().getCommands().add(
                    Commands.slash(config.getString("Command"), "Show player stats")
                            .setGuildOnly(true)
                            .addOption(OptionType.STRING, "name", "Give a user name", true));

        }
    }

    public static void loadModule(){
        loadConfig();
        loadDatabase();
        registerEvent();
        registerSlashCommand();
    }

}
