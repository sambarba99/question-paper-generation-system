package dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Subject;
import model.enums.SystemMessageType;

import interfaceviews.SystemMessageView;

import utils.Constants;

public class SubjectDAO {

	private static SubjectDAO instance;

	/**
	 * Add a subject to the subject CSV file
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
			csvWriter.append(Integer.toString(subject.getId()) + "," + subject.getTitle() + "\n");
			csvWriter.flush();
			csvWriter.close();
		} catch (Exception e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
	}

	/**
	 * Delete a subject by its unique ID, then any questions and papers of this subject
	 * 
	 * @param id - the ID of the subject to delete
	 */
	public void deleteSubjectById(int id) {
		try {
			List<Subject> allSubjects = getAllSubjects();
			File csvFile = new File(Constants.SUBJECTS_FILE_PATH);
			FileWriter csvWriter = new FileWriter(csvFile, false);

			for (Subject s : allSubjects) {
				if (s.getId() != id) {
					csvWriter.write(Integer.toString(s.getId()) + "," + s.getTitle() + "\n");
				}
			}
			csvWriter.flush();
			csvWriter.close();

			QuestionDAO.getInstance().deleteQuestionBySubjectId(id);
			QuestionPaperDAO.getInstance().deleteQuestionPaperBySubjectId(id);
		} catch (IOException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
	}

	/**
	 * Retrieve all subjects from CSV file
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
					String[] lineSplit = line.split(",");
					int id = Integer.parseInt(lineSplit[0]);
					String name = lineSplit[1];

					Subject subject = new Subject(id, name);
					subjects.add(subject);
				}
				input.close();
			}
		} catch (FileNotFoundException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
		return subjects;
	}

	/**
	 * Retrieve subject using its unique ID
	 * 
	 * @param id - the ID of the subject to retrieve
	 * @return subject with specified ID
	 */
	public Subject getSubjectById(int id) {
		return getAllSubjects().stream().filter(s -> s.getId() == id).findFirst().orElse(null);
	}

	public synchronized static SubjectDAO getInstance() {
		if (instance == null) {
			instance = new SubjectDAO();
		}
		return instance;
	}
}