package model.service;

import static org.junit.Assert.assertNotNull;

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

import view.utils.Constants;

/**
 * This class is a singleton, the use of which is to perform any functionality regarding questions.
 *
 * @author Sam Barba
 */
public class QuestionService {

	private static QuestionService instance;

	private QuestionDAO questionDao;

	private QuestionService(QuestionDAO questionDao) {
		assertNotNull(questionDao);
		this.questionDao = questionDao;
	}

	public synchronized static QuestionService getInstance() {
		if (instance == null) {
			instance = new QuestionService(QuestionDAO.getInstance());
		}
		return instance;
	}

	/**
	 * Add a question to the questions XML file.
	 * 
	 * @param question - the question to add
	 */
	public void addQuestion(Question question) {
		question.setDateCreated(LocalDateTime.now());
		questionDao.addQuestion(question);
	}

	/**
	 * Delete questions by their unique IDs.
	 * 
	 * @param ids - the IDs of the questions to delete
	 */
	public void deleteQuestionsByIds(List<Integer> ids) {
		questionDao.deleteQuestionsByIds(ids);
	}

	/**
	 * Retrieve all questions from questions XML file.
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
	public Optional<Question> getQuestionById(int id) {
		return getAllQuestions().stream()
			.filter(q -> q.getId() == id)
			.findFirst();
	}

	/**
	 * Retrieve all questions with a subject ID.
	 * 
	 * @param subjectId - the subject ID of the questions
	 * @return list of questions with specified subject ID
	 */
	public List<Question> getQuestionsBySubjectId(int subjectId) {
		return getAllQuestions().stream()
			.filter(q -> q.getSubjectId() == subjectId)
			.collect(Collectors.toList());
	}

	/**
	 * Get a new question ID, to be used when adding a new question to ensure uniqueness.
	 * 
	 * @return highest existing question ID
	 */
	public int getNewQuestionId() {
		List<Question> allQuestions = getAllQuestions();
		return allQuestions.isEmpty() ? 1
			: allQuestions.stream().max(Comparator.comparing(Question::getId)).get().getId() + 1;
	}

	/**
	 * Get all questions converted to DTOs for using in TableViews, with applied subject and skill
	 * level filters (if any selected in QuestionManagement).
	 * 
	 * @param skillLvls          - skill level IDs to filter by
	 * @param subjectIds         - subject IDs to filter by
	 * @param statementSubstring - statement substring to filter by
	 * @return (filtered) list of all questions as DTOs
	 */
	public List<QuestionDTO> getQuestionDTOsWithFilters(List<Integer> skillLvls, List<Integer> subjectIds,
		String statementSubstring) {

		/*
		 * If a list is empty, say skillLvls, then it means the user does not want to filter by
		 * skill level. This is why 'skillLvls.isEmpty()' is in a logical disjunction (||).
		 */
		return getAllQuestions().stream()
			.filter(q -> skillLvls.isEmpty() || skillLvls.contains(q.getSkillLevel().getIntVal()))
			.filter(q -> subjectIds.isEmpty() || subjectIds.contains(q.getSubjectId()))
			.filter(q -> statementSubstring.isEmpty()
				|| q.getStatement().toLowerCase().contains(statementSubstring.toLowerCase()))
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
		String subjectTitle = subjectOpt.isPresent() ? subjectOpt.get().getTitle() : Constants.SUBJECT_DELETED;

		QuestionDTO questionDto = new QuestionDTO();
		questionDto.setId(question.getId());
		questionDto.setSubjectTitle(subjectTitle);
		questionDto.setStatement(question.getStatement());
		questionDto.setSkillLevel(question.getSkillLevel().toString());
		questionDto.setMarks(question.getMarks());
		questionDto.setMinutesRequired(question.getMinutesRequired());
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

		List<Answer> answers = question.getAnswers();
		List<QuestionPaper> papersContainingQuestion = QuestionPaperService.getInstance()
			.getQuestionPapersByQuestionId(id);

		StringBuilder txtAreaStr = new StringBuilder();
		if (papersContainingQuestion.isEmpty()) {
			txtAreaStr.append("There are no papers which contain this question.\n");
		} else {
			txtAreaStr.append("Question papers containing this question:\n");
			for (QuestionPaper questionPaper : papersContainingQuestion) {
				txtAreaStr.append("- " + questionPaper.toString() + "\n");
			}
		}
		txtAreaStr.append("\n" + question.getStatement() + "\n");
		int correctAns = 0;
		for (int i = 0; i < answers.size(); i++) {
			Answer ans = answers.get(i);
			txtAreaStr.append("\n(" + ((char) (Constants.ASCII_UPPER_A + i)) + ") " + ans.getValue());
			if (ans.isCorrect()) {
				correctAns = i;
			}
		}
		txtAreaStr.append("\n\nCorrect answer: " + ((char) (Constants.ASCII_UPPER_A + correctAns)));

		return txtAreaStr.toString();
	}
}
