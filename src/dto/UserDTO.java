package dto;

import java.util.List;
import java.util.stream.Collectors;

import dao.UserDAO;

import model.User;

public class UserDTO {

	private UserDAO userDao = new UserDAO();

	public UserDTO() {
	}

	/**
	 * Get a list of all usernames and types for user ListView
	 * 
	 * @return list of all usernames and types
	 */
	public List<String> getUserListViewItems() {
		List<User> allUsers = userDao.getAllUsers();
		List<String> listViewItems = allUsers.stream().map(u -> (u.getUsername() + " (" + u.getType().toString() + ")"))
				.collect(Collectors.toList());
		return listViewItems;
	}
}
