package me.kallix.randomtp.commands.rtp;

import com.google.common.collect.Sets;
import me.kallix.randomtp.RandomTP;
import me.kallix.randomtp.commands.rtp.sub.*;
import me.kallix.randomtp.config.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

public final class RandomTPCommand implements CommandExecutor {

    private final Set<RandomTPSubCommand> subCommands = Sets.newHashSet();

    private final RandomTPSubCommand subHelp;
    private final RandomTPSubCommand subTeleport;

    private final Configuration configuration;

    public RandomTPCommand(RandomTP plugin) {
        this.configuration = plugin.getConfiguration();

        subCommands.add(subHelp = new SubHelp(configuration));
        subCommands.add(subTeleport = new SubTeleport(configuration, plugin.getTeleportProcessor()));
        subCommands.add(new SubCancel(configuration, plugin.getTeleportProcessor()));
        subCommands.add(new SubDebug(plugin.getTeleportProcessor()));
        subCommands.add(new SubReload(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {

        if (args.length == 0) {
            exec(command, sender, subTeleport, label, new String[]{""});
        } else {
            for (RandomTPSubCommand subCommand : subCommands) {
                if (subCommand.name().equalsIgnoreCase(args[0])) {
                    return exec(command, sender, subCommand, label, args);
                }
            }
            exec(command, sender, subHelp, label, args);
            return false;
        }
        return true;
    }

    public boolean exec(Command command, CommandSender sender, RandomTPSubCommand subCommand, String label, String[] args) {
        if (!subCommand.isPlayerOnly() || sender instanceof Player) {
            if (sender instanceof ConsoleCommandSender ||
                    (subCommand.permission() == null || sender.hasPermission(subCommand.permission()))) {
                subCommand.execute(sender, label, args.length == 1 ? new String[]{} :
                        Arrays.copyOfRange(args, 1, args.length - 1));
                return true;
            } else {
                //noinspection ConstantConditions
                sender.sendMessage(command.getPermissionMessage());
                return false;
            }
        } else {
            sender.sendMessage(configuration.getMessage_onlyPlayerCommand());
            return false;
        }
    }
}
