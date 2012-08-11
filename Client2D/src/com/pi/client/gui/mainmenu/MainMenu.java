package com.pi.client.gui.mainmenu;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.pi.client.Client;
import com.pi.graphics.device.IGraphics;
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
public class MainMenu extends PIContainer {
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
	 * The menu buttons.
	 */
	private PIButton[] optButtons = new PIButton[] {
			new PIButton(), new PIButton(), new PIButton(),
			new PIButton() };
	/**
	 * The button style set.
	 */
	private PIStyle.PIStyleSet set;
	/**
	 * The sub-container options.
	 */
	private PIContainer[] options = new PIContainer[] {
			new LoginContainer(this),
			new RegisterContainer(this), new CreditsContainer(),
			new SettingsContainer(this) };
	/**
	 * The server status component.
	 */
	private PIComponent serverStatus = new PIComponent();
	/**
	 * The style of the server status component.
	 */
	private PIStyle serverStatusStyle =
			GUIKit.DEFAULT_LABEL_STYLE.clone();
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
		set = GUIKit.BUTTON_STYLE_SET.clone();
		serverStatusStyle.hAlign = false;
		serverStatus.setStyle(StyleType.NORMAL,
				serverStatusStyle);
		add(serverStatus);

		for (int i = 0; i < options.length; i++) {
			options[i].setVisible(false);
			add(options[i]);
			add(optButtons[i]);
			final int btnTMP = i;
			optButtons[i].setStyleSet(set, false);
			optButtons[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					selectButton(btnTMP);
				}
			});
		}

		optButtons[0].setContent("Login");
		optButtons[1].setContent("New Account");
		optButtons[2].setContent("Credits");
		optButtons[3].setContent("Settings");

		add(serverStatus);
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
	 * @param button the button index selected
	 */
	private void selectButton(final int button) {
		for (int i = 0; i < optButtons.length; i++) {
			optButtons[i].setActive(button == i);
			options[i].setVisible(button == i);
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
		super.render(g);
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
		for (StyleType type : StyleType.values()) {
			if (set.getStyle(type) != null) {
				set.getStyle(type).setFontSize(fontSize);
			}
		}
		for (PIContainer p : options) {
			p.setSize(menuWidth, menuHeight);
			p.setLocation(buttonX, menuLocY);
		}
		serverStatus.setSize(3 * 5 * 5, 5 * 5);
		serverStatus.setLocation(5, height - (5 * 2 * 5));

		for (PIButton btn : optButtons) {
			btn.setSize(bWidth, bHeight);
			btn.setLocation(buttonX, buttonY);
			buttonX += buttonSpacing;
		}
		compile();
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
