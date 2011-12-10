package com.pi.client.gui.mainmenu;

import java.awt.*;
import java.awt.event.*;

import com.pi.client.Client;
import com.pi.client.graphics.Renderable;
import com.pi.client.graphics.device.IGraphics;
import com.pi.client.gui.*;
import com.pi.client.gui.PIStyle.StyleType;

public class MainMenu implements Renderable, MouseListener,
	MouseMotionListener, MouseWheelListener, KeyListener {
    private int width, height;
    private PIContainer buttonContainer = new PIContainer();
    private PIComponent loginButton = new PIComponent(),
	    newAccount = new PIComponent(), credits = new PIComponent(),
	    settings = new PIComponent();
    private PIStyle.PIStyleSet set;
    private PIContainer currentOption = null;
    private PIContainer loginOption = new LoginContainer(this),
	    creditsOption = new CreditsContainer(this),
	    registerOption = new RegisterContainer(this),
	    settingsOption = new SettingsContainer(this);
    private PIComponent serverStatus = new PIComponent();
    private PIStyle serverStatusStyle = GUIKit.label.clone();
    public final Client client;
    private PIPopup popup = new PIPopup();

    public MainMenu(final Client c) {
	this.client = c;
	set = GUIKit.buttonSet.clone();
	serverStatusStyle.hAlign = false;
	serverStatus.setStyle(StyleType.Normal, serverStatusStyle);
	loginButton.setContent("Login");
	loginButton.setStyleSet(set, false);
	loginButton.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		currentOption = loginOption;
		selectButton(loginButton);
	    }
	});
	newAccount.setContent("New Account");
	newAccount.setStyleSet(set, false);
	newAccount.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		currentOption = registerOption;
		selectButton(newAccount);
	    }
	});
	credits.setContent("Credits");
	credits.setStyleSet(set, false);
	credits.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		currentOption = creditsOption;
		selectButton(credits);
	    }
	});
	settings.setContent("Settings");
	settings.setStyleSet(set, false);
	settings.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		currentOption = settingsOption;
		selectButton(settings);
	    }
	});

	buttonContainer.add(loginButton);
	buttonContainer.add(newAccount);
	buttonContainer.add(credits);
	buttonContainer.add(serverStatus);
	buttonContainer.add(settings);
	onResize(c.getApplet().getWidth(), c.getApplet().getHeight());
	c.getApplet().addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent e) {
		onResize(c.getApplet().getWidth(), c.getApplet().getHeight());
	    }
	});
	registerToApplet();
    }

    public void registerToApplet() {
	Component c = client.getDisplayManager().getListenerRegistration();
	c.addMouseListener(this);
	c.addKeyListener(this);
	c.addMouseMotionListener(this);
	c.addMouseWheelListener(this);
	c.addMouseListener(popup);
    }

    public void unregisterFromApplet() {
	Component c = client.getDisplayManager().getListenerRegistration();
	c.removeMouseListener(this);
	c.removeKeyListener(this);
	c.removeMouseMotionListener(this);
	c.removeMouseWheelListener(this);
	c.removeMouseListener(popup);
    }

    private void selectButton(PIComponent button) {
	for (PIComponent p : buttonContainer.getChildren()) {
	    if (p == button) {
		p.setActive(true);
	    } else {
		p.setActive(false);
	    }
	}
    }

    @Override
    public void render(IGraphics g) {
	serverStatusStyle.foreground = client.isNetworkConnected() ? Color.green
		: Color.red;
	serverStatus.setStyle(StyleType.Normal, serverStatusStyle);
	serverStatus.setContent(client.isNetworkConnected() ? "Online"
		: "Offline");

	buttonContainer.render(g);
	if (currentOption != null) {
	    currentOption.render(g);
	}
	// Render popup
	popup.render(g, width, height);
    }

    private void onResize(int width, int height) {
	this.width = width;
	this.height = height;

	int bWidth = width / 5;
	int bHeight = height / 10;

	int menuSizeX = (3 * (width / 4)) + bWidth;
	int menuSizeY = (int) (height / 1.35);
	int menuLocX = (width / 8) - (bWidth / 2);
	int menuLocY = (height / 8) - bHeight;

	float fontSize = width / 40f;
	if (set.active != null)
	    set.active.setFontSize(fontSize);
	if (set.normal != null)
	    set.normal.setFontSize(fontSize);
	if (set.hover != null)
	    set.hover.setFontSize(fontSize);
	loginButton.setSize(bWidth, bHeight);
	newAccount.setSize(bWidth, bHeight);
	credits.setSize(bWidth, bHeight);
	settings.setSize(bWidth, bHeight);
	loginButton.setLocation((width / 8) - (bWidth / 2), 8 * (height / 10));
	newAccount.setLocation((3 * (width / 8)) - (bWidth / 2),
		8 * (height / 10));
	credits.setLocation((5 * (width / 8)) - (bWidth / 2), 8 * (height / 10));
	settings.setLocation((7 * (width / 8)) - (bWidth / 2),
		8 * (height / 10));

	loginOption.setSize(menuSizeX, menuSizeY);
	loginOption.setLocation(menuLocX, menuLocY);
	creditsOption.setSize(menuSizeX, menuSizeY);
	creditsOption.setLocation(menuLocX, menuLocY);
	registerOption.setSize(menuSizeX, menuSizeY);
	registerOption.setLocation(menuLocX, menuLocY);
	settingsOption.setSize(menuSizeX, menuSizeY);
	settingsOption.setLocation(menuLocX, menuLocY);
	serverStatus.setSize(75, 25);
	serverStatus.setLocation(5, height - 50);
	
	loginOption.compile();
	creditsOption.compile();
	registerOption.compile();
	settingsOption.compile();
	serverStatus.compile();
	buttonContainer.compile();
    }

    public void alert(String message) {
	popup.setVisible(true);
	popup.displayLoadingBar(false);
	popup.setContent(message);
	popup.setCloseable(true);
    }

    public void loading(String message) {
	popup.setVisible(true);
	popup.setContent(message);
	popup.setCloseable(false);
	popup.displayLoadingBar(true);
    }

    public boolean hasPopup() {
	return popup.isVisible();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
	if (!hasPopup()) {
	    buttonContainer.mouseWheelMoved(e);
	    if (currentOption != null)
		currentOption.mouseWheelMoved(e);
	}
    }

    @Override
    public void mouseDragged(MouseEvent e) {
	if (!hasPopup()) {
	    buttonContainer.mouseDragged(e);
	    if (currentOption != null)
		currentOption.mouseDragged(e);
	}
    }

    @Override
    public void mouseMoved(MouseEvent e) {
	if (!hasPopup()) {
	    buttonContainer.mouseMoved(e);
	    if (currentOption != null)
		currentOption.mouseMoved(e);
	}
    }

    @Override
    public void mouseClicked(MouseEvent e) {
	if (!hasPopup()) {
	    buttonContainer.mouseClicked(e);
	    if (currentOption != null)
		currentOption.mouseClicked(e);
	}
    }

    @Override
    public void mouseEntered(MouseEvent e) {
	if (!hasPopup()) {
	    buttonContainer.mouseEntered(e);
	    if (currentOption != null)
		currentOption.mouseEntered(e);
	}
    }

    @Override
    public void mouseExited(MouseEvent e) {
	if (!hasPopup()) {
	    buttonContainer.mouseExited(e);
	    if (currentOption != null)
		currentOption.mouseExited(e);
	}
    }

    @Override
    public void mousePressed(MouseEvent e) {
	if (!hasPopup()) {
	    buttonContainer.mousePressed(e);
	    if (currentOption != null)
		currentOption.mousePressed(e);
	}
    }

    @Override
    public void mouseReleased(MouseEvent e) {
	if (!hasPopup()) {
	    buttonContainer.mouseReleased(e);
	    if (currentOption != null)
		currentOption.mouseReleased(e);
	}
    }

    @Override
    public void keyPressed(KeyEvent e) {
	if (!hasPopup()) {
	    buttonContainer.keyPressed(e);
	    if (currentOption != null)
		currentOption.keyPressed(e);
	}
    }

    @Override
    public void keyReleased(KeyEvent e) {
	if (!hasPopup()) {
	    buttonContainer.keyReleased(e);
	    if (currentOption != null)
		currentOption.keyReleased(e);
	}
    }

    @Override
    public void keyTyped(KeyEvent e) {
	if (!hasPopup()) {
	    buttonContainer.keyTyped(e);
	    if (currentOption != null)
		currentOption.keyTyped(e);
	}
    }
}
