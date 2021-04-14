package model.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import model.builders.AnswerBuilder;
import model.builders.QuestionBuilder;
import model.dto.QuestionDTO;
import model.persisted.Answer;
import model.persisted.Question;

import view.enums.BloomSkillLevel;
import view.utils.Constants;

public class QuestionServiceTest {

	private static final int QUESTION_ID1 = 10;

	private static final int QUESTION_ID2 = 11;

	private static final int QUESTION_ID3 = 12;

	private static final int QUESTION_ID4 = 13;

	private static final int QUESTION_ID_NON_EXISTENT = -1;

	private static final int SUBJECT_ID1 = 14;

	private static final int SUBJECT_ID2 = 15;

	private static final int SUBJECT_ID3 = 16;

	private static final int SUBJECT_ID_NON_EXISTENT = -1;

	private static final String STATEMENT1 = "XyZ";

	private static final String STATEMENT2 = "xYz";

	private static final String STATEMENT3 = "yZz";

	private static final String STATEMENT4 = "zXy";

	private static final List<Integer> BLOOM_SKILL_FILTER = Collections
		.singletonList(BloomSkillLevel.KNOWLEDGE.getIntVal());

	private static final List<Integer> SUBJECT_FILTER = Collections.singletonList(SUBJECT_ID1);

	private QuestionService questionService = QuestionService.getInstance();

	// reset question file before each test
	@Before
	public void resetFile() {
		List<Integer> allIds = questionService.getAllQuestions().stream()
			.map(Question::getId)
			.collect(Collectors.toList());

		if (!allIds.isEmpty()) {
			questionService.deleteQuestionsByIds(allIds);
		}

		questionService.addQuestion(makeNewQuestion(QUESTION_ID1, SUBJECT_ID1,
			STATEMENT1, BloomSkillLevel.KNOWLEDGE));
		questionService.addQuestion(makeNewQuestion(QUESTION_ID2, SUBJECT_ID2,
			STATEMENT2, BloomSkillLevel.KNOWLEDGE));
		questionService.addQuestion(makeNewQuestion(QUESTION_ID3, SUBJECT_ID1,
			STATEMENT3, BloomSkillLevel.APPLICATION));
		questionService.addQuestion(makeNewQuestion(QUESTION_ID4, SUBJECT_ID3,
			STATEMENT4, BloomSkillLevel.APPLICATION));
	}

	@Test
	public void testGetQuestionById_id_exists() {
		Optional<Question> q = questionService.getQuestionById(QUESTION_ID1);

		boolean present = q.isPresent();
		if (!present) {
			fail("Not present.");
		}

		boolean correctProperties = q.get().getSubjectId() == SUBJECT_ID1
			&& q.get().getStatement().equals(STATEMENT1)
			&& q.get().getSkillLevel().equals(BloomSkillLevel.KNOWLEDGE);

		assertTrue(correctProperties);
	}

	@Test
	public void testGetQuestionById_id_not_exists() {
		Optional<Question> q = questionService.getQuestionById(QUESTION_ID_NON_EXISTENT);

		assertFalse(q.isPresent());
	}

	@Test
	public void testGetQuestionsBySubjectId_id_exists() {
		List<Question> questions = questionService.getQuestionsBySubjectId(SUBJECT_ID1);

		boolean correctListSize = questions.size() == 2;
		boolean correctIDs = questions.get(0).getId() == QUESTION_ID1
			&& questions.get(1).getId() == QUESTION_ID3;

		assertTrue(correctListSize && correctIDs);
	}

	@Test
	public void testGetQuestionsBySubjectId_id_not_exists() {
		List<Question> questions = questionService.getQuestionsBySubjectId(SUBJECT_ID_NON_EXISTENT);

		assertTrue(questions.isEmpty());
	}

	@Test
	public void getNewQuestionIdTest() {
		int newId = questionService.getNewQuestionId();

		assertEquals(newId, QUESTION_ID4 + 1);
	}

	@Test
	public void testGetQuestionDTOsWithFilters_bloom_filter() {
		List<QuestionDTO> questionDtos = questionService.getQuestionDTOsWithFilters(BLOOM_SKILL_FILTER,
			Collections.emptyList(), Constants.EMPTY);

		boolean correctListSize = questionDtos.size() == 2;
		boolean correctIDs = questionDtos.get(0).getId() == QUESTION_ID1
			&& questionDtos.get(1).getId() == QUESTION_ID2;

		assertTrue(correctListSize && correctIDs);
	}

	@Test
	public void testGetQuestionDTOsWithFilters_subject_filter() {
		List<QuestionDTO> questionDtos = questionService.getQuestionDTOsWithFilters(Collections.emptyList(),
			SUBJECT_FILTER, Constants.EMPTY);

		boolean correctListSize = questionDtos.size() == 2;
		boolean correctIDs = questionDtos.get(0).getId() == QUESTION_ID1
			&& questionDtos.get(1).getId() == QUESTION_ID3;

		assertTrue(correctListSize && correctIDs);
	}

	@Test
	public void testGetQuestionDTOsWithFilters_statement_filter() {
		List<QuestionDTO> questionDtos = questionService.getQuestionDTOsWithFilters(Collections.emptyList(),
			Collections.emptyList(), "X");

		boolean correctListSize = questionDtos.size() == 3;
		boolean correctIDs = questionDtos.get(0).getId() == QUESTION_ID1
			&& questionDtos.get(1).getId() == QUESTION_ID2
			&& questionDtos.get(2).getId() == QUESTION_ID4;

		assertTrue(correctListSize && correctIDs);
	}

	@Test
	public void testGetQuestionDTOsWithFilters_all_filters() {
		List<QuestionDTO> questionDtos = questionService.getQuestionDTOsWithFilters(BLOOM_SKILL_FILTER, SUBJECT_FILTER,
			"X");

		boolean correctListSize = questionDtos.size() == 1;
		boolean correctID = questionDtos.get(0).getId() == QUESTION_ID1;

		assertTrue(correctListSize && correctID);
	}

	private Question makeNewQuestion(int id, int subjectId, String statement, BloomSkillLevel skillLevel) {
		Answer ansA = new AnswerBuilder().withValue("val1").withIsCorrect(true).build();
		Answer ansB = new AnswerBuilder().withValue("val2").withIsCorrect(false).build();
		Answer ansC = new AnswerBuilder().withValue("val3").withIsCorrect(false).build();
		Answer ansD = new AnswerBuilder().withValue("val4").withIsCorrect(false).build();

		return new QuestionBuilder()
			.withId(id)
			.withSubjectId(subjectId)
			.withStatement(statement)
			.withAnswers(Arrays.asList(ansA, ansB, ansC, ansD))
			.withSkillLevel(skillLevel)
			.withMarks(1)
			.withMinutesRequired(1)
			.withDateCreated(LocalDateTime.now())
			.build();
	}
}
