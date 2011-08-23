package com.pi.launcher;

import java.applet.Applet;
import java.awt.event.*;
import java.io.IOException;

import com.pi.common.*;

public class PILauncher extends Applet {
    private static final long serialVersionUID = 1L;
    PILogger log;
    PILogViewer.LogPane pane;
    Disposable bound;

    public PILauncher() {
	setSize(500, 500);
	setLayout(null);
	pane = new PILogViewer.LogPane();
	pane.setSize(500, 500);
	pane.setLocation(0, 0);
	add(pane);
	log = new PILogger(pane.logOut);
    }

    @Override
    public void start() {
	try {
	    Updater.update(log);
	    bound = ClientLoader.loadClientApplet(this);
	    // remove(pane);
	    // cApp.init();
	    // cApp.start();
	    // bindApplet(cApp);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void destroy() {
	if (bound != null)
	    bound.dispose();
	super.destroy();
    }

    private void bindApplet(final Applet app) {
	addMouseListener(new MouseListener() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		for (MouseListener l : app.getMouseListeners())
		    l.mouseClicked(e);
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
		for (MouseListener l : app.getMouseListeners())
		    l.mouseEntered(e);
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
		for (MouseListener l : app.getMouseListeners())
		    l.mouseExited(e);
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
		for (MouseListener l : app.getMouseListeners())
		    l.mousePressed(e);
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
		for (MouseListener l : app.getMouseListeners())
		    l.mouseReleased(e);
	    }
	});
	addKeyListener(new KeyListener() {

	    @Override
	    public void keyPressed(KeyEvent e) {
		for (KeyListener l : app.getKeyListeners())
		    l.keyPressed(e);
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {
		for (KeyListener l : app.getKeyListeners())
		    l.keyReleased(e);
	    }

	    @Override
	    public void keyTyped(KeyEvent e) {
		for (KeyListener l : app.getKeyListeners())
		    l.keyTyped(e);
	    }
	});
	addMouseMotionListener(new MouseMotionListener() {

	    @Override
	    public void mouseDragged(MouseEvent e) {
		for (MouseMotionListener l : app.getMouseMotionListeners())
		    l.mouseDragged(e);
	    }

	    @Override
	    public void mouseMoved(MouseEvent e) {
		for (MouseMotionListener l : app.getMouseMotionListeners())
		    l.mouseMoved(e);
	    }
	});
	addMouseWheelListener(new MouseWheelListener() {

	    @Override
	    public void mouseWheelMoved(MouseWheelEvent e) {
		for (MouseWheelListener l : app.getMouseWheelListeners())
		    l.mouseWheelMoved(e);
	    }
	});
	addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent e) {
		app.setSize(getSize());
	    }
	});
	removeAll();
	add(app);
	//bound = app;
    }
}
