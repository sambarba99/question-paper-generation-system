package dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.ListView;

import service.QuestionPaperService;
import service.QuestionService;
import service.SubjectService;

import model.Question;
import model.QuestionPaper;
import model.Subject;

import utils.Constants;

/**
 * This class is a singleton which contains methods related to ListViews used to modify and display question papers.
 *
 * @author Sam Barba
 */
public class QuestionPaperDTO {

	private static QuestionPaperDTO instance;

	/**
	 * Get a list of all question papers for paper ListView objects.
	 * 
	 * @return list of all question papers
	 */
	public List<String> getQuestionPaperListViewItems() {
		List<QuestionPaper> allQuestionPapers = QuestionPaperService.getInstance().getAllQuestionPapers();
		List<String> listViewItems = allQuestionPapers.stream()
				.map(questionPaper -> (questionPaper.getTitle() + " (ID " + questionPaper.getId() + ")"))
				.collect(Collectors.toList());
		return listViewItems;
	}

	/**
	 * Get a list of question papers of specified subject IDs.
	 * 
	 * @param subjectIds - the list of subject IDs
	 * @return list of specified subject papers
	 */
	public List<String> getQuestionPaperListViewItemsBySubjectIds(List<Integer> subjectIds) {
		List<String> listViewItems = new ArrayList<>();
		for (QuestionPaper questionPaper : QuestionPaperService.getInstance().getAllQuestionPapers()) {
			if (subjectIds.contains(questionPaper.getSubjectId())) {
				listViewItems.add(questionPaper.getTitle() + " (ID " + questionPaper.getId() + ")");
			}
		}
		return listViewItems;
	}

	/**
	 * Get ID of selected question paper in ListView.
	 * 
	 * @param listViewQuestionPapers - the ListView of papers
	 * @return ID of selected paper
	 */
	public int getQuestionPaperId(ListView<String> listViewQuestionPapers) {
		/*
		 * here we are getting the element at position (length - 1) because there can be multiple spaces in the string,
		 * e.g. "Mathematics (ID 1)". We then remove the closing bracket.
		 */
		String questionPaper = listViewQuestionPapers.getSelectionModel().getSelectedItem();
		String[] questionPaperSplit = questionPaper.split(Constants.SPACE);
		String questionPaperIdStr = questionPaperSplit[questionPaperSplit.length - 1];
		questionPaperIdStr = questionPaperIdStr.replace(")", "");
		return Integer.parseInt(questionPaperIdStr);
	}

	/**
	 * Capitalise each word in paper title and trim whitespace.
	 * 
	 * @param title - the title to format
	 * @return formatted title
	 */
	public String formatTitle(String title) {
		String[] words = title.trim().split(Constants.SPACE);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			result.append(Character.toString(words[i].charAt(0)).toUpperCase());
			result.append(words[i].substring(1).toLowerCase() + Constants.SPACE);
		}
		return result.toString().trim(); // remove last space
	}

	/**
	 * Get a formatted question paper string for question paper TextArea object.
	 * 
	 * @param questionPaper - the paper to format
	 * @return question string
	 */
	public String getTxtAreaQuestionPaperStr(QuestionPaper questionPaper) {
		Subject subject = SubjectService.getInstance().getSubjectById(questionPaper.getSubjectId());

		StringBuilder txtAreaStr = new StringBuilder();
		txtAreaStr.append(questionPaper.getTitle() + " (ID " + questionPaper.getId() + ")");
		txtAreaStr.append(Constants.NEWLINE + "Subject: " + subject.getTitle() + " (ID " + subject.getId() + ")");
		txtAreaStr.append(Constants.NEWLINE + "Course: " + questionPaper.getCourseTitle() + " ("
				+ questionPaper.getCourseCode() + ")");
		txtAreaStr.append(Constants.NEWLINE + "Difficulty level: " + questionPaper.getDifficultyLevel().toString());
		txtAreaStr.append(Constants.NEWLINE + "Marks: " + questionPaper.getMarks());
		txtAreaStr.append(
				Constants.NEWLINE + "Time required (mins): " + questionPaper.getTimeRequiredMins() + Constants.NEWLINE);

		List<Integer> questionIds = questionPaper.getQuestionIds();
		for (int i = 0; i < questionIds.size(); i++) {
			Question question = QuestionService.getInstance().getQuestionById(questionIds.get(i));
			txtAreaStr.append(Constants.NEWLINE + "Question " + (i + 1) + ": " + question.getStatement());
			txtAreaStr.append(Constants.NEWLINE + "Answer option 1: " + question.getAnswerOptions().get(0));
			txtAreaStr.append(Constants.NEWLINE + "Answer option 2: " + question.getAnswerOptions().get(1));
			txtAreaStr.append(Constants.NEWLINE + "Answer option 3: " + question.getAnswerOptions().get(2));
			txtAreaStr.append(
					Constants.NEWLINE + "Answer option 4: " + question.getAnswerOptions().get(3) + Constants.NEWLINE);
		}

		return txtAreaStr.substring(0, txtAreaStr.length() - 1); // remove last '\n'
	}

	public synchronized static QuestionPaperDTO getInstance() {
		if (instance == null) {
			instance = new QuestionPaperDTO();
		}
		return instance;
	}
}
