package com.pi.gui;

import java.awt.Color;

import com.pi.common.database.GraphicsAnimation;
import com.pi.gui.PIStyle.StyleType;

/**
 * All of the preset and configured gui component styles.
 * 
 * @author Westin
 * 
 */
public final class GUIKit {
	/**
	 * Any preset graphics objects.
	 * 
	 * @author Westin
	 * 
	 */
	public static class Graphics {
		/**
		 * A graphical animation of a loading bar.
		 */
		public static final GraphicsAnimation LOADER =
				new GraphicsAnimation(12, 150, 1, 0, 0,
						126 * 12, 22);
	}

	/**
	 * The default component style.
	 * 
	 * @see PIComponent
	 */
	public static final PIStyle DEFAULT_STYLE = new PIStyle();

	/**
	 * The default label style.
	 */
	public static final PIStyle DEFAULT_LABEL_STYLE =
			new PIStyle();
	/**
	 * The default container style.
	 * 
	 * @see PIContainer
	 */
	public static final PIStyle DEFAULT_CONTAINER_STYLE =
			new PIStyle();

	/**
	 * The default text field style.
	 * 
	 * @see PITextField
	 */
	public static final PIStyle DEFAULT_TEXTFIELD_STYLE =
			new PIStyle();
	/**
	 * The default text field style when the button is being hovered.
	 * 
	 * @see PITextField
	 */
	public static final PIStyle HOVER_TEXTFIELD_STYLE =
			new PIStyle();
	/**
	 * The default text field style when the button is active.
	 * 
	 * @see PITextField
	 */
	public static final PIStyle ACTIVE_TEXTFIELD_STYLE =
			new PIStyle();
	/**
	 * The default text field style set.
	 * 
	 * @see PITextField
	 */
	public static final PIStyle.PIStyleSet TEXTFIELD_STYLE_SET =
			new PIStyle.PIStyleSet();

	/**
	 * The default button style.
	 * 
	 * @see PIButton
	 */
	public static final PIStyle DEFAULT_BUTTON_STYLE =
			new PIStyle();
	/**
	 * The default button style when the button is active.
	 * 
	 * @see PIButton
	 */
	public static final PIStyle ACTIVE_BUTTON_STYLE =
			new PIStyle();
	/**
	 * The default button style when the button is being hovered.
	 * 
	 * @see PIButton
	 */
	public static final PIStyle HOVER_BUTTON_STYLE =
			new PIStyle();
	/**
	 * The default button style set.
	 * 
	 * @see PIButton
	 */
	public static final PIStyle.PIStyleSet BUTTON_STYLE_SET =
			new PIStyle.PIStyleSet();

	/**
	 * The default check box style.
	 * 
	 * @see PICheckbox
	 */
	public static final PIStyle DEFAULT_CHECKBOX_STYLE =
			new PIStyle();
	/**
	 * The default check box style when the check box is checked.
	 * 
	 * @see PICheckbox
	 */
	public static final PIStyle ACTIVE_CHECKBOX_STYLE =
			new PIStyle();
	/**
	 * The default check box style when the check box is being hovered.
	 * 
	 * @see PICheckbox
	 */
	public static final PIStyle HOVER_CHECKBOX_STYLE =
			new PIStyle();
	/**
	 * The default check box style set.
	 * 
	 * @see PICheckbox
	 */
	public static final PIStyle.PIStyleSet CHECKBOX_STYLE_SET =
			new PIStyle.PIStyleSet();

	/**
	 * The default loading bar style.
	 * 
	 * @see LoadingBar
	 */
	public static final PIStyle DEFAULT_LOADING_BAR_STYLE =
			new PIStyle();

	/**
	 * The default scroll bar container style.
	 * 
	 * @see PIScrollBar
	 */
	public static final PIStyle DEFAULT_SCROLLBAR_CONTAINER_STYLE =
			new PIStyle();
	/**
	 * The default scroll bar current scroll amount style.
	 * 
	 * @see PIScrollBar
	 */
	public static final PIStyle DEFAULT_SCROLLBAR_CURRENT_STYLE =
			new PIStyle();

	/**
	 * If this GUIKit instance has been configured.
	 */
	private static boolean initiated = false;

	/**
	 * Initiate and configure all the styles in the {@link #initiated} is false.
	 */
	public static void init() {
		if (!initiated) {
			// Buttons start
			HOVER_BUTTON_STYLE.background =
					new Color(0.05f, 0.05f, 0.05f);
			HOVER_BUTTON_STYLE.foreground = Color.white;
			HOVER_BUTTON_STYLE.border =
					new Color(0.5f, 0.5f, 0.5f);
			ACTIVE_BUTTON_STYLE.background =
					new Color(0.1f, 0.1f, 0.1f);
			ACTIVE_BUTTON_STYLE.foreground = Color.white;
			ACTIVE_BUTTON_STYLE.border =
					new Color(0.5f, 0.5f, 0.5f);
			DEFAULT_BUTTON_STYLE.background =
					new Color(0.2f, 0.2f, 0.2f);
			DEFAULT_BUTTON_STYLE.foreground = Color.white;
			DEFAULT_BUTTON_STYLE.border =
					new Color(0.5f, 0.5f, 0.5f);
			BUTTON_STYLE_SET.setStyle(StyleType.ACTIVE,
					ACTIVE_BUTTON_STYLE);
			BUTTON_STYLE_SET.setStyle(StyleType.HOVER,
					HOVER_BUTTON_STYLE);
			BUTTON_STYLE_SET.setStyle(StyleType.NORMAL,
					DEFAULT_BUTTON_STYLE);
			// Buttons end

			// Textfield start
			DEFAULT_TEXTFIELD_STYLE.hAlign = false;
			DEFAULT_TEXTFIELD_STYLE.border =
					new Color(0.5f, 0.5f, 0.5f);
			DEFAULT_TEXTFIELD_STYLE.background =
					new Color(0.25f, 0.25f, 0.25f);
			DEFAULT_TEXTFIELD_STYLE.foreground = Color.white;

			ACTIVE_TEXTFIELD_STYLE.border =
					new Color(0.5f, 0.5f, 0.5f);
			ACTIVE_TEXTFIELD_STYLE.background =
					new Color(0.2f, 0.2f, 0.2f);
			ACTIVE_TEXTFIELD_STYLE.hAlign = false;
			ACTIVE_TEXTFIELD_STYLE.foreground = Color.white;

			HOVER_TEXTFIELD_STYLE.border =
					new Color(0.5f, 0.5f, 0.5f);
			HOVER_TEXTFIELD_STYLE.background =
					new Color(0.15f, 0.15f, 0.15f);
			HOVER_TEXTFIELD_STYLE.hAlign = false;
			HOVER_TEXTFIELD_STYLE.foreground = Color.white;

			TEXTFIELD_STYLE_SET.setStyle(StyleType.ACTIVE,
					ACTIVE_TEXTFIELD_STYLE);
			TEXTFIELD_STYLE_SET.setStyle(StyleType.NORMAL,
					DEFAULT_TEXTFIELD_STYLE);
			TEXTFIELD_STYLE_SET.setStyle(StyleType.HOVER,
					HOVER_TEXTFIELD_STYLE);
			// Textfield end

			// Checkbox start
			DEFAULT_CHECKBOX_STYLE.border =
					new Color(0.9f, 0.9f, 0.9f);
			DEFAULT_CHECKBOX_STYLE.background =
					new Color(0.75f, 0.75f, 0.75f);
			DEFAULT_CHECKBOX_STYLE.foreground = Color.white;
			DEFAULT_CHECKBOX_STYLE.hAlign = false;

			ACTIVE_CHECKBOX_STYLE.border =
					new Color(0.6f, 0.6f, 0.6f);
			ACTIVE_CHECKBOX_STYLE.background =
					new Color(0.45f, 0.45f, 0.45f);
			ACTIVE_CHECKBOX_STYLE.foreground = Color.white;
			ACTIVE_CHECKBOX_STYLE.hAlign = false;

			CHECKBOX_STYLE_SET.setStyle(StyleType.ACTIVE,
					ACTIVE_CHECKBOX_STYLE);
			CHECKBOX_STYLE_SET.setStyle(StyleType.NORMAL,
					DEFAULT_CHECKBOX_STYLE);
			// Checkbox end

			// Container start
			DEFAULT_CONTAINER_STYLE.background =
					new Color(0.2f, 0.2f, 0.2f);
			DEFAULT_CONTAINER_STYLE.foreground = Color.white;
			DEFAULT_CONTAINER_STYLE.border =
					new Color(0.5f, 0.5f, 0.5f);
			// Container end

			DEFAULT_LABEL_STYLE.foreground = Color.white;

			DEFAULT_LOADING_BAR_STYLE.foreground = Color.white;
			DEFAULT_LOADING_BAR_STYLE.border = Color.lightGray;
			DEFAULT_LOADING_BAR_STYLE.background =
					Color.darkGray;

			// Scroll bar start
			DEFAULT_SCROLLBAR_CONTAINER_STYLE.background =
					new Color(0.2f, 0.2f, 0.2f, 0.2f);
			DEFAULT_SCROLLBAR_CURRENT_STYLE.background =
					new Color(0.1f, 0.1f, 0.1f);
			// Scroll bar end

			initiated = true;
		}
	}

	/**
	 * Overridden constructor to prevent this class from being produced.
	 */
	private GUIKit() {
	}
}
