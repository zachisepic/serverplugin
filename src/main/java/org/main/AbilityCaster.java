package org.main;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.main.components.AbilityType;

public class AbilityCaster {

    private final Main plugin;

    public AbilityCaster(Main plugin) {
        this.plugin = plugin;
    }

    public void cast(Player player, AbilityType ability) {
        switch (ability) {
            case SHADOW_STEP:
                castShadowStep(player);
                break;
            case BLOOD_HEAL:
                castBloodHeal(player);
                break;
        }
    }

    private void castShadowStep(Player player) {
        Location loc = player.getLocation();
        Vector direction = loc.getDirection().normalize();
        
        // Move 5 blocks forward
        Location target = loc.clone().add(direction.multiply(5));
        
        // Simple "safe" check - don't teleport into walls
        if (target.getBlock().getType().isSolid()) {
            target = loc; // Fallback to current loc if blocked
            player.sendMessage("§cPath blocked!");
            return;
        }

        player.teleport(target);
        player.getWorld().spawnParticle(Particle.LARGE_SMOKE, target, 20, 0.2, 0.5, 0.2, 0.05);
        player.getWorld().playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
    }

    private void castBloodHeal(Player player) {
        double max = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
        player.setHealth(Math.min(max, player.getHealth() + 4)); // Heal 2 hearts (4 points)
        
        player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, player.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITCH_DRINK, 1.0f, 0.8f);
    }
}
