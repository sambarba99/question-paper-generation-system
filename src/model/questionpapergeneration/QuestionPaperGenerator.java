package model.questionpapergeneration;

import java.io.FileWriter;
import java.io.IOException;
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

	/**
	 * Generate a question paper with the GA, then create the persisted object with the specified parameters.
	 * 
	 * @param subjectId        - the subject ID of the paper
	 * @param title            - the title of the paper
	 * @param courseTitle      - the course title of the paper
	 * @param courseCode       - the course code of the paper
	 * @param skillLevel       - the mean skill level of the paper
	 * @param marks            - the approximate no. marks the user wants for the paper
	 * @param timeRequiredMins - the approximate time required the user wants for the paper
	 * @return a generated question paper
	 */
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

		int geneSize = determineGeneSize(questions);

		Individual[] population = gaUtils.initialiseIndividualArray(Constants.POP_SIZE, geneSize);
		Individual[] offspring = gaUtils.initialiseIndividualArray(Constants.POP_SIZE, geneSize);
		gaUtils.randomisePopulationGenes(population, geneSize, questions);

		for (int g = 1; g <= Constants.GENERATIONS; g++) {
			// initial selection = true
			gaUtils.selection(population, offspring, Constants.SELECTION_TYPE, Constants.TOURNAMENT_SIZE, true);

			gaUtils.crossover(population, offspring, geneSize, Constants.CROSSOVER_RATE);

			gaUtils.mutation(offspring, geneSize, Constants.MUTATION_RATE);

			// initial selection = false
			gaUtils.selection(population, offspring, Constants.SELECTION_TYPE, Constants.TOURNAMENT_SIZE, false);

			List<Double> meanHiLo = gaUtils.calculateTableFitnesses(population);
			csvWriter.append(g + Constants.COMMA);
			csvWriter.append(Double.toString(meanHiLo.get(0)) + Constants.COMMA);
			csvWriter.append(Double.toString(meanHiLo.get(1)) + Constants.COMMA);
			csvWriter.append(Double.toString(meanHiLo.get(2)) + Constants.NEWLINE);
		}
		csvWriter.flush();
		csvWriter.close();

		Individual fittest = gaUtils.findFittest(population);
		QuestionPaper questionPaper = makePaperOutOfIndividual(fittest, subjectId, title, courseTitle, courseCode);

		LOGGER.info("Question paper generated");
		return questionPaper;
	}

	/**
	 * Determine the optimal number of questions in a question paper, given list of possible questions to use.
	 * 
	 * @param questions - list of possible questions to include in paper
	 * @return calculated number of questions
	 */
	private int determineGeneSize(List<Question> questions) {
		double totalMarks = questions.stream().mapToDouble(Question::getMarks).reduce(0, Double::sum);
		double totalTimeRequired = questions.stream().mapToDouble(Question::getTimeRequiredMins).reduce(0, Double::sum);
		double totalSkillLvl = questions.stream()
			.mapToDouble(q -> q.getSkillLevel().getIntVal())
			.reduce(0, Double::sum);

		double meanMarks = totalMarks / questions.size();
		double meanTimeReq = totalTimeRequired / questions.size();
		double meanSkillLvl = totalSkillLvl / questions.size();

		// this determines the number of questions to have, given the mean number of marks per question
		double numQsGivenAvMarks = totalMarks / meanMarks;
		// this determines the number of questions to have, given the mean time taken (mins) per question
		double numQsGivenAvTimeReq = totalTimeRequired / meanTimeReq;
		// this determines the number of questions to have, given the mean skill level per question
		double numQsGivenAvSkillLvl = totalSkillLvl / meanSkillLvl;

		// we need the geometric mean of the 3 calculated doubles, which is the cube root of their product
		return (int) Math.round(Math.cbrt(numQsGivenAvMarks * numQsGivenAvTimeReq * numQsGivenAvSkillLvl));
	}

	/**
	 * Create a QuestionPaper object in order to write it to the papers CSV file, out of an Individual and other
	 * question paper parameters.
	 * 
	 * @param individual  - the individual to use, produced by the genetic algorithm
	 * @param subjectId   - the subject ID of the paper
	 * @param title       - the title of the paper
	 * @param courseTitle - the course title of the paper
	 * @param courseCode  - the course code of the paper
	 * @return the equivalent QuestionPaper object
	 */
	private QuestionPaper makePaperOutOfIndividual(Individual individual, int subjectId, String title,
		String courseTitle, String courseCode) {

		int id = QuestionPaperService.getInstance().getHighestQuestionPaperId() + 1;

		List<Question> questions = individual.getGene();
		List<Integer> questionIds = questions.stream().map(Question::getId).collect(Collectors.toList());

		int meanSkillLvlInt = (int) Math
			.round(questions.stream().mapToInt(q -> q.getSkillLevel().getIntVal()).average().getAsDouble());
		SkillLevel skillLevel = SkillLevel.getFromInt(meanSkillLvlInt);

		int totalMarks = questions.stream().map(Question::getMarks).reduce(0, Integer::sum);
		int totalTimeReq = questions.stream().mapToInt(Question::getTimeRequiredMins).reduce(0, Integer::sum);

		return new QuestionPaperBuilder().withId(id)
			.withSubjectId(subjectId)
			.withTitle(title)
			.withCourseTitle(courseTitle)
			.withCourseCode(courseCode)
			.withQuestionIds(questionIds)
			.withSkillLevel(skillLevel)
			.withMarks(totalMarks)
			.withTimeRequiredMins(totalTimeReq)
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
