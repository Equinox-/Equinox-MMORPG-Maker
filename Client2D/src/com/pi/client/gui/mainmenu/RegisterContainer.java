package com.pi.client.gui.mainmenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.pi.common.PICryptUtils;
import com.pi.common.contants.UserConstants;
import com.pi.common.net.packet.Packet3Register;
import com.pi.gui.GUIKit;
import com.pi.gui.PIComponent;
import com.pi.gui.PIContainer;
import com.pi.gui.PIStyle.StyleType;
import com.pi.gui.PITextField;

/**
 * A container showing the registration form.
 * 
 * @author Westin
 * 
 */
public class RegisterContainer extends PIContainer {
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
	 * The registration button, to send the verification packet to the server.
	 */
	private PIComponent registerButton = new PIComponent();
	/**
	 * The username field identification.
	 */
	private PIComponent usernameLabel = new PIComponent();
	/**
	 * The password field identification.
	 */
	private PIComponent passwordLabel = new PIComponent();
	/**
	 * The password confirmation field identification.
	 */
	private PIComponent confPasswordLabel = new PIComponent();
	/**
	 * The username field.
	 */
	private PITextField usernameField = new PITextField();
	/**
	 * The password field.
	 */
	private PITextField passwordField = new PITextField();
	/**
	 * The password confirmation field.
	 */
	private PITextField confPasswordField = new PITextField();

	/**
	 * Creates a registration container for the provided menu.
	 * 
	 * @param menu the provided menu
	 */
	public RegisterContainer(final MainMenu menu) {
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
		confPasswordLabel.setContent("Confirm Password");
		confPasswordLabel.setStyle(StyleType.NORMAL,
				GUIKit.DEFAULT_LABEL_STYLE);
		confPasswordField.setMask('*');
		confPasswordField
				.setMaxLength(UserConstants.PASSWORD_MAX_SIZE);
		registerButton.setContent("Register");
		registerButton.addMouseListener(new MouseAdapter() {
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
					} else if (!passwordField.getContent()
							.equals(confPasswordField
									.getContent())) {
						menu.getClient()
								.getRenderLoop()
								.alert("You must correctly confirm your password!");
					} else {
						Packet3Register pack =
								new Packet3Register();
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
		add(registerButton);
		add(usernameLabel);
		add(passwordLabel);
		add(usernameField);
		add(passwordField);
		add(confPasswordLabel);
		add(confPasswordField);
		setTabIndex(usernameField, 0);
		setTabIndex(passwordField, 1);
		setTabIndex(confPasswordField, 2);
		setTabIndex(registerButton, 3);
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

		confPasswordLabel.setLocation(lMarg, vMarg
				+ currentHeight);
		confPasswordLabel.setSize(fieldWidth, fieldHeight);
		currentHeight += fieldHeight;

		confPasswordField.setLocation(lMarg, vMarg
				+ currentHeight);
		confPasswordField.setSize(fieldWidth, fieldHeight);
		currentHeight += fieldHeight;

		registerButton.setLocation(lMarg, height - vMarg
				- fieldHeight);
		registerButton.setSize(fieldWidth, fieldHeight);
	}
}
