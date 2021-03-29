package model.questionpapergeneration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import model.builders.QuestionPaperBuilder;
import model.persisted.Question;
import model.persisted.QuestionPaper;
import model.service.QuestionPaperService;
import model.service.QuestionService;
import model.service.SubjectService;

import view.SystemNotification;
import view.enums.SkillLevel;
import view.enums.SystemNotificationType;
import view.utils.Constants;

/**
 * Generates a question paper with specified parameters using a GA.
 *
 * @author Sam Barba
 */
public class QuestionPaperGenerator {

	private static final Logger LOGGER = Logger.getLogger(QuestionPaperGenerator.class.getName());

	private static QuestionPaperGenerator instance;

	/**
	 * Generate a question paper with the GA, then create the persisted object with the user-specified parameters.
	 * 
	 * @param subjectId    - the subject ID of the paper
	 * @param title        - the title of the paper
	 * @param courseTitle  - the course title of the paper
	 * @param courseCode   - the course code of the paper
	 * @param skillLevel   - the mean skill level of the paper
	 * @param minsRequired - the approximate minutes required the user wants for the paper
	 * @return a generated question paper
	 */
	public Optional<QuestionPaper> generatePaper(int subjectId, String title, String courseTitle, String courseCode,
		SkillLevel skillLevel, int minsRequired) throws IOException {

		LOGGER.info("Generating question paper...");

		FileWriter csvWriter = new FileWriter(Constants.GENETIC_ALGORITHM_TEST_RESULTS);
		csvWriter.append("Generation,Population mean fitness,Highest,Lowest" + Constants.NEWLINE);

		GAUtils gaUtils = GAUtils.getInstance();

		// get questions by user-selected subject; 'fit' questions are already identified with this parameter
		List<Question> questions = QuestionService.getInstance()
			.getAllQuestions()
			.stream()
			.filter(q -> q.getSubjectId() == subjectId)
			.collect(Collectors.toList());

		if (questions.size() < Constants.MIN_QUESTIONS_PER_SUBJECT) {
			SystemNotification.display(SystemNotificationType.ERROR,
				"Insufficient questions for this subject:" + Constants.NEWLINE
					+ SubjectService.getInstance().getSubjectById(subjectId).get().getTitle() + " (ID " + subjectId
					+ ")" + Constants.NEWLINE + "Subjects require at least " + Constants.MIN_QUESTIONS_PER_SUBJECT
					+ " questions to generate a paper.");

			return Optional.empty();
		}

		int numGenes = gaUtils.calculateChromosomeLength(questions, skillLevel.getIntVal(), minsRequired);

		LOGGER.info("No. questions: " + numGenes);

		Individual[] population = gaUtils.initialiseIndividualArray(skillLevel.getIntVal(), minsRequired);
		Individual[] offspring = gaUtils.initialiseIndividualArray(skillLevel.getIntVal(), minsRequired);

		gaUtils.randomisePopulationGenes(population, numGenes, questions);

		for (int g = 1; g <= Constants.GENERATIONS; g++) {
			gaUtils.selection(population, offspring);

			gaUtils.crossover(offspring, skillLevel.getIntVal(), minsRequired);

			gaUtils.mutation(offspring, questions);

			/*
			 * In this final selection step, the next population is defined using the new offspring, so 'population' and
			 * 'offspring' are switched round when calling the function.
			 */
			gaUtils.selection(offspring, population);

			List<Double> meanHiLo = gaUtils.getTableFitnesses(population);
			csvWriter.append(g + Constants.COMMA);
			csvWriter.append(Double.toString(meanHiLo.get(0)) + Constants.COMMA);
			csvWriter.append(Double.toString(meanHiLo.get(1)) + Constants.COMMA);
			csvWriter.append(Double.toString(meanHiLo.get(2)) + Constants.NEWLINE);
		}
		csvWriter.flush();
		csvWriter.close();

		Individual fittest = gaUtils.findFittest(population);
		QuestionPaper questionPaper = makePaperOutOfFittest(fittest, subjectId, title, courseTitle, courseCode,
			skillLevel);

		LOGGER.info("Question paper generated");
		return Optional.of(questionPaper);
	}

	/**
	 * Create a QuestionPaper object in order to write it to the papers CSV file, out of an Individual and other
	 * question paper parameters.
	 * 
	 * @param fittest     - the fittest individual to use, produced by the genetic algorithm
	 * @param subjectId   - the subject ID of the paper
	 * @param title       - the title of the paper
	 * @param courseTitle - the course title of the paper
	 * @param courseCode  - the course code of the paper
	 * @param skillLevel  - the skill level of the paper
	 * @return the equivalent QuestionPaper object
	 */
	private QuestionPaper makePaperOutOfFittest(Individual fittest, int subjectId, String title, String courseTitle,
		String courseCode, SkillLevel skillLevel) {

		int id = QuestionPaperService.getInstance().getHighestQuestionPaperId() + 1;

		// sort questions in ascending order of marks, meaning harder questions appear towards the end
		fittest.getGenes().sort(Comparator.comparing(Question::getMarks));

		List<Integer> questionIds = fittest.getGenes().stream().map(Question::getId).collect(Collectors.toList());

		int marks = fittest.getGenes().stream().mapToInt(Question::getMarks).reduce(0, Integer::sum);
		int minsRequired = fittest.getGenes().stream().mapToInt(Question::getMinutesRequired).reduce(0, Integer::sum);

		return new QuestionPaperBuilder().withId(id)
			.withSubjectId(subjectId)
			.withTitle(title)
			.withCourseTitle(courseTitle)
			.withCourseCode(courseCode)
			.withQuestionIds(questionIds)
			.withSkillLevel(skillLevel)
			.withMarks(marks)
			.withMinutesRequired(minsRequired)
			.build();
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
