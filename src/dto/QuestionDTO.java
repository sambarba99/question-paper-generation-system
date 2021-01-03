package dto;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.ListView;

import service.QuestionPaperService;
import service.QuestionService;
import service.SubjectService;

import model.Question;
import model.QuestionPaper;
import model.Subject;

import utils.Constants;

/**
 * This class is a singleton which contains methods related to ListViews used to modify and view questions.
 *
 * @author Sam Barba
 */
public class QuestionDTO {

	private static QuestionDTO instance;

	/**
	 * Get a list of all questions for question ListView objects.
	 * 
	 * @return list of all questions
	 */
	public List<String> getQuestionListViewItems() {
		List<Question> allQuestions = QuestionService.getInstance().getAllQuestions();
		List<String> listViewItems = allQuestions.stream()
			.map(question -> (question.getStatement() + " (ID " + question.getId() + ")")).collect(Collectors.toList());
		return listViewItems;
	}

	/**
	 * Get ID of selected question in ListView.
	 * 
	 * @param listViewQuestions - the ListView of questions
	 * @return question ID
	 */
	public int getQuestionId(ListView<String> listViewQuestions) {
		String question = listViewQuestions.getSelectionModel().getSelectedItem();
		if (question == null) {
			return 0;
		}
		String[] qSplit = question.split(Constants.SPACE);
		String questionIdStr = qSplit[qSplit.length - 1];
		questionIdStr = questionIdStr.replace(")", "");
		return Integer.parseInt(questionIdStr);
	}

	/**
	 * Get a formatted question string for question TextArea.
	 * 
	 * @param id - the ID of the question to format
	 * @return question string
	 */
	public String getTxtAreaQuestionStr(int id) {
		Question question = QuestionService.getInstance().getQuestionById(id);
		Subject subject = SubjectService.getInstance().getSubjectById(question.getSubjectId());
		List<String> answerOptions = question.getAnswerOptions();
		int correctAnswerOption = question.getCorrectAnswerOptionNum();
		List<QuestionPaper> papersContainingQuestion = QuestionPaperService.getInstance()
			.getQuestionPapersByQuestionId(id);

		StringBuilder txtAreaStr = new StringBuilder();
		txtAreaStr.append("Subject: " + subject.getTitle() + " (ID " + subject.getId() + ")");
		txtAreaStr.append("\nDifficulty level: " + question.getDifficultyLevel().getStrVal());
		txtAreaStr.append("\nMarks: " + question.getMarks());
		txtAreaStr.append("\nTime required (mins): " + question.getTimeRequiredMins());
		if (papersContainingQuestion.isEmpty()) {
			txtAreaStr.append("\nThere are no papers which contain this question");
		} else {
			txtAreaStr.append("\nQuestion papers containing this question:");
			for (QuestionPaper questionPaper : papersContainingQuestion) {
				txtAreaStr.append("\n - " + questionPaper.getTitle() + " (ID " + questionPaper.getId() + ")");
			}
		}
		txtAreaStr.append("\n\nStatement: " + question.getStatement());
		for (int i = 0; i < Constants.ANSWERS_PER_QUESTION; i++) {
			txtAreaStr.append("\nAnswer option " + (i + 1) + ": " + answerOptions.get(i));
		}
		txtAreaStr.append("\nCorrect answer option: " + correctAnswerOption);

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
