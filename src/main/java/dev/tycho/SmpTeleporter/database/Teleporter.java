package dev.tycho.SmpTeleporter.database;

import com.j256.ormlite.field.DatabaseField;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class Teleporter {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int x;

    @DatabaseField
    private int y;

    @DatabaseField
    private int z;

    @DatabaseField
    private UUID world;

    @DatabaseField
    boolean active;

    @DatabaseField()
    private String name;

    @DatabaseField
    private UUID owner;

    @DatabaseField
    private String icon;

    public Teleporter() {

    }

    public Teleporter(Location location, Player owner, boolean active) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getWorld().getUID();
        this.owner = owner.getUniqueId();
        this.active = active;
        this.icon = Material.BEACON.name();
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public void setLocation(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Material getIcon() {
        return Material.getMaterial(icon);
    }

    public void setIcon(Material material) {
        this.icon = material.name();
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) { this.active = active; }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public int getLevel(Collection<Entity> enderCrystals) {
        int crystalcount = 0;
        for(int i = 0; i < enderCrystals.size(); i++) {
            crystalcount++;
        }

        Location sampleBlockLocation = getLocation();
        sampleBlockLocation.setY(sampleBlockLocation.getBlockY() - 1);
        Material teleporterMaterial = sampleBlockLocation.getBlock().getType();

        int blockCount = 1;
        for(int i = -1; i > -5; i--) {
            blockCount += 2;
            int offsetX = -i;
            int offsetY = -i;

            for(int ii = 0; ii < blockCount*blockCount; ii++) {
                Location checkLocation = getLocation();
                checkLocation.setX(checkLocation.getBlockX() + offsetX);
                checkLocation.setY(checkLocation.getBlockY() + i);
                checkLocation.setZ(checkLocation.getBlockZ() + offsetY);

                if(checkLocation.getBlock().getType() != teleporterMaterial) {
                    return 0;
                }

                if(offsetX == i) {
                    offsetX = -i;
                    offsetY--;
                } else {
                    offsetX--;
                }
            }
        }
        if(teleporterMaterial == Material.IRON_BLOCK && crystalcount > 1) {
            return 1;
        } else if(teleporterMaterial == Material.GOLD_BLOCK && crystalcount > 3) {
            return 2;
        } else if(teleporterMaterial == Material.EMERALD_BLOCK && crystalcount > 5) {
            return 3;
        } else if(teleporterMaterial == Material.DIAMOND_BLOCK && crystalcount > 7) {
            return 4;
        } else {
            return 0;
        }
    }
}