package org.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class FlowManager implements Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, Integer> flowLevels = new HashMap<>();

    public FlowManager(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startFlowUpdater();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        flowLevels.put(e.getPlayer().getUniqueId(), 100); // Start full
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        flowLevels.remove(e.getPlayer().getUniqueId());
    }

    public void setFlow(Player p, int newFlow) {
        UUID id = p.getUniqueId();
        newFlow = Math.max(0, Math.min(100, newFlow));
        flowLevels.put(id, newFlow);
    }

    public void addFlow(Player p, int amount) {
        setFlow(p, getFlow(p) + amount);
    }

    public int getFlow(Player p) {
        return flowLevels.getOrDefault(p.getUniqueId(), 0);
    }

    private void startFlowUpdater() {
        new BukkitRunnable() {
            int tickCounter = 0;

            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    int flow = getFlow(p);

                    // 🔁 Regenerate Flow every second (20 ticks)
                    if (tickCounter % 10 == 0 && flow < 100) {
                        addFlow(p, 5);
                        flow = getFlow(p);
                    }

                    // 🔵 Compact Action Bar Display (5 segments)
                    int filled = (int) Math.ceil(flow / 20.0);
                    StringBuilder bar = new StringBuilder("§b⟦");
                    for (int i = 0; i < 5; i++) {
                        bar.append(i < filled ? "§b█" : "§7█");
                    }
                    bar.append("§b⟧ §7").append(flow);

                    // Send Action Bar
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(bar.toString()));
                }

                tickCounter++;
            }
        }.runTaskTimer(plugin, 0, 10); // Update twice per second
    }
}
