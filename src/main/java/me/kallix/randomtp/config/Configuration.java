package me.kallix.randomtp.config;

import lombok.Getter;
import me.kallix.randomtp.error.ConfigLoadException;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public final class Configuration {

    private int minX, minZ, maxX, maxZ;
    private int queueBossBarUpdateTicks;
    private String queueBossBarFormat;
    private BarColor queueBossBarColor;

    private String message_alreadyInQueue;
    private String message_onlyPlayerCommand;
    private String message_joinedQueue;
    private String message_cancelledQueue;
    private String message_notInQueue;
    private String message_command_rtp_usage;
    private String message_noPermission;

    public Configuration(FileConfiguration configFile) {
        reloadConfig(configFile);
    }

    public void reloadConfig(FileConfiguration configFile) {

        int minX, minZ, maxX, maxZ;
        int queueBossBarUpdateTicks;
        String queueBossBarFormat;
        BarColor queueBossBarColor;

        String message_alreadyInQueue;
        String message_onlyPlayerCommand;
        String message_joinedQueue;
        String message_command_rtp_usage;
        String message_cancelledQueue;
        String message_notInQueue;
        String message_noPermission;

        try {
            minX = configFile.getInt("min-x");
            minZ = configFile.getInt("min-z");
            maxX = configFile.getInt("max-x");
            maxZ = configFile.getInt("max-z");

            queueBossBarUpdateTicks = configFile.getInt("queue-bossBar.update-ticks");
            queueBossBarFormat = ChatColor.translateAlternateColorCodes('&', configFile.getString("queue-bossBar.format"));
            queueBossBarColor = BarColor.valueOf(configFile.getString("queue-bossBar.color"));

            message_alreadyInQueue = ChatColor.translateAlternateColorCodes('&', configFile.getString("messages.already-in-queue"));
            message_onlyPlayerCommand = ChatColor.translateAlternateColorCodes('&', configFile.getString("messages.only-player-command"));
            message_joinedQueue = ChatColor.translateAlternateColorCodes('&', configFile.getString("messages.player-joined-queue"));
            message_cancelledQueue = ChatColor.translateAlternateColorCodes('&', configFile.getString("messages.player-cancelled-queue"));
            message_command_rtp_usage = ChatColor.translateAlternateColorCodes('&', configFile.getString("messages.command-rtp-usage"));
            message_notInQueue = ChatColor.translateAlternateColorCodes('&', configFile.getString("messages.player-not-in-queue"));
            message_noPermission = ChatColor.translateAlternateColorCodes('&', configFile.getString("messages.no-permission"));
        } catch (Exception e) {
            throw new ConfigLoadException(e);
        }

        if (minX > maxX) {
            throw new ConfigLoadException("max-x must be greater than min-x");
        }

        if (minZ > maxZ) {
            throw new ConfigLoadException("max-z must be greater than min-z");
        }

        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
        this.queueBossBarUpdateTicks = queueBossBarUpdateTicks;
        this.queueBossBarFormat = queueBossBarFormat;
        this.queueBossBarColor = queueBossBarColor;

        this.message_alreadyInQueue = message_alreadyInQueue;
        this.message_onlyPlayerCommand = message_onlyPlayerCommand;
        this.message_joinedQueue = message_joinedQueue;
        this.message_command_rtp_usage = message_command_rtp_usage;
        this.message_cancelledQueue = message_cancelledQueue;
        this.message_notInQueue = message_notInQueue;
        this.message_noPermission = message_noPermission;
    }
}
