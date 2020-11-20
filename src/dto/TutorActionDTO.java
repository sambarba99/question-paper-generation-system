package dto;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.ListView;

import model.enums.TutorAction;

public class TutorActionDTO {

	public TutorActionDTO() {
	}

	/**
	 * Get a list of all tutor actions for tutor action ListView
	 * 
	 * @return list of all tutor actions
	 */
	public List<String> getTutorActionListViewItems() {
		List<TutorAction> allActions = new ArrayList<>(EnumSet.allOf(TutorAction.class));
		allActions.remove(allActions.size() - 1); // remove NONE action
		List<String> listViewItems = allActions.stream().map(a -> a.getStrVal()).collect(Collectors.toList());
		return listViewItems;
	}

	/**
	 * Get enum value of selected action in action list view
	 * 
	 * @param listViewTutorActions - the ListView of tutor actions
	 * @return enum of selected tutor action
	 */
	public TutorAction getSelectedTutorAction(ListView<String> listViewTutorActions) {
		String actionSelected = listViewTutorActions.getSelectionModel().getSelectedItem();
		List<TutorAction> allActions = new ArrayList<>(EnumSet.allOf(TutorAction.class));
		return allActions.stream().filter(t -> t.getStrVal().equals(actionSelected)).findFirst()
				.orElse(TutorAction.NONE);
	}
}
