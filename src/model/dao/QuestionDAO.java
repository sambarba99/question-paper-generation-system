package model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import model.builders.AnswerBuilder;
import model.builders.QuestionBuilder;
import model.persisted.Answer;
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

	private static final int ASCII_A = 65;

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

			FileWriter csvWriter = new FileWriter(csvFile, true); // append = true
			addQuestionDataToFile(question, csvWriter, true);
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
			FileWriter csvWriter = new FileWriter(csvFile, false); // append = false

			for (Question question : allQuestions) {
				if (question.getId() != id) {
					addQuestionDataToFile(question, csvWriter, false);
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
						String line = input.nextLine();
						String[] lineArr = line.split(Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK);

						int id = Integer.parseInt(lineArr[0].replace(Constants.QUOT_MARK, Constants.EMPTY));
						int subjectId = Integer.parseInt(lineArr[1]);
						String statement = lineArr[2];
						List<String> strAnswers = Arrays.asList(lineArr[3], lineArr[4], lineArr[5], lineArr[6]);
						String correctAnswerLetter = lineArr[7];
						List<Answer> answers = makeAnswers(strAnswers, correctAnswerLetter);
						DifficultyLevel difficultyLevel = DifficultyLevel.getFromInt(Integer.parseInt(lineArr[8]));
						int marks = Integer.parseInt(lineArr[9]);
						int timeRequireMins = Integer
							.parseInt(lineArr[10].replace(Constants.QUOT_MARK, Constants.EMPTY));

						Question question = new QuestionBuilder().withId(id)
							.withSubjectId(subjectId)
							.withStatement(statement)
							.withAnswers(answers)
							.withDifficultyLevel(difficultyLevel)
							.withMarks(marks)
							.withTimeRequiredMins(timeRequireMins)
							.build();

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
	 * Add question data to the questions CSV file.
	 * 
	 * @param data      - the string values of the question data
	 * @param csvWriter - the file writer
	 * @param append    - whether to append or write to the file
	 */
	private void addQuestionDataToFile(Question question, FileWriter csvWriter, boolean append) throws IOException {
		Answer correctAnswer = question.getAnswers().stream().filter(Answer::isCorrect).findFirst().orElse(null);
		/*
		 * 1 line contains: ID, subject ID, statement, answers A-D, correct answer letter (A/B/C/D), difficulty level
		 * int value, marks, time required (mins)
		 */
		String line = Constants.QUOT_MARK + Integer.toString(question.getId()) + Constants.QUOT_MARK + Constants.COMMA
			+ Constants.QUOT_MARK + Integer.toString(question.getSubjectId()) + Constants.QUOT_MARK + Constants.COMMA
			+ Constants.QUOT_MARK + question.getStatement() + Constants.QUOT_MARK + Constants.COMMA
			+ Constants.QUOT_MARK + question.getAnswers().get(0).getValue() + Constants.QUOT_MARK + Constants.COMMA
			+ Constants.QUOT_MARK + question.getAnswers().get(1).getValue() + Constants.QUOT_MARK + Constants.COMMA
			+ Constants.QUOT_MARK + question.getAnswers().get(2).getValue() + Constants.QUOT_MARK + Constants.COMMA
			+ Constants.QUOT_MARK + question.getAnswers().get(3).getValue() + Constants.QUOT_MARK + Constants.COMMA
			+ Constants.QUOT_MARK + correctAnswer.getLetter() + Constants.QUOT_MARK + Constants.COMMA
			+ Constants.QUOT_MARK + Integer.toString(question.getDifficultyLevel().getIntVal()) + Constants.QUOT_MARK
			+ Constants.COMMA + Constants.QUOT_MARK + Integer.toString(question.getMarks()) + Constants.QUOT_MARK
			+ Constants.COMMA + Constants.QUOT_MARK + Integer.toString(question.getTimeRequiredMins())
			+ Constants.QUOT_MARK + Constants.NEWLINE;

		if (append) {
			csvWriter.append(line);
		} else { // write
			csvWriter.write(line);
		}
	}

	/**
	 * Make answers for a question, given a list of possible answers and the letter of the correct one.
	 * 
	 * @param answersStr          - the list of possible answers
	 * @param correctAnswerLetter - the letter of the correct answer
	 * @return - list of Answer objects to be used in Question building
	 */
	private List<Answer> makeAnswers(List<String> answersStr, String correctAnswerLetter) {
		/*
		 * Since correct answer is A-D, we subtract these 2 ASCII values to get its position in the list of answers.
		 * E.g. If C is correct, then position in list = 67 - 65 = 2.
		 */
		int correctAnswerPos = (int) correctAnswerLetter.charAt(0) - ASCII_A;

		List<Answer> answers = new ArrayList<>();

		for (int i = 0; i < answersStr.size(); i++) {
			Answer answer = new AnswerBuilder().withValue(answersStr.get(i))
				.withLetter(Character.toString((char) (ASCII_A + i)))
				.withIsCorrect(i == correctAnswerPos)
				.build();

			answers.add(answer);
		}
		return answers;
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
