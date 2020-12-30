package service;

import java.util.Comparator;
import java.util.List;

import dao.SubjectDAO;

import model.Subject;

public class SubjectService {

	private static SubjectService instance;

	private SubjectDAO subjectDao = SubjectDAO.getInstance();

	/**
	 * Add a subject to the subject CSV file
	 * 
	 * @param subject - the subject to add
	 */
	public void addSubject(Subject subject) {
		subjectDao.addSubject(subject);
	}

	/**
	 * Delete a subject by its unique ID, then any questions and papers of this subject
	 * 
	 * @param id - the ID of the subject to delete
	 */
	public void deleteSubjectById(int id) {
		subjectDao.deleteSubjectById(id);
	}

	/**
	 * Retrieve all subjects from CSV file
	 * 
	 * @return list of all subjects
	 */
	public List<Subject> getAllSubjects() {
		return subjectDao.getAllSubjects();
	}

	/**
	 * Retrieve subject using its unique ID
	 * 
	 * @param id - the ID of the subject to retrieve
	 * @return subject with specified ID
	 */
	public Subject getSubjectById(int id) {
		return subjectDao.getSubjectById(id);
	}

	/**
	 * Get the highest existing subject ID, to be used when adding a new subject to ensure uniqueness
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

	private SubjectService(SubjectDAO subjectDao) {
		if (subjectDao == null) {
			throw new IllegalArgumentException("Subject DAO cannot be null");
		}
		this.subjectDao = subjectDao;
	}

	public synchronized static SubjectService getInstance() {
		if (instance == null) {
			instance = new SubjectService(SubjectDAO.getInstance());
		}
		return instance;
	}
}