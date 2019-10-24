package dev.tycho.SmpTravelPoints.listener;

import com.j256.ormlite.stmt.QueryBuilder;
import dev.tycho.SmpTravelPoints.SmpTravelPoints;
import dev.tycho.SmpTravelPoints.database.Teleporter;
import dev.tycho.SmpTravelPoints.model.CustomItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Beacon;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class StructureListener implements Listener {
    @EventHandler
    public void onBeaconPlace(BlockPlaceEvent event) {
        if(!event.getItemInHand().isSimilar(CustomItems.teleporter)) {
            return;
        }

        Beacon beacon = (Beacon) event.getBlock().getState();

        Location beaconLocation = beacon.getLocation();
        Location checkLocation1 = beacon.getLocation();
        Location checkLocation2 = beacon.getLocation();
        Location teleportLocation = beacon.getLocation();
        Location buttonLocation = beacon.getLocation();

        checkLocation1.setY(checkLocation1.getY() - 1);
        checkLocation2.setY(checkLocation2.getY() - 2);
        teleportLocation.setY(teleportLocation.getY() - 0);
        buttonLocation.setY(buttonLocation.getBlockY() + 1);

        if(checkLocation1.getBlock().getType() != Material.DIAMOND_BLOCK) {
            return;
        }

        Collection<Entity> nearbyEntities = beaconLocation.getWorld().getNearbyEntities(beaconLocation, 18, 4, 18);

        int crystalCount = 0;

        for(Entity entity : nearbyEntities) {
            if(entity instanceof EnderCrystal) {
                ((EnderCrystal) entity).setBeamTarget(teleportLocation);
                crystalCount++;
            }
        }

        if(crystalCount < 3) {
            return;
        }

        Teleporter teleporter = new Teleporter(beaconLocation, event.getPlayer());
        try {
            SmpTravelPoints.teleportDao.create(teleporter);
            event.getPlayer().playSound(beaconLocation, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @EventHandler
    public void onBeaconBreak(BlockBreakEvent event) {
        if(event.getBlock().getType() != Material.BEACON) {
            return;
        }

        Location beaconLocation = event.getBlock().getLocation();

        QueryBuilder<Teleporter, Integer> queryBuilder = SmpTravelPoints.teleportDao.queryBuilder();
        try {
            List<Teleporter> teleporters = queryBuilder.where().eq("x", beaconLocation.getBlockX()).and().eq("y", beaconLocation.getBlockY()).and().eq("z", beaconLocation.getBlockZ()).query();
            if(teleporters.size() > 0) {
                SmpTravelPoints.teleportDao.delete(teleporters.get(0));

                Collection<Entity> nearbyEntities = beaconLocation.getWorld().getNearbyEntities(beaconLocation, 18, 4, 18);

                for(Entity entity : nearbyEntities) {
                    if(entity instanceof EnderCrystal) {
                        entity.remove();
                    }
                }

                event.setDropItems(false);

                event.getBlock().getWorld().dropItemNaturally(beaconLocation, CustomItems.teleporter);
                event.getPlayer().sendMessage(event.getBlock().getBlockData().getAsString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
