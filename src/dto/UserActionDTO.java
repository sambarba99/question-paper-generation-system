package dto;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.ListView;

import model.enums.UserAction;

/**
 * This class is a singleton which contains methods relating to ListViews of user actions.
 *
 * @author Sam Barba
 */
public class UserActionDTO {

	private static UserActionDTO instance;

	/**
	 * Get a list of all user actions for user action ListView objects.
	 * 
	 * @return list of all user actions
	 */
	public List<String> getUserActionListViewItems() {
		List<UserAction> allActions = new ArrayList<>(EnumSet.allOf(UserAction.class));
		allActions.remove(allActions.size() - 1); // remove NONE action
		List<String> listViewItems = allActions.stream().map(UserAction::getStrVal).collect(Collectors.toList());
		return listViewItems;
	}

	/**
	 * Get enum value of selected action in action ListView. The index of the selected item corresponds to the user
	 * action enum's int value.
	 * 
	 * @param listViewUserActions - the ListView of user actions
	 * @return enum of selected user action
	 */
	public UserAction getSelectedUserAction(ListView<String> listViewUserActions) {
		int userActionIntValSelected = listViewUserActions.getSelectionModel().getSelectedIndex();
		return UserAction.getFromInt(userActionIntValSelected);
	}

	public synchronized static UserActionDTO getInstance() {
		if (instance == null) {
			instance = new UserActionDTO();
		}
		return instance;
	}
}
