package me.rrs.discordutils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.segment.Segment;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.rrs.discordutils.bedwars.bedwars1058.BedwarsCore;
import me.rrs.discordutils.profile.command.ProfileCore;
import me.rrs.discordutils.utils.UpdateAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getPluginManager;

public final class DiscordUtils extends JavaPlugin {


    private static DiscordUtils instance;
    private YamlDocument config;
    private CoreDatabase database;
    private JDA jda;
    private final ArrayList<SlashCommandData> commands = new ArrayList<>();
    private File modulesDir;


    public static DiscordUtils getInstance() {
        return instance;
    }
    public YamlDocument getConfiguration() {
        return config;
    }
    public JDA getJda() {
        return jda;
    }
    public List<SlashCommandData> getCommands() {
        return commands;
    }
    public CoreDatabase getDatabase() {
        return database;
    }
    public File getModulesFolder(){
        return modulesDir;
    }

    @Override
    public void onLoad(){
        new Metrics(this, 19777);

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
        modulesDir = new File(getDataFolder(), File.separator + "modules");

    }

    @Override
    public void onEnable()  {

        Bukkit.getLogger().info("");
        Bukkit.getLogger().info("    " + getDescription().getName() + " v" + getDescription().getVersion());
        Bukkit.getLogger().info("    " + "Running on " + Bukkit.getName());
        Bukkit.getLogger().info("");


        try {
            new BukkitRunnable() {
                @Override
                public void run() {
                    updateChecker();
                }
            }.runTaskTimerAsynchronously(this, 0L, 20L * 60L * 30L);
        }catch (UnsupportedOperationException ignored) {}

        instance = this;
        database = new CoreDatabase();
        database.setupDataSource();

        Bukkit.getLogger().info("[DiscordUtils] Loading bot...");
        loadBot();

        if (config.getBoolean("Modules.Profile")) {
            ProfileCore.loadModule();
        }
        if (config.getBoolean("Modules.BedWars") && getPluginManager().isPluginEnabled("BedWars1058")) {
            BedwarsCore.loadModule();
        }

        jda.updateCommands().addCommands(commands).queue();

    }

    @Override
    public void onDisable() {
        if (jda != null) {
            jda.updateCommands().queue();
            jda.shutdown();
        }
    }

    private void loadBot() {
        if (config.getString("Bot.Token").isEmpty()){
            Bukkit.getLogger().severe("[DiscordUtils] Please set a valid bot token in config.yml");
            return;
        }
        jda = JDABuilder.createDefault(config.getString("Bot.Token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setAutoReconnect(true)
                .setActivity(Activity.of(Activity.ActivityType.valueOf(config.getString("Bot.Activity.Type")),
                        config.getString("Bot.Activity.Name")))
                .setStatus(OnlineStatus.valueOf(config.getString("Bot.Status")))
                .build();
        try {
            jda.awaitReady();
            Bukkit.getLogger().info("[DiscordUtils] Logged as " + jda.getSelfUser().getName() + "#" + jda.getSelfUser().getDiscriminator());
        } catch (InterruptedException ignored) {
        }
    }

    public void updateChecker() {
        UpdateAPI updateAPI = new UpdateAPI();

        if (updateAPI.hasGithubUpdate("RRS-9747", "DiscordUtils")) {
            String newVersion = updateAPI.getGithubVersion("RRS-9747", "DiscordUtils");
            if (config.getBoolean("Config.Update-Notify")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("discordutils.notify")) {
                        p.sendMessage("--------------------------------");
                        p.sendMessage("You are using DiscordUtils " + getDescription().getVersion());
                        p.sendMessage("However version " + newVersion + " is available.");
                        p.sendMessage("You can download it from: " + "https://www.spigotmc.org/resources/107771/");
                        p.sendMessage("--------------------------------");
                    }
                }
            }
            Bukkit.getLogger().info("--------------------------------");
            Bukkit.getLogger().info("You are using DiscordUtils " + getDescription().getVersion());
            Bukkit.getLogger().info("However version " + newVersion + " is available.");
            Bukkit.getLogger().info("You can download it from: " + "https://www.spigotmc.org/resources/107771/");
            Bukkit.getLogger().info("--------------------------------");

        }
    }
}
