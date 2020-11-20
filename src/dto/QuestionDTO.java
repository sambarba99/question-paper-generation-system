package dto;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.ListView;

import dao.QuestionDAO;
import dao.QuestionPaperDAO;
import dao.SubjectDAO;

import model.Question;
import model.QuestionPaper;
import model.Subject;

public class QuestionDTO {

	private QuestionDAO questionDao = new QuestionDAO();

	private QuestionPaperDAO questionPaperDao = new QuestionPaperDAO();

	private SubjectDAO subjectDao = new SubjectDAO();

	public QuestionDTO() {
	}

	/**
	 * Get a list of all questions for question ListView
	 * 
	 * @return list of all questions
	 */
	public List<String> getQuestionListViewItems() {
		List<Question> allQuestions = questionDao.getAllQuestions();
		List<String> listViewItems = allQuestions.stream().map(q -> (q.getStatement() + " (ID " + q.getId() + ")"))
				.collect(Collectors.toList());
		return listViewItems;
	}

	/**
	 * Get ID of selected question in list view
	 * 
	 * @param listViewQuestions - the ListView of questions
	 * @return question ID
	 */
	public int getQuestionId(ListView<String> listViewQuestions) {
		String question = listViewQuestions.getSelectionModel().getSelectedItem();
		if (question == null) {
			return 0;
		}
		String[] qSplit = question.split(" ");
		String questionIdStr = qSplit[qSplit.length - 1];
		questionIdStr = questionIdStr.replace(")", "");
		return Integer.parseInt(questionIdStr);
	}

	/**
	 * Get a formatted question string for question TextArea
	 * 
	 * @param id - the ID of the question to format
	 * @return question string
	 */
	public String getTxtAreaQuestionStr(int id) {
		Question question = questionDao.getQuestionById(id);
		Subject subject = subjectDao.getSubjectById(question.getSubjectId());
		List<String> answerOptions = question.getAnswerOptions();
		int correctAnswerOption = question.getCorrectAnswerOptionNum();
		List<QuestionPaper> papersContainingQuestion = questionPaperDao.getQuestionPapersByQuestionId(id);

		String txtAreaStr = "Subject: " + subject.getTitle() + " (ID " + subject.getId() + ")";
		txtAreaStr += "\nDifficulty level: " + question.getDifficultyLevel().toString();
		txtAreaStr += "\nMarks: " + question.getMarks();
		txtAreaStr += "\nTime required (mins): " + question.getTimeRequiredMins();
		if (papersContainingQuestion.isEmpty()) {
			txtAreaStr += "\nThere are no papers which contain this question";
		} else {
			txtAreaStr += "\nQuestion papers containing this question:";
			for (QuestionPaper qp : papersContainingQuestion) {
				txtAreaStr += "\n - " + qp.getTitle() + " (ID " + qp.getId() + ")";
			}
		}
		txtAreaStr += "\n\nStatement: " + question.getStatement();
		for (int i = 0; i < 4; i++) {
			txtAreaStr += "\nAnswer option " + (i + 1) + ": " + answerOptions.get(i);
		}
		txtAreaStr += "\nCorrect answer option: " + correctAnswerOption;

		return txtAreaStr;
	}
}