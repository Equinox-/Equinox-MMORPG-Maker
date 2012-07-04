package com.pi.editor.gui.map;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.pi.common.contants.SectorConstants;
import com.pi.common.contants.TileConstants;
import com.pi.common.contants.TileFlags;
import com.pi.common.database.GraphicsObject;
import com.pi.common.database.Sector;
import com.pi.common.database.Tile;
import com.pi.common.database.TileGraphicsObject;
import com.pi.common.database.TileLayer;
import com.pi.common.database.io.DatabaseIO;
import com.pi.editor.Paths;
import com.pi.graphics.device.IGraphics;
import com.pi.gui.GUIKit;
import com.pi.gui.PIButton;
import com.pi.gui.PICheckbox;
import com.pi.gui.PIComponent;
import com.pi.gui.PIContainer;
import com.pi.gui.PIScrollBar;
import com.pi.gui.PIScrollBar.ScrollBarListener;
import com.pi.gui.PIScrollBar.ScrollEvent;
import com.pi.gui.PIStyle.StyleType;

public class MapEditorObject extends PIContainer implements
		ScrollBarListener, MapInfoRenderer {
	private static int[] TILESETS = new int[] { 2 };
	private File lastDirectory;
	private MapRenderLoop loop;

	public static void init() {
		String s =
				JOptionPane
						.showInputDialog("List tileset image IDs, comma seperated values\n(From "
								+ Paths.getGraphicsDirectory()
										.getAbsolutePath() + ")");
		String[] dat = s.split(",");
		TILESETS = new int[dat.length];
		for (int i = 0; i < dat.length; i++) {
			TILESETS[i] = Integer.valueOf(dat[i]);
		}
	}

	private PIContainer tileSelector;
	private PIComponent graphicsData;
	private PIScrollBar tilesetSelector;
	private PIScrollBar verticalTile;
	private PIScrollBar horizontalTile;
	private PIScrollBar tileLayerSelector;

	private PIButton save, load, newS, fill;

	private PICheckbox directionBlockMode;

	private int tileset = TILESETS[0];

	private TileSelectionHandler tileSelectionHandler =
			new TileSelectionHandler();

	private int currentTileOffX, currentTileOffY;
	private boolean updateTilesetScroll = true;

	private MapViewerObject viewer;

	public MapEditorObject(MapViewerObject viewer,
			MapRenderLoop loop) {
		setLocation(500, 0);
		this.loop = loop;

		this.viewer = viewer;

		tileSelector = new PIContainer();
		tileSelector.setLocation(0, 0);
		tileSelector.setStyle(StyleType.NORMAL,
				GUIKit.DEFAULT_CONTAINER_STYLE);

		graphicsData = new PIComponent() {
			@Override
			public void render(IGraphics g) {
				super.render(g);
				if (isVisible && g.getImageWidth(tileset) > 0) {
					g.setColor(Color.BLACK);
					g.fillRect(getAbsoluteX(), getAbsoluteY(),
							getWidth(), getHeight());
					g.setColor(Color.WHITE);
					g.drawRect(getAbsoluteX(), getAbsoluteY(),
							getWidth(), getHeight());

					int iWidth = g.getImageWidth(tileset);
					int iHeight = g.getImageHeight(tileset);

					if (updateTilesetScroll) {
						horizontalTile
								.setVisible(iWidth > getWidth());
						verticalTile
								.setVisible(iHeight > getHeight());
						horizontalTile.setScrollAmount(0);
						verticalTile.setScrollAmount(0);

						if (horizontalTile.isVisible()) {
							horizontalTile
									.setStep(1f / (float) ((iWidth - getWidth()) / TileConstants.TILE_WIDTH));
						}

						if (verticalTile.isVisible()) {
							verticalTile
									.setStep(1f / (float) ((iHeight - getHeight()) / TileConstants.TILE_HEIGHT));
						}
					}
					currentTileOffX =
							(int) (horizontalTile
									.getScrollAmount() * (iWidth - getWidth()));
					currentTileOffY =
							(int) (verticalTile
									.getScrollAmount() * (iHeight - getHeight()));
					g.drawImage(tileset, getAbsoluteX() + 1,
							getAbsoluteY() + 1, currentTileOffX,
							currentTileOffY, Math.min(iWidth
									- currentTileOffX,
									getWidth() - 2), Math.min(
									iHeight - currentTileOffY,
									getHeight() - 2));

					int destX =
							(Math.min(tileX, dragTileX) * TileConstants.TILE_WIDTH)
									- currentTileOffX
									+ getAbsoluteX();
					int destY =
							(Math.min(tileY, dragTileY) * TileConstants.TILE_HEIGHT)
									- currentTileOffY
									+ getAbsoluteY();

					int destX2 =
							(Math.max(tileX, dragTileX) * TileConstants.TILE_WIDTH)
									- currentTileOffX
									+ getAbsoluteX();
					int destY2 =
							(Math.max(tileY, dragTileY) * TileConstants.TILE_HEIGHT)
									- currentTileOffY
									+ getAbsoluteY();
					g.setColor(Color.WHITE.darker());
					g.drawRect(destX + 1, destY + 1,
							(destX2 - destX)
									+ TileConstants.TILE_WIDTH
									- 1, (destY2 - destY)
									+ TileConstants.TILE_HEIGHT
									- 1);

					if (tileAX >= 0) {
						destX =
								(tileAX * TileConstants.TILE_WIDTH)
										- currentTileOffX
										+ getAbsoluteX();
						destY =
								(tileAY * TileConstants.TILE_HEIGHT)
										- currentTileOffY
										+ getAbsoluteY();

						destX2 =
								(dragTileAX * TileConstants.TILE_WIDTH)
										- currentTileOffX
										+ getAbsoluteX();
						destY2 =
								(dragTileAY * TileConstants.TILE_HEIGHT)
										- currentTileOffY
										+ getAbsoluteY();
						g.setColor(Color.WHITE);
						g.drawRect(
								destX + 1,
								destY + 1,
								(destX2 - destX)
										+ TileConstants.TILE_WIDTH
										- 1,
								(destY2 - destY)
										+ TileConstants.TILE_HEIGHT
										- 1);
					}
				}
			}
		};
		graphicsData
				.addMouseMotionListener(tileSelectionHandler);
		graphicsData.addMouseListener(tileSelectionHandler);

		horizontalTile = new PIScrollBar(true);

		verticalTile = new PIScrollBar(false);

		tilesetSelector = new PIScrollBar(true);
		tilesetSelector.setStep(1f / ((float) TILESETS.length));

		tileLayerSelector = new PIScrollBar(true);
		tileLayerSelector
				.setStep(1f / ((float) TileLayer.MAX_VALUE
						.ordinal() - 1));
		tileLayerSelector.addScrollBarListener(this);
		tileLayerSelector.setStyle(StyleType.NORMAL,
				tileLayerSelector.getStyle(StyleType.NORMAL));
		tileLayerSelector.getStyle(StyleType.NORMAL).foreground =
				Color.WHITE;
		tileLayerSelector.setOverlay((currentTileLayer =
				TileLayer.values()[Math.round(tileLayerSelector
						.getScrollAmount()
						* (TileLayer.MAX_VALUE.ordinal() - 1))])
				.name());

		tileSelector.add(graphicsData);
		tileSelector.add(horizontalTile);
		tileSelector.add(verticalTile);
		tileSelector.add(tilesetSelector);
		tileSelector.add(tileLayerSelector);

		add(tileSelector);

		load = new PIButton();
		load.setContent("Load");
		load.setLocation(325, 0);
		load.setSize(50, 25);
		load.addMouseListener(tileSelectionHandler);

		save = new PIButton();
		save.setLocation(325, 40);
		save.setContent("Save");
		save.setSize(50, 25);
		save.addMouseListener(tileSelectionHandler);

		newS = new PIButton();
		newS.setLocation(325, 80);
		newS.setContent("New");
		newS.setSize(50, 25);
		newS.addMouseListener(tileSelectionHandler);

		directionBlockMode = new PICheckbox();
		directionBlockMode.setContent("Directional Blocking");
		directionBlockMode.setLocation(325, 120);
		directionBlockMode.setSize(125, 25);
		directionBlockMode.setChecked(false);
		directionBlockMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tileSelector.setVisible(!directionBlockMode
						.isChecked());
			}
		});

		fill = new PIButton();
		fill.setLocation(325, 160);
		fill.setContent("Fill");
		fill.setSize(50, 25);
		fill.addMouseListener(tileSelectionHandler);

		add(load);
		add(save);
		add(newS);
		add(fill);
		add(directionBlockMode);

		setSize(500, 500);

		compile();

		viewer.infoRender = this;
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		tileSelector.setSize(300, height);
		graphicsData.setLocation(0, 0);
		graphicsData.setSize(tileSelector.getWidth() - 25,
				tileSelector.getHeight() - 75);

		horizontalTile.setLocation(0,
				tileSelector.getHeight() - 75);
		horizontalTile.setSize(tileSelector.getWidth() - 25, 25);
		verticalTile
				.setLocation(tileSelector.getWidth() - 25, 0);
		verticalTile.setSize(25, tileSelector.getHeight() - 75);
		tilesetSelector.setLocation(0,
				tileSelector.getHeight() - 50);
		tilesetSelector
				.setSize(tileSelector.getWidth() - 25, 25);
		tileLayerSelector.setLocation(0,
				tileSelector.getHeight() - 25);
		tileLayerSelector.setSize(tileSelector.getWidth() - 25,
				25);
	}

	@Override
	public void onScroll(ScrollEvent e) {
		if (e.getSource() == tilesetSelector) {
			int nTiles =
					Math.round(e.getScrollPosition()
							* TILESETS.length);
			if (nTiles != tileset) {
				horizontalTile.setScrollAmount(0);
				verticalTile.setScrollAmount(0);
				tileset = nTiles;
				tileX = 0;
				tileY = 0;
				dragTileX = 0;
				dragTileY = 0;
				tileAX = -1;
				updateTilesetScroll = true;
			}
		} else if (e.getSource() == tileLayerSelector) {
			tileLayerSelector.setOverlay((currentTileLayer =
					TileLayer.values()[Math
							.round(tileLayerSelector
									.getScrollAmount()
									* (TileLayer.MAX_VALUE
											.ordinal() - 1))])
					.name());
		}
	}

	public int tileX, tileY, dragTileX, dragTileY;
	public int tileAX = -1, tileAY, dragTileAX, dragTileAY;
	private boolean mouseDown = false;
	private TileLayer currentTileLayer = TileLayer.GROUND;

	private class TileSelectionHandler implements MouseListener,
			MouseMotionListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == save) {
				JFileChooser fc = new JFileChooser();
				if (lastDirectory != null)
					fc.setCurrentDirectory(lastDirectory);
				fc.requestFocus();
				int returnVal =
						fc.showSaveDialog(loop.getEditor()
								.getContainer());

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					lastDirectory = new File(file.getParent());
					try {
						DatabaseIO.write(file,
								viewer.getSector());
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								ex.toString());
					}
				}
			} else if (e.getSource() == load) {
				JFileChooser fc = new JFileChooser();
				if (lastDirectory != null)
					fc.setCurrentDirectory(lastDirectory);
				fc.requestFocus();
				int returnVal =
						fc.showOpenDialog(loop.getEditor()
								.getContainer());

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					lastDirectory = new File(file.getParent());
					try {
						viewer.setSector((Sector) DatabaseIO
								.read(file, new Sector()));
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								ex.toString());
					}
				}
			} else if (e.getSource() == newS) {
				viewer.setSector(new Sector());
			} else if (e.getSource() == fill
					&& viewer.getSector() != null
					&& !directionBlockMode.isChecked()) {
				int result =
						JOptionPane
								.showConfirmDialog(
										null,
										"Are you sure you want to fill with this tile?",
										"Fill map",
										JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					for (int x = 0; x < SectorConstants.SECTOR_WIDTH; x++) {
						for (int y = 0; y < SectorConstants.SECTOR_HEIGHT; y++) {
							onMapClick(viewer.getSector(),
									MouseEvent.BUTTON1, x, y, 0,
									0);
						}
					}
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mouseDown = e.getSource() == graphicsData;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (mouseDown) {
				tileAX = Math.min(tileX, dragTileX);
				tileAY = Math.min(tileY, dragTileY);
				dragTileAX = Math.max(tileX, dragTileX);
				dragTileAY = Math.max(tileY, dragTileY);
				mouseDown = false;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (e.getSource() == graphicsData) {
				dragTileX =
						(e.getX() + currentTileOffX)
								/ TileConstants.TILE_WIDTH;
				dragTileY =
						(e.getY() + currentTileOffY)
								/ TileConstants.TILE_HEIGHT;
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (e.getSource() == graphicsData) {
				tileX =
						(e.getX() + currentTileOffX)
								/ TileConstants.TILE_WIDTH;
				tileY =
						(e.getY() + currentTileOffY)
								/ TileConstants.TILE_HEIGHT;
				dragTileX = tileX;
				dragTileY = tileY;
			}
		}

	}

	private static final int hoffset = 2;
	private static final int voffset = 4;

	@Override
	public void renderMapTile(IGraphics g, int baseX, int baseY,
			int tileX, int tileY, Tile tile) {
		if (directionBlockMode.isChecked()) {
			g.drawText(
					"<",
					baseX + hoffset,
					baseY - voffset
							+ (TileConstants.TILE_HEIGHT / 3),
					GUIKit.DEFAULT_STYLE.font,
					((tile.getFlags() & TileFlags.WALL_WEST) == TileFlags.WALL_WEST) ? Color.RED
							: Color.GREEN);
			g.drawText(
					">",
					baseX + hoffset + 2
							* (TileConstants.TILE_WIDTH / 3),
					baseY - voffset
							+ (TileConstants.TILE_HEIGHT / 3),
					GUIKit.DEFAULT_STYLE.font,
					((tile.getFlags() & TileFlags.WALL_EAST) == TileFlags.WALL_EAST) ? Color.RED
							: Color.GREEN);

			g.drawText(
					"/\\",
					baseX + hoffset
							+ (TileConstants.TILE_WIDTH / 3),
					baseY - voffset,
					GUIKit.DEFAULT_STYLE.font,
					((tile.getFlags() & TileFlags.WALL_NORTH) == TileFlags.WALL_NORTH) ? Color.RED
							: Color.GREEN);
			g.drawText(
					"\\/",
					baseX + hoffset
							+ (TileConstants.TILE_WIDTH / 3),
					baseY
							- voffset
							+ (2 * (TileConstants.TILE_HEIGHT / 3)),
					GUIKit.DEFAULT_STYLE.font,
					((tile.getFlags() & TileFlags.WALL_SOUTH) == TileFlags.WALL_SOUTH) ? Color.RED
							: Color.GREEN);

			g.setColor((tile.getFlags() & TileFlags.BLOCKED) == TileFlags.BLOCKED ? Color.RED
					: Color.GREEN);
			g.fillRect(baseX + (TileConstants.TILE_WIDTH / 3)
					+ 2, baseY + (TileConstants.TILE_HEIGHT / 3)
					+ 2, (TileConstants.TILE_WIDTH / 3) - 4,
					(TileConstants.TILE_HEIGHT / 3) - 4);

			g.setColor(Color.BLUE);
			g.drawRect(baseX, baseY,
					TileConstants.TILE_WIDTH - 1,
					TileConstants.TILE_HEIGHT - 1);
		}
	}

	@Override
	public void onMapClick(Sector s, int button, int tileX,
			int tileY, int internalX, int internalY) {
		if (s != null) {
			if (button == MouseEvent.BUTTON1) {
				if (!directionBlockMode.isChecked()) {
					if (tileAX >= 0) {
						int tileWidth = dragTileAX - tileAX;
						int tileHeight = dragTileAY - tileAY;
						for (int x = tileX; x <= Math
								.min(tileX + tileWidth,
										SectorConstants.SECTOR_WIDTH - 1); x++) {
							for (int y = tileY; y <= Math
									.min(tileY + tileHeight,
											SectorConstants.SECTOR_HEIGHT - 1); y++) {
								TileGraphicsObject tObj =
										new TileGraphicsObject();
								tObj.setPosition(
										((x - tileX) + tileAX)
												* TileConstants.TILE_WIDTH,
										((y - tileY) + tileAY)
												* TileConstants.TILE_HEIGHT,
										TileConstants.TILE_WIDTH,
										TileConstants.TILE_HEIGHT);
								s.getLocalTile(x, y).setLayer(
										currentTileLayer, tObj);
							}
						}
					}
				} else {
					int flags =
							s.getLocalTile(tileX, tileY)
									.getFlags();
					if (internalX < (TileConstants.TILE_WIDTH / 3)) {
						if (internalY > (TileConstants.TILE_HEIGHT / 3)
								&& internalY < 2 * (TileConstants.TILE_HEIGHT / 3)) {
							if ((flags & TileFlags.WALL_WEST) == TileFlags.WALL_WEST)
								flags &= (~TileFlags.WALL_WEST);
							else
								flags |= TileFlags.WALL_WEST;
						}
					} else if (internalX > 2 * (TileConstants.TILE_WIDTH / 3)) {
						if (internalY > (TileConstants.TILE_HEIGHT / 3)
								&& internalY < 2 * (TileConstants.TILE_HEIGHT / 3)) {
							if ((flags & TileFlags.WALL_EAST) == TileFlags.WALL_EAST)
								flags &= (~TileFlags.WALL_EAST);
							else
								flags |= TileFlags.WALL_EAST;
						}
					} else {
						if (internalY < (TileConstants.TILE_HEIGHT / 3)) {
							if ((flags & TileFlags.WALL_NORTH) == TileFlags.WALL_NORTH)
								flags &= (~TileFlags.WALL_NORTH);
							else
								flags |= TileFlags.WALL_NORTH;
						} else if (internalY > 2 * (TileConstants.TILE_HEIGHT / 3)) {
							if ((flags & TileFlags.WALL_SOUTH) == TileFlags.WALL_SOUTH)
								flags &= (~TileFlags.WALL_SOUTH);
							else
								flags |= TileFlags.WALL_SOUTH;
						} else {
							if ((flags & TileFlags.BLOCKED) == TileFlags.BLOCKED) {
								flags = 0;
								if (tileX > 0) {
									s.getLocalTile(tileX - 1,
											tileY).removeFlag(
											TileFlags.WALL_EAST);
								}
								if (tileY > 0) {
									s.getLocalTile(tileX,
											tileY - 1)
											.removeFlag(
													TileFlags.WALL_SOUTH);
								}
								if (tileX < SectorConstants.SECTOR_WIDTH - 1) {
									s.getLocalTile(tileX + 1,
											tileY).removeFlag(
											TileFlags.WALL_WEST);
								}
								if (tileY < SectorConstants.SECTOR_HEIGHT - 1) {
									s.getLocalTile(tileX,
											tileY - 1)
											.removeFlag(
													TileFlags.WALL_NORTH);
								}
							} else {
								flags |= TileFlags.BLOCKED;
								if (tileX > 0) {
									s.getLocalTile(tileX - 1,
											tileY).applyFlag(
											TileFlags.WALL_EAST);
								}
								if (tileY > 0) {
									s.getLocalTile(tileX,
											tileY - 1)
											.applyFlag(
													TileFlags.WALL_SOUTH);
								}
								if (tileX < SectorConstants.SECTOR_WIDTH - 1) {
									s.getLocalTile(tileX + 1,
											tileY).applyFlag(
											TileFlags.WALL_WEST);
								}
								if (tileY < SectorConstants.SECTOR_HEIGHT - 1) {
									s.getLocalTile(tileX,
											tileY + 1)
											.applyFlag(
													TileFlags.WALL_NORTH);
								}
							}
						}
					}
					s.getLocalTile(tileX, tileY).setFlags(flags);
				}
			} else if (button == MouseEvent.BUTTON3) {
				Tile t = s.getLocalTile(tileX, tileY);
				if (t != null
						&& t.getLayer(currentTileLayer) != null) {
					tileset =
							t.getLayer(currentTileLayer)
									.getGraphic();
					tileAX =
							(int) (t.getLayer(currentTileLayer)
									.getPositionX() / TileConstants.TILE_WIDTH);
					tileAY =
							(int) (t.getLayer(currentTileLayer)
									.getPositionY() / TileConstants.TILE_HEIGHT);
					dragTileAX = tileAX;
					dragTileAY = tileAY;
				}
			}
		}
	}

	@Override
	public void onMapDrag(Sector s, int button, int tileX,
			int tileY, int internalX, int internalY) {
		if (button == MouseEvent.BUTTON1)
			onMapClick(s, button, tileX, tileY, internalX,
					internalY);
	}

	@Override
	public int[] getCurrentTiledata() {
		return new int[] { tileset, tileAX, tileAY, dragTileAX,
				dragTileAY };
	}

	@Override
	public TileLayer getCurrentTileLayer() {
		return directionBlockMode.isChecked() || tileAX < 0 ? null
				: currentTileLayer;
	}
}
