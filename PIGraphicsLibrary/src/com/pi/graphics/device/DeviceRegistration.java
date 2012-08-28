package com.pi.graphics.device;

import java.awt.Container;
import java.io.File;

import com.pi.common.debug.PILogger;

/**
 * An interface that allows abstract classes to be registered with the display
 * manager.
 * 
 * @see DisplayManager
 * @author Westin
 * 
 */
public interface DeviceRegistration {

	/**
	 * The logger used the log messages.
	 * 
	 * @return the logger
	 */
	PILogger getLog();

	/**
	 * Method to call when a fatal error occurs.
	 * 
	 * @param s the error message
	 */
	void fatalError(String s);

	/**
	 * Gets the container that the graphics manager should render in, and event
	 * listeners should be registered to.
	 * 
	 * @return the container
	 */
	Container getContainer();

	/**
	 * Gets the thread group any display manager threads should be registered
	 * to.
	 * 
	 * @return the display manager thread group
	 */
	ThreadGroup getThreadGroup();

	/**
	 * Gets the path to the graphics file with the given identification number,
	 * in the given directory.
	 * 
	 * @param dir the graphics directory
	 * @param id the graphics identification.
	 * @return the file instance
	 */
	File getGraphicsFile(int dir, int id);
}
