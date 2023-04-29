package me.kallix.randomtp.listeners;

import lombok.RequiredArgsConstructor;
import me.kallix.randomtp.processor.TeleportProcessor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class PlayerQuitListener implements Listener {

    private final TeleportProcessor teleportProcessor;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        teleportProcessor.cancelLoading(event.getPlayer());
    }
}
