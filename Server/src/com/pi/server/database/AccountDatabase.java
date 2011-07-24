package com.pi.server.database;

import java.io.*;
import java.util.ArrayList;

import com.pi.common.contants.GlobalConstants;
import com.pi.common.database.Account;

public class AccountDatabase {
    private AccountsList list;

    private static class AccountsList extends ArrayList<Account> {
	private static final long serialVersionUID = GlobalConstants.serialVersionUID;
    }

    public AccountDatabase() throws IOException, ClassNotFoundException {
	try {
	    ObjectInputStream in = new ObjectInputStream(new FileInputStream(
		    Paths.getAccountsDatabase()));
	    list = (AccountsList) in.readObject();
	    in.close();
	} catch (EOFException e) {
	}
	if (list == null)
	    list = new AccountsList();
	for (Account acc : list) {
	    System.out.println(acc.getUsername() + " -- "
		    + acc.getPasswordHash());
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
	ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
		Paths.getAccountsDatabase()));
	out.writeObject(list);
	out.close();
    }
}
