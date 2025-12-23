package org.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ItemStorageManager {

    private final JavaPlugin plugin;

    // Map<Player UUID, Map<CustomType, List<ItemStack>>>
    private final Map<UUID, Map<String, List<ItemStack>>> playerItems = new HashMap<>();

    public ItemStorageManager(JavaPlugin plugin) {
        this.plugin = plugin;

        // Ensure config section exists
        if (!plugin.getConfig().isConfigurationSection("player_items")) {
            plugin.getConfig().createSection("player_items");
            plugin.saveConfig();
        }
    }

    // Add a custom item to a player
    public void addItem(Player player, String type, ItemStack item) {
        Map<String, List<ItemStack>> items = playerItems.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        List<ItemStack> list = items.computeIfAbsent(type, k -> new ArrayList<>());
        list.add(item);
        savePlayer(player);
    }

    // Get all items of a type for a player
    public List<ItemStack> getItems(Player player, String type) {
        Map<String, List<ItemStack>> items = playerItems.get(player.getUniqueId());
        if (items == null) return new ArrayList<>();
        return items.getOrDefault(type, new ArrayList<>());
    }

    // Save a single player's items to config
    public void savePlayer(Player player) {
        Map<String, List<ItemStack>> items = playerItems.get(player.getUniqueId());
        if (items == null) return;
        for (Map.Entry<String, List<ItemStack>> entry : items.entrySet()) {
            plugin.getConfig().set("player_items." + player.getUniqueId() + "." + entry.getKey(), entry.getValue());
        }
        plugin.saveConfig();
    }

    // Save all players
    public void saveAll() {
        for (UUID uuid : playerItems.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) savePlayer(player);
        }
    }

    // Load all players from config
    @SuppressWarnings("unchecked")
    public void loadAll() {
        if (!plugin.getConfig().isConfigurationSection("player_items")) return;
        for (String uuidString : plugin.getConfig().getConfigurationSection("player_items").getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            Map<String, List<ItemStack>> items = new HashMap<>();
            for (String type : plugin.getConfig().getConfigurationSection("player_items." + uuidString).getKeys(false)) {
                List<ItemStack> list = (List<ItemStack>) plugin.getConfig().getList("player_items." + uuidString + "." + type);
                if (list != null) items.put(type, list);
            }
            playerItems.put(uuid, items);
        }
    }
}
