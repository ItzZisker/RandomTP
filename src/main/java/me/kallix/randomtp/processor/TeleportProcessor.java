package me.kallix.randomtp.processor;

import com.google.common.collect.Maps;
import lombok.Getter;
import me.kallix.randomtp.RandomTP;
import me.kallix.randomtp.config.Configuration;
import me.kallix.randomtp.utils.Pair;
import me.kallix.randomtp.utils.ScheduledQueue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class TeleportProcessor {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Getter
    private final Map<Player, Pair<BukkitTask, BossBar>> bossBarQueue = Maps.newHashMap();

    private final Plugin plugin;
    private final Configuration config;

    @Getter
    private final ScheduledQueue<Player> chunkLoadingQueue;

    public TeleportProcessor(RandomTP plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.chunkLoadingQueue = ScheduledQueue.newScheduledQueue(plugin, 0, config.getQueueWaitTicks(), true);
    }

    public void randomTeleport(Player player, World world) {

        if (isLoading(player)) {
            player.sendMessage(config.getMessage_alreadyInQueue());
            return;
        }

        int randomX = RANDOM.nextInt(config.getMinX(), config.getMaxX());
        int randomZ = RANDOM.nextInt(config.getMinZ(), config.getMaxZ());

        int chunkX = randomX >> 4;
        int chunkZ = randomZ >> 4;

        if (world.isChunkGenerated(chunkX, chunkZ)) {
            if (world.isChunkLoaded(chunkX, chunkZ)) {
                teleportAverageHeight(world, randomX, randomZ, player);
            } else {
                submitLoading(player, world, randomX, randomZ, false);
            }
        } else {
            submitLoading(player, world, randomX, randomZ, true);
        }
    }

    public boolean isLoading(Player player) {
        return chunkLoadingQueue.contains(player);
    }

    public void submitLoading(Player player, World world, int randomX, int randomZ, boolean generate) {
        chunkLoadingQueue.submit(player).onCompleteSync(() -> {
            world.loadChunk(randomX >> 4, randomZ >> 4, generate);
            teleportAverageHeight(world, randomX, randomZ, player);
            disposeBossBarQueue(player);
        }, true);
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (isLoading(player)) {
                submitBossBarQueue(player);
            }
        });
    }

    public void cancelLoading(Player player) {
        disposeBossBarQueue(player);
        chunkLoadingQueue.dispose(player);
    }

    public void submitBossBarQueue(Player player) {

        int pos = chunkLoadingQueue.positionOf(player);
        int all = chunkLoadingQueue.pool().size();

        if (pos != -1) {
            BossBar bossBar = Bukkit.createBossBar(config.getQueueBossBarFormat()
                            .replace("{pos}", String.valueOf(pos))
                            .replace("{all}", String.valueOf(all)),
                    config.getQueueBossBarColor(),
                    BarStyle.SOLID);

            bossBar.setProgress((double) pos / all);
            bossBar.addPlayer(player);

            bossBarQueue.put(player, new Pair<>(
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {

                        int _pos = chunkLoadingQueue.positionOf(player);
                        int _all = chunkLoadingQueue.pool().size();

                        if (_pos != -1) {
                            bossBar.setTitle(config.getQueueBossBarFormat()
                                    .replace("{pos}", String.valueOf(_pos))
                                    .replace("{all}", String.valueOf(_all)));
                            bossBar.setProgress((double) _pos / _all);
                        }
                    }, 0, config.getQueueBossBarUpdateTicks()), bossBar));
        }
    }

    public void disposeBossBarQueue(Player player) {
        Pair<BukkitTask, BossBar> pair = bossBarQueue.remove(player);
        if (pair != null) {
            pair.key().cancel();
            pair.value().removeAll();
        }
    }

    public void teleportAverageHeight(World world, int x, int z, Player player) {
        Location pLoc = player.getLocation();

        player.teleport(new Location(world, x,
                world.getHighestBlockYAt(x, z) + 1, z,
                pLoc.getYaw(), pLoc.getPitch()));
    }

    public void destroy() {
        bossBarQueue.values().forEach(pair -> {
            pair.key().cancel();
            pair.value().removeAll();
        });
        bossBarQueue.clear();
        chunkLoadingQueue.destroy();
    }
}
