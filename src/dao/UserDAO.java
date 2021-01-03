package dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import interfacecontroller.SystemNotification;

import model.User;
import model.enums.SystemNotificationType;
import model.enums.UserType;

import utils.Constants;

/**
 * This class is a singleton, the use of which is any database operation regarding users.
 *
 * @author Sam Barba
 */
public class UserDAO {

	private static UserDAO instance;

	/**
	 * Add a user to the users CSV file.
	 * 
	 * @param user - the user to add
	 */
	public void addUser(User user) {
		try {
			File csvFile = new File(Constants.USERS_FILE_PATH);
			if (!csvFile.exists()) {
				csvFile.getParentFile().mkdirs();
				csvFile.createNewFile();
			}

			String username = user.getUsername();
			String passHash = sha512(user.getPassword());

			FileWriter csvWriter = new FileWriter(csvFile, true); // append = true
			csvWriter.append(username + Constants.COMMA);
			csvWriter.append(passHash + Constants.COMMA);
			csvWriter.append(user.getType().getIntVal() + Constants.NEWLINE);
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
					Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
	}

	/**
	 * Update a user password by deleting user and re-adding with new password.
	 * 
	 * @param user - user to update password
	 * @param pass - the new password
	 */
	public void updatePassword(User user, String pass) {
		user.setPassword(pass);
		deleteUserByUsername(user.getUsername());
		addUser(user);
	}

	/**
	 * Delete a user by their unique username.
	 * 
	 * @param username - the username of the user to delete
	 */
	public void deleteUserByUsername(String username) {
		try {
			List<User> allUsers = getAllUsers();
			File csvFile = new File(Constants.USERS_FILE_PATH);
			FileWriter csvWriter = new FileWriter(csvFile, false);

			for (User user : allUsers) {
				if (!user.getUsername().equals(username)) {
					csvWriter.write(user.getUsername() + Constants.COMMA);
					csvWriter.write(user.getPassword() + Constants.COMMA);
					csvWriter.write(user.getType().getIntVal() + Constants.NEWLINE);
				}
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
					Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
	}

	/**
	 * Retrieve all users from users CSV file.
	 * 
	 * @return list of all users
	 */
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<>();

		try {
			File csvFile = new File(Constants.USERS_FILE_PATH);
			Scanner input = new Scanner(csvFile);

			while (input.hasNextLine()) {
				String line = input.nextLine();
				String[] lineSplit = line.split(Constants.COMMA);
				String username = lineSplit[0];
				String passHash = lineSplit[1];
				UserType userType = UserType.getFromInt(Integer.parseInt(lineSplit[2]));
				User user = new User(username, passHash, userType);
				users.add(user);
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
					Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
		return users;
	}

	/**
	 * Encrypt a string with SHA-512.
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
		for (int i = 0; i < data.length; i++) {
			result.append(Integer.toString((data[i] & 255) + 256, 16).substring(1));
		}
		return result.toString();
	}

	public synchronized static UserDAO getInstance() {
		if (instance == null) {
			instance = new UserDAO();
		}
		return instance;
	}
}
