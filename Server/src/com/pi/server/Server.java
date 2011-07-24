package com.pi.server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JTextPane;

import com.pi.common.PILogger;
import com.pi.server.database.ServerDatabase;
import com.pi.server.net.NetServer;

public class Server {
    private NetServer network;
    private PILogger log;
    private ServerDatabase database;

    public NetServer getNetwork() {
	return network;
    }

    public ServerDatabase getDatabase() {
	return database;
    }

    public PILogger getLog() {
	return log;
    }

    public Server() {
	try {
	    JFrame f = new JFrame("Server");
	    f.addWindowListener(new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
		    dispose();
		}
	    });
	    f.setSize(500, 500);
	    f.setLocation(250, 50);
	    f.setVisible(true);
	    final JTextPane tPane = new JTextPane();
	    tPane.setSize(500, 450);
	    tPane.setLocation(0, 0);
	    f.add(tPane);
	    log = new PILogger(new PrintStream(new File(
		    "/home/westin/Desktop/log")) {
		@Override
		public void println(String s) {
		    tPane.setText(tPane.getText() + s + "\n");
		}
	    });
	    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    int port = Integer.valueOf(9999);
	    database = new ServerDatabase(this);
	    network = new NetServer(this, port, null);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void dispose() {
	network.dispose();
	database.save();
    }

    public static void main(String[] args) {
	new Server();
    }
}
