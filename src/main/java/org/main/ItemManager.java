package org.main;

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

    public static void init(){
        createMemory_Disk();
    }

    private static void createMemory_Disk(){
        ItemStack item = new ItemStack(Material.MUSIC_DISC_11);

        ItemMeta meta = item.getItemMeta();


        meta.setDisplayName(Color.GRAY + "Memory disk");
        meta.addEnchant(Enchantment.BREACH, 5, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> lores = new ArrayList<>();
        lores.add("An empty memory disk");
        meta.setLore(lores);

        item.setItemMeta(meta);
        Memory_Disk = item;
    }
}

