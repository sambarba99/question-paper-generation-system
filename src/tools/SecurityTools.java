package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import dao.UserDAO;

import model.User;
import model.enums.SystemMessageType;

import interfaceviews.SystemMessageView;

public class SecurityTools {

	private static UserDAO userDao = new UserDAO();

	public static User checkUserExists(User user)
			throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		for (User u : userDao.getAllUsers()) {
			if (u.getUsername().equals(user.getUsername()) && u.getPassword().equals(sha512(user.getPassword()))) {
				return u;
			}
		}
		return null;
	}

	public static boolean checkUsernameExists(String username)
			throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		File csvFile = new File(Constants.USER_FILE_PATH);
		Scanner input = new Scanner(csvFile);

		while (input.hasNextLine()) {
			String line = input.nextLine();
			String[] lineSplit = line.split(",");
			String usernameRead = lineSplit[0];
			if (usernameRead.equals(username)) {
				input.close();
				return true;
			}
		}
		input.close();
		return false;
	}

	public static boolean validateFirstTimeLogin(String username, String pass)
			throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {

		if (username.length() == 0) {
			SystemMessageView.display(SystemMessageType.ERROR, "Please enter a username.");
			return false;
		}
		if (!username.matches(Constants.USERNAME_REGEX)) {
			SystemMessageView.display(SystemMessageType.ERROR,
					"Username must be letters only, and optionally end with digits.");
			return false;
		}
		if (!pass.matches(Constants.PASS_REGEX)) {
			SystemMessageView.display(SystemMessageType.ERROR,
					"Password must contain 0-9, a-z, A-Z, and be at least 8 long.");
			return false;
		}
		return true;
	}

	public static boolean validateResetPassword(User currentUser, String currentPass, String newPass,
			String repeatNewPass) throws NoSuchAlgorithmException, UnsupportedEncodingException {

		if (currentPass.length() == 0) {
			SystemMessageView.display(SystemMessageType.ERROR, "Please enter current password.");
			return false;
		}
		if (!sha512(currentPass).equals(currentUser.getPassword())) {
			SystemMessageView.display(SystemMessageType.ERROR, "Current password incorrect.");
			return false;
		}
		if (!newPass.matches(Constants.PASS_REGEX)) {
			SystemMessageView.display(SystemMessageType.ERROR,
					"Password must contain 0-9, a-z, A-Z, and be at least 8 long.");
			return false;
		}
		if (!newPass.equals(repeatNewPass)) {
			SystemMessageView.display(SystemMessageType.ERROR, "Those passwords don't match.");
			return false;
		}
		if (currentPass.equals(newPass)) {
			SystemMessageView.display(SystemMessageType.ERROR, "New password must be different to current.");
			return false;
		}
		return true;
	}

	public static boolean validateAddNewUserCreds(String username, String pass)
			throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {

		if (username.length() == 0) {
			SystemMessageView.display(SystemMessageType.ERROR, "Please enter a username.");
			return false;
		}
		if (checkUsernameExists(username)) {
			SystemMessageView.display(SystemMessageType.ERROR, "That username already exists.");
			return false;
		}
		if (!username.matches(Constants.USERNAME_REGEX)) {
			SystemMessageView.display(SystemMessageType.ERROR,
					"Username must be letters only, and optionally end with digits.");
			return false;
		}
		if (!pass.matches(Constants.PASS_REGEX)) {
			SystemMessageView.display(SystemMessageType.ERROR,
					"Password must contain 0-9, a-z, A-Z, and be at least 8 long.");
			return false;
		}
		return true;
	}

	public static boolean usersFileExists() {
		File csvFile = new File(Constants.USER_FILE_PATH);
		return csvFile.exists();
	}

	public static String sha512(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		byte[] sha512hash = md.digest();
		return convertToHex(sha512hash);
	}

	private static String convertToHex(byte[] data) {
		String result = "";
		for (int i = 0; i < data.length; i++) {
			result += Integer.toString((data[i] & 255) + 256, 16).substring(1);
		}
		return result;
	}
}
