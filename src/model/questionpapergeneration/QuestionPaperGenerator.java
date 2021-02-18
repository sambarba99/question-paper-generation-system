package model.questionpapergeneration;

import java.util.List;
import java.util.stream.Collectors;

import model.persisted.Question;
import model.persisted.QuestionPaper;
import model.service.QuestionPaperService;
import model.service.QuestionService;

import view.enums.DifficultyLevel;

/**
 * Generates a question paper with specified parameters using a GA.
 *
 * @author Sam Barba
 */
public class QuestionPaperGenerator {

	private static QuestionPaperGenerator instance;

	public QuestionPaper generatePaper(int subjectId, String title, String courseTitle, String courseCode,
		DifficultyLevel difficultyLevel, int marks, int timeRequiredMins) {
		int id = QuestionPaperService.getInstance().getHighestQuestionPaperId() + 1;
		List<Integer> questionIds = QuestionService.getInstance().getAllQuestions().stream().map(Question::getId)
			.collect(Collectors.toList());

		QuestionPaper questionPaper = new QuestionPaper(id, subjectId, title, courseTitle, courseCode, questionIds,
			difficultyLevel, marks, timeRequiredMins);
		return questionPaper;
	}

	public synchronized static QuestionPaperGenerator getInstance() {
		if (instance == null) {
			instance = new QuestionPaperGenerator();
		}
		return instance;
	}

	private QuestionPaperGenerator() {
	}
}
