package model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import model.builders.SubjectBuilder;
import model.persisted.Subject;

import view.SystemNotification;
import view.enums.SystemNotificationType;
import view.utils.Constants;

/**
 * This class is a singleton, the use of which is any database operation regarding subjects.
 *
 * @author Sam Barba
 */
public class SubjectDAO {

	public static final Logger LOGGER = Logger.getLogger(SubjectDAO.class.getName());

	private static SubjectDAO instance;

	/**
	 * Add a subject to the subjects CSV file.
	 * 
	 * @param subject - the subject to add
	 */
	public void addSubject(Subject subject) {
		try {
			File csvFile = new File(Constants.SUBJECTS_FILE_PATH);
			if (!csvFile.exists()) {
				csvFile.getParentFile().mkdirs();
				csvFile.createNewFile();
			}

			FileWriter csvWriter = new FileWriter(csvFile, true); // append = true
			csvWriter.append(Constants.QUOT_MARK + Integer.toString(subject.getId()) + Constants.QUOT_MARK
				+ Constants.COMMA + Constants.QUOT_MARK + subject.getTitle() + Constants.QUOT_MARK + Constants.NEWLINE);
			csvWriter.flush();
			csvWriter.close();
			LOGGER.info("Subject '" + subject.getTitle() + "' added");
		} catch (Exception e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
	}

	/**
	 * Delete a subject by its unique ID (but not any questions and papers of this subject).
	 * 
	 * @param id - the ID of the subject to delete
	 */
	public void deleteSubjectById(int id) {
		try {
			List<Subject> allSubjects = getAllSubjects();
			File csvFile = new File(Constants.SUBJECTS_FILE_PATH);
			FileWriter csvWriter = new FileWriter(csvFile, false);

			for (Subject subject : allSubjects) {
				if (subject.getId() != id) {
					csvWriter.write(
						Constants.QUOT_MARK + Integer.toString(subject.getId()) + Constants.QUOT_MARK + Constants.COMMA
							+ Constants.QUOT_MARK + subject.getTitle() + Constants.QUOT_MARK + Constants.NEWLINE);
				}
			}
			csvWriter.flush();
			csvWriter.close();
			LOGGER.info("Subject with ID " + id + " deleted");
		} catch (IOException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
	}

	/**
	 * Retrieve all subjects from subjects CSV file.
	 * 
	 * @return list of all subjects
	 */
	public List<Subject> getAllSubjects() {
		List<Subject> subjects = new ArrayList<>();

		try {
			File csvFile = new File(Constants.SUBJECTS_FILE_PATH);
			if (csvFile.exists()) {
				Scanner input = new Scanner(csvFile);

				while (input.hasNextLine()) {
					String line = input.nextLine();
					String[] lineArr = line.split(Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK);

					int id = Integer.parseInt(lineArr[0].replace(Constants.QUOT_MARK, Constants.EMPTY));
					String title = lineArr[1].replace(Constants.QUOT_MARK, Constants.EMPTY);

					Subject subject = new SubjectBuilder().withId(id).withTitle(title).build();
					subjects.add(subject);
				}
				input.close();
				LOGGER.info("Retrieved all " + subjects.size() + " subjects");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
		return subjects;
	}

	/**
	 * Retrieve subject using its unique ID.
	 * 
	 * @param id - the ID of the subject to retrieve
	 * @return subject with specified ID
	 */
	public Subject getSubjectById(int id) {
		LOGGER.info("Retrieving subject by ID " + id);
		return getAllSubjects().stream().filter(subject -> subject.getId() == id).findFirst().orElse(null);
	}

	public synchronized static SubjectDAO getInstance() {
		if (instance == null) {
			instance = new SubjectDAO();
		}
		return instance;
	}

	private SubjectDAO() {
	}
}
