package com.pi.client.gui.mainmenu;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.pi.client.Client;
import com.pi.graphics.device.IGraphics;
import com.pi.graphics.device.Renderable;
import com.pi.gui.GUIKit;
import com.pi.gui.PIButton;
import com.pi.gui.PIComponent;
import com.pi.gui.PIContainer;
import com.pi.gui.PIStyle;
import com.pi.gui.PIStyle.StyleType;

/**
 * The main menu renderable for displaying the main menu controls and sub
 * containers.
 * 
 * @author Westin
 * 
 */
public class MainMenu implements Renderable, MouseListener,
		MouseMotionListener, MouseWheelListener, KeyListener {
	/**
	 * The scalar button width.
	 */
	private static final float BUTTON_WIDTH = 0.2f;
	/**
	 * The scalar button height.
	 */
	private static final float BUTTON_HEIGHT = 0.1f;
	/**
	 * The scalar button y-position.
	 */
	private static final float BUTTON_Y_POSITION = 0.8f;
	/**
	 * The scalar button x-spacing between button centers.
	 */
	private static final float BUTTON_X_SPACING = 0.25f;
	/**
	 * The scalar menu y-position.
	 */
	private static final float MENU_Y_POSITION = 0.1f;
	/**
	 * The scalar font size, based on width.
	 */
	private static final float FONT_SIZE_WIDTH = 0.025f;

	/**
	 * The menu container.
	 */
	private PIContainer buttonContainer = new PIContainer();
	/**
	 * The menu buttons.
	 */
	private PIButton loginButton = new PIButton(),
			newAccount = new PIButton(),
			credits = new PIButton(), settings = new PIButton();
	/**
	 * The button style set.
	 */
	private PIStyle.PIStyleSet set;
	/**
	 * The currently open sub container.
	 */
	private PIContainer currentOption = null;
	/**
	 * The sub-container options.
	 */
	private PIContainer loginOption = new LoginContainer(this),
			creditsOption = new CreditsContainer(),
			registerOption = new RegisterContainer(this),
			settingsOption = new SettingsContainer(this);
	/**
	 * The server status component.
	 */
	private PIComponent serverStatus = new PIComponent();
	/**
	 * The style of the server status component.
	 */
	private PIStyle serverStatusStyle = GUIKit.label.clone();
	/**
	 * The bound client.
	 */
	private final Client client;

	/**
	 * Creates a main menu bound to the client.
	 * 
	 * @param c the bound client
	 */
	public MainMenu(final Client c) {
		this.client = c;
		set = GUIKit.buttonSet.clone();
		serverStatusStyle.hAlign = false;
		serverStatus.setStyle(StyleType.Normal,
				serverStatusStyle);
		loginButton.setContent("Login");
		loginButton.setStyleSet(set, false);
		loginButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				currentOption = loginOption;
				selectButton(loginButton);
			}
		});
		newAccount.setContent("New Account");
		newAccount.setStyleSet(set, false);
		newAccount.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				currentOption = registerOption;
				selectButton(newAccount);
			}
		});
		credits.setContent("Credits");
		credits.setStyleSet(set, false);
		credits.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				currentOption = creditsOption;
				selectButton(credits);
			}
		});
		settings.setContent("Settings");
		settings.setStyleSet(set, false);
		settings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				currentOption = settingsOption;
				selectButton(settings);
			}
		});

		buttonContainer.add(loginButton);
		buttonContainer.add(newAccount);
		buttonContainer.add(credits);
		buttonContainer.add(serverStatus);
		buttonContainer.add(settings);
		onResize(c.getApplet().getWidth(), c.getApplet()
				.getHeight());
		c.getApplet().addComponentListener(
				new ComponentAdapter() {
					@Override
					public void componentResized(
							final ComponentEvent e) {
						onResize(c.getApplet().getWidth(), c
								.getApplet().getHeight());
					}
				});
	}

	/**
	 * Sets the currently active button.
	 * 
	 * @param button the button to select.
	 */
	private void selectButton(final PIComponent button) {
		for (PIComponent p : buttonContainer.getChildren()) {
			if (p == button) {
				p.setActive(true);
			} else {
				p.setActive(false);
			}
		}
	}

	@Override
	public final void render(final IGraphics g) {
		if (client.isNetworkConnected()) {
			serverStatusStyle.foreground = Color.green;
			serverStatus.setContent("Online");
		} else {
			serverStatusStyle.foreground = Color.red;
			serverStatus.setContent("Offline");
		}
		serverStatus.setStyle(StyleType.Normal,
				serverStatusStyle);

		buttonContainer.render(g);
		if (currentOption != null) {
			currentOption.render(g);
		}
	}

	/**
	 * Called when the parent component is resized, correctly scaling all
	 * subcomponents.
	 * 
	 * @param width the new width
	 * @param height the new height
	 */
	private void onResize(final int width, final int height) {
		int bWidth = (int) (width * BUTTON_WIDTH);
		int bHeight = (int) (height * BUTTON_HEIGHT);
		int buttonSpacing = (int) (BUTTON_X_SPACING * width);
		int buttonX = (buttonSpacing / 2) - (bWidth / 2);
		int buttonY = (int) (height * BUTTON_Y_POSITION);

		int menuWidth = width - (buttonX * 2);
		int menuHeight =
				height - ((height - (buttonY + bHeight)) * 4);
		int menuLocY = (int) (height * MENU_Y_POSITION);

		float fontSize = width * FONT_SIZE_WIDTH;
		if (set.active != null) {
			set.active.setFontSize(fontSize);
		}
		if (set.normal != null) {
			set.normal.setFontSize(fontSize);
		}
		if (set.hover != null) {
			set.hover.setFontSize(fontSize);
		}

		loginOption.setSize(menuWidth, menuHeight);
		loginOption.setLocation(buttonX, menuLocY);
		creditsOption.setSize(menuWidth, menuHeight);
		creditsOption.setLocation(buttonX, menuLocY);
		registerOption.setSize(menuWidth, menuHeight);
		registerOption.setLocation(buttonX, menuLocY);
		settingsOption.setSize(menuWidth, menuHeight);
		settingsOption.setLocation(buttonX, menuLocY);
		serverStatus.setSize(3 * 5 * 5, 5 * 5);
		serverStatus.setLocation(5, height - (5 * 2 * 5));

		loginButton.setSize(bWidth, bHeight);
		newAccount.setSize(bWidth, bHeight);
		credits.setSize(bWidth, bHeight);
		settings.setSize(bWidth, bHeight);
		loginButton.setLocation(buttonX, buttonY);
		buttonX += buttonSpacing;

		newAccount.setLocation(buttonX, buttonY);
		buttonX += buttonSpacing;

		credits.setLocation(buttonX, buttonY);
		buttonX += buttonSpacing;

		settings.setLocation(buttonX, buttonY);
		buttonX += buttonSpacing;

		loginOption.compile();
		creditsOption.compile();
		registerOption.compile();
		settingsOption.compile();
		serverStatus.compile();
		buttonContainer.compile();
	}

	@Override
	public final void mouseWheelMoved(final MouseWheelEvent e) {
		buttonContainer.mouseWheelMoved(e);
		if (currentOption != null) {
			currentOption.mouseWheelMoved(e);
		}
	}

	@Override
	public final void mouseDragged(final MouseEvent e) {
		buttonContainer.mouseDragged(e);
		if (currentOption != null) {
			currentOption.mouseDragged(e);
		}
	}

	@Override
	public final void mouseMoved(final MouseEvent e) {
		buttonContainer.mouseMoved(e);
		if (currentOption != null) {
			currentOption.mouseMoved(e);
		}
	}

	@Override
	public final void mouseClicked(final MouseEvent e) {
		buttonContainer.mouseClicked(e);
		if (currentOption != null) {
			currentOption.mouseClicked(e);
		}
	}

	@Override
	public final void mouseEntered(final MouseEvent e) {
		buttonContainer.mouseEntered(e);
		if (currentOption != null) {
			currentOption.mouseEntered(e);
		}
	}

	@Override
	public final void mouseExited(final MouseEvent e) {
		buttonContainer.mouseExited(e);
		if (currentOption != null) {
			currentOption.mouseExited(e);
		}
	}

	@Override
	public final void mousePressed(final MouseEvent e) {
		buttonContainer.mousePressed(e);
		if (currentOption != null) {
			currentOption.mousePressed(e);
		}
	}

	@Override
	public final void mouseReleased(final MouseEvent e) {
		buttonContainer.mouseReleased(e);
		if (currentOption != null) {
			currentOption.mouseReleased(e);
		}
	}

	@Override
	public final void keyPressed(final KeyEvent e) {
		buttonContainer.keyPressed(e);
		if (currentOption != null) {
			currentOption.keyPressed(e);
		}
	}

	@Override
	public final void keyReleased(final KeyEvent e) {
		buttonContainer.keyReleased(e);
		if (currentOption != null) {
			currentOption.keyReleased(e);
		}
	}

	@Override
	public final void keyTyped(final KeyEvent e) {
		buttonContainer.keyTyped(e);
		if (currentOption != null) {
			currentOption.keyTyped(e);
		}
	}

	/**
	 * Returns the client instance linked to this menu.
	 * 
	 * @return the client instance
	 */
	public final Client getClient() {
		return client;
	}
}
