package com.pi.common.database;

import com.pi.common.contants.GlobalConstants;

public class GraphicsAnimation extends GraphicsObject {
    private static final long serialVersionUID = GlobalConstants.serialVersionUID;
    private int frames;
    private int frame_width;
    private int currentFrame = 0;
    private long lastFrame = 0;
    private long frameDuration;

    public GraphicsAnimation(int frames, int frame_width, long frameTime) {
	this.frames = frames;
	this.frame_width = frame_width;
	this.frameDuration = frameTime;
	lastFrame = System.currentTimeMillis();
    }

    @Override
    public float getPositionX() {
	if (lastFrame + frameDuration <= System.currentTimeMillis()) {
	    long passed = System.currentTimeMillis()
		    - lastFrame;
	    currentFrame += passed / frameDuration;
	    if (currentFrame >= frames) {
		currentFrame = 0;
	    }
	    lastFrame = System.currentTimeMillis();
	}
	return super.getPositionX() + (frame_width * currentFrame);
    }

    @Override
    public float getPositionWidth() {
	if (lastFrame + frameDuration <= System.currentTimeMillis()) {
	    long passed = System.currentTimeMillis()
		    - lastFrame;
	    currentFrame += passed / frameDuration;
	    if (currentFrame >= frames) {
		currentFrame = 0;
	    }
	    lastFrame = System.currentTimeMillis();
	}
	return super.getPositionWidth() / frames;
    }
}
