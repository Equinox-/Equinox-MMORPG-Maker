package com.pi.graphics.device;

/**
 * Interface to represent any class that can be rendered to a graphics device.
 * 
 * @author Westin
 * 
 */
public interface Renderable {
	/**
	 * Render this object to the specified graphics device.
	 * 
	 * @param graphics the graphics device
	 */
	void render(IGraphics graphics);
}
