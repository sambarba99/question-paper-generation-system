package view.utils;

import java.time.format.DateTimeFormatter;

import model.questionpapergeneration.SelectionType;

import view.SystemNotification;
import view.enums.SystemNotificationType;

/**
 * This class contains constants to be used system-wide.
 *
 * @author Sam Barba
 */
public class Constants {

	/*
	 * Paths were files are stored
	 */
	public static final String GENETIC_ALGORITHM_RESULTS_PATH = "C:\\Users\\Sam Barba\\Desktop\\results.csv";

	public static final String USERS_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\users.csv";

	public static final String QUESTIONS_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\questions.csv";

	public static final String QUESTION_PAPERS_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\question_papers.csv";

	public static final String SUBJECTS_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\subjects.csv";

	public static final String EXPORTED_PAPERS_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\exported\\";

	/*
	 * Character constants
	 */
	public static final String EMPTY = "";

	public static final String COMMA = ",";

	public static final String SPACE = " ";

	public static final String NEWLINE = "\n";

	public static final String QUOT_MARK = "\"";

	/**
	 * Question statement and answers must not have repeating spaces.
	 */
	public static final String QUESTION_STATEMENT_REGEX = "^([^\\s]\\s?)+$";

	/**
	 * Subject and question paper titles must be only alphanumeric characters, and no repeating spaces.
	 */
	public static final String TITLE_REGEX = "^(\\w\\s?)+$";

	/**
	 * Username must start with letter(s) and optionally have a number at the end, in case the same username exists.
	 */
	public static final String USERNAME_REGEX = "^[a-zA-Z]+\\d*$";

	/**
	 * Password must contain at least 1 of: 0-9, a-z, A-Z, and be at least 8 long.
	 */
	public static final String PASS_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$";

	/*
	 * Constants for the Genetic Algorithm
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
	public static final String SUBJECT_DELETED = "Subject deleted";

	public static final double ACADEMIC_MATERIAL_BTN_WIDTH = 238;

	public static final String UNEXPECTED_ERROR = "Unexpected error: ";

	public static final String LOGO_PATH = "src/logo.png";

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm");

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
