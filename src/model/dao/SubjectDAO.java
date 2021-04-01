package model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
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

	private static final Logger LOGGER = Logger.getLogger(SubjectDAO.class.getName());

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

			FileWriter writer = new FileWriter(csvFile, true); // append = true
			addSubjectDataToFile(subject, writer, true);
			writer.flush();
			writer.close();
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
			FileWriter writer = new FileWriter(csvFile, false); // append = false

			for (Subject subject : allSubjects) {
				if (subject.getId() != id) {
					addSubjectDataToFile(subject, writer, false);
				}
			}
			writer.flush();
			writer.close();
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
					String title = lineArr[1];
					LocalDateTime dateCreated = LocalDateTime
						.parse(lineArr[2].replace(Constants.QUOT_MARK, Constants.EMPTY), Constants.DATE_FORMATTER);

					Subject subject = new SubjectBuilder().withId(id)
						.withTitle(title)
						.withDateCreated(dateCreated)
						.build();

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
	 * Add subject data to the subjects CSV file.
	 * 
	 * @param subject - the subject to add
	 * @param writer  - the file writer
	 * @param append  - whether to append or write to the file
	 */
	private void addSubjectDataToFile(Subject subject, FileWriter writer, boolean append) throws IOException {
		/*
		 * 1 line contains: ID, title, date created
		 */
		String line = Constants.QUOT_MARK + subject.getId() + Constants.QUOT_MARK + Constants.COMMA
			+ Constants.QUOT_MARK + subject.getTitle() + Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK
			+ Constants.DATE_FORMATTER.format(subject.getDateCreated()) + Constants.QUOT_MARK + Constants.NEWLINE;

		if (append) {
			writer.append(line);
		} else { // write
			writer.write(line);
		}
	}
}
