package model.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import model.builders.AnswerBuilder;
import model.builders.QuestionBuilder;
import model.persisted.Answer;
import model.persisted.Question;

import view.enums.BloomSkillLevel;

public class QuestionDAOTest {

	private static final int QUESTION_ID1 = 1;

	private static final int QUESTION_ID2 = 2;

	private static final int QUESTION_ID3 = 3;

	private static final int QUESTION_ID_NON_EXISTENT = -1;

	private QuestionDAO questionDao = QuestionDAO.getInstance();

	// reset question file before each test
	@Before
	public void resetFile() {
		List<Integer> allIds = questionDao.getAllQuestions().stream()
			.map(Question::getId)
			.collect(Collectors.toList());

		if (!allIds.isEmpty()) {
			questionDao.deleteQuestionsByIds(allIds);
		}
	}

	@Test
	public void testAddQuestion() {
		questionDao.addQuestion(makeNewQuestion(QUESTION_ID1));

		assertTrue(questionWithIdExists(QUESTION_ID1));
	}

	@Test
	public void testDeleteQuestionById_id_exists() {
		// ensure question XML file exists
		questionDao.addQuestion(makeNewQuestion(QUESTION_ID2));

		questionDao.deleteQuestionsByIds(Collections.singletonList(QUESTION_ID2));

		assertFalse(questionWithIdExists(QUESTION_ID2));
	}

	@Test
	public void testDeleteQuestionByIdTest_id_not_exists() {
		// ensure question XML file exists
		questionDao.addQuestion(makeNewQuestion(QUESTION_ID3));

		// delete by non-existent ID
		questionDao.deleteQuestionsByIds(Collections.singletonList(QUESTION_ID_NON_EXISTENT));

		assertTrue(questionWithIdExists(QUESTION_ID3)
			&& !questionWithIdExists(QUESTION_ID_NON_EXISTENT));
	}

	@Test
	public void testGetAllQuestions() {
		questionDao.addQuestion(makeNewQuestion(QUESTION_ID1));
		questionDao.addQuestion(makeNewQuestion(QUESTION_ID2));
		questionDao.addQuestion(makeNewQuestion(QUESTION_ID3));

		assertEquals(questionDao.getAllQuestions().size(), 3);
	}

	private Question makeNewQuestion(int id) {
		Answer ansA = new AnswerBuilder().withValue("val1").withIsCorrect(true).build();
		Answer ansB = new AnswerBuilder().withValue("val2").withIsCorrect(false).build();
		Answer ansC = new AnswerBuilder().withValue("val3").withIsCorrect(false).build();
		Answer ansD = new AnswerBuilder().withValue("val4").withIsCorrect(false).build();

		return new QuestionBuilder()
			.withId(id)
			.withSubjectId(1)
			.withStatement("statement")
			.withAnswers(Arrays.asList(ansA, ansB, ansC, ansD))
			.withSkillLevel(BloomSkillLevel.KNOWLEDGE)
			.withMarks(1)
			.withMinutesRequired(1)
			.withDateCreated(LocalDateTime.now())
			.build();
	}

	private boolean questionWithIdExists(int id) {
		return questionDao.getAllQuestions().stream()
			.filter(q -> q.getId() == id)
			.findFirst()
			.isPresent();
	}
}
