package view;

import model.questionpapergeneration.SelectionType;

import view.enums.SystemNotificationType;

import controller.SystemNotification;

/**
 * This class contains constants to be used system-wide.
 *
 * @author Sam Barba
 */
public class Constants {

	/*
	 * Paths were files are stored.
	 */
	public static final String GENETIC_ALGORITHM_RESULTS_PATH = "C:\\Users\\Sam Barba\\Desktop\\results.csv";

	public static final String USERS_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\users.csv";

	public static final String QUESTION_PAPERS_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\question_papers.csv";

	public static final String QUESTIONS_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\questions.csv";

	public static final String SUBJECTS_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\subjects.csv";

	public static final String EXPORTED_PAPERS_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\exported\\";

	/*
	 * Character constants
	 */

	public static final String EMPTY = "";

	public static final String COMMA = ",";

	public static final String SPACE = " ";

	public static final String NEWLINE = "\n";

	/**
	 * Question statement and answer options must not have repeating spaces
	 */
	public static final String QUESTION_STATEMENT_REGEX = "^([^\\s]\\s?)+$";

	/**
	 * Subject and question paper titles must be only alphanumeric characters, and no repeating spaces
	 */
	public static final String TITLE_REGEX = "^(\\w\\s?)+$";

	/**
	 * must start with letter(s) and optionally have number at end
	 */
	public static final String USERNAME_REGEX = "^[a-zA-Z]+\\d*$";

	/**
	 * must contain at least 1 of: 0-9, a-z, A-Z, and be at least 8 long
	 */
	public static final String PASS_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$";

	/*
	 * Constants for the Genetic Algorithm.
	 */
	public static final SelectionType SELECTION_TYPE = SelectionType.ROULETTE_WHEEL;

	public static final int NUM_GENES = 50;

	public static final int POP_SIZE = 100;

	public static final double MUTATION_RATE = 0.02;

	public static final double CROSSOVER_RATE = 0.8;

	public static final int CROSSOVER_POINTS = 1;

	public static final int GENERATIONS = 100;

	public static final int TOURNAMENT_SIZE = 4;

	/*
	 * Misc constants
	 */

	public static final double BTN_HEIGHT = 37;

	public static final double ACADEMIC_MATERIAL_BTN_WIDTH = 238;

	public static final String UNEXPECTED_ERROR = "Unexpected error: ";

	/**
	 * The caller references constants using Constants.USER_FILE_PATH etc. Thus, the caller should be prevented from
	 * constructing objects of this class.
	 */
	public Constants() {
		SystemNotification.display(SystemNotificationType.ERROR,
			Constants.UNEXPECTED_ERROR + "Constants class constructor shouldn't be called!");
		throw new AssertionError();
	}
}
