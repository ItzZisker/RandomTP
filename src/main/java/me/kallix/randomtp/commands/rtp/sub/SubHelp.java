package me.kallix.randomtp.commands.rtp.sub;

import lombok.RequiredArgsConstructor;
import me.kallix.randomtp.commands.rtp.RandomTPSubCommand;
import me.kallix.randomtp.config.Configuration;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public final class SubHelp implements RandomTPSubCommand {

    private final Configuration configuration;

    @Override
    public void execute(CommandSender sender, String label, String[] newArgs) {
        sender.sendMessage(configuration.getMessage_command_rtp_usage().replace("{label}", label));
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String permission() {
        return null;
    }
}
