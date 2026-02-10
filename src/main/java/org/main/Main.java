package org.main;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public final class Main extends JavaPlugin {
    private RiftManager riftManager;

    // Flow system
    private final Map<UUID, Integer> flowLevels = new HashMap<>();

    // Custom item storage
    private ItemStorageManager storageManager;

    private World world;
    private BukkitTask task;
    private List<Entity> entities = new ArrayList<>();

    @Override
    public void onEnable() {
        world = Bukkit.getWorld("world");


        ItemManager.init();
        new CraftingManager(this).registerAllRecipes();
        riftManager = new RiftManager(Bukkit.getWorlds().get(0));

        startRiftScheduler(); // 👈 starts the 10–20 min cycle

        getLogger().info("Flow system initialized!");

        // Initialize storage manager
        storageManager = new ItemStorageManager(this);
        storageManager.loadAll(); // load all saved items

        // Initialize flow for online players (in case of reload)
        for (Player p : Bukkit.getOnlinePlayers()) {
            flowLevels.put(p.getUniqueId(), 100);
        }

        // Start updating flow bar and regen
        startFlowUpdater();
    }
    private void startRiftScheduler() {
        scheduleNextRift();
    }

    private void scheduleNextRift() {
        int minMinutes = 10;
        int maxMinutes = 20;

        int delayMinutes = minMinutes + new java.util.Random().nextInt(maxMinutes - minMinutes + 1);
        long delayTicks = delayMinutes * 60L * 20L; // minutes → ticks

        Bukkit.getScheduler().runTaskLater(this, () -> {
            spawnRift();          // spawn the rift
            scheduleNextRift();   // schedule the next one
        }, delayTicks);
    }
    private void spawnRift() {
        Location loc = riftManager.findRiftSpawnLocation();
        if (loc == null) return;

        Bukkit.broadcastMessage(
                "§5§l⚠ Instability detected! §dRift forming at §fX: "
                        + loc.getBlockX() + " Y: " + loc.getBlockZ()
        );
    }

    // mob spawning logic


    private int getRandomWithNeg(int size) {
        int random = (int) (Math.random() * (size + 1));
        if (Math.random() > 0.5) random *= -1;
        return random;
    }

    @Override
    public void onDisable() {
        // Save all custom items
        if (storageManager != null) {
            storageManager.saveAll();
        }

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

                    // Natural regen +1 per tick (every 0.5s)
                    if (flow < 100) {
                        flow = Math.min(100, flow + 1);
                        flowLevels.put(id, flow);
                    }

                    // Build small bar (20 segments)
                    int filled = (int) Math.ceil(flow / 5.0);
                    StringBuilder bar = new StringBuilder("§b⟦");
                    for (int i = 0; i < 20; i++) {
                        bar.append(i < filled ? "§b|" : "§7|");
                    }
                    bar.append("§b⟧ §7").append(flow);

                    // Send action bar
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(bar.toString()));
                }
                tickCounter++;
            }
        }.runTaskTimer(this, 0, 5); // every 0.5 seconds
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

        // Example command to give a Memory Disk
        if (command.getName().equalsIgnoreCase("givedisk")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players can use this command!");
                return true;
            }

            Player p = (Player) sender;
            ItemStack disk = ItemManager.Memory_Disk.clone();

            // Add to player's custom item storage
            storageManager.addItem(p, "memory_disks", disk);

            p.sendMessage("§bYou received a Memory Disk!");
            return true;
        }

        return false;
    }

    // Getter for storage manager
    public ItemStorageManager getStorageManager() {
        return storageManager;
    }
}
