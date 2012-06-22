package com.pi.server.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.pi.common.database.Account;
import com.pi.common.database.io.DatabaseIO;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

public class AccountDatabase implements PacketObject {
	private ArrayList<Account> list;

	public AccountDatabase() throws IOException {
		list = new ArrayList<Account>();
		if (Paths.getAccountsDatabase().exists())
			readData(new PacketInputStream(
					DatabaseIO.readByteBuffer(new FileInputStream(Paths
							.getAccountsDatabase()))));
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

	@Override
	public void writeData(PacketOutputStream pOut) throws IOException {
		pOut.writeInt(list.size());
		for (Account a : list)
			a.writeData(pOut);
	}

	@Override
	public int getLength() {
		int len = 4;
		for (Account a : list)
			len += a.getLength();
		return len;
	}

	@Override
	public void readData(PacketInputStream pIn) throws IOException {
		list.clear();
		if (pIn.getByteBuffer().remaining() >= 4) {
			int size = pIn.readInt();
			System.out.println(size);
			list.ensureCapacity(size);
			for (int i = 0; i < size; i++) {
				Account a = new Account();
				a.readData(pIn);
				list.add(a);
			}
		}
	}
}
