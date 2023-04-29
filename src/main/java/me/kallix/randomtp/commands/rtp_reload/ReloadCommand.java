package me.kallix.randomtp.commands.rtp_reload;

import lombok.RequiredArgsConstructor;
import me.kallix.randomtp.RandomTP;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public final class ReloadCommand implements CommandExecutor {

    private final RandomTP plugin;

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        try {
            plugin.reloadConfig();
            plugin.getConfiguration().reloadConfig(plugin.getConfig());
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "There's an exception during the reload process: " + e.getMessage());
            e.printStackTrace();

            return false;
        }
        return true;
    }
}
