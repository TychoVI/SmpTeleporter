package dev.tycho.SmpTravelPoints.gui;

import dev.tycho.SmpTravelPoints.SmpTravelPoints;
import dev.tycho.SmpTravelPoints.database.Teleporter;
import dev.tycho.SmpTravelPoints.util.CustomItems;
import dev.tycho.SmpTravelPoints.util.Util;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class TeleporterGui implements InventoryProvider {
    public static final SmartInventory getInventory(Teleporter teleporter, List<Teleporter> teleporters) {
        return SmartInventory.builder()
                .id("teleporterGui")
                .provider(new TeleporterGui(teleporter, teleporters))
                .size(3, 9)
                .title(ChatColor.BLUE + teleporter.getName())
                .manager(SmpTravelPoints.inventoryManager)
                .build();
    }

    private Teleporter teleporter;
    private List<Teleporter> teleporters;

    public TeleporterGui(Teleporter teleporter, List<Teleporter> teleporters) {
        super();
        this.teleporter = teleporter;
        this.teleporters = teleporters;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));

        contents.set(0,4, ClickableItem.of(CustomItems.getTeleporterInfoItem(teleporter), e -> {
            player.performCommand("tpicon");
            contents.inventory().close(player);
        }));

        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[teleporters.size()];

        for(int i = 0; i < teleporters.size(); i++) {
            int finalI = i;
            items[i] = ClickableItem.of(CustomItems.getTeleporterMenuItem(teleporters.get(i), teleporter), e -> {
                SmpTravelPoints.newChain().sync(() -> {
                    Util.playSound(player, Sound.BLOCK_CONDUIT_AMBIENT);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 220, 1, true, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 165, 200, true, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 165, 200, true, false, false));
                    contents.inventory().close(player);
                }).delay(80).sync(() -> {
                    Util.playSound(player, Sound.BLOCK_CONDUIT_AMBIENT);
                    Util.playSound(player, Sound.BLOCK_PORTAL_TRIGGER);
                }).delay(80).sync(() -> {
                    Location location = Util.offsetLocation(teleporters.get(finalI).getLocation(), 0.5, 1, 0.5);
                    location.setPitch(90);
                    Util.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT);
                    player.teleport(location);
                }).execute();
            });
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(7);

        ItemStack nextArrow = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextArrow.getItemMeta();
        nextMeta.setDisplayName(ChatColor.AQUA + "Next page");
        nextArrow.setItemMeta(nextMeta);

        ItemStack previousArrow = new ItemStack(Material.ARROW);
        ItemMeta previousMeta = previousArrow.getItemMeta();
        previousMeta.setDisplayName(ChatColor.AQUA + "Previous page");
        previousArrow.setItemMeta(previousMeta);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1));

        contents.set(2, 3, ClickableItem.of(previousArrow,
                e -> getInventory(teleporter, teleporters).open(player, pagination.previous().getPage())));
        contents.set(2, 5, ClickableItem.of(nextArrow,
                e -> getInventory(teleporter, teleporters).open(player, pagination.next().getPage())));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
