package model.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.ListView;

import model.dao.SubjectDAO;
import model.persisted.Subject;

import view.SystemNotification;
import view.enums.SystemNotificationType;
import view.utils.Constants;

/**
 * This class is a singleton, the use of which is to perform any functionality regarding subjects.
 *
 * @author Sam Barba
 */
public class SubjectService {

	private static SubjectService instance;

	private SubjectDAO subjectDao = SubjectDAO.getInstance();

	/**
	 * Add a subject to the subjects CSV file.
	 * 
	 * @param subject - the subject to add
	 */
	public void addSubject(Subject subject) {
		subjectDao.addSubject(subject);
	}

	/**
	 * Delete a subject by its unique ID, then any questions and papers of this subject.
	 * 
	 * @param id - the ID of the subject to delete
	 */
	public void deleteSubjectById(int id) {
		subjectDao.deleteSubjectById(id);
	}

	/**
	 * Retrieve all subjects from subjects CSV file.
	 * 
	 * @return list of all subjects
	 */
	public List<Subject> getAllSubjects() {
		return subjectDao.getAllSubjects();
	}

	/**
	 * Retrieve subject using its unique ID.
	 * 
	 * @param id - the ID of the subject to retrieve
	 * @return subject with specified ID
	 */
	public Subject getSubjectById(int id) {
		return subjectDao.getSubjectById(id);
	}

	/**
	 * Get the highest existing subject ID, to be used when adding a new subject to ensure uniqueness.
	 * 
	 * @returns highest existing subject ID
	 */
	public int getHighestSubjectId() {
		List<Subject> allSubjects = getAllSubjects();
		if (allSubjects.isEmpty()) {
			return 0;
		}
		return allSubjects.stream().max(Comparator.comparing(Subject::getId)).get().getId();
	}

	/**
	 * Get a list of all subjects for subjects ListView objects.
	 * 
	 * @return list of all subjects
	 */
	public List<String> getSubjectListViewItems() {
		return getAllSubjects().stream().map(Subject::toString).collect(Collectors.toList());
	}

	/**
	 * Get ID of selected subject in ListView or CheckBox etc. from its display string, e.g. "Maths (ID 1)".
	 * 
	 * @return the subject's ID
	 */
	public int getSubjectIdFromDisplayStr(String subjectDisplayStr) {
		/*
		 * Here and in getSelectedSubjectIds() we are getting the final element because there can be multiple spaces in
		 * the subject, e.g. "Mathematical Analysis (ID 4)". We then remove the closing parentheses.
		 */
		String[] split = subjectDisplayStr.split(Constants.SPACE);
		String subjectIdStr = split[split.length - 1];
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

	public synchronized static SubjectService getInstance() {
		if (instance == null) {
			instance = new SubjectService(SubjectDAO.getInstance());
		}
		return instance;
	}

	private SubjectService(SubjectDAO subjectDao) {
		if (subjectDao == null) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + "Subject DAO cannot be null!");
			throw new IllegalArgumentException("Subject DAO cannot be null!");
		}
		this.subjectDao = subjectDao;
	}
}
