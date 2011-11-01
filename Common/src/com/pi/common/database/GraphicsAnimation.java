package com.pi.common.database;

import com.pi.common.contants.GlobalConstants;

public class GraphicsAnimation extends GraphicsObject {
    private static final long serialVersionUID = GlobalConstants.serialVersionUID;
    private int frames;
    private int currentFrame = 0;
    private long lastFrame = 0;
    private long frameDuration;
    private int frameWidth;

    public GraphicsAnimation(int frames, long frameTime) {
	this.frames = frames;
	this.frameDuration = frameTime;
	this.lastFrame = System.currentTimeMillis();
    }

    public GraphicsAnimation(int frames, long frameTime, String graphic) {
	this(frames, frameTime);
	setGraphic(graphic);
    }
    public GraphicsAnimation(int frames, long frameTime, String graphic, int x, int y, int width, int height) {
	this(frames, frameTime, graphic);
	setPosition(x, y, width, height);
    }

    @Override
    public void setPosition(float x, float y, float width, float height) {
	super.setPosition(x, y, width, height);
	this.frameWidth = (int) (super.getPositionWidth() / frames);
    }

    @Override
    public float getPositionX() {
	advanceFrames();
	return super.getPositionX() + (frameWidth * currentFrame);
    }

    @Override
    public float getPositionWidth() {
	advanceFrames();
	return frameWidth;
    }

    private void advanceFrames() {
	if (lastFrame + frameDuration <= System.currentTimeMillis()) {
	    long passed = System.currentTimeMillis() - lastFrame;
	    currentFrame += passed / frameDuration;
	    if (currentFrame >= frames) {
		currentFrame -= frames;
	    }
	    lastFrame = System.currentTimeMillis();
	}
    }
}
