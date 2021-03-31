package model.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import model.builders.QuestionPaperBuilder;
import model.persisted.QuestionPaper;

import view.SystemNotification;
import view.enums.SkillLevel;
import view.enums.SystemNotificationType;
import view.utils.Constants;

/**
 * This class is a singleton, the use of which is any database operation regarding question papers.
 *
 * @author Sam Barba
 */
public class QuestionPaperDAO {

	private static final Logger LOGGER = Logger.getLogger(QuestionPaperDAO.class.getName());

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

			FileWriter writer = new FileWriter(csvFile, true); // append = true
			addQuestionPaperDataToFile(questionPaper, writer, true);
			writer.flush();
			writer.close();
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
			FileWriter writer = new FileWriter(csvFile, false); // append = false

			for (QuestionPaper questionPaper : allQuestionPapers) {
				if (questionPaper.getId() != id) {
					addQuestionPaperDataToFile(questionPaper, writer, false);
				}
			}
			writer.flush();
			writer.close();
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
					for (String questionIdStr : questionIdsStr) {
						questionIds.add(Integer.parseInt(questionIdStr));
					}
					SkillLevel skillLevel = SkillLevel.getFromStr(lineArr[6]);
					int marks = Integer.parseInt(lineArr[7]);
					int minutesRequired = Integer.parseInt(lineArr[8]);
					LocalDateTime dateCreated = LocalDateTime
						.parse(lineArr[9].replace(Constants.QUOT_MARK, Constants.EMPTY), Constants.DATE_FORMATTER);

					QuestionPaper questionPaper = new QuestionPaperBuilder().withId(id)
						.withSubjectId(subjectId)
						.withTitle(title)
						.withCourseTitle(courseTitle)
						.withCourseCode(courseCode)
						.withQuestionIds(questionIds)
						.withSkillLevel(skillLevel)
						.withMarks(marks)
						.withMinutesRequired(minutesRequired)
						.withDateCreated(dateCreated)
						.build();

					questionPapers.add(questionPaper);
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
	public Optional<QuestionPaper> getQuestionPaperById(int id) {
		return getAllQuestionPapers().stream().filter(qp -> qp.getId() == id).findFirst();
	}

	/**
	 * Retrieve all papers containing a certain question.
	 * 
	 * @param questionId - ID of the question to search for
	 * @return list of papers containing question with specified ID
	 */
	public List<QuestionPaper> getQuestionPapersByQuestionId(int questionId) {
		return getAllQuestionPapers().stream()
			.filter(qp -> qp.getQuestionIds().contains(questionId))
			.collect(Collectors.toList());
	}

	/**
	 * Add question paper data to the question papers CSV file.
	 * 
	 * @param questionPaper - the question paper to add
	 * @param writer        - the file writer
	 * @param append        - whether to append or write to the file
	 */
	private void addQuestionPaperDataToFile(QuestionPaper questionPaper, FileWriter writer, boolean append)
		throws IOException {

		StringBuilder questionIdsBld = new StringBuilder();
		for (Integer id : questionPaper.getQuestionIds()) {
			questionIdsBld.append(id);
			questionIdsBld.append(Constants.COMMA);
		}
		String questionIds = questionIdsBld.toString();
		questionIds = questionIds.substring(0, questionIds.length() - 1); // remove last comma

		/*
		 * 1 line contains: ID, subject ID, title, course title, course code, question IDs, skill level, marks, minutes
		 * required, date created
		 */
		String line = Constants.QUOT_MARK + Integer.toString(questionPaper.getId()) + Constants.QUOT_MARK
			+ Constants.COMMA + Constants.QUOT_MARK + Integer.toString(questionPaper.getSubjectId())
			+ Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK + questionPaper.getTitle()
			+ Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK + questionPaper.getCourseTitle()
			+ Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK + questionPaper.getCourseCode()
			+ Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK + questionIds + Constants.QUOT_MARK
			+ Constants.COMMA + Constants.QUOT_MARK + questionPaper.getSkillLevel().getStrVal() + Constants.QUOT_MARK
			+ Constants.COMMA + Constants.QUOT_MARK + Integer.toString(questionPaper.getMarks()) + Constants.QUOT_MARK
			+ Constants.COMMA + Constants.QUOT_MARK + Integer.toString(questionPaper.getMinutesRequired())
			+ Constants.QUOT_MARK + Constants.COMMA + Constants.QUOT_MARK
			+ Constants.DATE_FORMATTER.format(questionPaper.getDateCreated()) + Constants.QUOT_MARK + Constants.NEWLINE;

		if (append) {
			writer.append(line);
		} else { // write
			writer.write(line);
		}
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
