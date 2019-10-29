package dev.tycho.SmpTeleporter.listener;

import dev.tycho.SmpTeleporter.SmpTeleporter;
import dev.tycho.SmpTeleporter.database.Teleporter;
import dev.tycho.SmpTeleporter.util.Filter;
import dev.tycho.SmpTeleporter.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MessageListener implements Listener {

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if(!SmpTeleporter.nameSetters.containsKey(player.getUniqueId())) {
            return;
        }

        event.setCancelled(true);

        Location teleporterLocation = SmpTeleporter.nameSetters.get(player.getUniqueId());

        if(event.getMessage().length() < 1) {
            return;
        }

        String text = event.getMessage();

        if(text.length() > 32) {
            player.sendMessage(ChatColor.RED + "That name is too long! (max 32), please try again.");
            return;
        }

        if(text.startsWith(" ") || text.endsWith(" ")) {
            player.sendMessage(ChatColor.RED + "The name can not start or end with a space!");
            return;
        }

        SmpTeleporter.newChain().syncFirst(() -> teleporterLocation.getWorld().getNearbyEntities(teleporterLocation, 18, 8, 18, Filter.enderCrystalFilter)).asyncLast((enderCrystals) -> {
            try {
                Location crystalTarget = Util.offsetLocation(teleporterLocation,0, 1, 0);
                for(Entity entity :  enderCrystals) {
                    EnderCrystal enderCrystal = (EnderCrystal) entity;
                    enderCrystal.setBeamTarget(crystalTarget);
                }

                Util.offsetLocation(teleporterLocation, 0, -1, 0);

                List<Teleporter> teleporters = SmpTeleporter.teleportDao.queryBuilder().where()
                        .eq("x", teleporterLocation.getBlockX()).and()
                        .eq("y", teleporterLocation.getBlockY()).and()
                        .eq("z", teleporterLocation.getBlockZ()).query();

                Teleporter teleporter = teleporters.get(0);

                teleporter.setName(text);
                teleporter.setActive(true);
                SmpTeleporter.teleportDao.update(teleporter);

                player.getWorld().playSound(teleporterLocation, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                Bukkit.broadcastMessage(ChatColor.AQUA + "A teleporter to: '" + text + "' has just been activated!");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }).execute();
        SmpTeleporter.nameSetters.remove(player.getUniqueId());
    }
}
