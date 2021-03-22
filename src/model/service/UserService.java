package model.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javafx.scene.control.ChoiceBox;

import model.builders.UserBuilder;
import model.dao.UserDAO;
import model.persisted.User;

import view.SystemNotification;
import view.enums.SystemNotificationType;
import view.enums.UserType;
import view.utils.Constants;
import view.utils.SecurityUtils;

/**
 * This class is a singleton, the use of which is to perform any functionality regarding users.
 *
 * @author Sam Barba
 */
public class UserService {

	private static UserService instance;

	private UserDAO userDao = UserDAO.getInstance();

	/**
	 * Add a user to the users CSV file.
	 * 
	 * @param user - the user to add
	 */
	public void addUser(User user) {
		userDao.addUser(user);
	}

	/**
	 * Update a user password by deleting user and re-adding with new password.
	 * 
	 * @param user - user to update password
	 * @param pass - the new password
	 */
	public void updatePassword(User user, String pass) {
		userDao.updatePassword(user, pass);
	}

	/**
	 * Delete a user by their unique username.
	 * 
	 * @param username - the username of the user to delete
	 */
	public void deleteUserByUsername(String username) {
		userDao.deleteUserByUsername(username);
	}

	/**
	 * Retrieve all users from users CSV file.
	 * 
	 * @return list of all users
	 */
	public List<User> getAllUsers() {
		return userDao.getAllUsers();
	}

	/**
	 * Retrieve user by their unique username.
	 * 
	 * @param username - their unique username
	 * @return user with specified username
	 */
	public User getUserByUsername(String username) {
		return userDao.getAllUsers()
			.stream()
			.filter(user -> user.getUsername().equals(username))
			.findFirst()
			.orElse(null);
	}

	/**
	 * Check whether or not a user exists.
	 * 
	 * @param checkUser - the user to check
	 * @return the user if found (if not, returns null)
	 */
	public User checkUserExists(User checkUser)
		throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		for (User user : userDao.getAllUsers()) {
			if (user.getUsername().equals(checkUser.getUsername())
				&& user.getPassword().equals(SecurityUtils.getInstance().sha512(checkUser.getPassword()))) {
				return user;
			}
		}
		return null;
	}

	/**
	 * Check if a username already exists, to be used when an admin adds a new user.
	 * 
	 * @param username - the username to check
	 * @return whether or not it exists
	 */
	public boolean checkUsernameAlreadyExists(String username)
		throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		File csvFile = new File(Constants.USERS_FILE_PATH);
		Scanner input = new Scanner(csvFile);

		while (input.hasNextLine()) {
			String line = input.nextLine();
			String[] lineSplit = line.split(Constants.COMMA);
			String usernameRead = lineSplit[0];
			if (usernameRead.equals(username)) {
				input.close();
				return true;
			}
		}
		input.close();
		return false;
	}

	/**
	 * Retrieve the validated user once they attempt login.
	 * 
	 * @param username - the entered username
	 * @param pass     - the entered password
	 * @return the validated user, with hashed password
	 */
	public User login(String username, String pass)
		throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {

		if (username.length() != 0 && pass.length() != 0) {
			if (usersFileExists()) {
				// no need to pass the UserType here, as it is retrieved with checkUserExists(user).
				User user = new UserBuilder().withUsername(username).withPassword(pass).build();
				User validatedUser = checkUserExists(user);
				if (validatedUser == null) {
					SystemNotification.display(SystemNotificationType.ERROR, "Invalid username or password.");
				} else {
					return validatedUser;
				}
			} else {
				/*
				 * If we are here, the users file doesn't exist, so this user is the first one - so make them an admin.
				 */
				User user = new UserBuilder().withUsername(username)
					.withPassword(pass)
					.withType(UserType.ADMIN)
					.build();

				if (validateFirstTimeLogin(username, pass)) {
					user.encryptPassword(); // apply SHA-512 before adding
					userDao.addUser(user);
					return user;
				}
			}
		} else {
			SystemNotification.display(SystemNotificationType.ERROR, "Please enter credentials.");
		}
		return null;
	}

	/**
	 * Validate the first user's login.
	 * 
	 * @param username - the user's new username
	 * @param pass     - the user's new password
	 * @return whether or not credentials are valid
	 */
	public boolean validateFirstTimeLogin(String username, String pass)
		throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		if (username.length() == 0) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please enter a username.");
			return false;
		}
		if (!username.matches(Constants.USERNAME_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Username must be letters only, and optionally end with digits.");
			return false;
		}
		if (!pass.matches(Constants.PASS_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Password must contain 0-9, a-z, A-Z, and be at least 8 long.");
			return false;
		}
		return true;
	}

	/**
	 * Validate a password update.
	 * 
	 * @param currentUser   - the user performing this action
	 * @param currentPass   - the user's current password
	 * @param newPass       - the user's new password
	 * @param repeatNewPass - the user's repeated new password
	 * @return whether or not the new passwords are valid
	 */
	public boolean validateResetPassword(User currentUser, String currentPass, String newPass, String repeatNewPass)
		throws NoSuchAlgorithmException, UnsupportedEncodingException {
		if (currentPass.length() == 0) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please enter current password.");
			return false;
		}
		if (!SecurityUtils.getInstance().sha512(currentPass).equals(currentUser.getPassword())) {
			SystemNotification.display(SystemNotificationType.ERROR, "Current password incorrect.");
			return false;
		}
		if (!newPass.matches(Constants.PASS_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Password must contain 0-9, a-z, A-Z, and be at least 8 long.");
			return false;
		}
		if (!newPass.equals(repeatNewPass)) {
			SystemNotification.display(SystemNotificationType.ERROR, "Those passwords don't match.");
			return false;
		}
		if (currentPass.equals(newPass)) {
			SystemNotification.display(SystemNotificationType.ERROR, "New password must be different to current.");
			return false;
		}
		return true;
	}

	/**
	 * Validate the addition of a new user by an admin.
	 * 
	 * @param username - the username of the new user
	 * @param pass     - the temporary password of the new user
	 * @return whether or not the new credentials are valid
	 */
	public boolean validateAddNewUserCreds(String username, String pass)
		throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		if (username.length() == 0) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please enter a username.");
			return false;
		}
		if (checkUsernameAlreadyExists(username)) {
			SystemNotification.display(SystemNotificationType.ERROR, "That username already exists.");
			return false;
		}
		if (!username.matches(Constants.USERNAME_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Username must be letters only, and optionally end with digits.");
			return false;
		}
		if (!pass.matches(Constants.PASS_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Password must contain 0-9, a-z, A-Z, and be at least 8 long.");
			return false;
		}
		return true;
	}

	/**
	 * Check if the user CSV file exists
	 * 
	 * @return whether or not the file exists
	 */
	public boolean usersFileExists() {
		File csvFile = new File(Constants.USERS_FILE_PATH);
		return csvFile.exists();
	}

	/**
	 * Get a list of all usernames and user types, for users ListView objects.
	 * 
	 * @return list of all usernames and types
	 */
	public List<String> getUserListViewItems() {
		return getAllUsers().stream().map(User::toString).collect(Collectors.toList());
	}

	/**
	 * Get a list of user types, for user types ChoiceBox objects.
	 * 
	 * @return list of all user types
	 */
	public List<String> getUserTypeChoiceBoxItems() {
		List<String> choiceBoxItems = Arrays.asList(UserType.values())
			.stream()
			.map(UserType::toString)
			.collect(Collectors.toList());
		return choiceBoxItems;
	}

	/**
	 * Get enum value of selected user type in a ChoiceBox.
	 * 
	 * @param choiceUserType - the ChoiceBox of user types
	 * @return enum of selected user type
	 */
	public UserType getSelectedUserType(ChoiceBox choiceUserType) {
		String userTypeSelected = choiceUserType.getSelectionModel().getSelectedItem().toString();
		return UserType.getFromStr(userTypeSelected);
	}

	public synchronized static UserService getInstance() {
		if (instance == null) {
			instance = new UserService(UserDAO.getInstance());
		}
		return instance;
	}

	private UserService(UserDAO userDao) {
		if (userDao == null) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + "User DAO cannot be null!");
			throw new IllegalArgumentException("User DAO cannot be null!");
		}
		this.userDao = userDao;
	}
}
