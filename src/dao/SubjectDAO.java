package dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import interfacecontroller.SystemNotification;

import model.Subject;
import model.enums.SystemNotificationType;

import utils.Constants;

/**
 * This class is a singleton, the use of which is any database operation regarding subjects.
 *
 * @author Sam Barba
 */
public class SubjectDAO {

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
			csvWriter.append(Integer.toString(subject.getId()) + Constants.COMMA);
			csvWriter.append(subject.getTitle() + Constants.NEWLINE);
			csvWriter.flush();
			csvWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
	}

	/**
	 * Delete a subject by its unique ID, then any questions and papers of this subject.
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
					csvWriter.write(Integer.toString(subject.getId()) + Constants.COMMA);
					csvWriter.write(subject.getTitle() + Constants.NEWLINE);
				}
			}
			csvWriter.flush();
			csvWriter.close();

			QuestionDAO.getInstance().deleteQuestionBySubjectId(id);
			QuestionPaperDAO.getInstance().deleteQuestionPaperBySubjectId(id);
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
					String[] lineSplit = line.split(Constants.COMMA);
					int id = Integer.parseInt(lineSplit[0]);
					String title = lineSplit[1];

					Subject subject = new Subject(id, title);
					subjects.add(subject);
				}
				input.close();
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
