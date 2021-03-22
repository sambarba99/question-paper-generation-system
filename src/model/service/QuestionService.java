package model.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import model.dao.QuestionDAO;
import model.dto.QuestionDTO;
import model.persisted.Answer;
import model.persisted.Question;
import model.persisted.QuestionPaper;
import model.persisted.Subject;

import view.SystemNotification;
import view.enums.SystemNotificationType;
import view.utils.Constants;

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
		question.setDateCreated(LocalDateTime.now());
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
	 * Retrieve all questions from questions CSV file.
	 * 
	 * @return list of all questions
	 */
	public List<Question> getAllQuestions() {
		return questionDao.getAllQuestions();
	}

	/**
	 * Retrieve all questions with a subject ID.
	 * 
	 * @param subjectId - the subject ID of the questions
	 * @return list of questions with specified subject ID
	 */
	public List<Question> getQuestionsBySubjectId(int subjectId) {
		return getAllQuestions().stream().filter(q -> q.getSubjectId() == subjectId).collect(Collectors.toList());
	}

	/**
	 * Retrieve question using its unique ID.
	 * 
	 * @param id - the ID of the question to retrieve
	 * @return question with specified ID
	 */
	public Optional<Question> getQuestionById(int id) {
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

	/**
	 * Get all questions converted to DTOs for using in TableViews, with applied subject and difficulty level filters
	 * (if any selected in QuestionManagement).
	 * 
	 * @param difficultyLvls - difficulty level IDs to filter by
	 * @param subjectIds     - subject IDs to filter by
	 * @return (filtered) list of all questions as DTOs
	 */
	public List<QuestionDTO> getQuestionDTOsWithFilters(List<Integer> difficultyLvls, List<Integer> subjectIds) {
		/*
		 * If a list is empty, say difficultyLvls, then it means the user does not want to filter by difficulty. This is
		 * why we have the difficultyLvls.isEmpty() condition in a logical disjunction (||).
		 */
		return getAllQuestions().stream()
			.filter(q -> difficultyLvls.isEmpty() || difficultyLvls.contains(q.getDifficultyLevel().getIntVal()))
			.filter(q -> subjectIds.isEmpty() || subjectIds.contains(q.getSubjectId()))
			.map(this::convertToQuestionDTO)
			.collect(Collectors.toList());
	}

	/**
	 * Convert a question to its DTO equivalent.
	 * 
	 * @param question - the question to convert
	 * @return the equivalent QuestionDTO
	 */
	private QuestionDTO convertToQuestionDTO(Question question) {
		Optional<Subject> subjectOpt = SubjectService.getInstance().getSubjectById(question.getSubjectId());
		if (!subjectOpt.isPresent()) {
			throw new IllegalArgumentException("Invalid subject ID passed: " + question.getSubjectId());
		}

		QuestionDTO questionDto = new QuestionDTO();
		questionDto.setId(question.getId());
		questionDto.setSubjectTitle(subjectOpt.get().getTitle());
		questionDto.setStatement(question.getStatement());
		questionDto.setDifficultyLevel(question.getDifficultyLevel().getDisplayStr());
		questionDto.setMarks(question.getMarks());
		questionDto.setTimeRequiredMins(question.getTimeRequiredMins());
		questionDto.setDateCreated(Constants.DATE_FORMATTER.format(question.getDateCreated()));

		return questionDto;
	}

	/**
	 * Get a formatted question string for question TextArea.
	 * 
	 * @param id - the ID of the question to format
	 * @return question string
	 */
	public String getTxtAreaQuestionStr(int id) {
		Optional<Question> questionOpt = getQuestionById(id);
		Question question = null;
		if (questionOpt.isPresent()) {
			question = questionOpt.get();
		} else {
			throw new IllegalArgumentException("Invalid question ID passed: " + id);
		}

		Optional<Subject> subjectOpt = SubjectService.getInstance().getSubjectById(question.getSubjectId());
		if (!subjectOpt.isPresent()) {
			throw new IllegalArgumentException("Invalid subject ID passed: " + question.getSubjectId());
		}

		List<Answer> answers = question.getAnswers();
		Answer correctAnswer = answers.stream().filter(Answer::isCorrect).findFirst().orElse(null);
		List<QuestionPaper> papersContainingQuestion = QuestionPaperService.getInstance()
			.getQuestionPapersByQuestionId(id);

		StringBuilder txtAreaStr = new StringBuilder();
		txtAreaStr.append("Subject: " + subjectOpt.get().toString());
		txtAreaStr
			.append(Constants.NEWLINE + "Bloom difficulty level: " + question.getDifficultyLevel().getDisplayStr());
		if (papersContainingQuestion.isEmpty()) {
			txtAreaStr.append(Constants.NEWLINE + "There are no papers which contain this question.");
		} else {
			txtAreaStr.append(Constants.NEWLINE + "Question papers containing this question:");
			for (QuestionPaper questionPaper : papersContainingQuestion) {
				txtAreaStr.append(Constants.NEWLINE + "- " + questionPaper.toString());
			}
		}
		txtAreaStr.append(Constants.NEWLINE + Constants.NEWLINE + question.getStatement() + Constants.NEWLINE);
		for (Answer answer : answers) {
			txtAreaStr.append(Constants.NEWLINE + answer.toString());
		}
		txtAreaStr.append(Constants.NEWLINE + Constants.NEWLINE + "Correct answer: " + correctAnswer.getLetter());

		return txtAreaStr.toString();
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
