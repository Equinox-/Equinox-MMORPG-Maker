package com.pi.common.contants;

public class TileFlags {
    public static int WALL_SOUTHWEST = 16;
    public static int WALL_SOUTHEAST = 32;
    public static int WALL_NORTHWEST = 64;
    public static int WALL_NORTHEAST = 128;
    public static int WALL_NORTH = 1 | WALL_NORTHWEST | WALL_NORTHEAST;
    public static int WALL_SOUTH = 2 | WALL_SOUTHWEST | WALL_SOUTHEAST;
    public static int WALL_EAST = 4 | WALL_SOUTHEAST | WALL_NORTHEAST;
    public static int WALL_WEST = 8 | WALL_SOUTHWEST | WALL_NORTHWEST;
    public static int BLOCKED = 256 | WALL_NORTH | WALL_SOUTH | WALL_EAST
	    | WALL_WEST;
}
