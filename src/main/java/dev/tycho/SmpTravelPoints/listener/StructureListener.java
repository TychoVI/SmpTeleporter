package dev.tycho.SmpTravelPoints.listener;

import com.j256.ormlite.stmt.QueryBuilder;
import dev.tycho.SmpTravelPoints.SmpTravelPoints;
import dev.tycho.SmpTravelPoints.database.Teleporter;
import dev.tycho.SmpTravelPoints.util.CustomItems;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class StructureListener implements Listener {
    @EventHandler
    public void onBeaconPlace(BlockPlaceEvent event) {
        if(!event.getItemInHand().isSimilar(CustomItems.teleporter)) {
            return;
        }

        Location buttonLocation = event.getBlock().getLocation();
        buttonLocation.setY(buttonLocation.getBlockY() + 1);

        buttonLocation.getBlock().setType(Material.STONE_BUTTON);
        Switch faceData = (Switch) buttonLocation.getBlock().getBlockData();
        faceData.setFace(Switch.Face.FLOOR);
        buttonLocation.getBlock().setBlockData(faceData);

        SmpTravelPoints.newChain().async(() -> {
            try {
                Teleporter teleporter = new Teleporter(event.getBlock().getLocation(), event.getPlayer(), false);
                SmpTravelPoints.teleportDao.create(teleporter);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).execute();

    }

    @EventHandler
    public void onBeaconBreak(BlockBreakEvent event) {
        if(event.getBlock().getType() != Material.BEACON) {
            return;
        }

        event.setDropItems(false);

        Location beaconLocation = event.getBlock().getLocation();

        SmpTravelPoints.newChain().asyncFirst(() -> {
            QueryBuilder<Teleporter, Integer> queryBuilder = SmpTravelPoints.teleportDao.queryBuilder();
            List<Teleporter> teleporters = null;
            try {
                teleporters = queryBuilder.where().eq("x", beaconLocation.getBlockX()).and().eq("y", beaconLocation.getBlockY()).and().eq("z", beaconLocation.getBlockZ()).query();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return teleporters;
        }).sync((teleporters) -> {
            if(teleporters.size() > 0) {
                Teleporter teleporter = teleporters.get(0);

                if(teleporter.getActive()) {
                    Collection<Entity> nearbyEntities = beaconLocation.getWorld().getNearbyEntities(beaconLocation, 18, 8, 18);

                    for(Entity entity : nearbyEntities) {
                        if(entity instanceof EnderCrystal) {
                            entity.remove();
                        }
                    }
                }

                if(event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    event.getBlock().getWorld().dropItemNaturally(beaconLocation, CustomItems.teleporter);
                }
                return teleporter;
            } else {
                if(event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    event.getBlock().getWorld().dropItemNaturally(beaconLocation, new ItemStack(Material.BEACON));
                }
            }
            return null;
        }).abortIfNull().asyncLast(teleporter -> {
            try {
                SmpTravelPoints.teleportDao.delete(teleporter);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).execute();
    }
}
