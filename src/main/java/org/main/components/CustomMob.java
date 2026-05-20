package org.main.components;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;


import java.util.Arrays;
import java.util.List;

import static org.main.ItemManager.Unstable_Shard;
import static org.main.Utils.color;

public enum CustomMob {

    Dark_Zombie("&6Dark Zombie", 40, 50, EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD), null, new LootItem(Unstable_Shard, 1,3,100)),
    Dark_Skeleton("&6Dark Skeleton", 30, 50, EntityType.SKELETON, new ItemStack(Material.BOW), null, new LootItem(Unstable_Shard, 1,3,100))
    ;

    public static final NamespacedKey MOB_KEY = new NamespacedKey("serverplugin", "custom_mob");

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

    public void apply(LivingEntity entity) {
        entity.setCustomNameVisible(true);
        entity.setCustomName(color(name + " &r&c" + (int) maxHealth + "/" + (int) maxHealth + "♥"));
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        entity.setHealth(maxHealth);
        EntityEquipment inv = entity.getEquipment();
        if (inv != null) {
            if (armor != null) inv.setArmorContents(armor);
            inv.setBootsDropChance(0f);
            inv.setChestplateDropChance(0f);
            inv.setLeggingsDropChance(0f);
            inv.setHelmetDropChance(0f);
            inv.setItemInMainHand(mainItem);
            inv.setItemInMainHandDropChance(0f);
        }
        entity.getPersistentDataContainer().set(MOB_KEY, PersistentDataType.STRING, this.name());
    }

    public static CustomMob getCustomMob(Entity entity) {
        if (entity == null) return null;
        String name = entity.getPersistentDataContainer().get(MOB_KEY, PersistentDataType.STRING);
        if (name == null) return null;
        try {
            return CustomMob.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public LivingEntity spawn(Location location) {
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, type);
        apply(entity);
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

