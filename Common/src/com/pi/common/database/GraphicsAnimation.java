package com.pi.common.database;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A graphics object that has an animation.
 * 
 * @author Westin
 * 
 */
public class GraphicsAnimation extends GraphicsObject {
	/**
	 * The number for frames in this graphics object.
	 */
	private int frames;
	/**
	 * The currently visible frame.
	 */
	private int currentFrame = 0;
	/**
	 * The time in milliseconds that the last frame was rendered at.
	 */
	private long lastFrame = 0;
	/**
	 * How long in milliseconds each frame lasts.
	 */
	private long frameDuration;
	/**
	 * The width in pixels of each frame.
	 */
	private int frameWidth;
	/**
	 * The current frame x.
	 */
	private int currentFrameX = 0;

	/**
	 * Creates a graphics animation that still needs to be assigned a graphic id
	 * and a graphics position.
	 * 
	 * @param sFrames the number of frames
	 * @param sFrameTime the frame duration in milliseconds
	 */
	public GraphicsAnimation(final int sFrames,
			final long sFrameTime) {
		this.frames = sFrames;
		this.frameDuration = sFrameTime;
		this.lastFrame = System.currentTimeMillis();
	}

	/**
	 * Creates a graphics animation that still needs to be assigned a graphics
	 * position.
	 * 
	 * @param sFrames the number of frames
	 * @param sFrameTime the frame duration in milliseconds
	 * @param sGraphic the graphic id
	 */
	public GraphicsAnimation(final int sFrames,
			final long sFrameTime, final int sGraphic) {
		this(sFrames, sFrameTime);
		setGraphic(sGraphic);
	}

	/**
	 * Creates a graphics animation.
	 * 
	 * @param sFrames the number of frames
	 * @param sFrameTime the frame duration in milliseconds
	 * @param sGraphic the graphic id
	 * @param sX the x position of the graphic
	 * @param sY the y position of the graphic
	 * @param sWidth the width of the graphic
	 * @param sHeight the height of the graphic
	 */
	public GraphicsAnimation(final int sFrames,
			final long sFrameTime, final int sGraphic,
			final int sX, final int sY, final int sWidth,
			final int sHeight) {
		this(sFrames, sFrameTime, sGraphic);
		setPosition(sX, sY, sWidth, sHeight);
	}

	@Override
	public final void setPosition(final float x, final float y,
			final float width, final float height) {
		super.setPosition(x, y, width, height);
		this.frameWidth =
				(int) (super.getPositionWidth() / frames);
	}

	@Override
	public final float getPositionX() {
		advanceFrames();
		return super.getPositionX() + currentFrameX;
	}

	@Override
	public final float getPositionWidth() {
		advanceFrames();
		return frameWidth;
	}

	/**
	 * Recalculate the frame position.
	 */
	private void advanceFrames() {
		if (lastFrame + frameDuration <= System
				.currentTimeMillis()) {
			long passed = System.currentTimeMillis() - lastFrame;
			currentFrame += passed / frameDuration;
			if (currentFrame >= frames) {
				currentFrame -= frames;
			}
			currentFrameX = frameWidth * currentFrame;
			lastFrame = System.currentTimeMillis();
		}
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		super.writeData(pOut);
		pOut.writeInt(frames);
		pOut.writeInt(frameWidth);
		pOut.writeLong(frameDuration);
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		super.readData(pIn);
		frames = pIn.readInt();
		frameWidth = pIn.readInt();
		frameDuration = pIn.readLong();
	}

	@Override
	public final int getLength() {
		return super.getLength() + (SizeOf.INT * 2)
				+ SizeOf.LONG;
	}
}
