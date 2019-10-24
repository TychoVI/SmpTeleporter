package dev.tycho.SmpTravelPoints.database;

import com.j256.ormlite.field.DatabaseField;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

    @DatabaseField(unique = true)
    private String name;

    @DatabaseField
    private UUID owner;

    public Teleporter() {

    }

    public Teleporter(Location location, Player owner) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.owner = owner.getUniqueId();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }
}