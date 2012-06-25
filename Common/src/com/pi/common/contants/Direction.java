package com.pi.common.contants;

/**
 * An enum representing directions for entities.
 * 
 * @author Westin
 * 
 */
public enum Direction {
	/**
	 * Direction representing down, with an x offset of 0, and a z offset of 1.
	 */
	DOWN(0, 1),
	/**
	 * Direction representing left, with an x offset of -1, and a z offset of 0.
	 */
	LEFT(-1, 0),
	/**
	 * Direction representing right, with an x offset of 1, and a z offset of 0.
	 */
	RIGHT(1, 0),
	/**
	 * Direction representing up, with an x offset of 0, and a z offset of -1.
	 */
	UP(0, -1);

	/**
	 * The coordinates offset for this direction.
	 */
	private int xOff, zOff;

	/**
	 * Creates a direction with the specified x offset and z offset.
	 * 
	 * @param sXOff the x offset
	 * @param sZOff the z offset
	 */
	private Direction(final int sXOff, final int sZOff) {
		this.xOff = sXOff;
		this.zOff = sZOff;
	}

	/**
	 * Gets the x offset for this direction.
	 * 
	 * @return the x offset
	 */
	public int getXOff() {
		return xOff;
	}

	/**
	 * Gets the z offset for this direction.
	 * 
	 * @return the z offset
	 */
	public int getZOff() {
		return zOff;
	}

	/**
	 * Gets the opposite of this direction.
	 * 
	 * @return the opposite direction
	 */
	public Direction getInverse() {
		return getDirection(-xOff, -zOff);
	}

	/**
	 * Gets a direction with the given x offset and z offset.
	 * 
	 * @param xOff the x offset
	 * @param zOff the z offset
	 * @return the direction, or <code>null</code> if unfound
	 */
	public static Direction getDirection(final int xOff,
			final int zOff) {
		for (Direction d : values()) {
			if (d.getXOff() == xOff && d.getZOff() == zOff) {
				return d;
			}
		}
		return null;
	}

	/**
	 * Gets the best direction for an x offset and z offset.
	 * 
	 * @param xC the x offset
	 * @param zC the z offset
	 * @return the best direction, or <code>null</code> if unfound
	 */
	public static Direction getBestDirection(final int xC,
			final int zC) {
		if (Math.abs(xC) > Math.abs(zC) && xC != 0) {
			int xCR = xC / Math.abs(xC);
			for (Direction d : values()) {
				if (d.getXOff() == xCR) {
					return d;
				}
			}
		} else if (zC != 0) {
			int zCR = zC / Math.abs(zC);
			for (Direction d : values()) {
				if (d.getZOff() == zCR) {
					return d;
				}
			}
		}
		return null;
	}
}
