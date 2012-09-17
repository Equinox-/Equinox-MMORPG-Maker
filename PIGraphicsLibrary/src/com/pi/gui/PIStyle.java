package com.pi.gui;

import java.awt.Color;
import java.awt.Font;

import com.pi.common.constants.GraphicsConstants;
import com.pi.common.database.GraphicsObject;

/**
 * A style used to customize the graphical user interface components.
 * 
 * @author Westin
 * 
 */
public class PIStyle {
	/**
	 * Possible style types.
	 * 
	 * @author Westin
	 * 
	 */
	public static enum StyleType {
		/**
		 * The normal, default style.
		 */
		NORMAL,
		/**
		 * The style showed when the component is focused or active.
		 */
		ACTIVE,
		/**
		 * The style showed when the component is being hovered.
		 */
		HOVER
	}

	/**
	 * Class representing a set of styles.
	 * 
	 * @author Westin
	 * 
	 */
	public static class PIStyleSet {
		/**
		 * The style mapping for this style set.
		 */
		private PIStyle[] styles = new PIStyle[StyleType
				.values().length];

		/**
		 * Checks if the style mapping has a non-null entry for the given style
		 * type.
		 * 
		 * @param type the style type
		 * @return if the mapping is non-null
		 */
		public final boolean containsStyle(
				final PIStyle.StyleType type) {
			return styles[type.ordinal()] != null;
		}

		/**
		 * Gets the style bound to the specified style type.
		 * 
		 * @param type the style type
		 * @return the style instance
		 */
		public final PIStyle getStyle(
				final PIStyle.StyleType type) {
			return styles[type.ordinal()];
		}

		/**
		 * Sets the style bound to the specified type.
		 * 
		 * @param type the style type
		 * @param style the style
		 */
		public final void setStyle(final PIStyle.StyleType type,
				final PIStyle style) {
			styles[type.ordinal()] = style;
		}

		@Override
		public final PIStyleSet clone() {
			PIStyleSet clone = new PIStyleSet();
			for (int i = 0; i < styles.length; i++) {
				clone.styles[i] = styles[i].clone();
			}
			return clone;
		}
	}

	/**
	 * The border color, or <code>null</code> if it shouldn't be rendered.
	 */
	public Color border = null;
	/**
	 * The background color, or <code>null</code> if it shouldn't be rendered.
	 */
	public Color background = null;
	/**
	 * The foreground color, or text color. This should never be null.
	 */
	public Color foreground = GraphicsConstants.FONT_COLOR;
	/**
	 * The default font. This should never be null.
	 */
	public Font font = GraphicsConstants.FONT;
	/**
	 * The background image, or <code>null</code> if one doesn't exist.
	 */
	public GraphicsObject bgImage;
	/**
	 * If the background image should be stretched horizontally to fit the
	 * background, or if it should be tiled.
	 */
	public boolean stretchBackgroundX = false;
	/**
	 * If the background image should be stretched vertically to fit the
	 * background, or if it should be tiled.
	 */
	public boolean stretchBackgroundY = false;
	/**
	 * If the text content of components with this style should be centered
	 * vertically.
	 */
	public boolean vAlign = true;
	/**
	 * If the text content of components with this style should be centered
	 * horizontally.
	 */
	public boolean hAlign = true;

	@Override
	public final PIStyle clone() {
		PIStyle clone = new PIStyle();
		clone.border = border;
		clone.background = background;
		clone.font = font;
		clone.foreground = foreground;
		clone.bgImage = bgImage;
		clone.stretchBackgroundX = stretchBackgroundX;
		clone.stretchBackgroundY = stretchBackgroundY;
		clone.vAlign = vAlign;
		clone.hAlign = hAlign;
		return clone;
	}

	/**
	 * Sets the font size of this style.
	 * 
	 * @param size the new size
	 */
	public final void setFontSize(final float size) {
		font = font.deriveFont(size);
	}
}
