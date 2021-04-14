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

import model.builders.QuestionPaperBuilder;
import model.persisted.QuestionPaper;

import view.enums.BloomSkillLevel;

public class QuestionPaperDAOTest {

	private static final int PAPER_ID1 = 4;

	private static final int PAPER_ID2 = 5;

	private static final int PAPER_ID3 = 6;

	private static final int PAPER_ID_NON_EXISTENT = -1;

	private QuestionPaperDAO questionPaperDao = QuestionPaperDAO.getInstance();

	// reset paper file before each test
	@Before
	public void resetFile() {
		List<Integer> allIds = questionPaperDao.getAllQuestionPapers().stream()
			.map(QuestionPaper::getId)
			.collect(Collectors.toList());

		if (!allIds.isEmpty()) {
			questionPaperDao.deleteQuestionPapersByIds(allIds);
		}
	}

	@Test
	public void testAddQuestionPaper() {
		questionPaperDao.addQuestionPaper(makeNewQuestionPaper(PAPER_ID1));

		assertTrue(questionPaperWithIdExists(PAPER_ID1));
	}

	@Test
	public void testDeleteQuestionPaperById_id_exists() {
		// ensure question paper XML file exists
		questionPaperDao.addQuestionPaper(makeNewQuestionPaper(PAPER_ID2));

		questionPaperDao.deleteQuestionPapersByIds(Collections.singletonList(PAPER_ID2));

		assertFalse(questionPaperWithIdExists(PAPER_ID2));
	}

	@Test
	public void testDeleteQuestionPaperByIdTest_id_not_exists() {
		// ensure question paper XML file exists
		questionPaperDao.addQuestionPaper(makeNewQuestionPaper(PAPER_ID3));

		// delete by non-existent ID
		questionPaperDao.deleteQuestionPapersByIds(Collections.singletonList(PAPER_ID_NON_EXISTENT));

		assertTrue(questionPaperWithIdExists(PAPER_ID3)
			&& !questionPaperWithIdExists(PAPER_ID_NON_EXISTENT));
	}

	@Test
	public void testGetAllQuestionPapers() {
		questionPaperDao.addQuestionPaper(makeNewQuestionPaper(PAPER_ID1));
		questionPaperDao.addQuestionPaper(makeNewQuestionPaper(PAPER_ID2));
		questionPaperDao.addQuestionPaper(makeNewQuestionPaper(PAPER_ID3));

		assertEquals(questionPaperDao.getAllQuestionPapers().size(), 3);
	}

	private QuestionPaper makeNewQuestionPaper(int id) {
		return new QuestionPaperBuilder()
			.withId(id)
			.withSubjectId(1)
			.withTitle("title")
			.withCourseTitle("courseTitle")
			.withCourseCode("courseCode")
			.withQuestionIds(Arrays.asList(1, 2, 3, 4, 5))
			.withSkillLevel(BloomSkillLevel.ANALYSIS)
			.withMarks(50)
			.withMinutesRequired(60)
			.withDateCreated(LocalDateTime.now())
			.build();
	}

	private boolean questionPaperWithIdExists(int id) {
		return questionPaperDao.getAllQuestionPapers().stream()
			.filter(q -> q.getId() == id)
			.findFirst()
			.isPresent();
	}
}
