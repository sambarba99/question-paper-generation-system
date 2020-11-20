package questionpapergeneration;

import java.util.List;
import java.util.stream.Collectors;

import dao.QuestionDAO;
import dao.QuestionPaperDAO;

import model.QuestionPaper;
import model.enums.DifficultyLevel;

public class QuestionPaperGenerator {

	private QuestionPaperDAO questionPaperDao = new QuestionPaperDAO();

	private QuestionDAO questionDao = new QuestionDAO();

	public QuestionPaperGenerator() {
	}

	public QuestionPaper generatePaper(int subjectId, String title, String courseTitle, String courseCode,
			DifficultyLevel difficultyLevel, int marks, int timeRequiredMins) {
		int id = questionPaperDao.getHighestQuestionPaperId() + 1;
		List<Integer> questionIds = questionDao.getAllQuestions().stream().map(q -> q.getId())
				.collect(Collectors.toList());

		QuestionPaper qp = new QuestionPaper(id, subjectId, title, courseTitle, courseCode, questionIds,
				difficultyLevel, marks, timeRequiredMins);
		return qp;
	}
}