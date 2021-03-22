package model.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import model.dao.QuestionPaperDAO;
import model.dto.QuestionPaperDTO;
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
		questionPaper.setDateCreated(LocalDateTime.now());
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
	 * Get all question papers converted to DTOs for using in TableViews, with applied subject ID filters (if any
	 * selected in AcademicMaterialManagement).
	 * 
	 * @param subjectIds - subject IDs to filter by
	 * @return (filtered) list of all question papers as DTOs
	 */
	public List<QuestionPaperDTO> getQuestionPaperDTOsWithSubjectFilter(List<Integer> subjectIds) {
		/*
		 * If the subject IDs list is empty, then it means the user does not want to filter by subjects. This is why we
		 * have the subjectIds.isEmpty() condition in a logical disjunction (||).
		 */
		return getAllQuestionPapers().stream()
			.filter(s -> subjectIds.isEmpty() || subjectIds.contains(s.getId()))
			.map(this::convertToQuestionPaperDTO)
			.collect(Collectors.toList());
	}

	/**
	 * Convert a question paper to its DTO equivalent.
	 * 
	 * @param questionPaper - the paper to convert
	 * @return the equivalent QuestionPaperDTO
	 */
	private QuestionPaperDTO convertToQuestionPaperDTO(QuestionPaper questionPaper) {
		QuestionPaperDTO questionPaperDto = new QuestionPaperDTO();
		questionPaperDto.setId(questionPaper.getId());
		questionPaperDto.setTitle(questionPaper.getTitle());
		questionPaperDto
			.setSubjectTitle(SubjectService.getInstance().getSubjectById(questionPaper.getSubjectId()).getTitle());
		questionPaperDto.setCourse(questionPaper.getCourseTitle() + " (" + questionPaper.getCourseCode() + ")");
		questionPaperDto.setDateCreated(Constants.DATE_FORMATTER.format(questionPaper.getDateCreated()));

		return questionPaperDto;
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
		txtAreaStr.append(questionPaper.toString());
		txtAreaStr.append(Constants.NEWLINE + "Subject: " + subject.toString());
		txtAreaStr.append(Constants.NEWLINE + "Course: " + questionPaper.getCourseTitle() + " ("
			+ questionPaper.getCourseCode() + ")");
		txtAreaStr.append(
			Constants.NEWLINE + "Average difficulty level: " + questionPaper.getDifficultyLevel().getIntVal() + "/6");
		txtAreaStr.append(Constants.NEWLINE + "Marks: " + questionPaper.getMarks());
		txtAreaStr.append(
			Constants.NEWLINE + "Time required (mins): " + questionPaper.getTimeRequiredMins() + Constants.NEWLINE);

		List<Integer> questionIds = questionPaper.getQuestionIds();
		for (int i = 0; i < questionIds.size(); i++) {
			Question question = QuestionService.getInstance().getQuestionById(questionIds.get(i));
			txtAreaStr.append(Constants.NEWLINE + (i + 1) + ". " + question.getStatement());
			txtAreaStr.append(Constants.NEWLINE + question.getAnswers().get(0).toString());
			txtAreaStr.append(Constants.NEWLINE + question.getAnswers().get(1).toString());
			txtAreaStr.append(Constants.NEWLINE + question.getAnswers().get(2).toString());
			txtAreaStr.append(Constants.NEWLINE + question.getAnswers().get(3).toString() + Constants.NEWLINE);
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
