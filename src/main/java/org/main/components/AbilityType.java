package org.main.components;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.main.ItemManager;

public enum AbilityType {
    SHADOW_STEP("Shadow Step", 30, 100, "Teleport 5 blocks forward.", ItemManager.High_Disk),
    BLOOD_HEAL("Blood Heal", 50, 200, "Heal 2 hearts instantly.", ItemManager.Memory_Disk);

    private final String name;
    private final int flowCost;
    private final int cooldownTicks;
    private final String description;
    private final ItemStack requiredDisk;

    AbilityType(String name, int flowCost, int cooldownTicks, String description, ItemStack requiredDisk) {
        this.name = name;
        this.flowCost = flowCost;
        this.cooldownTicks = cooldownTicks;
        this.description = description;
        this.requiredDisk = requiredDisk;
    }

    public String getName() { return name; }
    public int getFlowCost() { return flowCost; }
    public int getCooldownTicks() { return cooldownTicks; }
    public String getDescription() { return description; }
    public ItemStack getRequiredDisk() { return requiredDisk; }
}
