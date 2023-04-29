package me.kallix.randomtp.commands.rtp;

import org.bukkit.command.CommandSender;

public interface SubCommand {

    void execute(CommandSender sender, String label, String[] newArgs);

    boolean isPlayerOnly();

    String name();

    String permission();
}
