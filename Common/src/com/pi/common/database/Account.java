package com.pi.common.database;

import java.io.IOException;

import com.pi.common.PICryptUtils;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Account implements DatabaseObject {
    private String username;
    private String passwordHash;
    private int entityDef;

    public void setUsername(String username) {
	this.username = username;
    }

    public void setPassword(String password) {
	this.passwordHash = PICryptUtils.crypt(password);
    }

    public void setPasswordHash(String passwordHash) {
	this.passwordHash = passwordHash;
    }

    public void setEntityDef(int def) {
	this.entityDef = def;
    }

    public int getEntityDef() {
	return this.entityDef;
    }

    public String getUsername() {
	return username;
    }

    public String getPasswordHash() {
	return passwordHash;
    }

    @Override
    public void write(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(entityDef);
	pOut.writeString(username);
	pOut.writeString(passwordHash);
    }

    @Override
    public void read(PacketInputStream pIn) throws IOException {
	entityDef = pIn.readInt();
	username = pIn.readString();
	passwordHash = pIn.readString();
    }

    @Override
    public int getLength() {
	return 12 + passwordHash.length() + username.length();
    }
}
