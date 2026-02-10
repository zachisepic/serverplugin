package org.main.components;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;


import javax.sound.sampled.FloatControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.main.ItemManager.Unstable_Shard;
import static org.main.Utils.color;

public enum CustomMob {

    Unstable_Husk("&6Unstable Husk", 40, 50, EntityType.HUSK, new ItemStack(Material.DIAMOND_SWORD), null, new LootItem(Unstable_Shard, 1,3,100))

    ;

    private String name;
    private double maxHealth, spawnChance;
    private EntityType type;
    private ItemStack mainItem;
    private  ItemStack[] armor;
    private List<LootItem> lootTable;

    CustomMob(String name, double maxHealth, double spawnChance, EntityType type, ItemStack mainItem, ItemStack[] armor, LootItem... lootItems) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.spawnChance = spawnChance;
        this.type = type;
        this.mainItem = mainItem;
        this.armor = armor;
        lootTable = Arrays.asList(lootItems);
    }

    public LivingEntity spawn(Location location) {
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, type);
        entity.setCustomNameVisible(true);
        entity.setCustomName(color(name + " &r&c" + (int) maxHealth + "/" + (int) + maxHealth + "♥"));
        entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
        entity.setHealth(maxHealth);
        EntityEquipment inv = entity.getEquipment();
        if (armor != null) inv.setArmorContents(armor);
        inv.setBootsDropChance(0f);
        inv.setChestplateDropChance(0f);
        inv.setLeggingsDropChance(0f);
        inv.setHelmetDropChance(0f);
        inv.setItemInMainHand(mainItem);
        inv.setItemInMainHandDropChance(0f);
        return entity;
    }

    public void tryDropLoot(Location location){
        for (LootItem item : lootTable){
            item.tryDropItem(location);
        }
    }

    public String getName() {
        return name;
    }

    public double getMaxHealth() { return maxHealth; }

    public double getSpawnChance() { return spawnChance; }
}

