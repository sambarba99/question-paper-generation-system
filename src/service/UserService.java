package service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;

import dao.UserDAO;

import model.User;
import model.enums.SystemMessageType;

import interfaceviews.SystemMessageView;

import utils.Constants;

public class UserService {

	private static UserService instance;

	private UserDAO userDao = UserDAO.getInstance();

	/**
	 * Add a user to the user CSV file
	 * 
	 * @param user - the user to add
	 */
	public void addUser(User user) {
		userDao.addUser(user);
	}

	/**
	 * Updates user password by deleting user and re-adding with new password
	 * 
	 * @param user - user to update password
	 * @param pass - the new password
	 */
	public void updatePassword(User user, String pass) {
		userDao.updatePassword(user, pass);
	}

	/**
	 * Delete a user by username (as usernames are unique)
	 * 
	 * @param username - the username of the user to delete
	 */
	public void deleteUserByUsername(String username) {
		userDao.deleteUserByUsername(username);
	}

	/**
	 * Retrieve all users from CSV file
	 * 
	 * @return list of all users
	 */
	public List<User> getAllUsers() {
		return userDao.getAllUsers();
	}

	/**
	 * Retrieve user by their unique username
	 * 
	 * @param username - their unique username
	 * @return user with specified username
	 */
	public User getUserByUsername(String username) {
		return getAllUsers().stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
	}

	public User checkUserExists(User user)
			throws FileNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		for (User u : UserDAO.getInstance().getAllUsers()) {
			if (u.getUsername().equals(user.getUsername())
					&& u.getPassword().equals(userDao.sha512(user.getPassword()))) {
				return u;
			}
		}
		return null;
	}

	public boolean checkUsernameExists(String username)
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

	public boolean validateFirstTimeLogin(String username, String pass)
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

	public boolean validateResetPassword(User currentUser, String currentPass, String newPass, String repeatNewPass)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		if (currentPass.length() == 0) {
			SystemMessageView.display(SystemMessageType.ERROR, "Please enter current password.");
			return false;
		}
		if (!userDao.sha512(currentPass).equals(currentUser.getPassword())) {
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

	public boolean validateAddNewUserCreds(String username, String pass)
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

	public boolean usersFileExists() {
		File csvFile = new File(Constants.USER_FILE_PATH);
		return csvFile.exists();
	}

	private UserService(UserDAO userDao) {
		if (userDao == null) {
			throw new IllegalArgumentException("User DAO cannot be null");
		}
		this.userDao = userDao;
	}

	public synchronized static UserService getInstance() {
		if (instance == null) {
			instance = new UserService(UserDAO.getInstance());
		}
		return instance;
	}
}