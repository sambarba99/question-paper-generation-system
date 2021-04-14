package model.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import model.builders.UserBuilder;
import model.dto.UserDTO;
import model.persisted.User;

import view.enums.UserPrivilege;

public class UserServiceTest {

	private static final String USERNAME = "alice";

	private static final String USERNAME_NON_EXISTENT = "xyz";

	private static final String PASSWORD = "aaaaaaA1";

	private static final String PASSWORD_SHA_512 = "94f6afd1872c459cdd9b0d8780c6b0f7ae911ad9cb35e06d58883bede079ba409984402b7141fb3b4992bd7c1645c4991047ef051e4eae145765719c2d13ccaa";

	private static final String NEW_PASSWORD = "bbbbbbB2";

	private static final String NEW_PASSWORD_SHA_512 = "f4d3fe57d6b4a85a23e4a3028fd0477aa48fa518408cd0a641f6ddd3f4fa831dac6d33612a49d6f96f1eed704c21157a0d7aaa131233ee7bd7081dcd9b1f366d";

	private static final String INCORRECT_PASSWORD = "incorrect";

	private UserService userService = UserService.getInstance();

	// reset user file before each test
	@Before
	public void resetFile() {
		List<String> allUsernames = userService.getAllUsers().stream()
			.map(User::getUsername)
			.collect(Collectors.toList());

		allUsernames.forEach(userService::deleteUserByUsername);
	}

	@Test
	public void testEncryptPassword() {
		// adding a new user encrypts their password
		User user = makeNewUser();
		userService.addUser(user);

		assertEquals(user.getPassword(), PASSWORD_SHA_512);
	}

	@Test
	public void testUpdatePassword() {
		User user = makeNewUser();
		userService.addUser(user);

		userService.updatePassword(user, NEW_PASSWORD);

		assertEquals(user.getPassword(), NEW_PASSWORD_SHA_512);
	}

	@Test
	public void testGetAllUserDTOs() {
		userService.addUser(makeNewUser());

		List<UserDTO> userDtos = userService.getAllUserDTOs();

		boolean correctListSize = userDtos.size() == 1;
		boolean correctUsername = userDtos.get(0).getUsername().equals(USERNAME);

		assertTrue(correctListSize && correctUsername);
	}

	@Test
	public void testCheckUserExists_username_exists() {
		userService.addUser(makeNewUser());

		Optional<User> user = userService.checkUserExists(USERNAME);

		assertTrue(user.isPresent());
	}

	@Test
	public void testCheckUserExists_username_not_exists() {
		userService.addUser(makeNewUser());

		Optional<User> user = userService.checkUserExists(USERNAME_NON_EXISTENT);

		assertFalse(user.isPresent());
	}

	@Test
	public void loginTest_valid_credentials() {
		userService.addUser(makeNewUser());

		Optional<User> user = null;
		try {
			user = userService.login(USERNAME, PASSWORD);
		} catch (FileNotFoundException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		assertTrue(user.isPresent());
	}

	@Test
	public void loginTest_invalid_credentials() {
		userService.addUser(makeNewUser());

		Optional<User> user = null;
		try {
			user = userService.login(USERNAME, INCORRECT_PASSWORD);
		} catch (FileNotFoundException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		assertFalse(user.isPresent());
	}

	private User makeNewUser() {
		return new UserBuilder()
			.withUsername(USERNAME)
			.withPassword(PASSWORD)
			.withPrivilege(UserPrivilege.TUTOR)
			.withDateCreated(LocalDateTime.now())
			.build();
	}
}
