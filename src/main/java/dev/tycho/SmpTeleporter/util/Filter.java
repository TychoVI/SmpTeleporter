package dev.tycho.SmpTeleporter.util;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;

import java.util.function.Predicate;

public class Filter {
    public static Predicate<Entity> enderCrystalFilter = entity -> entity instanceof EnderCrystal;
}
