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

import model.User;
import model.enums.SystemMessageType;
import model.enums.UserType;

import interfaceviews.SystemMessageView;

import utils.Constants;

public class UserDAO {

	private static UserDAO instance;

	/**
	 * Add a user to the user CSV file
	 * 
	 * @param user - the user to add
	 */
	public void addUser(User user) {
		try {
			File csvFile = new File(Constants.USER_FILE_PATH);
			if (!csvFile.exists()) {
				csvFile.getParentFile().mkdirs();
				csvFile.createNewFile();
			}

			String username = user.getUsername();
			String passHash = sha512(user.getPassword());

			FileWriter csvWriter = new FileWriter(csvFile, true); // append = true
			csvWriter.append(username + "," + passHash + "," + user.getType().toString() + "\n");
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException | NoSuchAlgorithmException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
	}

	/**
	 * Updates user password by deleting user and re-adding with new password
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
	 * Delete a user by username (as usernames are unique)
	 * 
	 * @param username - the username of the user to delete
	 */
	public void deleteUserByUsername(String username) {
		try {
			List<User> allUsers = getAllUsers();
			File csvFile = new File(Constants.USER_FILE_PATH);
			FileWriter csvWriter = new FileWriter(csvFile, false);

			for (User u : allUsers) {
				if (!u.getUsername().equals(username)) {
					String name = u.getUsername();
					String passHash = u.getPassword();
					String userType = u.getType().toString();
					csvWriter.write(name + "," + passHash + "," + userType + "\n");
				}
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve all users from CSV file
	 * 
	 * @return list of all users
	 */
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<>();

		try {
			File csvFile = new File(Constants.USER_FILE_PATH);
			Scanner input = new Scanner(csvFile);

			while (input.hasNextLine()) {
				String line = input.nextLine();
				String[] lineSplit = line.split(",");
				String username = lineSplit[0];
				String passHash = lineSplit[1];
				String userTypeStr = lineSplit[2];
				UserType userType = "ADMIN".equals(userTypeStr) ? UserType.ADMIN : UserType.TUTOR;
				User user = new User(username, passHash, userType);
				users.add(user);
			}
			input.close();
		} catch (FileNotFoundException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
		return users;
	}

	public String sha512(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		byte[] sha512hash = md.digest();
		return convertToHex(sha512hash);
	}

	private String convertToHex(byte[] data) {
		String result = "";
		for (int i = 0; i < data.length; i++) {
			result += Integer.toString((data[i] & 255) + 256, 16).substring(1);
		}
		return result;
	}

	public synchronized static UserDAO getInstance() {
		if (instance == null) {
			instance = new UserDAO();
		}
		return instance;
	}
}