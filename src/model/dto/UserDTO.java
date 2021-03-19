package model.dto;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import model.persisted.User;
import model.service.UserService;

/**
 * This class is a singleton which contains methods related to ListViews used to modify other users (admin privilege).
 *
 * @author Sam Barba
 */
public class UserDTO {

	public static final Logger LOGGER = Logger.getLogger(UserDTO.class.getName());

	private static UserDTO instance;

	/**
	 * Get a list of all usernames and user types, for users ListView objects.
	 * 
	 * @return list of all usernames and types
	 */
	public List<String> getUserListViewItems() {
		LOGGER.info("Retrieving list of users for ListView");
		List<User> allUsers = UserService.getInstance().getAllUsers();
		List<String> listViewItems = allUsers.stream()
			.map(user -> (user.getUsername() + " (" + user.getType().toString() + ")"))
			.collect(Collectors.toList());
		return listViewItems;
	}

	public synchronized static UserDTO getInstance() {
		if (instance == null) {
			instance = new UserDTO();
		}
		return instance;
	}

	private UserDTO() {
	}
}
