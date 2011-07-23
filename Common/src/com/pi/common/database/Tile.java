package com.pi.common.database;

import java.io.Serializable;

import com.pi.common.contants.GlobalConstants;

public class Tile implements Serializable {
    private static final long serialVersionUID = GlobalConstants.serialVersionUID;
    private int flags = 0;
    private GraphicsObject ground;
    private GraphicsObject mask1;
    private GraphicsObject fringe1;

    public int getFlags() {
	return flags;
    }

    public void setFlags(int flags) {
	this.flags = flags;
    }

    public boolean hasFlag(int flag) {
	return (flags & flag) == flag;
    }

    public void setGround(GraphicsObject ground) {
	this.ground = ground;
    }

    public void setMask1(GraphicsObject mask1) {
	this.mask1 = mask1;
    }

    public void setFringe1(GraphicsObject fringe1) {
	this.fringe1 = fringe1;
    }

    public GraphicsObject getGround() {
	return ground;
    }

    public GraphicsObject getMask1() {
	return mask1;
    }

    public GraphicsObject getFringe1() {
	return fringe1;
    }
}
