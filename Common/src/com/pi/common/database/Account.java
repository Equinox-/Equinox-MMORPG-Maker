package com.pi.common.database;

import java.io.Serializable;

import com.pi.common.PICryptUtils;
import com.pi.common.contants.GlobalConstants;

public class Account implements Serializable {
    private static final long serialVersionUID = GlobalConstants.serialVersionUID;
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
}
