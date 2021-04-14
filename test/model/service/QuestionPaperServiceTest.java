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

import model.builders.QuestionPaperBuilder;
import model.dto.QuestionPaperDTO;
import model.persisted.QuestionPaper;

import view.enums.BloomSkillLevel;

public class QuestionPaperServiceTest {

	private static final List<Integer> QUESTION_IDS1 = Arrays.asList(1, 2, 3, 4, 5);

	private static final List<Integer> QUESTION_IDS2 = Arrays.asList(6, 7, 8, 9);

	private static final List<Integer> QUESTION_IDS3 = Arrays.asList(9, 10, 11, 12);

	private static final int PAPER_ID1 = 17;

	private static final int PAPER_ID2 = 18;

	private static final int PAPER_ID3 = 19;

	private static final int PAPER_ID_NON_EXISTENT = -1;

	private static final int SUBJECT_ID1 = 20;

	private static final int SUBJECT_ID2 = 21;

	private static final List<Integer> SUBJECT_FILTER = Collections.singletonList(SUBJECT_ID2);

	private QuestionPaperService questionPaperService = QuestionPaperService.getInstance();

	// reset question paper file before each test
	@Before
	public void resetFile() {
		List<Integer> allIds = questionPaperService.getAllQuestionPapers().stream()
			.map(QuestionPaper::getId)
			.collect(Collectors.toList());

		if (!allIds.isEmpty()) {
			questionPaperService.deleteQuestionPapersByIds(allIds);
		}

		questionPaperService.addQuestionPaper(makeNewQuestionPaper(PAPER_ID1, SUBJECT_ID1, QUESTION_IDS1));
		questionPaperService.addQuestionPaper(makeNewQuestionPaper(PAPER_ID2, SUBJECT_ID2, QUESTION_IDS2));
		questionPaperService.addQuestionPaper(makeNewQuestionPaper(PAPER_ID3, SUBJECT_ID2, QUESTION_IDS3));
	}

	@Test
	public void testGetQuestionPaperById_id_exists() {
		Optional<QuestionPaper> qp = questionPaperService.getQuestionPaperById(PAPER_ID1);

		boolean present = qp.isPresent();
		if (!present) {
			fail("Not present.");
		}

		boolean correctSubjectId = qp.get().getSubjectId() == SUBJECT_ID1;

		assertTrue(correctSubjectId);
	}

	@Test
	public void testGetQuestionPaperById_id_not_exists() {
		Optional<QuestionPaper> qp = questionPaperService.getQuestionPaperById(PAPER_ID_NON_EXISTENT);

		assertFalse(qp.isPresent());
	}

	@Test
	public void testGetQuestionPaperByQuestionId_id_exists() {
		List<QuestionPaper> papers = questionPaperService.getQuestionPapersByQuestionId(QUESTION_IDS3.get(0));

		boolean correctListSize = papers.size() == 2;
		boolean correctIDs = papers.get(0).getId() == PAPER_ID2
			&& papers.get(1).getId() == PAPER_ID3;

		assertTrue(correctListSize && correctIDs);
	}

	@Test
	public void testGetQuestionPaperByQuestionId_id_not_exists() {
		List<QuestionPaper> papers = questionPaperService.getQuestionPapersByQuestionId(PAPER_ID_NON_EXISTENT);

		assertTrue(papers.isEmpty());
	}

	@Test
	public void testGetNewQuestionPaperId() {
		int newId = questionPaperService.getNewQuestionPaperId();

		assertEquals(newId, PAPER_ID3 + 1);
	}

	@Test
	public void testGetQuestionPaperDTOsWithSubjectFilter() {
		List<QuestionPaperDTO> paperDtos = questionPaperService.getQuestionPaperDTOsWithSubjectFilter(SUBJECT_FILTER);

		boolean correctListSize = paperDtos.size() == 2;
		boolean correctIDs = paperDtos.get(0).getId() == PAPER_ID2
			&& paperDtos.get(1).getId() == PAPER_ID3;

		assertTrue(correctListSize && correctIDs);
	}

	private QuestionPaper makeNewQuestionPaper(int id, int subjectId, List<Integer> questionIds) {
		return new QuestionPaperBuilder()
			.withId(id)
			.withSubjectId(subjectId)
			.withTitle("title")
			.withCourseTitle("courseTitle")
			.withCourseCode("courseCode")
			.withQuestionIds(questionIds)
			.withSkillLevel(BloomSkillLevel.ANALYSIS)
			.withMarks(50)
			.withMinutesRequired(60)
			.withDateCreated(LocalDateTime.now())
			.build();
	}
}
