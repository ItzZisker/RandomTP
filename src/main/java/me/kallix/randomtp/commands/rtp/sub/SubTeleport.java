package me.kallix.randomtp.commands.rtp.sub;

import lombok.RequiredArgsConstructor;
import me.kallix.randomtp.commands.rtp.RandomTPSubCommand;
import me.kallix.randomtp.config.Configuration;
import me.kallix.randomtp.processor.TeleportProcessor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class SubTeleport implements RandomTPSubCommand {

    private final Configuration configuration;
    private final TeleportProcessor teleportProcessor;

    @Override
    public void execute(CommandSender sender, String label, String[] newArgs) {

        Player player = (Player) sender;

        player.sendMessage(configuration.getMessage_joinedQueue());
        teleportProcessor.randomTeleport(player, player.getWorld());
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public String name() {
        return "teleport";
    }

    @Override
    public String permission() {
        return "rtp.teleport";
    }
}
