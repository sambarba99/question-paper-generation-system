package model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import model.builders.UserBuilder;
import model.persisted.User;

import view.SystemNotification;
import view.enums.SystemNotificationType;
import view.enums.UserPrivilege;
import view.utils.Constants;

/**
 * This class is a singleton, the use of which is any database operation regarding users.
 *
 * @author Sam Barba
 */
public class UserDAO {

	public static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

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

			FileWriter csvWriter = new FileWriter(csvFile, true); // append = true
			addUserDataToFile(user, csvWriter, true);
			csvWriter.flush();
			csvWriter.close();
			LOGGER.info("User '" + user.getUsername() + "' added");
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
		addUser(user);
		LOGGER.info("Password of user '" + user.getUsername() + "' updated");
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
			FileWriter csvWriter = new FileWriter(csvFile, false); // append = false

			for (User user : allUsers) {
				if (!user.getUsername().equals(username)) {
					addUserDataToFile(user, csvWriter, false);
				}
			}
			csvWriter.flush();
			csvWriter.close();
			LOGGER.info("User '" + username + "' deleted");
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
				UserPrivilege privilege = UserPrivilege.getFromStr(lineArr[2]);
				LocalDateTime dateCreated = LocalDateTime
					.parse(lineArr[3].replace(Constants.QUOT_MARK, Constants.EMPTY), Constants.DATE_FORMATTER);

				User user = new UserBuilder().withUsername(username)
					.withPassword(passHash)
					.withPrivilege(privilege)
					.withDateCreated(dateCreated)
					.build();

				users.add(user);
			}
			input.close();
			LOGGER.info("Retrieved all " + users.size() + " users");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
		return users;
	}

	/**
	 * Add user data to the users CSV file.
	 * 
	 * @param user      - the user to add
	 * @param csvWriter - the file writer
	 * @param append    - whether to append or write to the file
	 */
	private void addUserDataToFile(User user, FileWriter csvWriter, boolean append) throws IOException {
		/*
		 * 1 line contains: unique username (ID), encrypted password, privilege level, date created
		 */
		String line = Constants.QUOT_MARK + user.getUsername() + Constants.QUOT_MARK + Constants.COMMA
			+ Constants.QUOT_MARK + user.getPassword() + Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK
			+ user.getPrivilege().toString() + Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK
			+ Constants.DATE_FORMATTER.format(user.getDateCreated()) + Constants.QUOT_MARK + Constants.NEWLINE;

		if (append) {
			csvWriter.append(line);
		} else { // write
			csvWriter.write(line);
		}
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
