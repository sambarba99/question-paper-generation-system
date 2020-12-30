package utils;

public class Constants {

	public static final String USER_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\users.csv";

	public static final String QUESTION_PAPERS_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\question_papers.csv";

	public static final String QUESTIONS_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\questions.csv";

	public static final String SUBJECTS_FILE_PATH = "C:\\#QuestionPaperGenerationSystem\\subjects.csv";

	/**
	 * Question statement and answer options must not have repeating spaces
	 */
	public static final String QUESTION_REGEX = "^([^\\s]\\s?)+$";

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

	/**
	 * The caller references constants using Constants.USER_FILE_PATH etc. Thus, the caller should be prevented from
	 * constructing objects of this class.
	 */
	public Constants() {
		throw new AssertionError();
	}
}