package com.pi.client.net;

import javax.swing.JOptionPane;

import com.pi.client.Client;
import com.pi.common.net.NetHandler;
import com.pi.common.net.packet.*;
import com.pi.common.net.packet.Packet2Alert.AlertType;

public class NetClientHandler extends NetHandler {
    private final NetClientClient netClient;
    private final Client client;

    public NetClientHandler(NetClientClient netClient, Client client) {
	this.client = client;
	this.netClient = netClient;
    }

    @Override
    public void process(Packet p) {
    }

    public void process(Packet0Disconnect p) {
	JOptionPane.showMessageDialog(null, ((Packet0Disconnect) p).reason
		+ "\n" + ((Packet0Disconnect) p).details);
	netClient.dispose();
    }

    public void process(Packet2Alert p) {
	if (p.alertType.equals(AlertType.MAIN_MENU)
		&& client.getDisplayManager() != null
		&& client.getDisplayManager().getRenderLoop() != null
		&& client.getDisplayManager().getRenderLoop().getMainMenu() != null) {
	    client.getDisplayManager().getRenderLoop().getMainMenu()
		    .alert(p.message);
	}
    }
}
