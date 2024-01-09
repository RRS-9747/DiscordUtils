package me.rrs.discordutils.level;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LevelUp extends ListenerAdapter {

    private final Database database = new Database();
    private final int BASE_EXPERIENCE = 10;


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage()) {
            return;
        }

        User author = event.getAuthor();
        long userId = author.getIdLong();

        int currentLevel = getUserLevel(userId);
        int currentExperience = getUserExperience(userId);

        // Increment experience for each message
        currentExperience += BASE_EXPERIENCE;
        saveUserLevel(userId, currentLevel, currentExperience);

        if (shouldLevelUp(currentLevel, currentExperience)) {
            levelUpUser(userId, currentLevel);
            MessageChannel channel = event.getChannel();

            // Calculate message count based on total experience and base experience per message
            int messagesSent = currentExperience / BASE_EXPERIENCE;

            channel.sendMessage("Congratulations, " + author.getAsMention() +
                            "! You've reached level " + (currentLevel + 1) +
                            " with a total of " + messagesSent + " messages sent.")
                    .queue();
        }
    }

    private int getUserLevel(long userId) {
        return database.getUserLevel(userId);
    }

    private int getUserExperience(long userId) {
        return database.getUserExperience(userId);
    }

    private void saveUserLevel(long userId, int level, int experience) {
        database.updateUserLevel(userId, level, experience);
    }

    private void levelUpUser(long userId, int currentLevel) {
        database.updateUserLevel(userId, currentLevel + 1, 0);
    }

    private boolean shouldLevelUp(int level, int experience) {
        int experienceForNextLevel = (level + 1) * BASE_EXPERIENCE * 10;
        return experience >= experienceForNextLevel;
    }
}