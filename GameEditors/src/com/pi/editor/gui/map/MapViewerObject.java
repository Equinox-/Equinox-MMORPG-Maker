package com.pi.editor.gui.map;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.pi.common.contants.SectorConstants;
import com.pi.common.contants.TileConstants;
import com.pi.common.database.GraphicsObject;
import com.pi.common.database.Sector;
import com.pi.common.database.Tile;
import com.pi.common.database.Tile.TileLayer;
import com.pi.graphics.device.IGraphics;
import com.pi.gui.PIButton;
import com.pi.gui.PIContainer;

public class MapViewerObject extends PIContainer {
    private PIButton scrollLeft, scrollRight, scrollUp, scrollDown;

    private Sector sectorInfo;
    private int cX = 0, cZ = 0;

    private int xOff = 0, zOff = 0;

    private static final int BUTTON_SIZE = 25;

    public MapViewerObject() {
	super();
	ScrollMouseListener sML = new ScrollMouseListener(this);

	scrollLeft = new PIButton();
	scrollLeft.addMouseListener(sML);
	scrollLeft.setSize(BUTTON_SIZE, BUTTON_SIZE);
	scrollLeft.setContent("<");

	scrollRight = new PIButton();
	scrollRight.addMouseListener(sML);
	scrollRight.setSize(BUTTON_SIZE, BUTTON_SIZE);
	scrollRight.setContent(">");

	scrollUp = new PIButton();
	scrollUp.addMouseListener(sML);
	scrollUp.setSize(BUTTON_SIZE, BUTTON_SIZE);
	scrollUp.setContent("/\\");

	scrollDown = new PIButton();
	scrollDown.addMouseListener(sML);
	scrollDown.setSize(BUTTON_SIZE, BUTTON_SIZE);
	scrollDown.setContent("\\/");

	add(scrollLeft);
	add(scrollRight);
	add(scrollUp);
	add(scrollDown);

	setSize(15, 15);
	setOffset(0, 0);
    }

    public void setSector(Sector sec) {
	this.sectorInfo = sec;
    }

    @Override
    public void setSize(int width, int height) {
	super.setSize(width * TileConstants.TILE_WIDTH, height
		* TileConstants.TILE_HEIGHT);

	scrollDown.setLocation(getWidth() - BUTTON_SIZE, getHeight()
		- BUTTON_SIZE - BUTTON_SIZE);
	scrollUp.setLocation(getWidth() - BUTTON_SIZE, BUTTON_SIZE);
	scrollRight.setLocation(getWidth() - BUTTON_SIZE - BUTTON_SIZE, 0);
	scrollLeft.setLocation(BUTTON_SIZE, 0);
    }

    @Override
    public void render(IGraphics g) {
	for (TileLayer l : TileLayer.values()) {
	    renderLayer(g, l);
	}

	g.setColor(Color.WHITE);
	g.drawRect((cX - xOff) * TileConstants.TILE_WIDTH, (cZ - zOff)
		* TileConstants.TILE_HEIGHT, TileConstants.TILE_WIDTH - 1,
		TileConstants.TILE_HEIGHT - 1);

	super.render(g);
    }

    private void renderLayer(IGraphics g, TileLayer l) {
	for (int x = xOff; x < Math.min(xOff
		+ (getWidth() / TileConstants.TILE_WIDTH),
		SectorConstants.SECTOR_WIDTH); x++) {
	    for (int z = zOff; z < Math.min(zOff
		    + (getHeight() / TileConstants.TILE_HEIGHT),
		    SectorConstants.SECTOR_HEIGHT); z++) {
		if (sectorInfo != null) {
		    Tile lTile = sectorInfo.getLocalTile(x, z);
		    if (lTile != null) {
			GraphicsObject gO = lTile.getLayer(l);
			if (gO != null) {
			    g.drawImage(gO, TileConstants.TILE_WIDTH
				    * (x - xOff) + getAbsoluteX(),
				    TileConstants.TILE_HEIGHT * (z - zOff)
					    + getAbsoluteY());
			}
		    }
		}
	    }
	}
    }

    public void setOffset(int x, int y) {
	this.xOff = x;
	this.zOff = y;
	scrollLeft.setVisible(xOff > 0);
	scrollRight.setVisible(xOff < SectorConstants.SECTOR_WIDTH
		- (getWidth() / TileConstants.TILE_WIDTH));

	scrollUp.setVisible(zOff > 0);
	scrollDown.setVisible(zOff < SectorConstants.SECTOR_HEIGHT
		- (getHeight() / TileConstants.TILE_HEIGHT));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
	super.mouseMoved(e);
	cX = (e.getX() / TileConstants.TILE_WIDTH) + xOff;
	cZ = (e.getY() / TileConstants.TILE_HEIGHT) + zOff;
    }

    private static class ScrollMouseListener extends MouseAdapter {
	private MapViewerObject o;

	public ScrollMouseListener(MapViewerObject o) {
	    this.o = o;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	    if (e.getSource() == o.scrollUp)
		o.setOffset(o.xOff, o.zOff - 1);
	    else if (e.getSource() == o.scrollDown)
		o.setOffset(o.xOff, o.zOff + 1);
	    else if (e.getSource() == o.scrollLeft)
		o.setOffset(o.xOff - 1, o.zOff);
	    else if (e.getSource() == o.scrollRight)
		o.setOffset(o.xOff + 1, o.zOff);
	}
    }
}
