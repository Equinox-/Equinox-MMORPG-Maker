package com.pi.editor.gui.map;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.pi.common.contants.SectorConstants;
import com.pi.common.contants.TileConstants;
import com.pi.common.database.GraphicsObject;
import com.pi.common.database.Sector;
import com.pi.common.database.Tile;
import com.pi.common.database.Tile.TileLayer;
import com.pi.graphics.device.IGraphics;
import com.pi.gui.PIContainer;
import com.pi.gui.PIScrollBar;
import com.pi.gui.PIScrollBar.ScrollBarListener;
import com.pi.gui.PIScrollBar.ScrollEvent;

public class MapViewerObject extends PIContainer {

    private Sector sectorInfo;
    private PIScrollBar horiz, vert;

    int xOff = 0, zOff = 0;
    int cX = 0, cZ = 0;
    int iCX, iCY;

    int maxXOff, maxZOff;

    public MapInfoRenderer infoRender = null;

    public MapViewerObject() {
	super();
	EventHandler tHandle = new EventHandler();
	horiz = new PIScrollBar(true);
	vert = new PIScrollBar(false);
	horiz.addScrollBarListener(tHandle);
	vert.addScrollBarListener(tHandle);

	addMouseMotionListener(tHandle);
	addMouseListener(tHandle);

	add(horiz);
	add(vert);

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

	horiz.setLocation(25, 0);
	horiz.setSize(getWidth() - 50, 25);

	vert.setLocation(getWidth() - 25, 25);
	vert.setSize(25, getHeight() - 50);

	maxXOff = SectorConstants.SECTOR_WIDTH - width + 2;
	maxZOff = SectorConstants.SECTOR_HEIGHT - height + 2;
    }

    @Override
    public void render(IGraphics g) {
	for (TileLayer l : TileLayer.values()) {
	    renderLayer(g, l);
	}
	if (infoRender != null) {
	    for (int x = Math.max(xOff, 0); x < Math.min(xOff
		    + (getWidth() / TileConstants.TILE_WIDTH),
		    SectorConstants.SECTOR_WIDTH); x++) {
		for (int z = Math.max(zOff, 0); z < Math.min(zOff
			+ (getHeight() / TileConstants.TILE_HEIGHT),
			SectorConstants.SECTOR_HEIGHT); z++) {
		    if (sectorInfo != null) {
			Tile lTile = sectorInfo.getLocalTile(x, z);
			if (lTile != null) {
			    infoRender.renderMapTile(g,
				    TileConstants.TILE_WIDTH * (x - xOff)
					    + getAbsoluteX(),
				    TileConstants.TILE_HEIGHT * (z - zOff)
					    + getAbsoluteY(), lTile);
			}
		    }
		}
	    }
	}

	g.setColor(Color.WHITE);
	g.drawRect((cX - xOff) * TileConstants.TILE_WIDTH, (cZ - zOff)
		* TileConstants.TILE_HEIGHT, TileConstants.TILE_WIDTH - 1,
		TileConstants.TILE_HEIGHT - 1);
	super.render(g);
    }

    private void renderLayer(IGraphics g, TileLayer l) {
	for (int x = Math.max(xOff, 0); x < Math.min(xOff
		+ (getWidth() / TileConstants.TILE_WIDTH),
		SectorConstants.SECTOR_WIDTH); x++) {
	    for (int z = Math.max(zOff, 0); z < Math.min(zOff
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
    }

    private class EventHandler implements ScrollBarListener,
	    MouseMotionListener, MouseListener {
	@Override
	public void mouseDragged(MouseEvent e) {
	    iCX = e.getX() % TileConstants.TILE_WIDTH;
	    iCY = e.getY() % TileConstants.TILE_HEIGHT;

	    cX = (e.getX() / TileConstants.TILE_WIDTH) + xOff;
	    cZ = (e.getY() / TileConstants.TILE_HEIGHT) + zOff;
	    if (!vert.getBounds().contains(e.getX(), e.getY())
		    && !horiz.getBounds().contains(e.getX(), e.getY())
		    && infoRender != null)
		infoRender.onMapDrag(sectorInfo, cX, cZ, iCX, iCY);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	    iCX = e.getX() % TileConstants.TILE_WIDTH;
	    iCY = e.getY() % TileConstants.TILE_HEIGHT;

	    cX = (e.getX() / TileConstants.TILE_WIDTH) + xOff;
	    cZ = (e.getY() / TileConstants.TILE_HEIGHT) + zOff;
	}

	@Override
	public void onScroll(ScrollEvent e) {
	    if (e.getSource() == vert) {
		zOff = (int) (e.getScrollPosition() * maxZOff) - 1;
	    } else if (e.getSource() == horiz) {
		xOff = (int) (e.getScrollPosition() * maxXOff) - 1;
	    }
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	    if (!vert.getBounds().contains(e.getX(), e.getY())
		    && !horiz.getBounds().contains(e.getX(), e.getY())
		    && infoRender != null)
		infoRender.onMapClick(sectorInfo, cX, cZ, iCX, iCY);
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
    }

    public Sector getSector() {
	return sectorInfo;
    }
}
