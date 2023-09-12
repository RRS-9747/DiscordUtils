package me.rrs.discordutils.profile.command;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.segment.Segment;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.rrs.discordutils.DiscordUtils;
import me.rrs.discordutils.profile.command.discord.StatsCommand;
import me.rrs.discordutils.profile.command.minecraft.EditPAPICommand;
import me.rrs.discordutils.profile.command.minecraft.RemovePAPICommand;
import me.rrs.discordutils.profile.command.minecraft.SetPAPICommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class ProfileCore {

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
            config = YamlDocument.create(new File(DiscordUtils.getInstance().getModulesFolder(), "profile.yml"), DiscordUtils.getInstance().getResource("profile.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setAutoSave(true).setVersioning(new Pattern(Segment.range(1, Integer.MAX_VALUE),
                            Segment.literal("."), Segment.range(0, 100)), "Version").build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void loadCommands(){
        DiscordUtils.getInstance().getCommand("addpapi").setExecutor(new SetPAPICommand());
        DiscordUtils.getInstance().getCommand("removepapi").setExecutor(new RemovePAPICommand());
        DiscordUtils.getInstance().getCommand("editpapi").setExecutor(new EditPAPICommand());
    }

    private static void loadEvent(){
        DiscordUtils.getInstance().getJda().addEventListener(new StatsCommand());
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

    private static void loadDatabase(){
        database = new Database();
        database.createTable();
    }

    public static void loadModule(){
        loadConfig();
        loadDatabase();
        loadCommands();
        loadEvent();
        registerSlashCommand();

    }

}
