package org.main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.main.components.AbilityType;

import java.util.ArrayList;
import java.util.List;

public class AbilityMenu implements Listener {

    private final Main plugin;
    private final AbilityManager abilityManager;
    private static final String MENU_TITLE = "§8Select Ability";

    public AbilityMenu(Main plugin, AbilityManager abilityManager) {
        this.plugin = plugin;
        this.abilityManager = abilityManager;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, MENU_TITLE);

        for (AbilityType ability : AbilityType.values()) {
            ItemStack item = new ItemStack(ability.getRequiredDisk().getType());
            ItemMeta meta = item.getItemMeta();
            
            boolean unlocked = abilityManager.hasAbility(player, ability);
            boolean active = ability == abilityManager.getActiveAbility(player);

            meta.setDisplayName((unlocked ? "§b" : "§c") + ability.getName());
            
            List<String> lore = new ArrayList<>();
            lore.add("§7" + ability.getDescription());
            lore.add("");
            lore.add("§7Cost: §b" + ability.getFlowCost() + " Flow");
            lore.add("§7Cooldown: §f" + (ability.getCooldownTicks() / 20.0) + "s");
            lore.add("");
            
            if (active) {
                lore.add("§a▶ SELECTED");
            } else if (unlocked) {
                lore.add("§eClick to select!");
            } else {
                lore.add("§cLocked! Find a disk to unlock.");
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.addItem(item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(MENU_TITLE)) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;
        Player player = (Player) event.getWhoClicked();
        String name = event.getCurrentItem().getItemMeta().getDisplayName().substring(2); // Strip color code

        for (AbilityType ability : AbilityType.values()) {
            if (ability.getName().equals(name)) {
                abilityManager.setActiveAbility(player, ability);
                player.closeInventory();
                break;
            }
        }
    }
}
