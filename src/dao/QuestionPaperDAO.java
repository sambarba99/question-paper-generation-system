package dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import model.QuestionPaper;
import model.enums.DifficultyLevel;
import model.enums.SystemMessageType;

import interfaceviews.SystemMessageView;

import utils.Constants;

public class QuestionPaperDAO {

	private static QuestionPaperDAO instance;

	/**
	 * Add a question paper to the question paper CSV file
	 * 
	 * @param qp - the question paper to add
	 */
	public void addQuestionPaper(QuestionPaper qp) {
		try {
			File csvFile = new File(Constants.QUESTION_PAPERS_FILE_PATH);
			if (!csvFile.exists()) {
				csvFile.getParentFile().mkdirs();
				csvFile.createNewFile();
			}
			String[] data = getQuestionPaperStrData(qp);

			FileWriter csvWriter = new FileWriter(csvFile, true); // append = true
			// ID, subject ID, title
			csvWriter.append(data[0] + "," + data[1] + "," + data[2] + "\n");
			// course title
			csvWriter.append(data[3] + "\n");
			// course code, question IDs
			csvWriter.append(data[4] + "," + data[5] + "\n");
			// difficulty level, marks, time required
			csvWriter.append(data[6] + "," + data[7] + "," + data[8] + "\n");
			csvWriter.flush();
			csvWriter.close();
		} catch (Exception e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
	}

	/**
	 * Delete a question paper by its unique ID
	 * 
	 * @param id - the ID of the paper to delete
	 */
	public void deleteQuestionPaperById(int id) {
		try {
			List<QuestionPaper> allQuestionPapers = getAllQuestionPapers();
			File csvFile = new File(Constants.QUESTION_PAPERS_FILE_PATH);
			FileWriter csvWriter = new FileWriter(csvFile, false); // writing new file, not appending

			for (QuestionPaper qp : allQuestionPapers) {
				if (qp.getId() != id) {
					String[] data = getQuestionPaperStrData(qp);

					// ID, subject ID, title
					csvWriter.write(data[0] + "," + data[1] + "," + data[2] + "\n");
					// course title
					csvWriter.write(data[3] + "\n");
					// course code, question IDs
					csvWriter.write(data[4] + "," + data[5] + "\n");
					// difficulty level, marks, time required
					csvWriter.write(data[6] + "," + data[7] + "," + data[8] + "\n");
				}
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
	}

	/**
	 * Delete question paper(s) by a subject ID
	 * 
	 * @param subjectId - the subject ID of the paper(s) to delete
	 */
	public void deleteQuestionPaperBySubjectId(int subjectId) {
		if (getAllQuestionPapers().isEmpty()) {
			return;
		}

		try {
			List<QuestionPaper> allQuestionPapers = getAllQuestionPapers();
			File csvFile = new File(Constants.QUESTION_PAPERS_FILE_PATH);
			FileWriter csvWriter = new FileWriter(csvFile, false); // writing, not appending

			for (QuestionPaper qp : allQuestionPapers) {
				if (qp.getSubjectId() != subjectId) {
					String[] data = getQuestionPaperStrData(qp);

					// ID, subject ID, title
					csvWriter.write(data[0] + "," + data[1] + "," + data[2] + "\n");
					// course title
					csvWriter.write(data[3] + "\n");
					// course code, question IDs
					csvWriter.write(data[4] + "," + data[5] + "\n");
					// difficulty level, marks, time required
					csvWriter.write(data[6] + "," + data[7] + "," + data[8] + "\n");
				}
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
	}

	/**
	 * Delete question paper(s) by a question ID
	 * 
	 * @param questionId - the question ID in paper(s) to delete
	 */
	public void deleteQuestionPaperByQuestionId(int questionId) {
		if (getAllQuestionPapers().isEmpty()) {
			return;
		}

		try {
			List<QuestionPaper> allQuestionPapers = getAllQuestionPapers();
			File csvFile = new File(Constants.QUESTION_PAPERS_FILE_PATH);
			FileWriter csvWriter = new FileWriter(csvFile, false); // writing, not appending

			for (QuestionPaper qp : allQuestionPapers) {
				if (!qp.getQuestionIds().contains(questionId)) {
					String[] data = getQuestionPaperStrData(qp);

					// ID, subject ID, title
					csvWriter.write(data[0] + "," + data[1] + "," + data[2] + "\n");
					// course title
					csvWriter.write(data[3] + "\n");
					// course code, question IDs
					csvWriter.write(data[4] + "," + data[5] + "\n");
					// difficulty level, marks, time required
					csvWriter.write(data[6] + "," + data[7] + "," + data[8] + "\n");
				}
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
	}

	/**
	 * Retrieve all question papers from CSV file
	 */
	public List<QuestionPaper> getAllQuestionPapers() {
		List<QuestionPaper> questionPapers = new ArrayList<>();

		try {
			File csvFile = new File(Constants.QUESTION_PAPERS_FILE_PATH);

			if (csvFile.exists()) {
				Scanner input = new Scanner(csvFile);

				while (input.hasNextLine()) {
					try {
						String line1 = input.nextLine(); // ID, subject ID, title
						String line2 = input.nextLine(); // course title
						String line3 = input.nextLine(); // course code, question IDs
						String line4 = input.nextLine(); // difficulty level, marks, time required (mins)
						String[] line1split = line1.split(",");
						String[] line3split = line3.split(",");
						String[] line4split = line4.split(",");

						int id = Integer.parseInt(line1split[0]);
						int subjectId = Integer.parseInt(line1split[1]);
						String title = line1split[2];
						String courseTitle = line2;
						String courseCode = line3split[0];
						List<Integer> questionIds = new ArrayList<>();
						for (int i = 1; i < line3split.length; i++) { // start at 1, because 0 is course code
							questionIds.add(Integer.parseInt(line3split[i]));
						}
						DifficultyLevel difficultyLevel = null;
						switch (line4split[0]) {
							case "EASY":
								difficultyLevel = DifficultyLevel.EASY;
								break;
							case "MEDIUM":
								difficultyLevel = DifficultyLevel.MEDIUM;
								break;
							case "DIFFICULT":
								difficultyLevel = DifficultyLevel.DIFFICULT;
								break;
						}
						int marks = Integer.parseInt(line4split[1]);
						int timeRequiredMins = Integer.parseInt(line4split[2]);

						QuestionPaper qp = new QuestionPaper(id, subjectId, title, courseTitle, courseCode, questionIds,
								difficultyLevel, marks, timeRequiredMins);
						questionPapers.add(qp);
					} catch (Exception e) { // reached last line
						input.close();
						return questionPapers;
					}
				}
				input.close();
			}
		} catch (FileNotFoundException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
		return questionPapers;
	}

	/**
	 * Retrieve question paper by its unique ID
	 * 
	 * @param id - the ID of the paper to retrieve
	 * @return the question paper with the specified ID
	 */
	public QuestionPaper getQuestionPaperById(int id) {
		return getAllQuestionPapers().stream().filter(qp -> qp.getId() == id).findFirst().orElse(null);
	}

	/**
	 * Retrieve all papers containing specified question ID
	 * 
	 * @param questionId - ID of the question to search for
	 * @return list of papers containing question ID
	 */
	public List<QuestionPaper> getQuestionPapersByQuestionId(int questionId) {
		return getAllQuestionPapers().stream().filter(qp -> qp.getQuestionIds().contains(questionId))
				.collect(Collectors.toList());
	}

	/**
	 * Get string value of each attribute of a question paper
	 * 
	 * @param qp - the question paper of which to get String values
	 * @return array of attributes converted to String
	 */
	private String[] getQuestionPaperStrData(QuestionPaper qp) {
		String[] data = new String[9];
		data[0] = Integer.toString(qp.getId());
		data[1] = Integer.toString(qp.getSubjectId());
		data[2] = qp.getTitle();
		data[3] = qp.getCourseTitle();
		data[4] = qp.getCourseCode();
		String questionIds = "";
		for (Integer i : qp.getQuestionIds()) {
			questionIds += Integer.toString(i) + ",";
		}
		data[5] = questionIds.substring(0, questionIds.length() - 1); // remove last comma
		data[6] = qp.getDifficultyLevel().toString();
		data[7] = Integer.toString(qp.getMarks());
		data[8] = Integer.toString(qp.getTimeRequiredMins());
		return data;
	}

	public synchronized static QuestionPaperDAO getInstance() {
		if (instance == null) {
			instance = new QuestionPaperDAO();
		}
		return instance;
	}
}