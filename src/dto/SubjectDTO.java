package dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;

import dao.SubjectDAO;

import model.Subject;

public class SubjectDTO {

	private SubjectDAO subjectDao = new SubjectDAO();

	public SubjectDTO() {
	}

	/**
	 * Get a list of all subjects for subjects ListView
	 * 
	 * @return list of all subjects
	 */
	public List<String> getSubjectListViewItems() {
		List<Subject> allSubjects = subjectDao.getAllSubjects();
		List<String> listViewItems = allSubjects.stream().map(s -> (s.getTitle() + " (ID " + s.getId() + ")"))
				.collect(Collectors.toList());
		return listViewItems;
	}

	/**
	 * Get ID of selected subject in list view
	 * 
	 * @return subject ID
	 */
	public int getSubjectId(ChoiceBox cbSubject) {
		/*
		 * here and in getSelectedSubjectIds() we are getting element at position
		 * (length - 1) because there can be multiple spaces in the subject, e.g.
		 * "Mathematical Analysis (ID 4)". We then remove the closing bracket.
		 */
		String subject = cbSubject.getSelectionModel().getSelectedItem().toString();
		String[] sSplit = subject.split(" ");
		String subjectIdStr = sSplit[sSplit.length - 1];
		subjectIdStr = subjectIdStr.replace(")", "");
		return Integer.parseInt(subjectIdStr);
	}

	/**
	 * Get list of IDs of selected subjects in list view
	 * 
	 * @param listViewSubjects - the ListView of subjects
	 * @return list of subject IDs
	 */
	public List<Integer> getSelectedSubjectsIds(ListView<String> listViewSubjects) {
		List<String> subjects = listViewSubjects.getSelectionModel().getSelectedItems();
		List<Integer> subjectIds = new ArrayList<>();
		for (String s : subjects) {
			String[] subjectStrSplit = s.split(" ");
			String subjectIdStr = subjectStrSplit[subjectStrSplit.length - 1];
			subjectIdStr = subjectIdStr.replace(")", "");
			subjectIds.add(Integer.parseInt(subjectIdStr));
		}
		return subjectIds;
	}

	/**
	 * Capitalise each word in title and trim whitespace
	 * 
	 * @param title - the title to format
	 * @return formatted title
	 */
	public String formatTitle(String title) {
		String[] words = title.trim().split(" ");
		String result = "";
		for (int i = 0; i < words.length; i++) {
			result += Character.toString(words[i].charAt(0)).toUpperCase();
			result += words[i].substring(1).toLowerCase() + " ";
		}
		return result.trim(); // remove last space
	}
}
