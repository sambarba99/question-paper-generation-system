package model.rawquestiontransformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;

import model.builders.AnswerBuilder;
import model.builders.QuestionBuilder;
import model.builders.SubjectBuilder;
import model.persisted.Answer;
import model.persisted.Question;
import model.service.QuestionService;
import model.service.SubjectService;

import view.enums.DifficultyLevel;
import view.utils.Constants;

/**
 * This class is a singleton, the use of which is to perform the preliminary transformation of online-sourced
 * multiple-choice questions, in order to make them usable for this system.
 *
 * An example of a raw question:
 * 
 * The first milk produced by a woman in the first few days after giving birth is called:
 * 
 * 0 formula
 * 
 * 0 enrichment
 * 
 * 1 colostrum
 * 
 * 0 sputum
 * 
 * 0 amniocentesis
 * 
 * The answers must be turned into A-D (deleting one of the incorrect ones if > 4 answers), and recording the correct
 * option, which is found by checking if it starts with a 0 or 1.
 *
 * Subjects of downloaded questions from online are: Economics, Government, HistoryEurope, HistoryUS, HistoryWorld,
 * Marketing, Psychology
 * 
 * @author Sam Barba
 */
public class RawQuestionTransformer {

	public static final Logger LOGGER = Logger.getLogger(RawQuestionTransformer.class.getName());

	private static final String[] INPUT_SUBJECTS = { "Economics", "Government", "HistoryEurope", "HistoryUS",
		"HistoryWorld", "Marketing", "Psychology" };

	private static final String INPUT_DIRECTORY = "C:\\#rawquestions\\";

	private static final String INPUT_EXT = ".txt";

	private static final int MIN_DIFFICULTY = 1;

	private static final int MAX_DIFFICULTY = DifficultyLevel.values().length;

	private static final int MIN_TIME_REQUIRED = 1;

	private static final int MAX_TIME_REQUIRED = 15;

	private static final int MIN_MARKS = 1;

	private static final int MAX_MARKS = 10;

	private static final int ANSWERS_PER_QUESTION = 4;

	private static final int ASCII_A = 65;

	private static final Random RAND = new Random();

	private static RawQuestionTransformer instance;

	private static int subjectId = 1;

	private static int questionId = 1;

	/**
	 * Loop through subjects and add them to subjects CSV file, and write questions for each subject to their respective
	 * CSV file.
	 */
	public void transformAndSaveRawQuestions() {
		LOGGER.info("Transforming and saving raw questions...");

		for (int i = 0; i < INPUT_SUBJECTS.length; i++) {
			LOGGER.info("Saving " + INPUT_SUBJECTS[i] + " questions...");

			SubjectService.getInstance()
				.addSubject(new SubjectBuilder().withId(subjectId).withTitle(INPUT_SUBJECTS[i]).build());

			String inputFilePath = INPUT_DIRECTORY + INPUT_SUBJECTS[i] + INPUT_EXT;

			List<String> rawLines = new ArrayList<>();
			try {
				rawLines = getRawLinesFromFile(inputFilePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			List<Question> questions = makeQuestionsFromRawLines(rawLines);
			for (Question question : questions) {
				QuestionService.getInstance().addQuestion(question);
			}

			subjectId++;
		}

		LOGGER.info("Questions saved!");
	}

	/**
	 * Get list of raw lines from question txt file at specified path.
	 * 
	 * @param filePath - the path of the questions file
	 * @return list of raw questions and answers
	 */
	private static List<String> getRawLinesFromFile(String filePath) throws FileNotFoundException {
		File inputFile = new File(filePath);
		Scanner input = new Scanner(inputFile);

		List<String> rawLines = new ArrayList<>();

		while (input.hasNextLine()) {
			String line = input.nextLine()
				.replace(Constants.QUOT_MARK, "'")
				.replace(Constants.NEWLINE, Constants.EMPTY)
				.trim();

			if (line.length() > 0) {
				rawLines.add(line);
			}
		}
		input.close();

		return rawLines;
	}

	/**
	 * Convert raw extracted lines into usable Question objects.
	 * 
	 * @param rawLines - the lines of questions and answers to convert
	 * @return list of Questions
	 */
	private static List<Question> makeQuestionsFromRawLines(List<String> rawLines) {
		Map<String, List<String>> questionsAndAnswers = new HashMap<>();

		String questionStr = "";

		// determine which lines are questions and which are answers
		for (String line : rawLines) {
			char firstChar = line.charAt(0);

			if (firstChar != '0' && firstChar != '1') { // if line is a question
				// add new question entry to map
				questionStr = line;
				questionsAndAnswers.put(questionStr, new ArrayList<String>());
			} else {
				// add possible answer to correct question in map
				questionsAndAnswers.get(questionStr).add(line);
			}
		}

		// now create Question objects using the map
		List<Question> questions = new ArrayList<>();
		for (Entry<String, List<String>> entry : questionsAndAnswers.entrySet()) {
			List<String> answersStr = entry.getValue();

			while (answersStr.size() > ANSWERS_PER_QUESTION) { // we must delete a random wrong answer
				int randIndex = RAND.nextInt(answersStr.size());
				while (answersStr.get(randIndex).charAt(0) == '1') { // ensure deletion of WRONG answer
					randIndex = RAND.nextInt(answersStr.size());
				}
				answersStr.remove(randIndex);
			}

			// create answers for the current question key in map
			List<Answer> answers = new ArrayList<>();
			for (int i = 0; i < ANSWERS_PER_QUESTION; i++) {
				String answerStr = answersStr.get(i);
				boolean correct = answerStr.charAt(0) == '1';
				answerStr = answerStr.substring(2); // remove leading 0/1 and space
				answerStr = Character.toString(answerStr.charAt(0)).toUpperCase() + answerStr.substring(1); // capitalise
				String letter = Character.toString((char) (ASCII_A + i));

				Answer answer = new AnswerBuilder().withValue(answerStr)
					.withLetter(letter)
					.withIsCorrect(correct)
					.build();
				answers.add(answer);
			}

			/*
			 * Find shortest question and longest question statement in order to determine difficulty level, time
			 * required, and marks
			 */
			int minLength = Integer.MAX_VALUE, maxLength = 0;
			for (String questionKey : questionsAndAnswers.keySet()) {
				int len = questionKey.length();
				if (len < minLength) {
					minLength = len;
				}
				if (len > maxLength) {
					maxLength = len;
				}
			}

			/*
			 * Determine difficulty level, time required, and marks, by mapping question statement length to specified
			 * range e.g. between 1 and 10 for marks
			 */
			String questionStatement = entry.getKey();
			int len = questionStatement.length();
			int difficultyLevelInt = (int) Math.round(map(len, minLength, maxLength, MIN_DIFFICULTY, MAX_DIFFICULTY));
			DifficultyLevel difficultyLevel = DifficultyLevel.getFromInt(difficultyLevelInt);
			int timeRequired = (int) Math.round(map(len, minLength, maxLength, MIN_TIME_REQUIRED, MAX_TIME_REQUIRED));
			int marks = (int) Math.round(map(len, minLength, maxLength, MIN_MARKS, MAX_MARKS));

			Question question = new QuestionBuilder().withId(questionId)
				.withSubjectId(subjectId)
				.withStatement(questionStatement)
				.withAnswers(answers)
				.withDifficultyLevel(difficultyLevel)
				.withMarks(marks)
				.withTimeRequiredMins(timeRequired)
				.build();

			questions.add(question);
			questionId++;
		}

		return questions;
	}

	/**
	 * Map a value from one range to another. E.g. If x = 60 and is in the range 20 to 100, what would x become if the
	 * range were 1 to 5?
	 * 
	 * x = (60 - 20) * (5 - 1) / (100 - 20) + 1
	 * 
	 * x = 3
	 * 
	 * @param x       - the value to map
	 * @param r1start - the start of range 1
	 * @param r1end   - the end of range 1
	 * @param r2start - the start of range 2
	 * @param r2end   - the end of range 2
	 * @return the converted value
	 */
	private static double map(double x, double r1start, double r1end, double r2start, double r2end) {
		return (x - r1start) * (r2end - r2start) / (r1end - r1start) + r2start;
	}

	public synchronized static RawQuestionTransformer getInstance() {
		if (instance == null) {
			instance = new RawQuestionTransformer();
		}
		return instance;
	}

	private RawQuestionTransformer() {
	}
}
