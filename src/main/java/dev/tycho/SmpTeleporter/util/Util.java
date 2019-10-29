package dev.tycho.SmpTeleporter.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Util {
    public static Location offsetLocation(Location location, double xOffset, double yOffset, double zOffset) {
        location.setX(location.getX() + xOffset);
        location.setY(location.getY() + yOffset);
        location.setZ(location.getZ() + zOffset);
        return location;
    }

    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1, 1);
    }
}
