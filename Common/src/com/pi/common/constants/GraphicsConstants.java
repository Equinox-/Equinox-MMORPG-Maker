package com.pi.common.constants;

import java.awt.Color;
import java.awt.Font;

/**
 * Class containing graphical constants.
 * 
 * @author Westin
 * 
 */
public final class GraphicsConstants {
	/**
	 * The default font used by graphics.
	 */
	public static final Font FONT = new Font(Font.SERIF,
			Font.PLAIN, 12);
	/**
	 * The default font color.
	 */
	public static final Color FONT_COLOR = Color.black;

	/**
	 * Overridden constructor to prevent instances of this class from being
	 * created.
	 */
	private GraphicsConstants() {

	}
}
