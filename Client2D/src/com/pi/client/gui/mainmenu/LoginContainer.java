package com.pi.client.gui.mainmenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.pi.common.contants.UserConstants;
import com.pi.common.net.packet.Packet1Login;
import com.pi.common.util.PICryptUtils;
import com.pi.gui.GUIKit;
import com.pi.gui.PIButton;
import com.pi.gui.PIComponent;
import com.pi.gui.PIContainer;
import com.pi.gui.PIStyle.StyleType;
import com.pi.gui.PITextField;

/**
 * A container showing the login form.
 * 
 * @author Westin
 * 
 */
public class LoginContainer extends PIContainer {
	/**
	 * The scalar field width.
	 */
	private static final float FIELD_WIDTH = 0.5f;
	/**
	 * The scalar field height.
	 */
	private static final float FIELD_HEIGHT = 0.1f;
	/**
	 * The scalar horizontal margin.
	 */
	private static final float HORIZONTAL_MARGIN = 0.0666f;
	/**
	 * The scalar vertical margin.
	 */
	private static final float VERTICAL_MARGIN = 0.0666f;

	/**
	 * The login button, to send the verification packet to the server.
	 */
	private PIComponent loginButton = new PIButton();
	/**
	 * The username field identification.
	 */
	private PIComponent usernameLabel = new PIComponent();
	/**
	 * The password field identification.
	 */
	private PIComponent passwordLabel = new PIComponent();
	/**
	 * The username field.
	 */
	private PITextField usernameField = new PITextField();
	/**
	 * The password field.
	 */
	private PITextField passwordField = new PITextField();

	/**
	 * Create a login container for the provided main menu.
	 * 
	 * @param menu the logic container
	 */
	public LoginContainer(final MainMenu menu) {
		setStyle(StyleType.NORMAL,
				GUIKit.DEFAULT_CONTAINER_STYLE);
		usernameField
				.setMaxLength(UserConstants.USERNAME_MAX_SIZE);
		usernameLabel.setStyle(StyleType.NORMAL,
				GUIKit.DEFAULT_LABEL_STYLE);
		usernameLabel.setContent("Username");
		passwordLabel.setContent("Password");
		passwordLabel.setStyle(StyleType.NORMAL,
				GUIKit.DEFAULT_LABEL_STYLE);
		passwordField.setMask('*');
		passwordField
				.setMaxLength(UserConstants.PASSWORD_MAX_SIZE);
		loginButton.setContent("Login");
		loginButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (menu.getClient().isNetworkConnected()) {
					if (usernameField.getContent().length() <= 0) {
						menu.getClient()
								.getRenderLoop()
								.alert("You must enter a username!");
					} else if (passwordField.getContent()
							.length() <= 0) {
						menu.getClient()
								.getRenderLoop()
								.alert("You must enter a password!");
					} else {
						Packet1Login pack = new Packet1Login();
						pack.username =
								usernameField.getContent();
						pack.password =
								PICryptUtils.crypt(passwordField
										.getContent());
						menu.getClient().getNetwork().send(pack);
						menu.getClient()
								.getRenderLoop()
								.loading(
										"Connecting to server...");
					}
				} else {
					menu.getClient()
							.getRenderLoop()
							.alert("Cannot login without network connection!");
				}
			}
		});
		add(loginButton);
		add(usernameLabel);
		add(passwordLabel);
		add(usernameField);
		add(passwordField);
		setTabIndex(usernameField, 0);
		setTabIndex(passwordField, 1);
		setTabIndex(loginButton, 2);
	}

	@Override
	public final void setSize(final int width, final int height) {
		super.setSize(width, height);
		int fieldWidth = (int) (width * FIELD_WIDTH);
		int fieldHeight = (int) (height * FIELD_HEIGHT);
		int lMarg = (int) (width * HORIZONTAL_MARGIN);
		int vMarg = (int) (height * VERTICAL_MARGIN);
		int currentHeight = 0;
		usernameLabel.setLocation(lMarg, vMarg + currentHeight);
		usernameLabel.setSize(fieldWidth, fieldHeight);
		currentHeight += fieldHeight;

		usernameField.setLocation(lMarg, vMarg + currentHeight);
		usernameField.setSize(fieldWidth, fieldHeight);
		currentHeight += fieldHeight;

		passwordLabel.setLocation(lMarg, vMarg + currentHeight);
		passwordLabel.setSize(fieldWidth, fieldHeight);
		currentHeight += fieldHeight;

		passwordField.setLocation(lMarg, vMarg + currentHeight);
		passwordField.setSize(fieldWidth, fieldHeight);
		currentHeight += fieldHeight;

		loginButton.setLocation(lMarg, height - fieldHeight
				- vMarg);
		loginButton.setSize(fieldWidth, fieldHeight);
	}
}
