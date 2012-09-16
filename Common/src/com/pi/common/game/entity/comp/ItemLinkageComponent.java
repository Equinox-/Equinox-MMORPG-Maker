package com.pi.common.game.entity.comp;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class ItemLinkageComponent extends EntityComponent {
	private int itemDefID;

	public ItemLinkageComponent() {
	}

	public ItemLinkageComponent(final int sItem) {
		this.itemDefID = sItem;
	}

	@Override
	public void writeData(PacketOutputStream pOut) throws IOException {
		pOut.writeInt(itemDefID);
	}

	@Override
	public int getLength() {
		return SizeOf.INT;
	}

	@Override
	public void readData(PacketInputStream pIn) throws IOException {
		itemDefID = pIn.readInt();
	}

	public int getItemID() {
		return itemDefID;
	}

}
