package model.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import model.persisted.Question;
import model.xml.XMLQuestionSerialiser;

import view.enums.SystemNotificationType;
import view.utils.Constants;

import controller.SystemNotification;

/**
 * This class is a singleton, the use of which is any database operation regarding paper questions.
 *
 * @author Sam Barba
 */
public class QuestionDAO {

	private static final Logger LOGGER = Logger.getLogger(QuestionDAO.class.getName());

	private XMLQuestionSerialiser questionSerialiser = XMLQuestionSerialiser.getInstance();

	private static QuestionDAO instance;

	private QuestionDAO() {
	}

	public synchronized static QuestionDAO getInstance() {
		if (instance == null) {
			instance = new QuestionDAO();
		}
		return instance;
	}

	/**
	 * Add a question to the questions XML file.
	 * 
	 * @param question - the question to add
	 */
	public void addQuestion(Question question) {
		try {
			File xmlFile = new File(Constants.QUESTIONS_FILE_PATH);
			List<Question> allQuestions = getAllQuestions();
			if (!xmlFile.exists()) {
				xmlFile.getParentFile().mkdirs();
				xmlFile.createNewFile();
			}

			allQuestions.add(question);
			questionSerialiser.write(allQuestions);
			LOGGER.info("Question with ID " + question.getId() + " added");
		} catch (Exception e) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
		}
	}

	/**
	 * Delete questions by their unique IDs.
	 * 
	 * @param ids - the IDs of the questions to delete
	 */
	public void deleteQuestionsByIds(List<Integer> ids) {
		try {
			List<Question> allQuestions = getAllQuestions();
			List<Question> writeQuestions = allQuestions.stream()
				.filter(q -> !ids.contains(q.getId()))
				.collect(Collectors.toList());

			questionSerialiser.write(writeQuestions);

			LOGGER.info("Questions with specified IDs deleted");
		} catch (Exception e) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
		}
	}

	/**
	 * Retrieve all questions.
	 * 
	 * @return list of all questions
	 */
	public List<Question> getAllQuestions() {
		List<Question> allQuestions = new ArrayList<>();

		File xmlFile = new File(Constants.QUESTIONS_FILE_PATH);
		if (xmlFile.exists()) {
			try {
				allQuestions = (List<Question>) questionSerialiser.readAll();
			} catch (Exception e) {
				SystemNotification.display(SystemNotificationType.ERROR,
					Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
			}
		}

		return allQuestions;
	}
}
