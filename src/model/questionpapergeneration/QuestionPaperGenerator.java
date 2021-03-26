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

import view.enums.SkillLevel;
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
	 * @param subjectId        - the subject ID of the paper
	 * @param title            - the title of the paper
	 * @param courseTitle      - the course title of the paper
	 * @param courseCode       - the course code of the paper
	 * @param skillLevel       - the mean skill level of the paper
	 * @param timeRequiredMins - the approximate time required the user wants for the paper
	 * @return a generated question paper
	 */
	public Optional<QuestionPaper> generatePaper(int subjectId, String title, String courseTitle, String courseCode,
		SkillLevel skillLevel, int timeRequiredMins) throws IOException {

		LOGGER.info("Generating question paper...");

		FileWriter csvWriter = new FileWriter(Constants.GENETIC_ALGORITHM_TEST_RESULTS_PATH);
		csvWriter.append("Generation,Population mean fitness,Highest,Lowest" + Constants.NEWLINE);

		GAUtils gaUtils = GAUtils.getInstance();

		// get questions by user-selected subject, so we already identify 'fit' questions with this parameter
		List<Question> questions = QuestionService.getInstance()
			.getAllQuestions()
			.stream()
			.filter(q -> q.getSubjectId() == subjectId)
			.collect(Collectors.toList());

		int numGenes = gaUtils.calculateChromosomeSize(questions);

		Individual[] population = gaUtils.initialiseIndividualArray(Constants.POP_SIZE, numGenes,
			skillLevel.getIntVal(), timeRequiredMins);
		Individual[] offspring = gaUtils.initialiseIndividualArray(Constants.POP_SIZE, numGenes, skillLevel.getIntVal(),
			timeRequiredMins);
		gaUtils.randomisePopulationGenes(population, numGenes, questions);

		for (int g = 1; g <= Constants.GENERATIONS; g++) {
			// initial selection = true
			gaUtils.selection(population, offspring, Constants.SELECTION_TYPE, Constants.TOURNAMENT_SIZE, true);

			gaUtils.crossover(offspring, Constants.CROSSOVER_RATE, skillLevel.getIntVal(), timeRequiredMins);

			gaUtils.mutation(offspring, numGenes, Constants.MUTATION_RATE, questions);

			// initial selection = false
			gaUtils.selection(population, offspring, Constants.SELECTION_TYPE, Constants.TOURNAMENT_SIZE, false);

			List<Double> meanHiLo = gaUtils.getTableFitnesses(population);
			csvWriter.append(g + Constants.COMMA);
			csvWriter.append(Double.toString(meanHiLo.get(0)) + Constants.COMMA);
			csvWriter.append(Double.toString(meanHiLo.get(1)) + Constants.COMMA);
			csvWriter.append(Double.toString(meanHiLo.get(2)) + Constants.NEWLINE);
		}
		csvWriter.flush();
		csvWriter.close();

		Individual fittest = gaUtils.findFittest(population);
		QuestionPaper questionPaper = makePaperOutOfIndividual(fittest, subjectId, title, courseTitle, courseCode,
			skillLevel, timeRequiredMins);

		LOGGER.info("Question paper generated");
		return Optional.of(questionPaper);
	}

	/**
	 * Create a QuestionPaper object in order to write it to the papers CSV file, out of an Individual and other
	 * question paper parameters.
	 * 
	 * @param individual       - the individual to use, produced by the genetic algorithm
	 * @param subjectId        - the subject ID of the paper
	 * @param title            - the title of the paper
	 * @param courseTitle      - the course title of the paper
	 * @param courseCode       - the course code of the paper
	 * @param skillLevel       - the skill level of the paper
	 * @param timeRequiredMins - the time required to complete the paper
	 * @return the equivalent QuestionPaper object
	 */
	private QuestionPaper makePaperOutOfIndividual(Individual individual, int subjectId, String title,
		String courseTitle, String courseCode, SkillLevel skillLevel, int timeRequiredMins) {

		int id = QuestionPaperService.getInstance().getHighestQuestionPaperId() + 1;

		// sort questions in ascending order of marks, meaning harder questions appear towards the end
		individual.getGenes().sort(Comparator.comparing(Question::getMarks));

		List<Integer> questionIds = individual.getGenes().stream().map(Question::getId).collect(Collectors.toList());

		int marks = individual.getGenes().stream().mapToInt(Question::getMarks).reduce(0, Integer::sum);

		return new QuestionPaperBuilder().withId(id)
			.withSubjectId(subjectId)
			.withTitle(title)
			.withCourseTitle(courseTitle)
			.withCourseCode(courseCode)
			.withQuestionIds(questionIds)
			.withSkillLevel(skillLevel)
			.withMarks(marks)
			.withTimeRequiredMins(timeRequiredMins)
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
