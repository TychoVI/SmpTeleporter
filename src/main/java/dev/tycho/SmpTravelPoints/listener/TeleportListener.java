package dev.tycho.SmpTravelPoints.listener;

import com.j256.ormlite.stmt.QueryBuilder;
import dev.tycho.SmpTravelPoints.SmpTravelPoints;
import dev.tycho.SmpTravelPoints.database.Teleporter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.SQLException;
import java.util.List;

public class TeleportListener implements Listener {

    @EventHandler
    public void buttonPress(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) {
            return;
        }
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() != Material.STONE_BUTTON) {
            return;
        }
        Location location = event.getClickedBlock().getLocation();
        location.setY(location.getBlockY() - 1);
        if(location.getBlock().getType() != Material.BEACON) {
            return;
        }

        QueryBuilder<Teleporter, Integer> queryBuilder = SmpTravelPoints.teleportDao.queryBuilder();
        try {
            List<Teleporter> teleporters = queryBuilder.where().eq("x", location.getBlockX()).and().eq("y", location.getBlockY()).and().eq("z", location.getBlockZ()).query();
            if(teleporters.size() > 0) {
                Location to = new Location(event.getPlayer().getWorld(), 231, 88, -204);
                event.getPlayer().teleport(to);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
