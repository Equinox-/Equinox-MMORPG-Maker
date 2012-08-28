package com.pi.client.gui.game;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.pi.common.database.GraphicsObject;
import com.pi.common.database.io.GraphicsDirectories;
import com.pi.gui.PIComponent;
import com.pi.gui.PIContainer;
import com.pi.gui.PIStyle;
import com.pi.gui.PIStyle.StyleType;

/**
 * Container for the main game buttons.
 * 
 * @author Westin
 * 
 */
public class MainGameMenu extends PIContainer {
	/**
	 * The width in pixels of each button.
	 */
	private static final int BUTTON_WIDTH = 30;
	/**
	 * The height in pixels of each button.
	 */
	private static final int BUTTON_HEIGHT = 30;
	/**
	 * If this should layout it's sub elements vertically instead of
	 * horizontally.
	 */
	private static final boolean LAYOUT_VERTICALLY = false;

	/**
	 * The controlled array.
	 */
	private PIComponent[] menuOptions;

	/**
	 * The button array.
	 */
	private PIComponent[] menuButtons;

	/**
	 * The GUI instance this bound to.
	 */
	private MainGameGUI gui;

	/**
	 * The dimension the current layout is for.
	 */
	private Dimension currentLayout = null;

	/**
	 * Creates the main game buttons and sub menus for the given GUI instance.
	 * 
	 * @param sGUI the GUI instance to bind to
	 */
	public MainGameMenu(final MainGameGUI sGUI) {
		this.gui = sGUI;
		menuOptions =
				new PIComponent[] { null/* equip */,
						null/* skills */, new InventoryGUI(gui) };
		menuButtons = new PIComponent[menuOptions.length];
		for (int i = 0; i < menuButtons.length; i++) {
			menuButtons[i] = new PIComponent();
			menuButtons[i].setStyle(StyleType.NORMAL,
					new PIStyle());
			menuButtons[i].setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
			menuButtons[i].getStyle(StyleType.NORMAL).bgImage =
					new GraphicsObject(GraphicsDirectories.GUI.getFileID(2), 0, i * BUTTON_HEIGHT,
							BUTTON_WIDTH, BUTTON_HEIGHT);
			add(menuButtons[i]);
			final int tempIndex = i;
			menuButtons[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					toggleElement(tempIndex);
				}
			});
			if (menuOptions[i] != null) {
				menuOptions[i].setVisible(false);
				add(menuOptions[i]);
			}
		}
		reLayout();
	}

	/**
	 * Toggles the given menu element's visible state, and also hides all the
	 * other menu elements.
	 * 
	 * @param ele the menu index
	 */
	private void toggleElement(final int ele) {
		for (int i = 0; i < menuButtons.length; i++) {
			if (menuOptions[i] != null) {
				if (i == ele) {
					menuOptions[i].setVisible(!menuOptions[i]
							.isVisible());
				} else {
					menuOptions[i].setVisible(false);
				}
			}
		}
	}

	@Override
	public final void update() {
		if (!gui.getClient().getApplet().getSize()
				.equals(currentLayout)) {
			reLayout();
		}
	}

	/**
	 * Layouts all the components of this container.
	 */
	private void reLayout() {
		currentLayout = gui.getClient().getApplet().getSize();
		for (int i = 0; i < menuButtons.length; i++) {
			if (LAYOUT_VERTICALLY) {
				menuButtons[i].setLocation(
						(int) currentLayout.getWidth()
								- BUTTON_WIDTH,
						(int) currentLayout.getHeight()
								- ((i + 1) * BUTTON_HEIGHT));
				if (menuOptions[i] != null) {
					menuOptions[i]
							.setLocation(
									menuButtons[i].getX()
											- menuOptions[i]
													.getWidth(),
									menuButtons[i].getY()
											+ BUTTON_HEIGHT
											- menuOptions[i]
													.getHeight());
				}
			} else {
				menuButtons[i].setLocation(
						(int) currentLayout.getWidth()
								- ((i + 1) * BUTTON_WIDTH),
						(int) currentLayout.getHeight()
								- BUTTON_HEIGHT);
				gui.getClient()
						.getLog()
						.info(menuButtons[i].getX() + ","
								+ menuButtons[i].getY());
				if (menuOptions[i] != null) {
					menuOptions[i]
							.setLocation(
									menuButtons[i].getX()
											+ BUTTON_WIDTH
											- menuOptions[i]
													.getWidth(),
									menuButtons[i].getY()
											- menuOptions[i]
													.getHeight());
				}
			}
		}
		compile();
	}
}
