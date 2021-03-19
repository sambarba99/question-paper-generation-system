package model.dto;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.scene.control.ListView;

import model.persisted.Answer;
import model.persisted.Question;
import model.persisted.QuestionPaper;
import model.persisted.Subject;
import model.service.QuestionPaperService;
import model.service.QuestionService;
import model.service.SubjectService;

import view.utils.Constants;

/**
 * This class is a singleton which contains methods related to ListViews used to modify and view questions.
 *
 * @author Sam Barba
 */
public class QuestionDTO {

	public static final Logger LOGGER = Logger.getLogger(QuestionDTO.class.getName());

	private static QuestionDTO instance;

	/**
	 * Get a list of all questions for question ListView objects.
	 * 
	 * @return list of all questions
	 */
	public List<String> getQuestionListViewItems() {
		LOGGER.info("Retrieving questions for ListView");
		List<Question> allQuestions = QuestionService.getInstance().getAllQuestions();
		List<String> listViewItems = allQuestions.stream()
			.map(question -> (question.getStatement() + " (ID " + question.getId() + ")"))
			.collect(Collectors.toList());
		return listViewItems;
	}

	/**
	 * Get ID of selected question in ListView.
	 * 
	 * @param listViewQuestions - the ListView of questions
	 * @return question ID
	 */
	public int getQuestionId(ListView<String> listViewQuestions) {
		LOGGER.info("Retrieving selected question ID from ListView");
		String question = listViewQuestions.getSelectionModel().getSelectedItem();
		if (question == null) {
			return 0;
		}
		String[] qSplit = question.split(Constants.SPACE);
		String questionIdStr = qSplit[qSplit.length - 1];
		questionIdStr = questionIdStr.replace(")", Constants.EMPTY);
		return Integer.parseInt(questionIdStr);
	}

	/**
	 * Get a formatted question string for question TextArea.
	 * 
	 * @param id - the ID of the question to format
	 * @return question string
	 */
	public String getTxtAreaQuestionStr(int id) {
		LOGGER.info("Formatting question data for TextArea");

		Question question = QuestionService.getInstance().getQuestionById(id);
		Subject subject = SubjectService.getInstance().getSubjectById(question.getSubjectId());
		List<Answer> answers = question.getAnswers();
		Answer correctAnswer = answers.stream().filter(Answer::isCorrect).findFirst().orElse(null);
		List<QuestionPaper> papersContainingQuestion = QuestionPaperService.getInstance()
			.getQuestionPapersByQuestionId(id);

		StringBuilder txtAreaStr = new StringBuilder();
		txtAreaStr.append("Subject: " + subject.getTitle() + " (ID " + subject.getId() + ")");
		txtAreaStr.append(Constants.NEWLINE + "Difficulty level: " + question.getDifficultyLevel().getIntVal() + " ("
			+ question.getDifficultyLevel().getStrVal() + ")");
		txtAreaStr.append(Constants.NEWLINE + "Marks: " + question.getMarks());
		txtAreaStr.append(Constants.NEWLINE + "Time required (mins): " + question.getTimeRequiredMins());
		if (papersContainingQuestion.isEmpty()) {
			txtAreaStr.append(Constants.NEWLINE + "There are no papers which contain this question.");
		} else {
			txtAreaStr.append(Constants.NEWLINE + "Question papers containing this question:");
			for (QuestionPaper questionPaper : papersContainingQuestion) {
				txtAreaStr.append(
					Constants.NEWLINE + "- " + questionPaper.getTitle() + " (ID " + questionPaper.getId() + ")");
			}
		}
		txtAreaStr.append(Constants.NEWLINE + Constants.NEWLINE + question.getStatement());
		for (Answer answer : answers) {
			txtAreaStr.append(Constants.NEWLINE + "(" + answer.getLetter() + ") " + answer.getValue());
		}
		txtAreaStr.append(Constants.NEWLINE + "Correct answer: " + correctAnswer.getLetter());

		return txtAreaStr.toString();
	}

	public synchronized static QuestionDTO getInstance() {
		if (instance == null) {
			instance = new QuestionDTO();
		}
		return instance;
	}

	private QuestionDTO() {
	}
}
