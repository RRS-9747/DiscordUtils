package me.rrs.discordstats;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.segment.Segment;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.rrs.discordstats.command.discord.StatsCommand;
import me.rrs.discordstats.command.minecraft.EditPAPICommand;
import me.rrs.discordstats.command.minecraft.RemovePAPICommand;
import me.rrs.discordstats.command.minecraft.SetPAPICommand;
import me.rrs.discordstats.utils.UpdateAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public final class DiscordStats extends JavaPlugin {

    private static YamlDocument config;
    private static Database database;
    private static DiscordStats instance;
    private static JDA jda;


    public static YamlDocument getConfiguration() {
        return config;
    }
    public static DiscordStats getInstance() {
        return instance;
    }
    public static Database getDatabase() {
        return database;
    }
    public static JDA getJda() {
        return jda;
    }

    @Override
    public void onEnable() {
        new Metrics(this, 19485);

        try {
            config = YamlDocument.create(new File(getDataFolder(), "config.yml"), getResource("config.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setAutoSave(true).setVersioning(new Pattern(Segment.range(1, Integer.MAX_VALUE),
                            Segment.literal("."), Segment.range(0, 100)), "Version").build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            new BukkitRunnable() {
                @Override
                public void run() {
                    updateChecker();
                }
            }.runTaskTimerAsynchronously(this, 0L, 20L * 60L * 30L);
        }catch (UnsupportedOperationException ignored) {}

        instance = this;
        database = new Database();
        database.setupDataSource();
        database.createTable();

        Bukkit.getLogger().info("Loading bot...");
        loadBot();

        getCommand("addpapi").setExecutor(new SetPAPICommand());
        getCommand("removepapi").setExecutor(new RemovePAPICommand());
        getCommand("editpapi").setExecutor(new EditPAPICommand());

        jda.addEventListener(new StatsCommand());

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("DiscordSRV")){

            jda.updateCommands().addCommands(
                    Commands.slash(config.getString("Bot.Command"), "Show player stats")
                                    .setGuildOnly(true)
                                    .addOption(OptionType.USER, "user", "Give a user", false)
                                    .addOption(OptionType.STRING, "name", "Give a user name", false)
                            ).queue();
        }else {

            jda.updateCommands().addCommands(
                    Commands.slash(config.getString("Bot.Command"), "Show player stats")
                            .setGuildOnly(true)
                            .addOption(OptionType.STRING, "name", "Give a user name", true)
            ).queue();
        }
    }

    @Override
    public void onDisable() {
        if (jda != null) {
            jda.updateCommands().queue();
            jda.shutdown();
        }
    }

    private void loadBot() {
        jda = JDABuilder.createDefault(config.getString("Bot.Token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setAutoReconnect(true)
                .setActivity(Activity.of(Activity.ActivityType.valueOf(config.getString("Bot.Activity.Type")),
                        config.getString("Bot.Activity.Name")))
                .setStatus(OnlineStatus.valueOf(config.getString("Bot.Status")))
                .build();
        try {
            jda.awaitReady();
            Bukkit.getLogger().info("Logged as " + jda.getSelfUser().getName() + "#" + jda.getSelfUser().getDiscriminator());
        } catch (InterruptedException ignored) {
        }
    }

    public void updateChecker() {
        UpdateAPI updateAPI = new UpdateAPI();

        if (updateAPI.hasGithubUpdate("RRS-9747", "DiscordStats")) {
            String newVersion = updateAPI.getGithubVersion("RRS-9747", "DiscordStats");
            if (config.getBoolean("Config.Update-Notify")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("discordstats.notify")) {
                        p.sendMessage("--------------------------------");
                        p.sendMessage("You are using DiscordStats " + getDescription().getVersion());
                        p.sendMessage("However version " + newVersion + " is available.");
                        p.sendMessage("You can download it from: " + "https://www.spigotmc.org/resources/107771/");
                        p.sendMessage("--------------------------------");
                    }
                }
            }
            Bukkit.getLogger().info("--------------------------------");
            Bukkit.getLogger().info("You are using DiscordStats " + getDescription().getVersion());
            Bukkit.getLogger().info("However version " + newVersion + " is available.");
            Bukkit.getLogger().info("You can download it from: " + "https://www.spigotmc.org/resources/107771/");
            Bukkit.getLogger().info("--------------------------------");

        }
    }
}
