package com.pi.gui;

import java.awt.Color;

import com.pi.common.database.GraphicsAnimation;

public class GUIKit {
    public static class Graphics {
	public static final GraphicsAnimation loader = new GraphicsAnimation(
		12, 150, 1, 0, 0, 126 * 12, 22);
    }

    public static final PIStyle defaultStyle = new PIStyle();

    public static final PIStyle label = new PIStyle();
    public static final PIStyle containerNormal = new PIStyle();

    public static final PIStyle textfieldNormal = new PIStyle();
    public static final PIStyle textfieldHover = new PIStyle();
    public static final PIStyle textfieldActive = new PIStyle();
    public static final PIStyle.PIStyleSet textfieldSet = new PIStyle.PIStyleSet();

    public static final PIStyle buttonNormal = new PIStyle();
    public static final PIStyle buttonActive = new PIStyle();
    public static final PIStyle buttonHover = new PIStyle();
    public static final PIStyle.PIStyleSet buttonSet = new PIStyle.PIStyleSet();

    public static final PIStyle checkboxNormal = new PIStyle();
    public static final PIStyle checkboxActive = new PIStyle();
    public static final PIStyle checkboxHover = new PIStyle();
    public static final PIStyle.PIStyleSet checkboxSet = new PIStyle.PIStyleSet();

    public static final PIStyle loadingBar = new PIStyle();
    private static boolean init = false;

    public static void init() {
	if (!init) {
	    // Buttons start
	    buttonHover.background = new Color(0.05f, 0.05f, 0.05f);
	    buttonHover.foreground = Color.white;
	    buttonHover.border = new Color(0.5f, 0.5f, 0.5f);
	    buttonActive.background = new Color(0.1f, 0.1f, 0.1f);
	    buttonActive.foreground = Color.white;
	    buttonActive.border = new Color(0.5f, 0.5f, 0.5f);
	    buttonNormal.background = new Color(0.2f, 0.2f, 0.2f);
	    buttonNormal.foreground = Color.white;
	    buttonNormal.border = new Color(0.5f, 0.5f, 0.5f);
	    buttonSet.active = buttonActive;
	    buttonSet.hover = buttonHover;
	    buttonSet.normal = buttonNormal;
	    // Buttons end

	    // Textfield start
	    textfieldNormal.hAlign = false;
	    textfieldNormal.border = new Color(0.5f, 0.5f, 0.5f);
	    textfieldNormal.background = new Color(0.25f, 0.25f, 0.25f);
	    textfieldNormal.foreground = Color.white;

	    textfieldActive.border = new Color(0.5f, 0.5f, 0.5f);
	    textfieldActive.background = new Color(0.2f, 0.2f, 0.2f);
	    textfieldActive.hAlign = false;
	    textfieldActive.foreground = Color.white;

	    textfieldHover.border = new Color(0.5f, 0.5f, 0.5f);
	    textfieldHover.background = new Color(0.15f, 0.15f, 0.15f);
	    textfieldHover.hAlign = false;
	    textfieldHover.foreground = Color.white;

	    textfieldSet.active = textfieldActive;
	    textfieldSet.normal = textfieldNormal;
	    textfieldSet.hover = textfieldHover;
	    // Textfield end

	    // Checkbox start
	    checkboxNormal.border = new Color(0.9f, 0.9f, 0.9f);
	    checkboxNormal.background = new Color(0.75f, 0.75f, 0.75f);
	    checkboxNormal.foreground = Color.white;
	    checkboxNormal.hAlign = false;

	    checkboxActive.border = new Color(0.6f, 0.6f, 0.6f);
	    checkboxActive.background = new Color(0.45f, 0.45f, 0.45f);
	    checkboxActive.foreground = Color.white;
	    checkboxActive.hAlign = false;

	    checkboxSet.active = checkboxActive;
	    checkboxSet.normal = checkboxNormal;
	    // Checkbox end

	    // Container start
	    containerNormal.background = new Color(0.2f, 0.2f, 0.2f);
	    containerNormal.foreground = Color.white;
	    containerNormal.border = new Color(0.5f, 0.5f, 0.5f);
	    // Container end

	    label.foreground = Color.white;

	    loadingBar.foreground = Color.white;
	    loadingBar.border = Color.lightGray;
	    loadingBar.background = Color.darkGray;
	}
    }
}
