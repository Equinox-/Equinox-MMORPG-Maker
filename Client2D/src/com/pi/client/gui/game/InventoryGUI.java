package com.pi.client.gui.game;

import java.awt.Point;

import com.pi.common.contants.ItemConstants;
import com.pi.common.database.Inventory;
import com.pi.common.database.Item;
import com.pi.common.database.def.ItemDef;
import com.pi.graphics.device.IGraphics;
import com.pi.gui.PIComponent;

/**
 * GUI class that displays the player's inventory.
 * 
 * @author Westin
 * 
 */
public class InventoryGUI extends PIComponent {
	private static final int INVENTORY_WIDTH = 4;
	private static final int INVENTORY_SLOT_PADDING = 2;
	private static final int INVENTORY_SLOT_SIZE = 32;
	private final MainGameGUI gui;

	public InventoryGUI(final MainGameGUI sGUI) {
		this.gui = sGUI;
		setLocation(10, 10);
		setSize(INVENTORY_WIDTH * INVENTORY_SLOT_SIZE,
				(int) (Math
						.ceil((ItemConstants.PLAYER_INVENTORY_SIZE / 6)) * INVENTORY_SLOT_SIZE));
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
				Item item = new Item(0, 1);
				renderItemInSlot(g, item, slot);
			}
		}
	}

	private final void renderItemInSlot(IGraphics g, Item i,
			int slot) {
		ItemDef def =
				gui.getClient().getDefs().getItemLoader()
						.getDef(i.getItemID());
		if (def != null) {
			Point p = slotToScreen(slot);
			if (def.getPositionWidth() > INVENTORY_SLOT_SIZE
					|| def.getPositionHeight() > INVENTORY_SLOT_SIZE) {
				float aspect =
						def.getPositionHeight()
								/ def.getPositionWidth();
				float expectedWidth =
						((float) INVENTORY_SLOT_SIZE) / aspect;
				float expectedHeight =
						INVENTORY_SLOT_SIZE * aspect;
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
									/ 2, p.y,
							(int) expectedWidth,
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
										.getPositionHeight())
								/ 2);
			}
		}
	}

	private final Point slotToScreen(int slot) {
		return new Point(
				getAbsoluteX()
						+ ((INVENTORY_SLOT_SIZE + (INVENTORY_SLOT_PADDING * 2)) * (slot % INVENTORY_WIDTH))
						+ INVENTORY_SLOT_PADDING,
				getAbsoluteY()
						+ ((INVENTORY_SLOT_SIZE + (INVENTORY_SLOT_PADDING * 2)) * (int) (slot / INVENTORY_WIDTH))
						+ INVENTORY_SLOT_PADDING);
	}
}
