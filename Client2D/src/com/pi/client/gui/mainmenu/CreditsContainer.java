package com.pi.client.gui.mainmenu;

import com.pi.client.gui.*;
import com.pi.client.gui.PIStyle.StyleType;

public class CreditsContainer extends PIContainer {
	PIComponent creditLabel = new PIComponent();

	public CreditsContainer(MainMenu menu) {
		setStyle(StyleType.Normal, GUIKit.containerNormal);
		creditLabel.setStyle(StyleType.Normal, GUIKit.label);
		creditLabel
				.setContent("Equinox MMORPG Maker\n\nCreated by Westin Miller alias 314piwm alias Equinox\n\nThank you to the Jogamp.com Community for JOGL, the Java Bindings for OpenGL");
		add(creditLabel);
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		creditLabel.setSize((int) (width * .8), (int) (height * .8));
		creditLabel.setLocation((int) (width * .1), (int) (height * .1));
	}
}
