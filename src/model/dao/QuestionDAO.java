package model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import model.persisted.Question;

import view.Constants;
import view.enums.DifficultyLevel;
import view.enums.SystemNotificationType;

import controller.SystemNotification;

/**
 * This class is a singleton, the use of which is any database operation regarding paper questions.
 *
 * @author Sam Barba
 */
public class QuestionDAO {

	private static QuestionDAO instance;

	/**
	 * Add a question to the questions CSV file.
	 * 
	 * @param question - the question to add
	 */
	public void addQuestion(Question question) {
		try {
			File csvFile = new File(Constants.QUESTIONS_FILE_PATH);
			if (!csvFile.exists()) {
				csvFile.getParentFile().mkdirs();
				csvFile.createNewFile();
			}
			String[] data = getQuestionStrData(question);

			FileWriter csvWriter = new FileWriter(csvFile, true); // append = true
			// ID, subject ID, statement
			csvWriter.append(data[0] + Constants.COMMA);
			csvWriter.append(data[1] + Constants.COMMA);
			csvWriter.append(data[2] + Constants.NEWLINE);
			// 4 answer options
			csvWriter.append(data[3] + Constants.NEWLINE);
			csvWriter.append(data[4] + Constants.NEWLINE);
			csvWriter.append(data[5] + Constants.NEWLINE);
			csvWriter.append(data[6] + Constants.NEWLINE);
			// Correct answer num, difficulty level, marks, time required (mins)
			csvWriter.append(data[7] + Constants.COMMA);
			csvWriter.append(data[8] + Constants.COMMA);
			csvWriter.append(data[9] + Constants.COMMA);
			csvWriter.append(data[10] + Constants.NEWLINE);
			csvWriter.flush();
			csvWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
	}

	/**
	 * Delete a question by its unique ID.
	 * 
	 * @param id - the ID of the question to delete
	 */
	public void deleteQuestionById(int id) {
		try {
			List<Question> allQuestions = getAllQuestions();
			File csvFile = new File(Constants.QUESTIONS_FILE_PATH);
			FileWriter csvWriter = new FileWriter(csvFile, false);

			for (Question question : allQuestions) {
				if (question.getId() != id) {
					String[] data = getQuestionStrData(question);
					writeQuestionDataToFile(data, csvWriter);
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
	 * Retrieve all questions from CSV file.
	 * 
	 * @return list of all questions
	 */
	public List<Question> getAllQuestions() {
		List<Question> questions = new ArrayList<>();

		try {
			File csvFile = new File(Constants.QUESTIONS_FILE_PATH);

			if (csvFile.exists()) {
				Scanner input = new Scanner(csvFile);

				while (input.hasNextLine()) {
					try {
						String line1 = input.nextLine(); // ID, subject ID, statement
						String line2 = input.nextLine(); // answer option 1
						String line3 = input.nextLine(); // answer option 2
						String line4 = input.nextLine(); // answer option 3
						String line5 = input.nextLine(); // answer option 4
						String line6 = input.nextLine(); // correct answer num, difficulty level, marks, time required
						String[] line1split = line1.split(Constants.COMMA);
						String[] line6split = line6.split(Constants.COMMA);

						int id = Integer.parseInt(line1split[0]);
						int subjectId = Integer.parseInt(line1split[1]);
						String statement = line1split[2];
						List<String> answerOptions = Arrays.asList(line2, line3, line4, line5);
						int correctAnsNo = Integer.parseInt(line6split[0]);
						DifficultyLevel difficultyLevel = DifficultyLevel.getFromInt(Integer.parseInt(line6split[1]));
						int marks = Integer.parseInt(line6split[2]);
						int timeRequireMins = Integer.parseInt(line6split[3]);

						Question question = new Question(id, subjectId, statement, answerOptions, correctAnsNo,
							difficultyLevel, marks, timeRequireMins);
						questions.add(question);
					} catch (Exception e) { // reached last line
						input.close();
						return questions;
					}
				}
				input.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName());
		}
		return questions;
	}

	/**
	 * Retrieve question using its unique ID.
	 * 
	 * @param id - the ID of the question to retrieve
	 * @return question with specified ID
	 */
	public Question getQuestionById(int id) {
		return getAllQuestions().stream().filter(question -> question.getId() == id).findFirst().orElse(null);
	}

	/**
	 * Get string value of each attribute of a question.
	 * 
	 * @param question - the question of which to get String values
	 * @return array of attributes converted to String
	 */
	private String[] getQuestionStrData(Question question) {
		String[] data = new String[11];
		data[0] = Integer.toString(question.getId());
		data[1] = Integer.toString(question.getSubjectId());
		data[2] = question.getStatement();
		data[3] = question.getAnswerOptions().get(0);
		data[4] = question.getAnswerOptions().get(1);
		data[5] = question.getAnswerOptions().get(2);
		data[6] = question.getAnswerOptions().get(3);
		data[7] = Integer.toString(question.getCorrectAnswerOptionNum());
		data[8] = Integer.toString(question.getDifficultyLevel().getIntVal());
		data[9] = Integer.toString(question.getMarks());
		data[10] = Integer.toString(question.getTimeRequiredMins());
		return data;
	}

	/**
	 * Write question data to the questions CSV file.
	 * 
	 * @param data      - the string values of the question data
	 * @param csvWriter - the file writer
	 */
	private void writeQuestionDataToFile(String[] data, FileWriter csvWriter) throws IOException {
		// ID, subject ID, statement
		csvWriter.write(data[0] + Constants.COMMA);
		csvWriter.write(data[1] + Constants.COMMA);
		csvWriter.write(data[2] + Constants.NEWLINE);
		// 4 answer options
		csvWriter.write(data[3] + Constants.NEWLINE);
		csvWriter.write(data[4] + Constants.NEWLINE);
		csvWriter.write(data[5] + Constants.NEWLINE);
		csvWriter.write(data[6] + Constants.NEWLINE);
		// Correct answer num, difficulty level, marks, time required (mins)
		csvWriter.write(data[7] + Constants.COMMA);
		csvWriter.write(data[8] + Constants.COMMA);
		csvWriter.write(data[9] + Constants.COMMA);
		csvWriter.write(data[10] + Constants.NEWLINE);
	}

	public synchronized static QuestionDAO getInstance() {
		if (instance == null) {
			instance = new QuestionDAO();
		}
		return instance;
	}

	private QuestionDAO() {
	}
}
