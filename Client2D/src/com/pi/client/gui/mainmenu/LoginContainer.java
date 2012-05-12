package com.pi.client.gui.mainmenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.pi.common.PICryptUtils;
import com.pi.common.net.packet.Packet1Login;
import com.pi.gui.GUIKit;
import com.pi.gui.PIButton;
import com.pi.gui.PIComponent;
import com.pi.gui.PIContainer;
import com.pi.gui.PIStyle.StyleType;
import com.pi.gui.PITextField;

public class LoginContainer extends PIContainer {
    PIComponent loginButton = new PIButton();
    PIComponent usernameLabel = new PIComponent();
    PIComponent passwordLabel = new PIComponent();
    PITextField usernameField = new PITextField();
    PITextField passwordField = new PITextField();

    public LoginContainer(final MainMenu menu) {
	setStyle(StyleType.Normal, GUIKit.containerNormal);
	usernameField.setStyleSet(GUIKit.textfieldSet, false);
	usernameField.setMaxLength(16);
	usernameLabel.setStyle(StyleType.Normal, GUIKit.label);
	usernameLabel.setContent("Username");
	passwordLabel.setContent("Password");
	passwordLabel.setStyle(StyleType.Normal, GUIKit.label);
	passwordField.setStyleSet(GUIKit.textfieldSet, false);
	passwordField.setMask('*');
	passwordField.setMaxLength(16);
	loginButton.setStyleSet(GUIKit.buttonSet, false);
	loginButton.setContent("Login");
	loginButton.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (menu.client.isNetworkConnected()) {
		    if (usernameField.getContent().length() <= 0) {
			menu.client.getRenderLoop().alert(
				"You must enter a username!");
		    } else if (passwordField.getContent().length() <= 0) {
			menu.client.getRenderLoop().alert(
				"You must enter a password!");
		    } else {
			Packet1Login pack = new Packet1Login();
			pack.username = usernameField.getContent();
			pack.password = PICryptUtils.crypt(passwordField
				.getContent());
			menu.client.getNetwork().send(pack);
			menu.client.getRenderLoop().loading(
				"Connecting to server...");
		    }
		} else {
		    menu.client.getRenderLoop().alert(
			    "Cannot login without network connection!");
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
    public void setSize(int width, int height) {
	super.setSize(width, height);
	int fieldWidth = width / 2;
	int fieldHeight = height / 10;
	int lMarg = width / 15;
	int vMarg = height / 15;
	usernameLabel.setLocation(lMarg, vMarg);
	usernameLabel.setSize(fieldWidth, fieldHeight);
	usernameField.setLocation(lMarg, vMarg + fieldHeight);
	usernameField.setSize(fieldWidth, fieldHeight);
	passwordLabel.setLocation(lMarg, vMarg + (2 * fieldHeight));
	passwordLabel.setSize(fieldWidth, fieldHeight);
	passwordField.setLocation(lMarg, vMarg + (3 * fieldHeight));
	passwordField.setSize(fieldWidth, fieldHeight);
	loginButton.setLocation(lMarg, height - fieldHeight - vMarg);
	loginButton.setSize(fieldWidth, fieldHeight);
    }
}
