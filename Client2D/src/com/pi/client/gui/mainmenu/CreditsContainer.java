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
	 * The amounts of pixels per millisecond for scrolling credits.
	 */
	private static final double PIXELS_PER_MILLISECOND = 0.01D;
	/**
	 * The scalar width of the credits label.
	 */
	private static final float CREDITS_LABEL_WIDTH = 0.8f;

	/**
	 * The component that actually shows the credits.
	 */
	private final PIComponent creditLabel = new PIComponent() {
		private long lastUpdate = -1;
		private double yPosition = 0;

		@Override
		public void update() {
			if (!isVisible()
					|| (getParent() != null && !getParent().isVisible())) {
				lastUpdate = -1;
				yPosition = 0;
				return;
			}
			if (lastUpdate != -1) {
				yPosition +=
						(double) (System.currentTimeMillis() - lastUpdate)
								* PIXELS_PER_MILLISECOND;
				setY(getParent().getHeight() - (int) yPosition);
				compile();
			}
			lastUpdate = System.currentTimeMillis();
		}
	};

	/**
	 * Creates an instance of the credits container.
	 */
	public CreditsContainer() {
		setStyle(StyleType.NORMAL,
				GUIKit.DEFAULT_CONTAINER_STYLE);
		creditLabel.setStyle(StyleType.NORMAL,
				GUIKit.DEFAULT_LABEL_STYLE.clone());
		creditLabel.getStyle(StyleType.NORMAL).vAlign = false;
		creditLabel
				.setContent("Equinox MMORPG Maker\n\n"
						+ "Created by Westin Miller alias 314piwm alias Equinox\n\n"
						+ "Thank you to the Jogamp.com Community for JOGL, the Java Bindings for OpenGL");
		setClipContents(true);
		add(creditLabel);
	}

	@Override
	public final void setSize(final int width, final int height) {
		super.setSize(width, height);
		creditLabel.setSize((int) (width * CREDITS_LABEL_WIDTH),
				Integer.MAX_VALUE);
		creditLabel
				.setX((int) (width * ((1f - CREDITS_LABEL_WIDTH) / 2f)));
	}

	@Override
	public final void update() {
		if (!isVisible()) {
			creditLabel.update();
		}
	}
}
