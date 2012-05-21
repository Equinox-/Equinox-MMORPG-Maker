package com.pi.common.contants;

public class TileFlags {
	public static int WALL_NORTH = 1;
	public static int WALL_SOUTH = 2;
	public static int WALL_EAST = 4;
	public static int WALL_WEST = 8;
	public static int BLOCKED = WALL_NORTH | WALL_SOUTH | WALL_EAST | WALL_WEST;
}
