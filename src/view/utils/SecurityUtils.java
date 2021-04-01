package view.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is a singleton, the use of which is to perform SHA-512 encryption to use when adding new users and
 * validating login credentials.
 *
 * @author Sam Barba
 */
public class SecurityUtils {

	private static SecurityUtils instance;

	private SecurityUtils() {
	}

	public synchronized static SecurityUtils getInstance() {
		if (instance == null) {
			instance = new SecurityUtils();
		}
		return instance;
	}

	/**
	 * Encrypt a string using SHA-512.
	 * 
	 * @param text - the string to encrypt
	 * @return the encrypted text
	 */
	public String sha512(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		byte[] sha512hash = md.digest();
		return convertToHex(sha512hash);
	}

	/**
	 * Convert an array of bytes to hexadecimal.
	 * 
	 * @param data - the byte array to convert
	 * @return the string hexadecimal result
	 */
	private String convertToHex(byte[] data) {
		StringBuilder result = new StringBuilder();
		for (Byte b : data) {
			result.append(Integer.toString((b & 255) + 256, 16).substring(1));
		}
		return result.toString();
	}
}
