package me.kallix.randomtp.commands.rtp.sub;

import lombok.RequiredArgsConstructor;
import me.kallix.randomtp.commands.rtp.RandomTPSubCommand;
import me.kallix.randomtp.processor.TeleportProcessor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public final class SubDebug implements RandomTPSubCommand {

    private final TeleportProcessor teleportProcessor;

    @Override
    public void execute(CommandSender sender, String label, String[] newArgs) {
        sender.sendMessage("ChunkLoadQueue:");
        sender.sendMessage(teleportProcessor.getChunkLoadingQueue().toString());
        sender.sendMessage("BossBarQueue:");
        sender.sendMessage(teleportProcessor.getBossBarQueue().toString());
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String name() {
        return "debug";
    }

    @Override
    public String permission() {
        return "rtp.debug";
    }
}
