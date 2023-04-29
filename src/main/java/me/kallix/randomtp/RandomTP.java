package me.kallix.randomtp;

import lombok.Getter;
import me.kallix.randomtp.commands.rtp.RandomTPCommand;
import me.kallix.randomtp.commands.rtp_reload.ReloadCommand;
import me.kallix.randomtp.config.Configuration;
import me.kallix.randomtp.processor.TeleportProcessor;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class RandomTP extends JavaPlugin {

    private Configuration configuration;
    private TeleportProcessor teleportProcessor;

    @Override
    public void onEnable() {

        this.configuration = new Configuration(getConfig());
        this.teleportProcessor = new TeleportProcessor(this);

        try {
            getCommand("rtp").setExecutor(new RandomTPCommand(configuration, teleportProcessor));
            getCommand("rtp-reload").setExecutor(new ReloadCommand(this));
        } catch (NullPointerException e) {
            throw new IllegalStateException("Bad plugin.yml");
        }
    }

    @Override
    public void onDisable() {
        if (teleportProcessor != null) {
            teleportProcessor.destroy();
            teleportProcessor = null;
        }
        configuration = null;
    }
}
