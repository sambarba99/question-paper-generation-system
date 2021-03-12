package model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import model.builders.QuestionPaperBuilder;
import model.persisted.QuestionPaper;

import view.Constants;
import view.enums.DifficultyLevel;
import view.enums.SystemNotificationType;

import controller.SystemNotification;

/**
 * This class is a singleton, the use of which is any database operation regarding question papers.
 *
 * @author Sam Barba
 */
public class QuestionPaperDAO {

	private static QuestionPaperDAO instance;

	/**
	 * Add a question paper to the question papers CSV file.
	 * 
	 * @param questionPaper - the question paper to add
	 */
	public void addQuestionPaper(QuestionPaper questionPaper) {
		try {
			File csvFile = new File(Constants.QUESTION_PAPERS_FILE_PATH);
			if (!csvFile.exists()) {
				csvFile.getParentFile().mkdirs();
				csvFile.createNewFile();
			}
			String[] data = getQuestionPaperStrData(questionPaper);

			FileWriter csvWriter = new FileWriter(csvFile, true); // append = true
			// ID, subject ID, title
			csvWriter.append(data[0] + Constants.COMMA);
			csvWriter.append(data[1] + Constants.COMMA);
			csvWriter.append(data[2] + Constants.NEWLINE);
			// course title
			csvWriter.append(data[3] + Constants.NEWLINE);
			// course code, question IDs
			csvWriter.append(data[4] + Constants.COMMA);
			csvWriter.append(data[5] + Constants.NEWLINE);
			// difficulty level, marks, time required
			csvWriter.append(data[6] + Constants.COMMA);
			csvWriter.append(data[7] + Constants.COMMA);
			csvWriter.append(data[8] + Constants.NEWLINE);
			csvWriter.flush();
			csvWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
	}

	/**
	 * Delete a question paper by its unique ID.
	 * 
	 * @param id - the ID of the paper to delete
	 */
	public void deleteQuestionPaperById(int id) {
		try {
			List<QuestionPaper> allQuestionPapers = getAllQuestionPapers();
			File csvFile = new File(Constants.QUESTION_PAPERS_FILE_PATH);
			FileWriter csvWriter = new FileWriter(csvFile, false); // writing, not appending

			for (QuestionPaper questionPaper : allQuestionPapers) {
				if (questionPaper.getId() != id) {
					String[] data = getQuestionPaperStrData(questionPaper);
					writeQuestionPaperDataToFile(data, csvWriter);
				}
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
	}

	/**
	 * Retrieve all question papers from CSV file.
	 * 
	 * @return list of all question papers
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
						String[] line1split = line1.split(Constants.COMMA);
						String[] line3split = line3.split(Constants.COMMA);
						String[] line4split = line4.split(Constants.COMMA);

						int id = Integer.parseInt(line1split[0]);
						int subjectId = Integer.parseInt(line1split[1]);
						String title = line1split[2];
						String courseTitle = line2;
						String courseCode = line3split[0];
						List<Integer> questionIds = new ArrayList<>();
						for (int i = 1; i < line3split.length; i++) { // start at 1, because 0 is course code
							questionIds.add(Integer.parseInt(line3split[i]));
						}
						DifficultyLevel difficultyLevel = DifficultyLevel.getFromInt(Integer.parseInt(line4split[0]));
						int marks = Integer.parseInt(line4split[1]);
						int timeRequiredMins = Integer.parseInt(line4split[2]);

						QuestionPaper questionPaper = new QuestionPaperBuilder().withId(id).withSubjectId(subjectId)
							.withTitle(title).withCourseTitle(courseTitle).withCourseCode(courseCode)
							.withQuestionIds(questionIds).withDifficultyLevel(difficultyLevel).withMarks(marks)
							.withTimeRequiredMins(timeRequiredMins).build();

						questionPapers.add(questionPaper);
					} catch (Exception e) { // reached last line
						input.close();
						return questionPapers;
					}
				}
				input.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
		return questionPapers;
	}

	/**
	 * Retrieve question paper by its unique ID.
	 * 
	 * @param id - the ID of the paper to retrieve
	 * @return the question paper with the specified ID
	 */
	public QuestionPaper getQuestionPaperById(int id) {
		return getAllQuestionPapers().stream().filter(questionPaper -> questionPaper.getId() == id).findFirst()
			.orElse(null);
	}

	/**
	 * Retrieve all papers containing specified question ID
	 * 
	 * @param questionId - ID of the question to search for
	 * @return list of papers containing question ID
	 */
	public List<QuestionPaper> getQuestionPapersByQuestionId(int questionId) {
		return getAllQuestionPapers().stream()
			.filter(questionPaper -> questionPaper.getQuestionIds().contains(questionId)).collect(Collectors.toList());
	}

	/**
	 * Get string value of each attribute of a question paper.
	 * 
	 * @param questionPaper - the question paper of which to get String values
	 * @return array of attributes converted to String
	 */
	private String[] getQuestionPaperStrData(QuestionPaper questionPaper) {
		String[] data = new String[9];
		data[0] = Integer.toString(questionPaper.getId());
		data[1] = Integer.toString(questionPaper.getSubjectId());
		data[2] = questionPaper.getTitle();
		data[3] = questionPaper.getCourseTitle();
		data[4] = questionPaper.getCourseCode();
		StringBuilder questionIdsStrBld = new StringBuilder();
		for (Integer i : questionPaper.getQuestionIds()) {
			questionIdsStrBld.append(Integer.toString(i));
			questionIdsStrBld.append(Constants.COMMA);
		}
		String questionIds = questionIdsStrBld.toString();
		data[5] = questionIds.substring(0, questionIds.length() - 1); // remove last comma
		data[6] = Integer.toString(questionPaper.getDifficultyLevel().getIntVal());
		data[7] = Integer.toString(questionPaper.getMarks());
		data[8] = Integer.toString(questionPaper.getTimeRequiredMins());
		return data;
	}

	/**
	 * Write question paper data to the question papers CSV file.
	 * 
	 * @param data      - the string values of the question paper data
	 * @param csvWriter - the file writer
	 */
	private void writeQuestionPaperDataToFile(String[] data, FileWriter csvWriter) throws IOException {
		// ID, subject ID, title
		csvWriter.write(data[0] + Constants.COMMA);
		csvWriter.write(data[1] + Constants.COMMA);
		csvWriter.write(data[2] + Constants.NEWLINE);
		// course title
		csvWriter.write(data[3] + Constants.NEWLINE);
		// course code, question IDs
		csvWriter.write(data[4] + Constants.COMMA);
		csvWriter.write(data[5] + Constants.NEWLINE);
		// difficulty level, marks, time required
		csvWriter.write(data[6] + Constants.COMMA);
		csvWriter.write(data[7] + Constants.COMMA);
		csvWriter.write(data[8] + Constants.NEWLINE);
	}

	public synchronized static QuestionPaperDAO getInstance() {
		if (instance == null) {
			instance = new QuestionPaperDAO();
		}
		return instance;
	}

	private QuestionPaperDAO() {
	}
}
