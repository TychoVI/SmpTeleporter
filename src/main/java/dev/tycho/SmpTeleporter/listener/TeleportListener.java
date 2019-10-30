package dev.tycho.SmpTeleporter.listener;

import dev.tycho.SmpTeleporter.SmpTeleporter;
import dev.tycho.SmpTeleporter.database.Teleporter;
import dev.tycho.SmpTeleporter.gui.TeleporterGui;
import dev.tycho.SmpTeleporter.util.Filter;
import dev.tycho.SmpTeleporter.util.Util;
import fr.minuskube.inv.SmartInventory;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class TeleportListener implements Listener {
    @EventHandler
    public void buttonPress(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) {
            return;
        }
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock().getType() != Material.STONE_BUTTON) {
            return;
        }

        Collection<Entity> enderCrystals = event.getClickedBlock().getLocation().getWorld().getNearbyEntities(event.getClickedBlock().getLocation(), 18, 8, 18, Filter.enderCrystalFilter);

        SmpTeleporter.newChain().asyncFirst(() -> {
            Block button = event.getClickedBlock();
            Location playerLocation = event.getPlayer().getLocation();

            Object inventory = null;

            try {
                List<Teleporter> teleporters = SmpTeleporter.teleportDao.queryBuilder().where()
                        .eq("x", button.getLocation().getBlockX()).and()
                        .eq("y", button.getLocation().getBlockY() - 1).and()
                        .eq("z", button.getLocation().getBlockZ()).query();

                if(teleporters.size() < 1) {
                    return null;
                }

                for(Teleporter teleporter : teleporters) {
                    event.getPlayer().playSound(playerLocation, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                    int level = teleporter.getLevel(enderCrystals);


                    if(teleporter.getActive()) {
                        int range = 0;

                        if(level == 0) {
                            event.getPlayer().sendMessage(ChatColor.RED + "Teleporter structure not valid!");
                            teleporter.setActive(false);
                            teleporter.setName(null);
                            SmpTeleporter.teleportDao.update(teleporter);
                            return null;
                        } else if(level == 1) {
                            range = 500;
                        } else if(level == 2) {
                            range = 1000;
                        } else if(level == 3) {
                            range = 2500;
                        } else if(level == 4) {
                            range = 10000;
                        }

                        for(Entity entity :  enderCrystals) {
                            EnderCrystal enderCrystal = (EnderCrystal) entity;
                            enderCrystal.setBeamTarget(event.getClickedBlock().getLocation());
                        }

                        List<Teleporter> validTeleporters = SmpTeleporter.teleportDao.queryBuilder().orderBy("name", true).where()
                                .between("x", teleporter.getX() - range, teleporter.getX() + range).and()
                                .between("z", teleporter.getZ() - range, teleporter.getZ() + range).and()
                                .eq("active", 1).and()
                                .not().eq("id", teleporter.getId()).query();


                        inventory = TeleporterGui.getInventory(teleporter, validTeleporters);

                    } else {
                        if(level == 0) {
                            event.getPlayer().sendMessage(ChatColor.RED + "Teleporter structure not valid!");
                            return null;
                        }

                        if(!teleporter.getOwner().equals(event.getPlayer().getUniqueId())) {
                            event.getPlayer().sendMessage(ChatColor.RED + "You are not the owner of that teleporter! Tell the owner to activate it.");
                            SmpTeleporter.iconSetters.remove(event.getPlayer().getUniqueId());
                            return null;
                        }

                        SmpTeleporter.nameSetters.put(event.getPlayer().getUniqueId(), Util.offsetLocation(button.getLocation(), 0, -1, 0));

                        for(int i = 0; i < 50; i++) {
                            event.getPlayer().sendMessage(" ");
                        }

                        event.getPlayer().sendMessage(ChatColor.AQUA + "Please type the name you want for this teleporter in chat like you would a normal message.");

                        return null;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return inventory;
        }).abortIfNull().syncLast((inventory) -> {
            if(inventory instanceof SmartInventory) {
                ((SmartInventory) inventory).open(event.getPlayer());
            }
        }).execute();
    }
}
