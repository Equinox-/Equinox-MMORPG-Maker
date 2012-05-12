package com.pi.editor;

import java.applet.Applet;

public class EditorApplet extends Applet {
    private static final long serialVersionUID = 1L;
    public Editor editor;

    public EditorApplet() {
	setVisible(true);
	setSize(500, 500);
	setLayout(null);
	setFocusTraversalKeysEnabled(false);
    }

    @Override
    public void init() {
	super.init();
	this.editor = new Editor(this);
    }

    @Override
    public void destroy() {
	super.destroy();
	this.editor.dispose();
    }
}
