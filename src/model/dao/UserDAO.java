package model.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import model.persisted.User;
import model.xml.XMLUserSerialiser;

import view.enums.SystemNotificationType;
import view.utils.Constants;

import controller.SystemNotification;

/**
 * This class is a singleton, the use of which is any database operation regarding users.
 *
 * @author Sam Barba
 */
public class UserDAO {

	private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

	private XMLUserSerialiser userSerialiser = XMLUserSerialiser.getInstance();

	private static UserDAO instance;

	private UserDAO() {
	}

	public synchronized static UserDAO getInstance() {
		if (instance == null) {
			instance = new UserDAO();
		}
		return instance;
	}

	/**
	 * Add a user to the users XML file.
	 * 
	 * @param user - the user to add
	 */
	public void addUser(User user) {
		try {
			File xmlFile = new File(Constants.USERS_FILE_PATH);
			List<User> allUsers = getAllUsers();
			if (!xmlFile.exists()) {
				xmlFile.getParentFile().mkdirs();
				xmlFile.createNewFile();
			}

			allUsers.add(user);
			userSerialiser.write(allUsers);
			LOGGER.info("User with name '" + user.getUsername() + "' added");
		} catch (Exception e) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
		}
	}

	/**
	 * Delete a user by their unique username.
	 * 
	 * @param username - the username of the user to delete
	 */
	public void deleteUserByUsername(String username) {
		try {
			List<User> allUsers = getAllUsers();
			List<User> writeUsers = allUsers.stream()
				.filter(u -> !u.getUsername().equals(username))
				.collect(Collectors.toList());

			userSerialiser.write(writeUsers);

			LOGGER.info("User with name '" + username + "' deleted");
		} catch (Exception e) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
		}
	}

	/**
	 * Retrieve all users.
	 * 
	 * @return list of all users
	 */
	public List<User> getAllUsers() {
		List<User> allUsers = new ArrayList<>();

		File xmlFile = new File(Constants.USERS_FILE_PATH);
		if (xmlFile.exists()) {
			try {
				allUsers = (List<User>) userSerialiser.readAll();
			} catch (Exception e) {
				SystemNotification.display(SystemNotificationType.ERROR,
					Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
			}
		}

		return allUsers;
	}
}
