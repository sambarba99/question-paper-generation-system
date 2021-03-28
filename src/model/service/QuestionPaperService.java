package model.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
	public Optional<QuestionPaper> getQuestionPaperById(int id) {
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
		return allQuestionPapers.isEmpty() ? 0
			: allQuestionPapers.stream().max(Comparator.comparing(QuestionPaper::getId)).get().getId();
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
			.filter(qp -> subjectIds.isEmpty() || subjectIds.contains(qp.getSubjectId()))
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
		Optional<Subject> subjectOpt = SubjectService.getInstance().getSubjectById(questionPaper.getSubjectId());
		String subjectTitle = subjectOpt.isPresent() ? subjectOpt.get().getTitle() : Constants.SUBJECT_DELETED;

		QuestionPaperDTO questionPaperDto = new QuestionPaperDTO();
		questionPaperDto.setId(questionPaper.getId());
		questionPaperDto.setTitle(questionPaper.getTitle());
		questionPaperDto.setSubjectTitle(subjectTitle);
		questionPaperDto.setCourse(questionPaper.getCourseTitle() + " (" + questionPaper.getCourseCode() + ")");
		questionPaperDto.setDateCreated(Constants.DATE_FORMATTER.format(questionPaper.getDateCreated()));

		return questionPaperDto;
	}

	/**
	 * Get a formatted question paper string for question paper TextArea object.
	 * 
	 * @param questionPaper - the paper to format
	 * @return question paper as a string
	 */
	public String getTxtAreaQuestionPaperStr(QuestionPaper questionPaper) {
		Optional<Subject> subjectOpt = SubjectService.getInstance().getSubjectById(questionPaper.getSubjectId());
		String subjectTitle = subjectOpt.isPresent() ? subjectOpt.get().getTitle() : Constants.SUBJECT_DELETED;

		StringBuilder resultBld = new StringBuilder();
		resultBld.append(questionPaper.toString());
		resultBld.append(Constants.NEWLINE + "Subject: " + subjectTitle);
		resultBld.append(Constants.NEWLINE + "Course: " + questionPaper.getCourseTitle() + " ("
			+ questionPaper.getCourseCode() + ")");
		resultBld.append(
			Constants.NEWLINE + "Approx. Bloom skill level: " + questionPaper.getSkillLevel().getIntVal() + "/6");
		resultBld.append(Constants.NEWLINE + "Marks: " + questionPaper.getMarks());
		resultBld.append(Constants.NEWLINE + "Time required: " + questionPaper.getTimeRequiredMins() + " minutes");

		List<Integer> questionIds = questionPaper.getQuestionIds();
		int numQ = questionIds.size();
		for (int i = 0; i < questionIds.size(); i++) {
			Question question = QuestionService.getInstance().getQuestionById(questionIds.get(i)).get();

			resultBld.append(Constants.NEWLINE + Constants.NEWLINE + "Question " + (i + 1) + "/" + numQ + " ("
				+ question.getMarks() + " marks). " + question.getStatement() + Constants.NEWLINE);

			for (int j = 0; j < question.getAnswers().size(); j++) {
				resultBld.append(Constants.NEWLINE + "(" + ((char) (Constants.ASCII_A + j)) + ") "
					+ question.getAnswers().get(j).getValue());
			}
		}

		return resultBld.toString();
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
