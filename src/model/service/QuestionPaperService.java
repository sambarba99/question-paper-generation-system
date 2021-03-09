package model.service;

import java.util.Comparator;
import java.util.List;

import model.dao.QuestionPaperDAO;
import model.persisted.QuestionPaper;

import view.Constants;
import view.enums.SystemNotificationType;

import controller.SystemNotification;

/**
 * This class is a singleton, the use of which is to perform any functionality regarding question papers.
 *
 * @author Sam Barba
 */
public class QuestionPaperService {

	private static QuestionPaperService instance;

	private QuestionPaperDAO questionPaperDao = QuestionPaperDAO.getInstance();

	/**
	 * Add a question paper to the question papers CSV file.
	 * 
	 * @param questionPaper - the question paper to add
	 */
	public void addQuestionPaper(QuestionPaper questionPaper) {
		questionPaperDao.addQuestionPaper(questionPaper);
	}

	/**
	 * Delete a question paper by its unique ID.
	 * 
	 * @param id - the ID of the paper to delete
	 */
	public void deleteQuestionPaperById(int id) {
		questionPaperDao.deleteQuestionPaperById(id);
	}

	/**
	 * Retrieve all question papers from question papers CSV file.
	 */
	public List<QuestionPaper> getAllQuestionPapers() {
		return questionPaperDao.getAllQuestionPapers();
	}

	/**
	 * Retrieve question paper by its unique ID.
	 * 
	 * @param id - the ID of the paper to retrieve
	 * @return the question paper with the specified ID
	 */
	public QuestionPaper getQuestionPaperById(int id) {
		return questionPaperDao.getQuestionPaperById(id);
	}

	/**
	 * Retrieve all papers containing specified question ID.
	 * 
	 * @param questionId - ID of the question to search for
	 * @return list of papers containing question ID
	 */
	public List<QuestionPaper> getQuestionPapersByQuestionId(int questionId) {
		return questionPaperDao.getQuestionPapersByQuestionId(questionId);
	}

	/**
	 * Get the highest existing question paper ID, to be used when generating a new question paper to ensure uniqueness.
	 * 
	 * @returns highest existing paper ID
	 */
	public int getHighestQuestionPaperId() {
		List<QuestionPaper> allQuestionPapers = getAllQuestionPapers();
		if (allQuestionPapers.isEmpty()) {
			return 0;
		}
		return allQuestionPapers.stream().max(Comparator.comparing(QuestionPaper::getId)).get().getId();
	}

	public synchronized static QuestionPaperService getInstance() {
		if (instance == null) {
			instance = new QuestionPaperService(QuestionPaperDAO.getInstance());
		}
		return instance;
	}

	private QuestionPaperService(QuestionPaperDAO questionPaperDao) {
		if (questionPaperDao == null) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + "Question paper DAO cannot be null!");
			throw new IllegalArgumentException("Question paper DAO cannot be null!");
		}
		this.questionPaperDao = questionPaperDao;
	}
}
