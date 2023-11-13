package me.rrs.discordutils.bedwars.mbedwars;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.segment.Segment;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.rrs.discordutils.DiscordUtils;
import me.rrs.discordutils.bedwars.bedwars1058.commands.discord.BedWars1058PlayerStats;
import me.rrs.discordutils.bedwars.mbedwars.commands.discord.MBedWarsPlayerStats;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class MBedWarsCore {

    private static YamlDocument config;
    public static YamlDocument getConfig() {
        return config;
    }


    private static void loadConfig(){
        try {
            config = YamlDocument.create(new File(DiscordUtils.getInstance().getModulesFolder(), "bedwars.yml"), DiscordUtils.getInstance().getResource("mbedwars.yml"),
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

    private static void registerEvent(){
        DiscordUtils.getInstance().getJda().addEventListener(new MBedWarsPlayerStats());
    }

    private static void registerSlashCommand(){
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("DiscordSRV") ||
                Bukkit.getServer().getPluginManager().isPluginEnabled("EssentialsDiscordLink")){
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
        registerEvent();
        registerSlashCommand();
    }
}
