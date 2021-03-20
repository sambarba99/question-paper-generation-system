package model.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.ListView;

import model.dao.QuestionPaperDAO;
import model.persisted.Question;
import model.persisted.QuestionPaper;
import model.persisted.Subject;

import view.SystemNotification;
import view.enums.SystemNotificationType;
import view.utils.Constants;

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

	/**
	 * Get a list of all question papers for paper ListView objects.
	 * 
	 * @return list of all question papers
	 */
	public List<String> getQuestionPaperListViewItems() {
		return getAllQuestionPapers().stream()
			.map(questionPaper -> (questionPaper.getTitle() + " (ID " + questionPaper.getId() + ")"))
			.collect(Collectors.toList());
	}

	/**
	 * Get a list of question papers of specified subject IDs.
	 * 
	 * @param subjectIds - the list of subject IDs
	 * @return list of specified subject papers
	 */
	public List<String> getQuestionPaperListViewItemsBySubjectIds(List<Integer> subjectIds) {
		List<String> listViewItems = new ArrayList<>();
		for (QuestionPaper questionPaper : getAllQuestionPapers()) {
			if (subjectIds.contains(questionPaper.getSubjectId())) {
				listViewItems.add(questionPaper.getTitle() + " (ID " + questionPaper.getId() + ")");
			}
		}
		return listViewItems;
	}

	/**
	 * Get ID of selected question paper in ListView.
	 * 
	 * @param listViewQuestionPapers - the ListView of papers
	 * @return ID of selected paper
	 */
	public int getQuestionPaperId(ListView<String> listViewQuestionPapers) {
		/*
		 * Here we are getting the element at position (length - 1) because there can be multiple spaces in the string,
		 * e.g. "Mathematics (ID 1)". We then remove the closing bracket.
		 */
		String questionPaper = listViewQuestionPapers.getSelectionModel().getSelectedItem();
		String[] questionPaperSplit = questionPaper.split(Constants.SPACE);
		String questionPaperIdStr = questionPaperSplit[questionPaperSplit.length - 1];
		questionPaperIdStr = questionPaperIdStr.replace(")", Constants.EMPTY);
		return Integer.parseInt(questionPaperIdStr);
	}

	/**
	 * Get a formatted question paper string for question paper TextArea object.
	 * 
	 * @param questionPaper - the paper to format
	 * @return question string
	 */
	public String getTxtAreaQuestionPaperStr(QuestionPaper questionPaper) {
		Subject subject = SubjectService.getInstance().getSubjectById(questionPaper.getSubjectId());

		StringBuilder txtAreaStr = new StringBuilder();
		txtAreaStr.append(questionPaper.getTitle() + " (ID " + questionPaper.getId() + ")");
		txtAreaStr.append(Constants.NEWLINE + "Subject: " + subject.getTitle() + " (ID " + subject.getId() + ")");
		txtAreaStr.append(Constants.NEWLINE + "Course: " + questionPaper.getCourseTitle() + " ("
			+ questionPaper.getCourseCode() + ")");
		txtAreaStr
			.append(Constants.NEWLINE + "Difficulty level: " + questionPaper.getDifficultyLevel().getIntVal() + "/6");
		txtAreaStr.append(Constants.NEWLINE + "Marks: " + questionPaper.getMarks());
		txtAreaStr.append(
			Constants.NEWLINE + "Time required (mins): " + questionPaper.getTimeRequiredMins() + Constants.NEWLINE);

		List<Integer> questionIds = questionPaper.getQuestionIds();
		for (int i = 0; i < questionIds.size(); i++) {
			Question question = QuestionService.getInstance().getQuestionById(questionIds.get(i));
			txtAreaStr.append(Constants.NEWLINE + (i + 1) + ". " + question.getStatement());
			txtAreaStr.append(Constants.NEWLINE + "(A) " + question.getAnswers().get(0).getValue());
			txtAreaStr.append(Constants.NEWLINE + "(B) " + question.getAnswers().get(1).getValue());
			txtAreaStr.append(Constants.NEWLINE + "(C) " + question.getAnswers().get(2).getValue());
			txtAreaStr.append(Constants.NEWLINE + "(D) " + question.getAnswers().get(3).getValue() + Constants.NEWLINE);
		}

		return txtAreaStr.substring(0, txtAreaStr.length() - 1); // remove last '\n'
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
