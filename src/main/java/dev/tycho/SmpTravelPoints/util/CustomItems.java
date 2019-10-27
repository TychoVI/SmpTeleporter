package dev.tycho.SmpTravelPoints.util;

import dev.tycho.SmpTravelPoints.database.Teleporter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class CustomItems {
    public static ItemStack teleporter;
    public static ItemStack enderDiamond;

    public static void init() {
        //Teleporter
        teleporter = new ItemStack(Material.BEACON, 1);
        ItemMeta teleporterMeta = teleporter.getItemMeta();
        teleporterMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleporter");
        teleporterMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        teleporterMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        teleporter.setItemMeta(teleporterMeta);

        //EnderDiamond
        enderDiamond = new ItemStack(Material.DIAMOND, 1);
        ItemMeta enderDiamondMeta = enderDiamond.getItemMeta();
        enderDiamondMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Ender Diamond");
        enderDiamondMeta.addEnchant(Enchantment.CHANNELING, 1, true);
        enderDiamondMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        enderDiamond.setItemMeta(enderDiamondMeta);
    }

    public static ItemStack getTeleporterMenuItem(Teleporter teleporter, Teleporter origin) {
        ItemStack teleporterItem = new ItemStack(teleporter.getIcon());
        ItemMeta itemMeta = teleporterItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + teleporter.getName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "Distance: " + Math.round(Point2D.distance(teleporter.getX(), teleporter.getZ(), origin.getX(), origin.getZ())) + " blocks.");
        itemMeta.setLore(lore);
        teleporterItem.setItemMeta(itemMeta);

        return teleporterItem;
    }
}
