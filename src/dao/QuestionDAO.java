package dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import model.Question;
import model.enums.DifficultyLevel;
import model.enums.SystemMessageType;

import interfaceviews.SystemMessageView;

import utils.Constants;

public class QuestionDAO {

	private QuestionPaperDAO questionPaperDao = new QuestionPaperDAO();

	public QuestionDAO() {
	}

	/**
	 * Add a question to the questions CSV file
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
			csvWriter.append(data[0] + "," + data[1] + "," + data[2] + "\n"); // ID, subject ID, statement
			csvWriter.append(data[3] + "\n"); // answer option 1
			csvWriter.append(data[4] + "\n"); // answer option 2
			csvWriter.append(data[5] + "\n"); // answer option 3
			csvWriter.append(data[6] + "\n"); // answer option 4
			// correct answer num, difficulty level, marks, time required (mins)
			csvWriter.append(data[7] + "," + data[8] + "," + data[9] + "," + data[10] + "\n");
			csvWriter.flush();
			csvWriter.close();
		} catch (Exception e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
	}

	/**
	 * Delete a question by its unique ID
	 * 
	 * @param id - the ID of the question to delete
	 */
	public void deleteQuestionById(int id) {
		try {
			List<Question> allQuestions = getAllQuestions();
			File csvFile = new File(Constants.QUESTIONS_FILE_PATH);
			FileWriter csvWriter = new FileWriter(csvFile, false);

			for (Question q : allQuestions) {
				if (q.getId() != id) {
					String[] data = getQuestionStrData(q);

					csvWriter.write(data[0] + "," + data[1] + "," + data[2] + "\n"); // ID, subject ID, statement
					csvWriter.write(data[3] + "\n"); // answer option 1
					csvWriter.write(data[4] + "\n"); // answer option 2
					csvWriter.write(data[5] + "\n"); // answer option 3
					csvWriter.write(data[6] + "\n"); // answer option 4
					// correct answer num, difficulty level, marks, time required (mins)
					csvWriter.write(data[7] + "," + data[8] + "," + data[9] + "," + data[10] + "\n");
				}
			}
			csvWriter.flush();
			csvWriter.close();

			questionPaperDao.deleteQuestionPaperByQuestionId(id);
		} catch (IOException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
	}

	/**
	 * Delete question(s) a subject ID
	 * 
	 * @param subjectId - the subject ID of the question(s) to delete
	 */
	public void deleteQuestionBySubjectId(int subjectId) {
		if (getAllQuestions().isEmpty()) {
			return;
		}

		try {
			List<Question> allQuestions = getAllQuestions();
			File csvFile = new File(Constants.QUESTIONS_FILE_PATH);
			FileWriter csvWriter = new FileWriter(csvFile, false);

			for (Question q : allQuestions) {
				if (q.getSubjectId() != subjectId) {
					String[] data = getQuestionStrData(q);

					csvWriter.write(data[0] + "," + data[1] + "," + data[2] + "\n"); // ID, subject ID, statement
					csvWriter.write(data[3] + "\n"); // answer option 1
					csvWriter.write(data[4] + "\n"); // answer option 2
					csvWriter.write(data[5] + "\n"); // answer option 3
					csvWriter.write(data[6] + "\n"); // answer option 4
					// correct answer num, difficulty level, marks, time required (mins)
					csvWriter.write(data[7] + "," + data[8] + "," + data[9] + "," + data[10] + "\n");
				}
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
	}

	/**
	 * Retrieve all questions from CSV file
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
						String[] line1split = line1.split(",");
						String[] line6split = line6.split(",");

						int id = Integer.parseInt(line1split[0]);
						int subjectId = Integer.parseInt(line1split[1]);
						String statement = line1split[2];
						List<String> answerOptions = Arrays.asList(line2, line3, line4, line5);
						int answerNo = Integer.parseInt(line6split[0]);
						DifficultyLevel difficultyLevel = null;
						switch (line6split[1]) {
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
						int marks = Integer.parseInt(line6split[2]);
						int timeRequireMins = Integer.parseInt(line6split[3]);

						Question q = new Question(id, subjectId, statement, answerOptions, answerNo, difficultyLevel,
								marks, timeRequireMins);
						questions.add(q);
					} catch (Exception e) { // reached last line
						input.close();
						return questions;
					}
				}
				input.close();
			}
		} catch (FileNotFoundException e) {
			SystemMessageView.display(SystemMessageType.ERROR, "Unexpected error: " + e.getClass().getName());
		}
		return questions;
	}

	/**
	 * Retrieve question using its unique ID
	 * 
	 * @param id - the ID of the question to retrieve
	 * @return question with specified ID
	 */
	public Question getQuestionById(int id) {
		return getAllQuestions().stream().filter(q -> q.getId() == id).findFirst().orElse(null);
	}

	/**
	 * Get string value of each attribute of a question
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
		data[8] = question.getDifficultyLevel().toString();
		data[9] = Integer.toString(question.getMarks());
		data[10] = Integer.toString(question.getTimeRequiredMins());
		return data;
	}

	/**
	 * Get highest existing question ID, to be used when adding a new question to ensure uniqueness
	 * 
	 * @return highest existing question ID
	 */
	public int getHighestQuestionId() {
		if (getAllQuestions().isEmpty()) {
			return 0;
		}
		return getAllQuestions().stream().max(Comparator.comparing(Question::getId)).get().getId();
	}
}