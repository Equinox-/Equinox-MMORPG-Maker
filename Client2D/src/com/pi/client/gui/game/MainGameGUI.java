package com.pi.client.gui.game;

import com.pi.client.Client;
import com.pi.gui.PIContainer;

/**
 * A container class for all the GUI components of the main game GUI.
 * 
 * @author Westin
 * 
 */
public class MainGameGUI extends PIContainer {
	/**
	 * The client instance this is bound to.
	 */
	private Client client;

	/**
	 * The GUI component displaying this container's inventory.
	 */
	private InventoryGUI inventoryGUI;

	/**
	 * Creates a main game GUI for the given client.
	 * 
	 * @param cli the client to bind to
	 */
	public MainGameGUI(final Client cli) {
		this.client = cli;
		this.inventoryGUI = new InventoryGUI(this);
		add(this.inventoryGUI);
		setLocation(0,0);
		compile();
	}

	/**
	 * Gets the client instance this GUI is bound to.
	 * 
	 * @return the client
	 */
	public final Client getClient() {
		return client;
	}
}
