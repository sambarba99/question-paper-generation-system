package model.service;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import model.builders.UserBuilder;
import model.dao.UserDAO;
import model.dto.UserDTO;
import model.persisted.User;

import view.enums.SystemNotificationType;
import view.enums.UserPrivilege;
import view.utils.Constants;
import view.utils.SecurityUtils;

import controller.SystemNotification;

/**
 * This class is a singleton, the use of which is to perform any functionality regarding users.
 *
 * @author Sam Barba
 */
public class UserService {

	private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

	private static UserService instance;

	private UserDAO userDao;

	private SecurityUtils securityUtils;

	private UserService(UserDAO userDao, SecurityUtils securityUtils) {
		assertNotNull(userDao);
		assertNotNull(securityUtils);

		this.userDao = userDao;
		this.securityUtils = securityUtils;
	}

	public synchronized static UserService getInstance() {
		if (instance == null) {
			instance = new UserService(UserDAO.getInstance(), SecurityUtils.getInstance());
		}
		return instance;
	}

	/**
	 * Encrypt a user's password before adding them to the users XML file.
	 * 
	 * @param user - the user to add
	 */
	public void addUser(User user) {
		encryptPassword(user);
		user.setDateCreated(LocalDateTime.now());
		userDao.addUser(user);
	}

	/**
	 * Update a user password by deleting user and re-adding with new password.
	 * 
	 * @param user    - user to update password
	 * @param newPass - the new password
	 */
	public void updatePassword(User user, String newPass) {
		deleteUserByUsername(user.getUsername());
		user.setPassword(newPass);
		addUser(user);
		LOGGER.info("Password of user '" + user.getUsername() + "' updated");
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
	 * Retrieve all users from users XML file.
	 * 
	 * @return list of all users
	 */
	public List<User> getAllUsers() {
		return userDao.getAllUsers();
	}

	/**
	 * Get all users converted to DTOs for using in TableViews.
	 * 
	 * @return list of all users as DTOs
	 */
	public List<UserDTO> getAllUserDTOs() {
		return getAllUsers().stream()
			.map(this::convertToUserDTO)
			.collect(Collectors.toList());
	}

	/**
	 * Convert a user to its DTO equivalent.
	 * 
	 * @param user - the user to convert
	 * @return the equivalent UserDTO
	 */
	private UserDTO convertToUserDTO(User user) {
		UserDTO userDto = new UserDTO();
		userDto.setUsername(user.getUsername());
		userDto.setPrivilege(user.getPrivilege().toString());
		userDto.setDateCreated(Constants.DATE_FORMATTER.format(user.getDateCreated()));

		return userDto;
	}

	private void encryptPassword(User user) {
		try {
			user.setPassword(securityUtils.sha512(user.getPassword()));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
		}
	}

	/**
	 * Check whether or not a user exists.
	 * 
	 * @param username - the username to check against
	 * @return the optional user
	 */
	public Optional<User> checkUserExists(String username) {
		return getAllUsers().stream()
			.filter(u -> u.getUsername().equals(username))
			.findFirst();
	}

	/**
	 * Retrieve the validated user once they attempt login.
	 * 
	 * @param username - the entered username
	 * @param pass     - the entered password
	 * @return the validated user, with hashed password
	 */
	public Optional<User> login(String username, String pass) throws FileNotFoundException,
		NoSuchAlgorithmException, UnsupportedEncodingException {

		if (usersFileExists()) {
			Optional<User> validatedUser = checkUserExists(username);

			if (validatedUser.isPresent()
				&& validatedUser.get().getPassword().equals(securityUtils.sha512(pass))) {
				return validatedUser;
			}
		} else {
			/*
			 * If here, it means the users file doesn't exist, so this user is the first one - so
			 * make them an admin.
			 */
			User user = new UserBuilder()
				.withUsername(username)
				.withPassword(pass)
				.withPrivilege(UserPrivilege.ADMIN)
				.build();

			if (validateFirstTimeLogin(username, pass)) {
				addUser(user);
				return Optional.of(user);
			}
		}
		return Optional.empty();
	}

	/**
	 * Validate the first user's login.
	 * 
	 * @param username - the user's new username
	 * @param pass     - the user's new password
	 * @return whether or not credentials are valid
	 */
	public boolean validateFirstTimeLogin(String username, String pass) throws FileNotFoundException,
		NoSuchAlgorithmException, UnsupportedEncodingException {

		if (username.isEmpty()) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please enter username.");
			return false;
		}
		if (pass.isEmpty()) {
			SystemNotification.display(SystemNotificationType.ERROR, "Please enter password.");
			return false;
		}
		if (!username.matches(Constants.USERNAME_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Username must be letters only, and optionally end with digits.");
			return false;
		}
		if (!pass.matches(Constants.PASSWORD_REGEX)) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Password must contain 0-9, a-z, A-Z, and be at least 8 long.");
			return false;
		}
		return true;
	}

	/**
	 * Check if the XML file of users exists
	 * 
	 * @return whether or not the file exists
	 */
	public boolean usersFileExists() {
		File xmlFile = new File(Constants.USERS_FILE_PATH);
		return xmlFile.exists();
	}
}
