package org.main;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.main.components.CustomMob;

import java.util.Random;

public class MobListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        CustomMob customMob = null;
        if (event.getEntityType() == EntityType.ZOMBIE) {
            customMob = CustomMob.Dark_Zombie;
        } else if (event.getEntityType() == EntityType.SKELETON) {
            customMob = CustomMob.Dark_Skeleton;
        }

        if (customMob != null) {
            double roll = random.nextDouble() * 100;
            if (roll <= customMob.getSpawnChance()) {
                customMob.apply((LivingEntity) event.getEntity());
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        CustomMob customMob = CustomMob.getCustomMob(event.getEntity());
        if (customMob != null) {
            event.getDrops().clear(); // Clear default Zombie/Skeleton drops
            customMob.tryDropLoot(event.getEntity().getLocation());
        }
    }
}
