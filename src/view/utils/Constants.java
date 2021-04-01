package view.utils;

import java.time.format.DateTimeFormatter;

import view.enums.SelectionType;

/**
 * This class contains constants to be used system-wide.
 *
 * @author Sam Barba
 */
public final class Constants {

	/*
	 * File path constants
	 */
	public static final String USERS_FILE_PATH = "C:\\#QPGS\\users.csv";

	public static final String QUESTIONS_FILE_PATH = "C:\\#QPGS\\questions.csv";

	public static final String QUESTION_PAPERS_FILE_PATH = "C:\\#QPGS\\questionpapers.csv";

	public static final String SUBJECTS_FILE_PATH = "C:\\#QPGS\\subjects.csv";

	public static final String TXT_EXT = ".txt";

	/*
	 * Character constants
	 */
	public static final String EMPTY = "";

	public static final String COMMA = ",";

	public static final String NEWLINE = "\n";

	public static final String QUOT_MARK = "\"";

	public static final int ASCII_A = 65;

	/**
	 * Question statement and answers must not have repeating spaces.
	 */
	public static final String QUESTION_STATEMENT_REGEX = "^([^\\s]\\s?)+$";

	/**
	 * Subject and question paper titles must be only alphanumeric characters, and no repeating spaces.
	 */
	public static final String TITLE_REGEX = "^(\\w\\s?)+$";

	/**
	 * Username must start with letter(s) and optionally have a number at the end, in case the same username exists
	 * already and can be made unique.
	 */
	public static final String USERNAME_REGEX = "^[a-z]+\\d*$";

	/**
	 * Password must contain at least 1 of: 0-9, a-z, A-Z, and be at least 8 long.
	 */
	public static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$";

	/*
	 * Constants for the Genetic Algorithm
	 */
	public static final int MIN_QUESTIONS_PER_PAPER = 2;

	public static final SelectionType SELECTION_TYPE = SelectionType.TOURNAMENT;

	public static final int POP_SIZE = 2000;

	public static final double MUTATION_RATE = 0.01;

	public static final double CROSSOVER_RATE = 0.8;

	public static final int GENERATIONS = 25;

	public static final int TOURNAMENT_SIZE = 2;

	public static final boolean TEST_MODE = false;

	public static final String GENETIC_ALGORITHM_TEST_RESULTS = "C:\\Users\\Sam Barba\\Desktop\\Work\\Uni\\Year 3\\DSP\\GA-test-results.csv";

	/*
	 * UI design constants
	 */
	public static final double ACADEMIC_MATERIAL_BTN_WIDTH = 238;

	public static final String LOGO_PATH = "src/logo.png";

	public static final String CSS_STYLE_PATH = "style.css";

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm");

	/*
	 * Misc constants
	 */
	public static final String SURE_TO_DELETE = "Are you sure you wish to delete the selected item(s)?";

	public static final String SURE_TO_EXIT = "Are you sure you wish to exit the application?" + NEWLINE
		+ "Any changes have been saved.";

	public static final String QUESTION_DELETED = "This question has been deleted!";

	public static final String SUBJECT_DELETED = "Subject deleted!";

	public static final String UNEXPECTED_ERROR = "Unexpected error: ";

	private Constants() {
	}
}
