package model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import model.builders.QuestionPaperBuilder;
import model.persisted.QuestionPaper;

import view.SystemNotification;
import view.enums.DifficultyLevel;
import view.enums.SystemNotificationType;
import view.utils.Constants;

/**
 * This class is a singleton, the use of which is any database operation regarding question papers.
 *
 * @author Sam Barba
 */
public class QuestionPaperDAO {

	public static final Logger LOGGER = Logger.getLogger(QuestionPaperDAO.class.getName());

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

			FileWriter csvWriter = new FileWriter(csvFile, true); // append = true
			addQuestionPaperDataToFile(questionPaper, csvWriter, true);
			csvWriter.flush();
			csvWriter.close();
			LOGGER.info("Question paper '" + questionPaper.getTitle() + "' added");
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
			FileWriter csvWriter = new FileWriter(csvFile, false); // append = false

			for (QuestionPaper questionPaper : allQuestionPapers) {
				if (questionPaper.getId() != id) {
					addQuestionPaperDataToFile(questionPaper, csvWriter, false);
				}
			}
			csvWriter.flush();
			csvWriter.close();
			LOGGER.info("Question paper with ID " + id + " deleted");
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
					String line = input.nextLine();
					String[] lineArr = line.split(Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK);

					int id = Integer.parseInt(lineArr[0].replace(Constants.QUOT_MARK, Constants.EMPTY));
					int subjectId = Integer.parseInt(lineArr[1]);
					String title = lineArr[2];
					String courseTitle = lineArr[3];
					String courseCode = lineArr[4];
					String[] questionIdsStr = lineArr[5].split(Constants.COMMA);
					List<Integer> questionIds = new ArrayList<>();
					for (int i = 0; i < questionIdsStr.length; i++) {
						questionIds.add(Integer.parseInt(questionIdsStr[i]));
					}
					DifficultyLevel difficultyLevel = DifficultyLevel.getFromStr(lineArr[6]);
					int marks = Integer.parseInt(lineArr[7]);
					int timeRequiredMins = Integer.parseInt(lineArr[8].replace(Constants.QUOT_MARK, Constants.EMPTY));

					QuestionPaper questionPaper = new QuestionPaperBuilder().withId(id)
						.withSubjectId(subjectId)
						.withTitle(title)
						.withCourseTitle(courseTitle)
						.withCourseCode(courseCode)
						.withQuestionIds(questionIds)
						.withDifficultyLevel(difficultyLevel)
						.withMarks(marks)
						.withTimeRequiredMins(timeRequiredMins)
						.build();

					questionPapers.add(questionPaper);
				}
				input.close();
				LOGGER.info("Retrieved all " + questionPapers.size() + " question papers");
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
		LOGGER.info("Retrieving question paper by ID " + id);
		return getAllQuestionPapers().stream()
			.filter(questionPaper -> questionPaper.getId() == id)
			.findFirst()
			.orElse(null);
	}

	/**
	 * Retrieve all papers containing a certain question.
	 * 
	 * @param questionId - ID of the question to search for
	 * @return list of papers containing question with specified ID
	 */
	public List<QuestionPaper> getQuestionPapersByQuestionId(int questionId) {
		LOGGER.info("Retrieving question papers by question ID " + questionId);
		return getAllQuestionPapers().stream()
			.filter(questionPaper -> questionPaper.getQuestionIds().contains(questionId))
			.collect(Collectors.toList());
	}

	/**
	 * Add question paper data to the question papers CSV file.
	 * 
	 * @param data      - the string values of the question paper data
	 * @param csvWriter - the file writer
	 * @param append    - whether to append or write to the file
	 */
	private void addQuestionPaperDataToFile(QuestionPaper questionPaper, FileWriter csvWriter, boolean append)
		throws IOException {
		StringBuilder questionIdsBld = new StringBuilder();
		for (Integer id : questionPaper.getQuestionIds()) {
			questionIdsBld.append(id);
			questionIdsBld.append(Constants.COMMA);
		}
		String questionIds = questionIdsBld.toString();
		questionIds = questionIds.substring(0, questionIds.length() - 1); // remove last comma

		/*
		 * 1 line contains: ID, subject ID, title, course title, course code, question IDs, difficulty level, marks,
		 * time required (mins)
		 */
		String line = Constants.QUOT_MARK + Integer.toString(questionPaper.getId()) + Constants.QUOT_MARK
			+ Constants.COMMA + Constants.QUOT_MARK + Integer.toString(questionPaper.getSubjectId())
			+ Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK + questionPaper.getTitle()
			+ Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK + questionPaper.getCourseTitle()
			+ Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK + questionPaper.getCourseCode()
			+ Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK + questionIds + Constants.QUOT_MARK
			+ Constants.COMMA + Constants.QUOT_MARK + questionPaper.getDifficultyLevel().getStrVal()
			+ Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK + Integer.toString(questionPaper.getMarks())
			+ Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK
			+ Integer.toString(questionPaper.getTimeRequiredMins()) + Constants.QUOT_MARK + Constants.NEWLINE;

		if (append) {
			csvWriter.append(line);
		} else { // write
			csvWriter.write(line);
		}

		LOGGER.info("Added data of question paper ID " + questionPaper.getId() + " to file");
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
