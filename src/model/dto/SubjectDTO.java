package model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;

import model.persisted.Subject;
import model.service.SubjectService;

import view.Constants;

/**
 * This class is a singleton containing methods related to ListViews used to modify subjects.
 *
 * @author Sam Barba
 */
public class SubjectDTO {

	private static SubjectDTO instance;

	/**
	 * Get a list of all subjects for subjects ListView objects.
	 * 
	 * @return list of all subjects
	 */
	public List<String> getSubjectListViewItems() {
		List<Subject> allSubjects = SubjectService.getInstance().getAllSubjects();
		List<String> listViewItems = allSubjects.stream()
			.map(subject -> (subject.getTitle() + " (ID " + subject.getId() + ")"))
			.collect(Collectors.toList());
		return listViewItems;
	}

	/**
	 * Get ID of selected subject in ListView.
	 * 
	 * @return subject ID
	 */
	public int getSubjectId(ChoiceBox cbSubject) {
		/*
		 * Here and in getSelectedSubjectIds() we are getting element at position (length - 1) because there can be
		 * multiple spaces in the subject, e.g. "Mathematical Analysis (ID 4)". We then remove the closing bracket.
		 */
		String subject = cbSubject.getSelectionModel().getSelectedItem().toString();
		String[] sSplit = subject.split(Constants.SPACE);
		String subjectIdStr = sSplit[sSplit.length - 1];
		subjectIdStr = subjectIdStr.replace(")", Constants.EMPTY);
		return Integer.parseInt(subjectIdStr);
	}

	/**
	 * Get list of IDs of selected subjects in ListView.
	 * 
	 * @param listViewSubjects - the ListView of subjects
	 * @return list of subject IDs
	 */
	public List<Integer> getSelectedSubjectsIds(ListView<String> listViewSubjects) {
		List<String> subjects = listViewSubjects.getSelectionModel().getSelectedItems();
		List<Integer> subjectIds = new ArrayList<>();
		for (String s : subjects) {
			String[] subjectStrSplit = s.split(Constants.SPACE);
			String subjectIdStr = subjectStrSplit[subjectStrSplit.length - 1];
			subjectIdStr = subjectIdStr.replace(")", Constants.EMPTY);
			subjectIds.add(Integer.parseInt(subjectIdStr));
		}
		return subjectIds;
	}

	/**
	 * Capitalise each word in subject title and trim whitespace.
	 * 
	 * @param title - the title to format
	 * @return formatted title
	 */
	public String formatTitle(String title) {
		// remove characters that could potentially harm CSV read/write functionality
		title = title.replace(Constants.NEWLINE, Constants.EMPTY).replace(Constants.QUOT_MARK, "'");
		String[] words = title.trim().split(Constants.SPACE);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			result.append(Character.toString(words[i].charAt(0)).toUpperCase());
			result.append(words[i].substring(1).toLowerCase() + Constants.SPACE);
		}
		return result.toString().trim(); // remove last space
	}

	public synchronized static SubjectDTO getInstance() {
		if (instance == null) {
			instance = new SubjectDTO();
		}
		return instance;
	}

	private SubjectDTO() {
	}
}
