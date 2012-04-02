package com.pi.common.contants;

public enum Direction {
    DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0), UP(0, -1);
    int xOff, zOff;

    private Direction(int xOff, int zOff) {
	this.xOff = xOff;
	this.zOff = zOff;
    }

    public int getXOff() {
	return xOff;
    }

    public int getZOff() {
	return zOff;
    }
}
