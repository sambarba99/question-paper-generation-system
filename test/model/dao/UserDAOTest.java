package model.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import model.builders.UserBuilder;
import model.persisted.User;

import view.enums.UserPrivilege;

public class UserDAOTest {

	private static final String USERNAME1 = "alice";

	private static final String USERNAME2 = "bob";

	private static final String USERNAME3 = "charlie";

	private static final String USERNAME_NON_EXISTENT = "xyz";

	private UserDAO userDao = UserDAO.getInstance();

	// reset user file before each test
	@Before
	public void resetFile() {
		List<String> allUsernames = userDao.getAllUsers().stream()
			.map(User::getUsername)
			.collect(Collectors.toList());

		allUsernames.forEach(userDao::deleteUserByUsername);
	}

	@Test
	public void testAddUser() {
		userDao.addUser(makeNewUser(USERNAME1));

		assertTrue(userWithUsernameExists(USERNAME1));
	}

	@Test
	public void testDeleteUserByUsername_username_exists() {
		// ensure user XML file exists
		userDao.addUser(makeNewUser(USERNAME1));

		userDao.deleteUserByUsername(USERNAME1);

		assertFalse(userWithUsernameExists(USERNAME1));
	}

	@Test
	public void testDeleteUserByUsername_username_not_exists() {
		// ensure user XML file exists
		userDao.addUser(makeNewUser(USERNAME1));

		userDao.deleteUserByUsername(USERNAME_NON_EXISTENT);

		assertTrue(userWithUsernameExists(USERNAME1)
			&& !userWithUsernameExists(USERNAME_NON_EXISTENT));
	}

	@Test
	public void testGetAllUsers() {
		userDao.addUser(makeNewUser(USERNAME1));
		userDao.addUser(makeNewUser(USERNAME2));
		userDao.addUser(makeNewUser(USERNAME3));

		assertEquals(userDao.getAllUsers().size(), 3);
	}

	private User makeNewUser(String username) {
		return new UserBuilder()
			.withUsername(username)
			.withPassword("unencrypted")
			.withPrivilege(UserPrivilege.TUTOR)
			.withDateCreated(LocalDateTime.now())
			.build();
	}

	private boolean userWithUsernameExists(String username) {
		return userDao.getAllUsers().stream()
			.filter(u -> u.getUsername().equals(username))
			.findFirst()
			.isPresent();
	}
}
