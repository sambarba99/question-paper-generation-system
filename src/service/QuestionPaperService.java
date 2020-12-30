package service;

import java.util.Comparator;
import java.util.List;

import dao.QuestionPaperDAO;

import model.QuestionPaper;

public class QuestionPaperService {

	private static QuestionPaperService instance;

	private QuestionPaperDAO questionPaperDao = QuestionPaperDAO.getInstance();

	/**
	 * Add a question paper to the question paper CSV file
	 * 
	 * @param qp - the question paper to add
	 */
	public void addQuestionPaper(QuestionPaper qp) {
		questionPaperDao.addQuestionPaper(qp);
	}

	/**
	 * Delete a question paper by its unique ID
	 * 
	 * @param id - the ID of the paper to delete
	 */
	public void deleteQuestionPaperById(int id) {
		questionPaperDao.deleteQuestionPaperById(id);
	}

	/**
	 * Delete question paper(s) by a subject ID
	 * 
	 * @param subjectId - the subject ID of the paper(s) to delete
	 */
	public void deleteQuestionPaperBySubjectId(int subjectId) {
		questionPaperDao.deleteQuestionPaperBySubjectId(subjectId);
	}

	/**
	 * Delete question paper(s) by a question ID
	 * 
	 * @param questionId - the question ID in paper(s) to delete
	 */
	public void deleteQuestionPaperByQuestionId(int questionId) {
		questionPaperDao.deleteQuestionPaperByQuestionId(questionId);
	}

	/**
	 * Retrieve all question papers from CSV file
	 */
	public List<QuestionPaper> getAllQuestionPapers() {
		return questionPaperDao.getAllQuestionPapers();
	}

	/**
	 * Retrieve question paper by its unique ID
	 * 
	 * @param id - the ID of the paper to retrieve
	 * @return the question paper with the specified ID
	 */
	public QuestionPaper getQuestionPaperById(int id) {
		return questionPaperDao.getQuestionPaperById(id);
	}

	/**
	 * Retrieve all papers containing specified question ID
	 * 
	 * @param questionId - ID of the question to search for
	 * @return list of papers containing question ID
	 */
	public List<QuestionPaper> getQuestionPapersByQuestionId(int questionId) {
		return questionPaperDao.getQuestionPapersByQuestionId(questionId);
	}

	/**
	 * Get the highest existing question paper ID, to be used when generating a new question paper to ensure uniqueness
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

	private QuestionPaperService(QuestionPaperDAO questionPaperDao) {
		if (questionPaperDao == null) {
			throw new IllegalArgumentException("Question paper DAO cannot be null");
		}
		this.questionPaperDao = questionPaperDao;
	}

	public synchronized static QuestionPaperService getInstance() {
		if (instance == null) {
			instance = new QuestionPaperService(QuestionPaperDAO.getInstance());
		}
		return instance;
	}
}