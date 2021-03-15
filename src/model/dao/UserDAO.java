package model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.builders.UserBuilder;
import model.persisted.User;

import view.Constants;
import view.enums.SystemNotificationType;
import view.enums.UserType;

import controller.SystemNotification;

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
			String password = user.getPassword();

			FileWriter csvWriter = new FileWriter(csvFile, true); // append = true
			csvWriter.append(Constants.QUOT_MARK + username + Constants.QUOT_MARK + Constants.COMMA
				+ Constants.QUOT_MARK + password + Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK
				+ user.getType().toString() + Constants.QUOT_MARK + Constants.NEWLINE);
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
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
		user.encryptPassword();
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
					csvWriter.write(Constants.QUOT_MARK + user.getUsername() + Constants.QUOT_MARK + Constants.COMMA
						+ Constants.QUOT_MARK + user.getPassword() + Constants.QUOT_MARK + Constants.COMMA
						+ Constants.QUOT_MARK + user.getType().toString() + Constants.QUOT_MARK + Constants.NEWLINE);
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
				String[] lineArr = line.split(Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK);

				String username = lineArr[0].replace(Constants.QUOT_MARK, Constants.EMPTY);
				String passHash = lineArr[1];
				UserType userType = UserType.getFromStr(lineArr[2].replace(Constants.QUOT_MARK, Constants.EMPTY));

				User user = new UserBuilder().withUsername(username).withPassword(passHash).withType(userType).build();
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

	public synchronized static UserDAO getInstance() {
		if (instance == null) {
			instance = new UserDAO();
		}
		return instance;
	}

	private UserDAO() {
	}
}
