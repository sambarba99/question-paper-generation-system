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

import view.enums.BloomSkillLevel;
import view.utils.Constants;

/**
 * Generates a question paper with specified parameters using a GA.
 *
 * @author Sam Barba
 */
public class QuestionPaperGenerator {

	private static final Logger LOGGER = Logger.getLogger(QuestionPaperGenerator.class.getName());

	private static QuestionPaperGenerator instance;

	private QuestionPaperGenerator() {
	}

	public synchronized static QuestionPaperGenerator getInstance() {
		if (instance == null) {
			instance = new QuestionPaperGenerator();
		}
		return instance;
	}

	/**
	 * Generate a question paper with the GA, then create the persisted object with the
	 * user-specified parameters.
	 * 
	 * @param questions    - the list of possible questions to include in the paper
	 * @param subjectId    - the subject ID of the paper
	 * @param title        - the title of the paper
	 * @param courseTitle  - the course title of the paper
	 * @param courseCode   - the course code of the paper
	 * @param skillLevel   - the mean skill level of the paper
	 * @param minsRequired - the approximate minutes required the user wants for the paper
	 * @return a generated question paper
	 */
	public Optional<QuestionPaper> generatePaper(List<Question> questions, int subjectId, String title,
		String courseTitle, String courseCode, BloomSkillLevel skillLevel, int minsRequired) throws IOException {

		LOGGER.info("Generating question paper...");

		FileWriter writer;
		if (Constants.TEST_MODE) {
			writer = new FileWriter(Constants.GENETIC_ALGORITHM_TEST_RESULTS);
			writer.append("Generation,Population mean fitness,Highest,Lowest\n");
		}

		GAUtils gaUtils = GAUtils.getInstance();

		long startTime = System.currentTimeMillis();

		int numGenes = gaUtils.calculateChromosomeLength(questions, skillLevel.getIntVal(), minsRequired);

		LOGGER.info("No. questions: " + numGenes);

		Individual[] population = gaUtils.initialiseIndividualArray(skillLevel.getIntVal(), minsRequired);
		Individual[] offspring = gaUtils.initialiseIndividualArray(skillLevel.getIntVal(), minsRequired);

		gaUtils.randomisePopulationGenes(population, numGenes, questions);
		gaUtils.evaluate(population);

		for (int g = 1; g <= Constants.GENERATIONS; g++) {
			gaUtils.selection(population, offspring);
			gaUtils.evaluate(offspring);

			gaUtils.crossover(offspring, skillLevel.getIntVal(), minsRequired);
			gaUtils.evaluate(offspring);

			gaUtils.mutation(offspring, questions);
			gaUtils.evaluate(offspring);

			/*
			 * In this final selection step, the next population is defined using the new offspring,
			 * so 'population' and 'offspring' are switched round when calling the function.
			 */
			gaUtils.selection(offspring, population);
			gaUtils.evaluate(population);

			List<Double> meanHiLo = gaUtils.getTableFitnesses(population);
			if (Constants.TEST_MODE) {
				writer.append(g + ",");
				writer.append(Double.toString(meanHiLo.get(0)) + ",");
				writer.append(Double.toString(meanHiLo.get(1)) + ",");
				writer.append(Double.toString(meanHiLo.get(2)) + "\n");

				LOGGER.info("Generation: " + g + " / " + Constants.GENERATIONS + " mean fitness: " + meanHiLo.get(0)
					+ " highest: " + meanHiLo.get(1) + " lowest: " + meanHiLo.get(2));
			}
		}
		if (Constants.TEST_MODE) {
			writer.flush();
			writer.close();
		}

		Individual fittest = gaUtils.findFittest(population);
		QuestionPaper questionPaper = makePaperOutOfFittest(fittest, subjectId, title, courseTitle, courseCode,
			skillLevel);

		long finishTime = System.currentTimeMillis();

		LOGGER.info("Question paper generated in " + (finishTime - startTime) + " ms");
		return Optional.of(questionPaper);
	}

	/**
	 * Create a QuestionPaper object in order to write it to the papers XML file, out of an
	 * Individual and other question paper parameters.
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
		String courseCode, BloomSkillLevel skillLevel) {

		int id = QuestionPaperService.getInstance().getNewQuestionPaperId();

		// sort questions in ascending order of marks, meaning longer questions appear towards the end
		List<Question> questions = fittest.getGenes();
		questions.sort(Comparator.comparing(Question::getMarks));

		List<Integer> questionIds = questions.stream().map(Question::getId).collect(Collectors.toList());

		int marks = questions.stream().mapToInt(Question::getMarks).reduce(0, Integer::sum);
		int minsRequired = questions.stream().mapToInt(Question::getMinutesRequired).reduce(0, Integer::sum);

		return new QuestionPaperBuilder()
			.withId(id)
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
}
