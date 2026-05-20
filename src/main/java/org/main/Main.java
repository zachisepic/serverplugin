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

    // Flow system
    private final Map<UUID, Integer> flowLevels = new HashMap<>();

    // Custom item storage
    private ItemStorageManager storageManager;
    private AbilityManager abilityManager;
    private AbilityMenu abilityMenu;

    private World world;
    private BukkitTask task;

    @Override
    public void onEnable() {
        world = Bukkit.getWorld("world");


        ItemManager.init();
        new CraftingManager(this).registerAllRecipes();

        this.abilityManager = new AbilityManager(this);
        this.abilityMenu = new AbilityMenu(this, abilityManager);

        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new AbilityListener(this, abilityManager), this);
        getServer().getPluginManager().registerEvents(abilityMenu, this);

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

                    // Show active ability
                    org.main.components.AbilityType active = abilityManager.getActiveAbility(p);
                    String abilityName = (active != null) ? " §8| §f" + active.getName() : " §8| §7None";
                    bar.append(abilityName);

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

        if (command.getName().equalsIgnoreCase("abilities")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players can use this command!");
                return true;
            }
            abilityMenu.open((Player) sender);
            return true;
        }

        return false;
    }

    // Getter for storage manager
    public ItemStorageManager getStorageManager() {
        return storageManager;
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public int getFlow(Player player) {
        return flowLevels.getOrDefault(player.getUniqueId(), 0);
    }

    public void setFlow(Player player, int amount) {
        flowLevels.put(player.getUniqueId(), Math.max(0, Math.min(100, amount)));
    }
}
