package me.kallix.randomtp.commands.rtp.sub;

import lombok.RequiredArgsConstructor;
import me.kallix.randomtp.commands.rtp.SubCommand;
import me.kallix.randomtp.config.Configuration;
import me.kallix.randomtp.processor.TeleportProcessor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class SubCancel implements SubCommand {

    private final Configuration configuration;
    private final TeleportProcessor teleportProcessor;

    @Override
    public void execute(CommandSender sender, String label, String[] newArgs) {
        Player player = (Player) sender;

        if (teleportProcessor.isLoading(player)) {
            teleportProcessor.cancelLoading(player);
            player.sendMessage(configuration.getMessage_cancelledQueue());
        } else {
            player.sendMessage();
        }
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public String name() {
        return "cancel";
    }

    @Override
    public String permission() {
        return "rtp.cancel";
    }
}
