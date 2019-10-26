package dev.tycho.SmpTravelPoints.model;

import dev.tycho.SmpTravelPoints.util.CustomItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderDiamondRecipe {
    public EnderDiamondRecipe(JavaPlugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "enderDiamond");
        ShapedRecipe recipe = new ShapedRecipe(key, CustomItems.enderDiamond);

        recipe.shape("DDD", "DPD", "DDD");

        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('P', Material.ENDER_PEARL);

        Bukkit.addRecipe(recipe);
    }
}
