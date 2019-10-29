package dev.tycho.SmpTeleporter.listener;

import dev.tycho.SmpTeleporter.SmpTeleporter;
import dev.tycho.SmpTeleporter.database.Teleporter;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.SQLException;
import java.util.List;

public class SetIconListener implements Listener {

    @EventHandler
    public void onBlockPunch(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_BLOCK && SmpTeleporter.iconSetters.contains(event.getPlayer().getUniqueId())) {
            SmpTeleporter.newChain().async(() -> {
                Block beacon = event.getClickedBlock();
                try {
                    List<Teleporter> teleporters = SmpTeleporter.teleportDao.queryBuilder().where()
                            .eq("x", beacon.getLocation().getBlockX()).and()
                            .eq("y", beacon.getLocation().getBlockY()).and()
                            .eq("z", beacon.getLocation().getBlockZ()).query();

                    if(teleporters.size() > 0) {

                        if(!teleporters.get(0).getOwner().equals(event.getPlayer().getUniqueId())) {
                            event.getPlayer().sendMessage(ChatColor.RED + "You are not the owner of that teleporter!");
                            SmpTeleporter.iconSetters.remove(event.getPlayer().getUniqueId());
                        } else {
                            event.getPlayer().sendMessage(ChatColor.GREEN + "Set icon successfully!");
                            teleporters.get(0).setIcon(event.getPlayer().getInventory().getItemInMainHand().getType());
                            SmpTeleporter.teleportDao.update(teleporters.get(0));
                        }
                    } else {
                        event.getPlayer().sendMessage(ChatColor.RED + "That is not a teleporter!");
                        SmpTeleporter.iconSetters.remove(event.getPlayer().getUniqueId());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }).execute();
        }
    }
}
