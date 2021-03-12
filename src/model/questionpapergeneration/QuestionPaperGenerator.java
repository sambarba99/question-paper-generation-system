package model.questionpapergeneration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.builders.QuestionPaperBuilder;
import model.persisted.Question;
import model.persisted.QuestionPaper;
import model.service.QuestionPaperService;
import model.service.QuestionService;

import view.Constants;
import view.enums.DifficultyLevel;

/**
 * Generates a question paper with specified parameters using a GA.
 *
 * @author Sam Barba
 */
public class QuestionPaperGenerator {

	private static QuestionPaperGenerator instance;

	public QuestionPaper generatePaper(int subjectId, String title, String courseTitle, String courseCode,
		DifficultyLevel difficultyLevel, int marks, int timeRequiredMins) throws IOException {

		FileWriter csvWriter = new FileWriter(Constants.GENETIC_ALGORITHM_RESULTS_PATH);
		csvWriter.append("Generation,Population mean fitness,Highest,Lowest\n");

		GAUtils gaUtils = GAUtils.getInstance();

		List<Question> allQuestions = QuestionService.getInstance().getAllQuestions();

		Individual[] population = gaUtils.initialiseIndividualArray(Constants.POP_SIZE, Constants.NUM_GENES);
		Individual[] offspring = gaUtils.initialiseIndividualArray(Constants.POP_SIZE, Constants.NUM_GENES);
		gaUtils.randomisePopulationGenes(population, allQuestions);

		for (int g = 1; g <= Constants.GENERATIONS; g++) {
			if (g % 10 == 0) {
				System.out.println("Generation: " + g);
			}

			// initial selection = true
			gaUtils.selection(population, offspring, Constants.SELECTION_TYPE, Constants.TOURNAMENT_SIZE, true);

			gaUtils.crossover(population, offspring, Constants.CROSSOVER_RATE, Constants.CROSSOVER_POINTS);

			gaUtils.mutation(offspring, Constants.MUTATION_RATE);

			// initial selection = false
			gaUtils.selection(population, offspring, Constants.SELECTION_TYPE, Constants.TOURNAMENT_SIZE, false);

			List<Double> averageHiLo = gaUtils.calculateTableFitnesses(population);
			csvWriter.append(g + ",");
			csvWriter.append(Double.toString(averageHiLo.get(0)) + ",");
			csvWriter.append(Double.toString(averageHiLo.get(1)) + ",");
			csvWriter.append(Double.toString(averageHiLo.get(2)) + "\n");
		}
		csvWriter.flush();
		csvWriter.close();

		Individual fittest = gaUtils.findFittest(population);
		QuestionPaper questionPaper = makePaperOutOfIndividual(fittest, subjectId, title, courseTitle, courseCode,
			difficultyLevel, marks, timeRequiredMins);

		return questionPaper;
	}

	public QuestionPaper makePaperOutOfIndividual(Individual individual, int subjectId, String title,
		String courseTitle, String courseCode, DifficultyLevel difficultyLevel, int marks, int timeRequiredMins) {

		int id = QuestionPaperService.getInstance().getHighestQuestionPaperId() + 1;

		List<Question> questions = Arrays.asList(individual.getGenes());

		List<Integer> questionIds = questions.stream().map(Question::getId).collect(Collectors.toList());

		return new QuestionPaperBuilder().withId(id).withSubjectId(subjectId).withTitle(title)
			.withCourseTitle(courseTitle).withCourseCode(courseCode).withQuestionIds(questionIds)
			.withDifficultyLevel(difficultyLevel).withMarks(marks).withTimeRequiredMins(timeRequiredMins).build();
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
