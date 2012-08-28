package com.pi.graphics.device;

/**
 * Abstract class used to represent a storage graphics object and the time of
 * the last use.
 * 
 * @author Westin
 * 
 */
public class GraphicsStorage {
	/**
	 * The last time at which this graphics object was used.
	 */
	private long lastUsed;

	/**
	 * The graphics object this class is storing.
	 */
	private Object gObj;

	/**
	 * The graphics object that is wrapped by this storage object.
	 * 
	 * @return the object
	 */
	public final Object getGraphic() {
		return gObj;
	}

	/**
	 * Sets the graphics object that is wrapped by this storage object.
	 * 
	 * @param t the new object
	 */
	public final void setGraphic(final Object t) {
		this.gObj = t;
	}

	/**
	 * Updates the last used time to the current time.
	 */
	public final void updateLastUsed() {
		lastUsed = System.currentTimeMillis();
	}

	/**
	 * Gets the last used time for this graphics object.
	 * 
	 * @return the last used time
	 */
	public final long getLastUsedTime() {
		return lastUsed;
	}
}
