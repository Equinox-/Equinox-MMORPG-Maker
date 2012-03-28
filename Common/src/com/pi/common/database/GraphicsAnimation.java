package com.pi.common.database;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class GraphicsAnimation extends GraphicsObject {
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

    public GraphicsAnimation(int frames, long frameTime, int graphic) {
	this(frames, frameTime);
	setGraphic(graphic);
    }

    public GraphicsAnimation(int frames, long frameTime, int graphic, int x,
	    int y, int width, int height) {
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

    @Override
    public void writeData(PacketOutputStream pOut) throws IOException {
	super.writeData(pOut);
	pOut.writeInt(frames);
	pOut.writeInt(frameWidth);
	pOut.writeLong(frameDuration);
    }

    @Override
    public void readData(PacketInputStream pIn) throws IOException {
	super.readData(pIn);
	frames = pIn.readInt();
	frameWidth = pIn.readInt();
	frameDuration = pIn.readLong();
    }

    @Override
    public int getLength() {
	return super.getLength() + 16;
    }
}
