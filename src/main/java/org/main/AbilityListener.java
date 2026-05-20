package org.main;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.main.components.AbilityType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityListener implements Listener {

    private final Main plugin;
    private final AbilityManager abilityManager;
    private final AbilityCaster abilityCaster;
    
    // UUID -> AbilityName -> EndTimeMillis
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public AbilityListener(Main plugin, AbilityManager abilityManager) {
        this.plugin = plugin;
        this.abilityManager = abilityManager;
        this.abilityCaster = new AbilityCaster(plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // --- UNLOCK LOGIC (Right Click with Disk) ---
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (item != null && item.getType() != Material.AIR) {
                for (AbilityType ability : AbilityType.values()) {
                    if (item.isSimilar(ability.getRequiredDisk())) {
                        event.setCancelled(true);
                        abilityManager.unlockAbility(player, ability);
                        
                        if (item.getAmount() > 1) {
                            item.setAmount(item.getAmount() - 1);
                        } else {
                            player.getInventory().setItemInMainHand(null);
                        }
                        return;
                    }
                }
            }
        }

        // --- CAST LOGIC (Shift + Left Click) ---
        if (player.isSneaking() && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            AbilityType active = abilityManager.getActiveAbility(player);
            if (active != null) {
                attemptCast(player, active);
            } else {
                player.sendMessage("§cYou don't have an active ability selected! Use /abilities");
            }
        }
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            event.setCancelled(true);
            abilityManager.cycleAbility(player);
        }
    }

    private void attemptCast(Player player, AbilityType ability) {
        // 1. Check Cooldown
        long now = System.currentTimeMillis();
        Map<String, Long> playerCooldowns = cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        if (playerCooldowns.getOrDefault(ability.name(), 0L) > now) {
            long remaining = (playerCooldowns.get(ability.name()) - now) / 1000;
            player.sendMessage("§c" + ability.getName() + " is on cooldown! (" + remaining + "s)");
            return;
        }

        // 2. Check Flow
        int currentFlow = plugin.getFlow(player);
        if (currentFlow < ability.getFlowCost()) {
            player.sendMessage("§cNot enough Flow! (Need " + ability.getFlowCost() + ")");
            return;
        }

        // 3. Cast
        abilityCaster.cast(player, ability);
        
        // 4. Deduct Flow and Set Cooldown
        plugin.setFlow(player, currentFlow - ability.getFlowCost());
        playerCooldowns.put(ability.name(), now + (ability.getCooldownTicks() * 50L)); // 1 tick = 50ms
        
        player.sendMessage("§b§l[Ability] §7Used §f" + ability.getName() + "§7!");
    }
}
