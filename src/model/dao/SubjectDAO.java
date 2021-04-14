package model.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import model.persisted.Subject;
import model.xml.XMLSubjectSerialiser;

import view.enums.SystemNotificationType;
import view.utils.Constants;

import controller.SystemNotification;

/**
 * This class is a singleton, the use of which is any database operation regarding subjects.
 *
 * @author Sam Barba
 */
public class SubjectDAO {

	private static final Logger LOGGER = Logger.getLogger(SubjectDAO.class.getName());

	private XMLSubjectSerialiser subjectSerialiser = XMLSubjectSerialiser.getInstance();

	private static SubjectDAO instance;

	private SubjectDAO() {
	}

	public synchronized static SubjectDAO getInstance() {
		if (instance == null) {
			instance = new SubjectDAO();
		}
		return instance;
	}

	/**
	 * Add a subject to the subjects XML file.
	 * 
	 * @param subject - the subject to add
	 */
	public void addSubject(Subject subject) {
		try {
			File xmlFile = new File(Constants.SUBJECTS_FILE_PATH);
			List<Subject> allSubjects = getAllSubjects();
			if (!xmlFile.exists()) {
				xmlFile.getParentFile().mkdirs();
				xmlFile.createNewFile();
			}

			allSubjects.add(subject);
			subjectSerialiser.write(allSubjects);
			LOGGER.info("Subject with ID " + subject.getId() + " added");
		} catch (Exception e) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
		}
	}

	/**
	 * Delete subjects by their unique IDs.
	 * 
	 * @param ids - the IDs of the subjects to delete
	 */
	public void deleteSubjectsByIds(List<Integer> ids) {
		try {
			List<Subject> allSubjects = getAllSubjects();
			List<Subject> writeSubjects = allSubjects.stream()
				.filter(s -> !ids.contains(s.getId()))
				.collect(Collectors.toList());

			subjectSerialiser.write(writeSubjects);

			LOGGER.info("Subjects with specified IDs deleted");
		} catch (Exception e) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
		}
	}

	/**
	 * Retrieve all subjects.
	 * 
	 * @return list of all subjects
	 */
	public List<Subject> getAllSubjects() {
		List<Subject> allSubjects = new ArrayList<>();

		File xmlFile = new File(Constants.SUBJECTS_FILE_PATH);
		if (xmlFile.exists()) {
			try {
				allSubjects = (List<Subject>) subjectSerialiser.readAll();
			} catch (Exception e) {
				SystemNotification.display(SystemNotificationType.ERROR,
					Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
			}
		}

		return allSubjects;
	}
}
