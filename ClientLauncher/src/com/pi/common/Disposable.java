package com.pi.common;

/**
 * Interface representing any disposable object.
 * 
 * @author Westin
 * 
 */
public interface Disposable {
	/**
	 * Dispose any resources used by this class, and stops any threads it uses.
	 */
	void dispose();
}
