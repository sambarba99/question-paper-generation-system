package questionpapergeneration;

import java.util.List;

import model.QuestionPaper;
import model.enums.DifficultyLevel;

/**
 * Represents an individual question paper.
 *
 * @author Sam Barba
 */
public class Individual extends QuestionPaper {

	public Individual(int id, int subjectId, String title, String courseTitle, String courseCode,
			List<Integer> questionIds, DifficultyLevel difficultyLevel, int marks, int timeRequiredMins) {
		super(id, subjectId, title, courseTitle, courseCode, questionIds, difficultyLevel, marks, timeRequiredMins);
	}
}
