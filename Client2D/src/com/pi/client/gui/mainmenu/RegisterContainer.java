package com.pi.client.gui.mainmenu;

import com.pi.client.gui.*;
import com.pi.client.gui.PIStyle.StyleType;

public class RegisterContainer extends PIContainer {
    PIComponent registerButton = new PIComponent();
    PIComponent usernameLabel = new PIComponent();
    PIComponent passwordLabel = new PIComponent();
    PITextField usernameField = new PITextField();
    PITextField passwordField = new PITextField();
    PIComponent confpasswordLabel = new PIComponent();
    PITextField confpasswordField = new PITextField();
    private final MainMenu menu;

    public RegisterContainer(MainMenu menu) {
	this.menu = menu;
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
	confpasswordLabel.setContent("Confirm Password");
	confpasswordLabel.setStyle(StyleType.Normal, GUIKit.label);
	confpasswordField.setStyleSet(GUIKit.textfieldSet, false);
	confpasswordField.setMask('*');
	confpasswordField.setMaxLength(16);
	registerButton.setStyleSet(GUIKit.buttonSet, false);
	registerButton.setContent("Register");
	add(registerButton);
	add(usernameLabel);
	add(passwordLabel);
	add(usernameField);
	add(passwordField);
	add(confpasswordLabel);
	add(confpasswordField);
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
	confpasswordLabel.setLocation(lMarg, vMarg + (4 * fieldHeight));
	confpasswordLabel.setSize(fieldWidth, fieldHeight);
	confpasswordField.setLocation(lMarg, vMarg + (5 * fieldHeight));
	confpasswordField.setSize(fieldWidth, fieldHeight);
	registerButton.setLocation(lMarg, height - vMarg - fieldHeight);
	registerButton.setSize(fieldWidth, fieldHeight);
    }
}
