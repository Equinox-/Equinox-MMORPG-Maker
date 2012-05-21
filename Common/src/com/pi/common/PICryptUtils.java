package com.pi.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PICryptUtils {
	public static String crypt(String to) {
		try {
			return sha1(to);
		} catch (NoSuchAlgorithmException e) {
			return to;
		}
	}

	public static String toHexString(byte[] data) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			String hex = Integer.toHexString(0xFF & data[i]);
			if (hex.length() == 1)
				hexString.append('0');

			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static String md5(String to) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.reset();
		md5.update(to.getBytes());
		return toHexString(md5.digest());
	}

	public static String sha1(String to) throws NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		sha1.reset();
		sha1.update(to.getBytes());
		return toHexString(sha1.digest());
	}
}
