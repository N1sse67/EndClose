package me.endclose;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EndListener implements Listener {

    private final EndClosePlugin plugin;
    private final Map<UUID, Long> lastMessage = new HashMap<>();

    public EndListener(EndClosePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        handle(event.getPlayer(), event.getFrom(), event.getTo(), event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        handle(event.getPlayer(), event.getFrom(), event.getTo(), event);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.isClosed()
                && plugin.isEnd(player.getWorld())
                && !player.hasPermission("endclose.bypass")) {
            Bukkit.getScheduler().runTask(plugin, () -> plugin.sendOutOfEnd(player));
        }
    }

    private void handle(Player player, Location from, Location to, Cancellable event) {
        if (!plugin.isClosed()) return;
        if (to == null || !plugin.isEnd(to.getWorld())) return;
        if (from != null && plugin.isEnd(from.getWorld())) return;
        if (player.hasPermission("endclose.bypass")) return;

        event.setCancelled(true);

        long now = System.currentTimeMillis();
        long cooldown = plugin.getConfig().getLong("message-cooldown-seconds", 3) * 1000L;
        Long last = lastMessage.get(player.getUniqueId());
        if (last == null || now - last >= cooldown) {
            lastMessage.put(player.getUniqueId(), now);
            player.sendMessage(plugin.msg("denied"));
        }
    }
            }
