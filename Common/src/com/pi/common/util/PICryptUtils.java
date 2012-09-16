package com.pi.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A utility class to encrypt strings.
 * 
 * @author Westin
 * 
 */
public final class PICryptUtils {
	/**
	 * Overridden constructor to prevent instances of this class from being
	 * produced.
	 */
	private PICryptUtils() {
	}

	/**
	 * Encrypt the provided string using the default encryption method.
	 * 
	 * @param to the string to encrypt
	 * @return the encrypted string
	 */
	public static String crypt(final String to) {
		try {
			return sha1(to);
		} catch (NoSuchAlgorithmException e) {
			return to;
		}
	}

	/**
	 * Convert an array of bytes to a hexadecimal string.
	 * 
	 * @param data the data to convert to a string
	 * @return the hexadecimal string
	 */
	public static String toHexString(final byte[] data) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			String hex = Integer.toHexString(0xFF & data[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}

			hexString.append(hex);
		}
		return hexString.toString();
	}

	/**
	 * Encrypt a string using the MD5 encryption algorithm.
	 * 
	 * @param to The string to encrypt
	 * @return the encrypted string
	 * @throws NoSuchAlgorithmException if the MD5 algorithm was not found
	 */
	public static String md5(final String to)
			throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.reset();
		md5.update(to.getBytes());
		return toHexString(md5.digest());
	}

	/**
	 * Encrypt a string using the SHA1 encryption algorithm.
	 * 
	 * @param to The string to encrypt
	 * @return the encrypted string
	 * @throws NoSuchAlgorithmException if the SHA1 algorithm was not found
	 */
	public static String sha1(final String to)
			throws NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		sha1.reset();
		sha1.update(to.getBytes());
		return toHexString(sha1.digest());
	}
}
