package me.rrs.discordutils.utils;

import me.rrs.discordutils.DiscordUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class UpdateAPI {
    public boolean hasGithubUpdate(String owner, String repository) {
        try (InputStream inputStream = new URI(
                "https://api.github.com/repos/" + owner + "/" + repository + "/releases/latest").toURL().openStream()) {
            JSONObject response = new JSONObject(new org.json.JSONTokener(inputStream));
            String currentVersion = DiscordUtils.getInstance().getDescription().getVersion();
            String latestVersion = response.getString("tag_name");
            return !currentVersion.equals(latestVersion);
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public String getGithubVersion(String owner, String repository) {
        try (InputStream inputStream = new URI(
                "https://api.github.com/repos/" + owner + "/" + repository + "/releases/latest").toURL().openStream()) {
            org.json.JSONObject response = new org.json.JSONObject(new org.json.JSONTokener(inputStream));
            return response.getString("tag_name");
        } catch (Exception exception) {
            exception.printStackTrace();
            return DiscordUtils.getInstance().getDescription().getVersion();
        }
    }
}
