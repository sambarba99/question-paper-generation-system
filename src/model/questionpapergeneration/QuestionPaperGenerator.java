package model.questionpapergeneration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

	public QuestionPaper generatePaper(int subjectId, String title, String courseTitle, String courseCode,
		SkillLevel skillLevel, int marks, int timeRequiredMins) throws IOException {

		LOGGER.info("Generating question paper...");

		FileWriter csvWriter = new FileWriter(Constants.GENETIC_ALGORITHM_RESULTS_PATH);
		csvWriter.append("Generation,Population mean fitness,Highest,Lowest" + Constants.NEWLINE);

		GAUtils gaUtils = GAUtils.getInstance();

		// get questions by user-selected subject, so we already identify 'fit' questions with this parameter
		List<Question> questions = QuestionService.getInstance()
			.getAllQuestions()
			.stream()
			.filter(q -> q.getSubjectId() == subjectId)
			.collect(Collectors.toList());

		Individual[] population = gaUtils.initialiseIndividualArray(Constants.POP_SIZE, Constants.GENE_SIZE);
		Individual[] offspring = gaUtils.initialiseIndividualArray(Constants.POP_SIZE, Constants.GENE_SIZE);
		gaUtils.randomisePopulationGenes(population, questions);

		for (int g = 1; g <= Constants.GENERATIONS; g++) {
			// initial selection = true
			gaUtils.selection(population, offspring, Constants.SELECTION_TYPE, Constants.TOURNAMENT_SIZE, true);

			gaUtils.crossover(population, offspring, Constants.CROSSOVER_RATE, Constants.CROSSOVER_POINTS);

			gaUtils.mutation(offspring, Constants.MUTATION_RATE);

			// initial selection = false
			gaUtils.selection(population, offspring, Constants.SELECTION_TYPE, Constants.TOURNAMENT_SIZE, false);

			List<Double> averageHiLo = gaUtils.calculateTableFitnesses(population);
			csvWriter.append(g + Constants.COMMA);
			csvWriter.append(Double.toString(averageHiLo.get(0)) + Constants.COMMA);
			csvWriter.append(Double.toString(averageHiLo.get(1)) + Constants.COMMA);
			csvWriter.append(Double.toString(averageHiLo.get(2)) + Constants.NEWLINE);
		}
		csvWriter.flush();
		csvWriter.close();

		Individual fittest = gaUtils.findFittest(population);
		QuestionPaper questionPaper = makePaperOutOfIndividual(fittest, subjectId, title, courseTitle, courseCode,
			skillLevel, marks, timeRequiredMins);

		LOGGER.info("Question paper generated");
		return questionPaper;
	}

	private QuestionPaper makePaperOutOfIndividual(Individual individual, int subjectId, String title,
		String courseTitle, String courseCode, SkillLevel skillLevel, int marks, int timeRequiredMins) {

		int id = QuestionPaperService.getInstance().getHighestQuestionPaperId() + 1;

		List<Integer> questionIds = Arrays.asList(individual.getGene())
			.stream()
			.map(Question::getId)
			.collect(Collectors.toList());

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
