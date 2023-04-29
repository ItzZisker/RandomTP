package me.kallix.randomtp.commands.rtp;

import com.google.common.collect.Sets;
import me.kallix.randomtp.commands.rtp.sub.SubCancel;
import me.kallix.randomtp.commands.rtp.sub.SubDebug;
import me.kallix.randomtp.commands.rtp.sub.SubHelp;
import me.kallix.randomtp.commands.rtp.sub.SubTeleport;
import me.kallix.randomtp.config.Configuration;
import me.kallix.randomtp.processor.TeleportProcessor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

public final class RandomTPCommand implements CommandExecutor {

    private final Set<SubCommand> subCommands = Sets.newHashSet();

    private final SubCommand subHelp;
    private final SubCommand subTeleport;

    private final Configuration configuration;

    public RandomTPCommand(Configuration configuration, TeleportProcessor teleportProcessor) {
        this.configuration = configuration;
        this.subHelp = new SubHelp(configuration);
        this.subTeleport = new SubTeleport(configuration, teleportProcessor);

        subCommands.add(subTeleport);
        subCommands.add(subHelp);
        subCommands.add(new SubDebug(teleportProcessor));
        subCommands.add(new SubCancel(configuration, teleportProcessor));
    }

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {

        if (args.length == 0) {
            exec(sender, subTeleport, label, args);
        } else {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.name().equals(args[0])) {
                    return exec(sender, subCommand, label, args);
                }
            }
            exec(sender, subHelp, label, args);
            return false;
        }
        return true;
    }

    public boolean exec(CommandSender sender, SubCommand subCommand, String label, String[] args) {
        if (subCommand.isPlayerOnly() == sender instanceof Player) {
            if (sender instanceof ConsoleCommandSender || sender.hasPermission(subCommand.permission())) {
                subCommand.execute(sender, label, Arrays.copyOfRange(args, 1, args.length - 1));
                return true;
            } else {
                sender.sendMessage(configuration.getMessage_noPermission());
                return false;
            }
        } else {
            sender.sendMessage(configuration.getMessage_onlyPlayerCommand());
            return false;
        }
    }
}
