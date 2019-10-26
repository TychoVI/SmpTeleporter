package dev.tycho.SmpTravelPoints.model;

import dev.tycho.SmpTravelPoints.util.CustomItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleporterRecipe implements Listener {
    public TeleporterRecipe(JavaPlugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "teleporter");
        ShapedRecipe recipe = new ShapedRecipe(key, CustomItems.teleporter);

        recipe.shape("PDP", "DBD", "PDP");

        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('B', Material.BEACON);
        recipe.setIngredient('P', Material.ENDER_PEARL);

        Bukkit.addRecipe(recipe);
    }

    @EventHandler
    public void teleporterCraft(CraftItemEvent event) {
        CraftingInventory inv = event.getInventory();

        if(inv.getResult() == null) {
            return;
        }

        if(inv.getResult().isSimilar(CustomItems.teleporter)) {
            for(ItemStack item : inv.getMatrix()) {
                if(item.getType() == Material.DIAMOND && !item.isSimilar(CustomItems.enderDiamond)) {
                    inv.setResult(null);
                    event.setCancelled(true);
                }
            }
        }
    }
}
