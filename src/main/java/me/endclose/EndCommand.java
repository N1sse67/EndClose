package me.endclose;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;
import java.util.Locale;

public final class EndCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = List.of("close", "open", "status", "reload");

    private final EndClosePlugin plugin;

    public EndCommand(EndClosePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("endclose.use")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(plugin.msg("usage"));
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "close" -> {
                if (plugin.isClosed()) {
                    sender.sendMessage(plugin.msg("already-closed"));
                    return true;
                }
                plugin.setClosed(true);
                if (plugin.getConfig().getBoolean("kick-players-out", true)) {
                    plugin.evacuateEnd();
                }
                if (plugin.getConfig().getBoolean("broadcast", true)) {
                    Bukkit.broadcast(plugin.msg("closed-broadcast"));
                } else {
                    sender.sendMessage(plugin.msg("status-closed"));
                }
            }
            case "open" -> {
                if (!plugin.isClosed()) {
                    sender.sendMessage(plugin.msg("already-open"));
                    return true;
                }
                plugin.setClosed(false);
                if (plugin.getConfig().getBoolean("broadcast", true)) {
                    Bukkit.broadcast(plugin.msg("opened-broadcast"));
                } else {
                    sender.sendMessage(plugin.msg("status-open"));
                }
            }
            case "status" -> sender.sendMessage(plugin.msg(plugin.isClosed() ? "status-closed" : "status-open"));
            case "reload" -> {
                plugin.reload();
                sender.sendMessage(plugin.msg("reloaded"));
            }
            default -> sender.sendMessage(plugin.msg("usage"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("endclose.use")) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).toList();
        }
        return List.of();
    }
}
