package model.service;

import java.util.Comparator;
import java.util.List;

import model.dao.QuestionDAO;
import model.persisted.Question;

import view.Constants;
import view.enums.SystemNotificationType;

import controller.SystemNotification;

/**
 * This class is a singleton, the use of which is to perform any functionality regarding questions.
 *
 * @author Sam Barba
 */
public class QuestionService {

	private static QuestionService instance;

	private QuestionDAO questionDao = QuestionDAO.getInstance();

	/**
	 * Add a question to the questions CSV file.
	 * 
	 * @param question - the question to add
	 */
	public void addQuestion(Question question) {
		questionDao.addQuestion(question);
	}

	/**
	 * Delete a question by its unique ID.
	 * 
	 * @param id - the ID of the question to delete
	 */
	public void deleteQuestionById(int id) {
		questionDao.deleteQuestionById(id);
	}

	/**
	 * Delete question(s) a subject ID.
	 * 
	 * @param subjectId - the subject ID of the question(s) to delete
	 */
	public void deleteQuestionBySubjectId(int id) {
		questionDao.deleteQuestionBySubjectId(id);
	}

	/**
	 * Retrieve all questions from questions CSV file.
	 * 
	 * @return list of all questions
	 */
	public List<Question> getAllQuestions() {
		return questionDao.getAllQuestions();
	}

	/**
	 * Retrieve question using its unique ID.
	 * 
	 * @param id - the ID of the question to retrieve
	 * @return question with specified ID
	 */
	public Question getQuestionById(int id) {
		return questionDao.getQuestionById(id);
	}

	/**
	 * Get highest existing question ID, to be used when adding a new question to ensure uniqueness.
	 * 
	 * @return highest existing question ID
	 */
	public int getHighestQuestionId() {
		List<Question> allQuestions = getAllQuestions();
		if (allQuestions.isEmpty()) {
			return 0;
		}
		return allQuestions.stream().max(Comparator.comparing(Question::getId)).get().getId();
	}

	public synchronized static QuestionService getInstance() {
		if (instance == null) {
			instance = new QuestionService(QuestionDAO.getInstance());
		}
		return instance;
	}

	private QuestionService(QuestionDAO questionDao) {
		if (questionDao == null) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + "Question DAO cannot be null!");
			throw new IllegalArgumentException("Question DAO cannot be null!");
		}
		this.questionDao = questionDao;
	}
}
