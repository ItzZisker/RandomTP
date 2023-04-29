package me.kallix.randomtp;

import lombok.Getter;
import me.kallix.randomtp.commands.rtp.RandomTPCommand;
import me.kallix.randomtp.config.Configuration;
import me.kallix.randomtp.listeners.PlayerQuitListener;
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
            getCommand("rtp").setExecutor(new RandomTPCommand(this));
        } catch (NullPointerException e) {
            throw new IllegalStateException("Bad plugin.yml");
        }
        getServer().getPluginManager()
                .registerEvents(new PlayerQuitListener(teleportProcessor), this);
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
