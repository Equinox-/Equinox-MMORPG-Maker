package com.pi.client.gui.game;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import com.pi.common.contants.GraphicsConstants;
import com.pi.common.contants.ItemConstants;
import com.pi.common.database.Inventory;
import com.pi.common.database.Item;
import com.pi.common.database.def.ItemDef;
import com.pi.graphics.device.IGraphics;
import com.pi.gui.GUIKit;
import com.pi.gui.PIComponent;
import com.pi.gui.PIStyle.StyleType;

/**
 * GUI class that displays the player's inventory.
 * 
 * @author Westin
 * 
 */
public class InventoryGUI extends PIComponent {
	/**
	 * The number of slots per inventory row.
	 */
	private static final int INVENTORY_WIDTH = 4;
	/**
	 * The number of pixels that each side of a slot is padded with.
	 */
	private static final int INVENTORY_SLOT_PADDING = 1;
	/**
	 * The width and height in pixels of each slot.
	 */
	private static final int INVENTORY_SLOT_SIZE = 32;
	/**
	 * The main game GUI instance this inventory is bound to.
	 */
	private final MainGameGUI gui;

	/**
	 * Creates an inventory GUI for the given GUI context.
	 * 
	 * @param sGUI the GUI to bind to
	 */
	public InventoryGUI(final MainGameGUI sGUI) {
		this.gui = sGUI;

		setSize((INVENTORY_SLOT_SIZE + (INVENTORY_SLOT_PADDING * 2))
				* INVENTORY_WIDTH,
				(INVENTORY_SLOT_SIZE + (INVENTORY_SLOT_PADDING * 2))
						* (int) Math
								.ceil(ItemConstants.PLAYER_INVENTORY_SIZE
										/ INVENTORY_WIDTH));
		setStyle(StyleType.NORMAL,
				GUIKit.DEFAULT_CONTAINER_STYLE);
	}

	@Override
	public final void render(final IGraphics g) {
		super.render(g);
		if (isVisible()) {
			// Render the inventory!
			Inventory inv =
					gui.getClient().getMainGame()
							.getClientCache().getInventory();
			for (int slot = 0; slot < inv.getSlotCount(); slot++) {
				renderItemInSlot(g, inv.getInventoryAt(slot),
						slot);
			}
		}
	}

	/**
	 * Renders the given item, on the given graphics context, at the screen
	 * location specified by {@link #slotToScreen(int)}, with an argument of
	 * slot.
	 * 
	 * @param g the graphics context
	 * @param i the item
	 * @param slot the slot
	 */
	private void renderItemInSlot(final IGraphics g,
			final Item i, final int slot) {
		if (i == null || i.getItemCount() < 1) {
			return;
		}
		ItemDef def =
				gui.getClient().getDefs().getItemLoader()
						.getDef(i.getItemID());
		if (def == null) {
			return;
		}

		Point p = slotToScreen(slot);
		if (def.getPositionWidth() > INVENTORY_SLOT_SIZE
				|| def.getPositionHeight() > INVENTORY_SLOT_SIZE) {
			float aspect =
					def.getPositionHeight()
							/ def.getPositionWidth();
			float expectedWidth =
					((float) INVENTORY_SLOT_SIZE) / aspect;
			float expectedHeight = INVENTORY_SLOT_SIZE * aspect;
			if (expectedWidth > INVENTORY_SLOT_SIZE) {
				g.drawImage(
						def,
						p.x,
						p.y
								+ (int) (INVENTORY_SLOT_SIZE - expectedHeight)
								/ 2, INVENTORY_SLOT_SIZE,
						(int) expectedHeight);
			} else {
				g.drawImage(
						def,
						p.x
								+ (int) (INVENTORY_SLOT_SIZE - expectedWidth)
								/ 2, p.y, (int) expectedWidth,
						INVENTORY_SLOT_SIZE);
			}
		} else {
			g.drawImage(
					def,
					p.x
							+ (int) (INVENTORY_SLOT_SIZE - def
									.getPositionWidth()) / 2,
					p.y
							+ (int) (INVENTORY_SLOT_SIZE - def
									.getPositionHeight()) / 2);
		}
		if (i.getItemCount() > 1) {
			Rectangle2D bounds =
					g.getStringBounds(GraphicsConstants.FONT,
							Integer.toString(i.getItemCount()));
			g.drawText(
					Integer.toString(i.getItemCount()),
					p.x + INVENTORY_SLOT_SIZE
							- (int) bounds.getWidth(),
					p.y + INVENTORY_SLOT_SIZE
							- (int) bounds.getHeight(),
					GraphicsConstants.FONT, Color.RED);
		}
	}

	/**
	 * Converts a slot to screen space.
	 * 
	 * @param slot the slot index
	 * @return the upper left corner of the slot
	 */
	private Point slotToScreen(final int slot) {
		return new Point(
				getAbsoluteX()
						+ ((INVENTORY_SLOT_SIZE + (INVENTORY_SLOT_PADDING * 2)) * (slot % INVENTORY_WIDTH))
						+ INVENTORY_SLOT_PADDING,
				getAbsoluteY()
						+ ((INVENTORY_SLOT_SIZE + (INVENTORY_SLOT_PADDING * 2)) * (int) (slot / INVENTORY_WIDTH))
						+ INVENTORY_SLOT_PADDING);
	}
}
