package controller;

import javafx.stage.Stage;

import model.service.QuestionPaperService;
import model.service.QuestionService;
import model.service.SubjectService;
import model.service.UserService;

/**
 * UI controller from which other UI pages are extended, as this contains each service.
 *
 * @author Sam Barba
 */
public class UIController {

	protected static Stage stage;

	protected static QuestionService questionService = QuestionService.getInstance();

	protected static QuestionPaperService questionPaperService = QuestionPaperService.getInstance();

	protected static SubjectService subjectService = SubjectService.getInstance();

	protected static UserService userService = UserService.getInstance();
}
