package org.main;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Main extends JavaPlugin {

    private final Map<UUID, Integer> flowLevels = new HashMap<>();

    @Override
    public void onEnable() {
        System.out.println("Flow system initialized!");

        // Initialize flow for online players (in case of reload)
        for (Player p : Bukkit.getOnlinePlayers()) {
            flowLevels.put(p.getUniqueId(), 100);
        }

        // Start updating flow bar and regen
        startFlowUpdater();
    }

    @Override
    public void onDisable() {
        flowLevels.clear();
    }

    private void startFlowUpdater() {
        new BukkitRunnable() {
            int tickCounter = 0;

            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    UUID id = p.getUniqueId();
                    flowLevels.putIfAbsent(id, 100);

                    int flow = flowLevels.get(id);

                    // Natural regen +10 per second
                    if (tickCounter % 1 == 0 && flow < 100) {
                        flow = Math.min(100, flow + 1);
                        flowLevels.put(id, flow);
                    }

                    // Build small bar (5 blocks)
                    int filled = (int) Math.ceil(flow / 5);
                    StringBuilder bar = new StringBuilder("§b⟦");
                    for (int i = 0; i < 20; i++) {
                        bar.append(i < filled ? "§b|" : "§7|");
                    }
                    bar.append("§b⟧ §7").append(flow);

                    // Send action bar message
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(bar.toString()));
                }
                tickCounter++;
            }
        }.runTaskTimer(this, 0, 5); // update every 0.5 seconds
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("setflow")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players can use this command!");
                return true;
            }

            Player p = (Player) sender;
            if (args.length != 1) {
                p.sendMessage("§7Usage: §b/setflow <amount>");
                return true;
            }

            try {
                int amount = Integer.parseInt(args[0]);
                amount = Math.max(0, Math.min(100, amount));
                flowLevels.put(p.getUniqueId(), amount);
                p.sendMessage("§b[Flow] §7Set Flow to §f" + amount);
            } catch (NumberFormatException e) {
                p.sendMessage("§cPlease enter a valid number!");
            }
            return true;
        }
        return false;
    }
}
