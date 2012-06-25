package com.pi.client.gui.mainmenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.pi.graphics.device.DisplayManager.GraphicsMode;
import com.pi.gui.GUIKit;
import com.pi.gui.PICheckbox;
import com.pi.gui.PIContainer;
import com.pi.gui.PIStyle.StyleType;

/**
 * Container providing settings configuration.
 * 
 * @author Westin
 * 
 */
public class SettingsContainer extends PIContainer {
	/**
	 * The scalar horizontal padding of this container.
	 */
	private static final float HORIZONTAL_PADDING = 0.02f;
	/**
	 * The scalar vertical padding of this container.
	 */
	private static final float VERTICAL_PADDING = 0.02f;

	/**
	 * The scalar checkbox width.
	 */
	private static final float CHECKBOX_WIDTH = 0.2f;
	/**
	 * The scalar checkbox height.
	 */
	private static final float CHECKBOX_HEIGHT = 0.066f;

	/**
	 * The main menu instance that this container is bound to.
	 */
	private final MainMenu menu;
	/**
	 * The checkboxes for setting the graphic mode to OpenGL.
	 */
	private final PICheckbox opengl = new PICheckbox();
	/**
	 * The checkboxes for setting the graphic mode to Java2D, or AWT.
	 */
	private final PICheckbox awt = new PICheckbox();

	/**
	 * Creates and binds a settings container to the provided main menu.
	 * 
	 * @param sMenu the main menu
	 */
	public SettingsContainer(final MainMenu sMenu) {
		setStyle(StyleType.Normal, GUIKit.containerNormal);
		this.menu = sMenu;
		add(opengl);
		opengl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (!menu.getClient().getDisplayManager()
						.getMode().equals(GraphicsMode.OpenGL)) {
					menu.getClient().getDisplayManager()
							.setMode(GraphicsMode.OpenGL);
				}
			}
		});
		opengl.setContent("OpenGL");

		add(awt);
		awt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (!menu.getClient().getDisplayManager()
						.getMode().equals(GraphicsMode.AWT)) {
					menu.getClient().getDisplayManager()
							.setMode(GraphicsMode.AWT);
				}
			}
		});
		awt.setContent("Safe Mode");
	}

	@Override
	public final void update() {
		opengl.setVisible(menu.getClient().getDisplayManager()
				.hasOpenGL());
		if (menu.getClient().getDisplayManager().getMode()
				.equals(GraphicsMode.AWT)) {
			awt.setChecked(true);
			opengl.setChecked(false);
		} else {
			opengl.setChecked(true);
			awt.setChecked(false);
		}
	}

	@Override
	public final void setSize(final int width, final int height) {
		super.setSize(width, height);
		awt.setLocation((int) (width * HORIZONTAL_PADDING),
				(int) (height * VERTICAL_PADDING));
		awt.setSize((int) (width * CHECKBOX_WIDTH),
				(int) (height * CHECKBOX_HEIGHT));
		opengl.setLocation(width / 2,
				(int) (height * VERTICAL_PADDING));
		opengl.setSize((int) (width * CHECKBOX_WIDTH),
				(int) (height * CHECKBOX_HEIGHT));
	}
}
