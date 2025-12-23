package org.main;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    public static ItemStack Memory_Disk;
    public static ItemStack High_Disk;

    public static void init(){
        createMemory_Disk();
        createHigh_Disk();
    }
    private static void createHigh_Disk(){
        ItemStack item = new ItemStack(Material.MUSIC_DISC_13);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("High disk");
        meta.addEnchant(Enchantment.BINDING_CURSE, 1 , true);
        List<String> lores = new ArrayList<>();
        lores.add("An high energy memory disk");
        meta.setLore(lores);

        item.setItemMeta(meta);
        High_Disk = item;
    }
    private static void createMemory_Disk(){
        ItemStack item = new ItemStack(Material.MUSIC_DISC_11);

        ItemMeta meta = item.getItemMeta();


        meta.setDisplayName("Memory disk");
        meta.addEnchant(Enchantment.BINDING_CURSE, 1 , true);
        List<String> lores = new ArrayList<>();
        lores.add("An empty memory disk");
        meta.setLore(lores);

        item.setItemMeta(meta);
        Memory_Disk = item;
    }
}

