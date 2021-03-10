package model.dto;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.ChoiceBox;

import view.enums.UserType;

/**
 * This class is a singleton which contains methods related to ChoiceBox objects containing user types.
 *
 * @author Sam Barba
 */
public class UserTypeDTO {

	private static UserTypeDTO instance;

	/**
	 * Get a list of user types, for user types ChoiceBox objects.
	 * 
	 * @return list of all user types
	 */
	public List<String> getUserTypeChoiceBoxItems() {
		List<UserType> allUserTypes = new ArrayList<>(EnumSet.allOf(UserType.class));
		List<String> choiceBoxItems = allUserTypes.stream().map(UserType::toString).collect(Collectors.toList());
		return choiceBoxItems;
	}

	/**
	 * Get enum value of selected user type in a ChoiceBox. The index of the selected item corresponds to the user
	 * type's int value.
	 * 
	 * @param cbUserType - the ChoiceBox of user types
	 * @return enum of selected user type
	 */
	public UserType getSelectedUserType(ChoiceBox cbUserType) {
		String userTypeSelected = cbUserType.getSelectionModel().getSelectedItem().toString();
		return UserType.getFromStr(userTypeSelected);
	}

	public synchronized static UserTypeDTO getInstance() {
		if (instance == null) {
			instance = new UserTypeDTO();
		}
		return instance;
	}

	private UserTypeDTO() {
	}
}
