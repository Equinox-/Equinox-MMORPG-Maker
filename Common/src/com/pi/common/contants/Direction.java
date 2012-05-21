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

	public static Direction getBestDirection(int xC, int zC) {
		if (Math.abs(xC) > Math.abs(zC) && xC != 0) {
			xC = xC / Math.abs(xC);
			for (Direction d : values()) {
				if (d.getXOff() == xC)
					return d;
			}
		} else if (zC != 0) {
			zC = zC / Math.abs(zC);
			for (Direction d : values()) {
				if (d.getZOff() == zC)
					return d;
			}
		}
		return UP;
	}
}
