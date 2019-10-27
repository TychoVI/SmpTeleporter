package dev.tycho.SmpTravelPoints.listener;

import dev.tycho.SmpTravelPoints.SmpTravelPoints;
import dev.tycho.SmpTravelPoints.database.Teleporter;
import dev.tycho.SmpTravelPoints.gui.TeleporterGui;
import dev.tycho.SmpTravelPoints.util.Filter;
import fr.minuskube.inv.SmartInventory;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

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

        SmpTravelPoints.newChain().asyncFirst(() -> {
            Block button = event.getClickedBlock();
            Location playerLocation = event.getPlayer().getLocation();

            Object inventory = null;

            try {
                List<Teleporter> teleporters = SmpTravelPoints.teleportDao.queryBuilder().where()
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
                            SmpTravelPoints.teleportDao.update(teleporter);
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

                        List<Teleporter> validTeleporters = SmpTravelPoints.teleportDao.queryBuilder().orderBy("name", true).where()
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
                        inventory = new AnvilGUI.Builder()
                                .onComplete((player, text) -> {
                                    try {
                                        for(Entity entity :  enderCrystals) {
                                            EnderCrystal enderCrystal = (EnderCrystal) entity;
                                            enderCrystal.setBeamTarget(event.getClickedBlock().getLocation());
                                        }

                                        teleporter.setName(text);
                                        teleporter.setActive(true);
                                        SmpTravelPoints.teleportDao.update(teleporter);

                                        player.getWorld().playSound(playerLocation, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                                        Bukkit.broadcastMessage(ChatColor.AQUA + "A teleporter to: '" + text + "' has just been activated!");
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return AnvilGUI.Response.close();
                                })
                                .preventClose()
                                .text("Location name")
                                .plugin(Bukkit.getServer().getPluginManager().getPlugin("SmpTravelPoints"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return inventory;
        }).abortIfNull().syncLast((inventory) -> {
            if(inventory instanceof SmartInventory) {
                ((SmartInventory) inventory).open(event.getPlayer());
            } else {
                ((AnvilGUI.Builder) inventory).open(event.getPlayer());
            }
        }).execute();
    }
}
