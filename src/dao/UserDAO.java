package dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.User;
import model.enums.SystemMessageType;
import model.enums.UserType;

import tools.Constants;
import tools.SecurityTools;

import interfaceviews.SystemMessageView;

public class UserDAO {

	public UserDAO() {
	}

	/**
	 * Adds a user to the user CSV file
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
			String passHash = SecurityTools.sha512(user.getPassword());

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

	/**
	 * Retrieve user by their unique username
	 * 
	 * @param username - their unique username
	 * @return user with specified username
	 */
	public User getUserByUsername(String username) {
		for (User u : getAllUsers()) {
			if (u.getUsername().equals(username)) {
				return u;
			}
		}
		return null;
	}
}