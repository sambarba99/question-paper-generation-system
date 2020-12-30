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

public class QuestionPaperDTO {

	private static QuestionPaperDTO instance;

	/**
	 * Get a list of all question papers for paper ListView
	 * 
	 * @return list of all question papers
	 */
	public List<String> getQuestionPaperListViewItems() {
		List<QuestionPaper> allQuestionPapers = QuestionPaperService.getInstance().getAllQuestionPapers();
		List<String> listViewItems = allQuestionPapers.stream().map(qp -> (qp.getTitle() + " (ID " + qp.getId() + ")"))
				.collect(Collectors.toList());
		return listViewItems;
	}

	/**
	 * Get a list of question papers of specified subject IDs
	 * 
	 * @param subjectIds - the list of subject IDs
	 * @return list of specified subject papers
	 */
	public List<String> getQuestionPaperListViewItemsBySubjectIds(List<Integer> subjectIds) {
		List<String> listViewItems = new ArrayList<>();
		for (QuestionPaper qp : QuestionPaperService.getInstance().getAllQuestionPapers()) {
			if (subjectIds.contains(qp.getSubjectId())) {
				listViewItems.add(qp.getTitle() + " (ID " + qp.getId() + ")");
			}
		}
		return listViewItems;
	}

	/**
	 * Get ID of selected question paper in list view
	 * 
	 * @param listViewQuestionPapers - the ListView of papers
	 * @return ID of selected paper
	 */
	public int getQpId(ListView<String> listViewQuestionPapers) {
		/*
		 * here we are getting the element at position (length - 1) because there can be multiple spaces in the string,
		 * e.g. "Mathematics (ID 1)". We then remove the closing bracket.
		 */
		String qp = listViewQuestionPapers.getSelectionModel().getSelectedItem();
		String[] qpSplit = qp.split(" ");
		String qpIdStr = qpSplit[qpSplit.length - 1];
		qpIdStr = qpIdStr.replace(")", "");
		return Integer.parseInt(qpIdStr);
	}

	/**
	 * Capitalise each word in title and trim whitespace
	 * 
	 * @param title - the title to format
	 * @return formatted title
	 */
	public String formatTitle(String title) {
		String[] words = title.trim().split(" ");
		String result = "";
		for (int i = 0; i < words.length; i++) {
			result += Character.toString(words[i].charAt(0)).toUpperCase();
			result += words[i].substring(1).toLowerCase() + " ";
		}
		return result.trim(); // remove last space
	}

	/**
	 * Get a formatted question paper string for question paper TextArea
	 * 
	 * @param qp - the paper to format
	 * @return question string
	 */
	public String getTxtAreaQuestionPaperStr(QuestionPaper qp) {
		Subject subject = SubjectService.getInstance().getSubjectById(qp.getSubjectId());

		String txtAreaStr = qp.getTitle() + " (ID " + qp.getId() + ")";
		txtAreaStr += "\nSubject: " + subject.getTitle() + " (ID " + subject.getId() + ")";
		txtAreaStr += "\nCourse: " + qp.getCourseTitle() + " (" + qp.getCourseCode() + ")";
		txtAreaStr += "\nDifficulty level: " + qp.getDifficultyLevel().toString();
		txtAreaStr += "\nMarks: " + qp.getMarks();
		txtAreaStr += "\nTime required (mins): " + qp.getTimeRequiredMins() + "\n";

		List<Integer> questionIds = qp.getQuestionIds();
		for (int i = 0; i < questionIds.size(); i++) {
			Question question = QuestionService.getInstance().getQuestionById(questionIds.get(i));
			txtAreaStr += "\nQuestion " + (i + 1) + ": " + question.getStatement();
			txtAreaStr += "\nAnswer option 1: " + question.getAnswerOptions().get(0);
			txtAreaStr += "\nAnswer option 2: " + question.getAnswerOptions().get(1);
			txtAreaStr += "\nAnswer option 3: " + question.getAnswerOptions().get(2);
			txtAreaStr += "\nAnswer option 4: " + question.getAnswerOptions().get(3) + "\n";
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
