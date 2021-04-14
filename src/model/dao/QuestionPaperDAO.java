package model.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import model.persisted.QuestionPaper;
import model.xml.XMLQuestionPaperSerialiser;

import view.enums.SystemNotificationType;
import view.utils.Constants;

import controller.SystemNotification;

/**
 * This class is a singleton, the use of which is any database operation regarding question papers.
 *
 * @author Sam Barba
 */
public class QuestionPaperDAO {

	private static final Logger LOGGER = Logger.getLogger(QuestionPaperDAO.class.getName());

	private XMLQuestionPaperSerialiser questionPaperSerialiser = XMLQuestionPaperSerialiser.getInstance();

	private static QuestionPaperDAO instance;

	private QuestionPaperDAO() {
	}

	public synchronized static QuestionPaperDAO getInstance() {
		if (instance == null) {
			instance = new QuestionPaperDAO();
		}
		return instance;
	}

	/**
	 * Add a question paper to the papers XML file.
	 * 
	 * @param questionPaper - the paper to add
	 */
	public void addQuestionPaper(QuestionPaper questionPaper) {
		try {
			File xmlFile = new File(Constants.QUESTION_PAPERS_FILE_PATH);
			List<QuestionPaper> allPapers = getAllQuestionPapers();
			if (!xmlFile.exists()) {
				xmlFile.getParentFile().mkdirs();
				xmlFile.createNewFile();
			}

			allPapers.add(questionPaper);
			questionPaperSerialiser.write(allPapers);
			LOGGER.info("Question paper with ID " + questionPaper.getId() + " added");
		} catch (Exception e) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
		}
	}

	/**
	 * Delete papers by their unique IDs.
	 * 
	 * @param ids - the IDs of the papers to delete
	 */
	public void deleteQuestionPapersByIds(List<Integer> ids) {
		try {
			List<QuestionPaper> allPapers = getAllQuestionPapers();
			List<QuestionPaper> writePapers = allPapers.stream()
				.filter(p -> !ids.contains(p.getId()))
				.collect(Collectors.toList());

			questionPaperSerialiser.write(writePapers);

			LOGGER.info("Question papers with specified IDs deleted");
		} catch (Exception e) {
			SystemNotification.display(SystemNotificationType.ERROR,
				Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
		}
	}

	/**
	 * Retrieve all question papers.
	 * 
	 * @return list of all papers
	 */
	public List<QuestionPaper> getAllQuestionPapers() {
		List<QuestionPaper> allPapers = new ArrayList<>();

		File xmlFile = new File(Constants.QUESTION_PAPERS_FILE_PATH);
		if (xmlFile.exists()) {
			try {
				allPapers = (List<QuestionPaper>) questionPaperSerialiser.readAll();
			} catch (Exception e) {
				SystemNotification.display(SystemNotificationType.ERROR,
					Constants.UNEXPECTED_ERROR + e.getClass().getName() + "\nIn: " + this.getClass().getName());
			}
		}

		return allPapers;
	}
}
