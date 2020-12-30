package dto;

import java.util.List;
import java.util.stream.Collectors;

import service.UserService;

import model.User;

public class UserDTO {

	private static UserDTO instance;

	/**
	 * Get a list of all usernames and types for user ListView
	 * 
	 * @return list of all usernames and types
	 */
	public List<String> getUserListViewItems() {
		List<User> allUsers = UserService.getInstance().getAllUsers();
		List<String> listViewItems = allUsers.stream().map(u -> (u.getUsername() + " (" + u.getType().toString() + ")"))
				.collect(Collectors.toList());
		return listViewItems;
	}

	public synchronized static UserDTO getInstance() {
		if (instance == null) {
			instance = new UserDTO();
		}
		return instance;
	}
}
