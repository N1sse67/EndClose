package me.endclose;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class EndClosePlugin extends JavaPlugin {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private boolean closed;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        closed = getConfig().getBoolean("closed", false);

        getServer().getPluginManager().registerEvents(new EndListener(this), this);

        EndCommand handler = new EndCommand(this);
        PluginCommand command = getCommand("end");
        if (command != null) {
            command.setExecutor(handler);
            command.setTabCompleter(handler);
        }

        getLogger().info("EndClose включён. Энд сейчас " + (closed ? "закрыт" : "открыт") + ".");
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean value) {
        this.closed = value;
        getConfig().set("closed", value);
        saveConfig();
    }

    public void reload() {
        reloadConfig();
        this.closed = getConfig().getBoolean("closed", false);
    }

    public Component msg(String key) {
        String prefix = getConfig().getString("messages.prefix", "");
        String text = getConfig().getString("messages." + key, key);
        return LEGACY.deserialize(prefix + text);
    }

    public boolean isEnd(World world) {
        return world != null && world.getEnvironment() == World.Environment.THE_END;
    }

    public void sendOutOfEnd(Player player) {
        World main = Bukkit.getWorlds().get(0);
        player.teleport(main.getSpawnLocation());
        player.sendMessage(msg("kicked"));
    }

    public void evacuateEnd() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isEnd(player.getWorld()) && !player.hasPermission("endclose.bypass")) {
                sendOutOfEnd(player);
            }
        }
    }
    }
