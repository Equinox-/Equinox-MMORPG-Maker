package com.pi.editor;

import java.applet.Applet;
import java.awt.Container;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;

import com.pi.common.Disposable;
import com.pi.common.debug.PILogger;
import com.pi.common.debug.PILoggerPane;
import com.pi.common.debug.PIResourceViewer;
import com.pi.common.debug.ThreadMonitorPanel;
import com.pi.editor.gui.EditorPage;
import com.pi.editor.gui.map.MapEditorObject;
import com.pi.editor.gui.map.MapRenderLoop;
import com.pi.graphics.device.DeviceRegistration;
import com.pi.graphics.device.DisplayManager;
import com.pi.graphics.device.IGraphics;
import com.pi.graphics.device.Renderable;
import com.pi.gui.GUIKit;

public class Editor implements Disposable, Renderable, DeviceRegistration {
    static {
	GUIKit.init();
    }
    private ThreadGroup clientThreads;

    private boolean disposing = false;

    // Graphics start
    private Applet cApplet;
    private DisplayManager displayManager;
    // Graphics end

    // GUI Start
    private EditorState editState;
    // GUI End

    // Debug Start
    private final PILogger logger;
    private PIResourceViewer reView;
    // Debug End

    // Graphics Parts
    private EditorPage[] editorPages;

    // Graphics Parts End

    public Editor(Applet applet) {
	clientThreads = new ThreadGroup("ClientThreads");
	reView = new PIResourceViewer("Client");
	reView.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	reView.addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e) {
		dispose();
	    }
	});
	PILoggerPane plp = new PILoggerPane();
	logger = new PILogger(Paths.getLogFile(), plp.logOut);
	reView.addTab("Logger", plp);
	reView.addTab("Threads", new ThreadMonitorPanel(clientThreads));

	this.cApplet = applet;
	this.displayManager = new DisplayManager(this, this);

	// Post INIT
	this.displayManager.postInititation();

	logger.info("Put image files here: "
		+ Paths.getGraphicsDirectory().getAbsolutePath());
	MapEditorObject.init();

	// Create editor pages
	editorPages = new EditorPage[EditorState.values().length];
	editorPages[EditorState.MapEditor.ordinal()] = new MapRenderLoop(this);

	changeState(EditorState.MapEditor);

	cApplet.addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent e) {
		if (editState != null && editorPages != null
			&& editorPages[editState.ordinal()] != null)
		    editorPages[editState.ordinal()].resize(cApplet.getWidth(),
			    cApplet.getHeight());
	    }
	});
    }

    public void changeState(EditorState e) {
	if (editState != null && editorPages[editState.ordinal()] != null) {
	    editorPages[editState.ordinal()].unRegister();
	}
	editState = e;
	if (editorPages[editState.ordinal()] != null)
	    editorPages[editState.ordinal()].register();
    }

    public Applet getApplet() {
	return this.cApplet;
    }

    public DisplayManager getDisplayManager() {
	return displayManager;
    }

    @Override
    public PILogger getLog() {
	return logger;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void dispose() {
	if (!disposing) {
	    disposing = true;
	    if (displayManager != null)
		displayManager.dispose();
	} else {
	    if (reView != null)
		reView.dispose();

	    clientThreads.stop();
	    logger.close();
	}
    }

    @Override
    public ThreadGroup getThreadGroup() {
	return clientThreads;
    }

    @Override
    public void fatalError(String string) {
	logger.severe(string);
	dispose();
    }

    public EditorState getGameState() {
	return editState;
    }

    public void setGameState(EditorState state) {
	editState = state;
    }

    @Override
    public Container getContainer() {
	return getApplet();
    }

    @Override
    public File getGraphicsFile(int id) {
	return Paths.getGraphicsFile(id);
    }

    @Override
    public void render(IGraphics graphics) {
	if (editState != null && editorPages != null
		&& editorPages[editState.ordinal()] != null)
	    editorPages[editState.ordinal()].render(graphics);
    }
}
