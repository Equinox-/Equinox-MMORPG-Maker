package com.pi.common.database;

import com.pi.common.contants.GlobalConstants;

public class Entity {
    private int x, y;
    private byte dir;

    public int getX() {
	return x;
    }

    public int getY() {
	return y;
    }

    public byte getDir() {
	return dir;
    }

    public void moveTo(int x, int y) {
	if (Math.abs(this.x - x) > Math.abs(this.y - y)) {
	    if (this.x < x) {
		dir = GlobalConstants.DIR_RIGHT;
	    }else{
		dir = GlobalConstants.DIR_LEFT;
	    }
	}else{
	    if (this.y < y) {
		dir = GlobalConstants.DIR_DOWN;
	    }else{
		dir = GlobalConstants.DIR_UP;
	    }
	}
	this.x = x;
	this.y = y;
    }
}
