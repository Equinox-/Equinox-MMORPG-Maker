package com.pi.server.database;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.pi.common.database.Account;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class AccountDatabase {
    private ArrayList<Account> list;

    public AccountDatabase() throws IOException {

	list = new ArrayList<Account>();
	try {
	    PacketInputStream fIn = new PacketInputStream(new FileInputStream(
		    Paths.getAccountsDatabase()));
	    int num = fIn.readInt();
	    for (int i = 0; i < num; i++) {
		Account acc = new Account();
		acc.readData(fIn);
		list.add(acc);
	    }
	} catch (EOFException e) {
	    if (Paths.getAccountsDatabase().exists())
		Paths.getAccountsDatabase().delete();
	}
    }

    public Account getAccount(String username) {
	for (Account acc : list) {
	    if (acc.getUsername().equalsIgnoreCase(username))
		return acc;
	}
	return null;
    }

    public boolean addAccount(String username, String passwordHash) {
	if (getAccount(username) != null)
	    return false;
	Account acc = new Account();
	acc.setPasswordHash(passwordHash);
	acc.setUsername(username);
	list.add(acc);
	return true;
    }

    public void writeData() throws IOException {
	PacketOutputStream fOut = new PacketOutputStream(new FileOutputStream(
		Paths.getAccountsDatabase()));
	fOut.writeInt(list.size());
	for (int i = 0; i < list.size(); i++)
	    list.get(i).writeData(fOut);
	fOut.close();
    }
}
