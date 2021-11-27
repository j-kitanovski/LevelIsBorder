package com.jannis.levelisborder;

import org.bukkit.World;

public class Util {

    public static final String OVER_WORLD = "over_world";
    public static final String NETHER = "nether";
    public static final String THE_END = "the_end";

    public static String getWorldType(World world) {
        if (world.getName().endsWith(NETHER)) {
            return NETHER;
        } else if (world.getName().endsWith(THE_END)) {
            return THE_END;
        } else {
            return OVER_WORLD;
        }
    }

    public static boolean isOverWorld(World world) {
        return getWorldType(world).equals(OVER_WORLD);
    }

    public static boolean isNether(World world) {
        return getWorldType(world).equals(NETHER);
    }

    public static boolean isTheEnd(World world) {
        return getWorldType(world).equals(THE_END);
    }
}
