package me.kallix.randomtp.commands.rtp.sub;

import lombok.RequiredArgsConstructor;
import me.kallix.randomtp.RandomTP;
import me.kallix.randomtp.commands.rtp.RandomTPSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public final class SubReload implements RandomTPSubCommand {

    private final RandomTP plugin;

    @Override
    public void execute(CommandSender sender, String label, String[] newArgs) {
        try {
            plugin.reloadConfig();
            plugin.getConfiguration().reloadConfig(plugin.getConfig());
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "There's an exception during the reload process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String permission() {
        return "rtp.reload";
    }
}
