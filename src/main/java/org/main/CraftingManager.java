package org.main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import static org.main.ItemManager.*;


public class CraftingManager {

    private final JavaPlugin plugin;

    public CraftingManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerAllRecipes() {
        registerMemoryDiskRecipe();
        registerHighFlowDiskRecipe();
        registerLowFlowDiskRecipe();
        // add more here
    }

    private void registerMemoryDiskRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(
                NamespacedKey.fromString("memory_disk", plugin),
                ItemManager.Memory_Disk // your custom item
        );

        recipe.shape("   ", " e ", " q ");
        recipe.setIngredient('e', Material.ECHO_SHARD);
        recipe.setIngredient('q', Material.QUARTZ_BLOCK);
        Bukkit.addRecipe(recipe);
    }

    private void registerHighFlowDiskRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(
                NamespacedKey.fromString("high_disk", plugin),
                ItemManager.High_Disk // your custom item
        );

        recipe.shape("   ", " e ", " q ");
        recipe.setIngredient('e', Material.IRON_INGOT);
        recipe.setIngredient('q', new RecipeChoice.ExactChoice(Memory_Disk));
        Bukkit.addRecipe(recipe);
    }

    private void registerLowFlowDiskRecipe() {
        // same idea, your custom ingredients
    }
}
