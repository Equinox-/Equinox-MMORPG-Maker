package com.pi.client.gui.mainmenu;

import com.pi.gui.GUIKit;
import com.pi.gui.PIComponent;
import com.pi.gui.PIContainer;
import com.pi.gui.PIStyle.StyleType;

/**
 * A container showing the credits for the game engine.
 * 
 * @author Westin
 * 
 */
public class CreditsContainer extends PIContainer {
	/**
	 * The scalar width of the credits label.
	 */
	private static final float CREDITS_LABEL_WIDTH = 0.8f;
	/**
	 * The scalar height of the credits label.
	 */
	private static final float CREDITS_LABEL_HEIGHT = 0.8f;

	/**
	 * The component that actually shows the credits.
	 */
	private final PIComponent creditLabel = new PIComponent();

	/**
	 * Creates an instance of the credits container.
	 */
	public CreditsContainer() {
		setStyle(StyleType.NORMAL,
				GUIKit.DEFAULT_CONTAINER_STYLE);
		creditLabel.setStyle(StyleType.NORMAL,
				GUIKit.DEFAULT_LABEL_STYLE);
		creditLabel
				.setContent("Equinox MMORPG Maker\n\n"
						+ "Created by Westin Miller alias 314piwm alias Equinox\n\n"
						+ "Thank you to the Jogamp.com Community for JOGL, the Java Bindings for OpenGL");
		add(creditLabel);
	}

	@Override
	public final void setSize(final int width, final int height) {
		super.setSize(width, height);
		creditLabel.setSize((int) (width * CREDITS_LABEL_WIDTH),
				(int) (height * CREDITS_LABEL_HEIGHT));
		creditLabel
				.setLocation(
						(int) (width * ((1f - CREDITS_LABEL_WIDTH) / 2f)),
						(int) (height * ((1f - CREDITS_LABEL_HEIGHT) / 2f)));
	}
}
