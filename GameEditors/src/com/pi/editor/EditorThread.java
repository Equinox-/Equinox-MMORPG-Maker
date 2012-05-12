package com.pi.editor;


public abstract class EditorThread extends Thread {
    protected boolean running = true;
    protected final Editor editor;
    protected Object mutex;

    public EditorThread(Editor editor) {
	super(editor.getThreadGroup(), "EditorThread");
	super.setName(getClass().getSimpleName());
	this.editor = editor;
    }

    @Override
    public void run() {
	editor.getLog().fine("Started: " + getClass().getSimpleName());
	while (running && shouldLoop()) {
	    loop();
	}
	editor.getLog().fine("Stopped: " + getClass().getSimpleName());
    }

    private boolean shouldLoop() {
	return true;
    }

    protected abstract void loop();

    public void dispose() {
	running = false;
	if (mutex != null) {
	    synchronized (mutex) {
		mutex.notify();
	    }
	}
	try {
	    join();
	} catch (InterruptedException e) {
	    editor.getLog().printStackTrace(e);
	    System.exit(0);
	}
    }
}
