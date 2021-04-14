package model.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import model.builders.SubjectBuilder;
import model.persisted.Subject;

public class SubjectDAOTest {

	private static final int SUBJECT_ID1 = 7;

	private static final int SUBJECT_ID2 = 8;

	private static final int SUBJECT_ID3 = 9;

	private static final int SUBJECT_ID_NON_EXISTENT = -1;

	private SubjectDAO subjectDao = SubjectDAO.getInstance();

	// reset subject file before each test
	@Before
	public void resetFile() {
		List<Integer> allIds = subjectDao.getAllSubjects().stream()
			.map(Subject::getId)
			.collect(Collectors.toList());

		if (!allIds.isEmpty()) {
			subjectDao.deleteSubjectsByIds(allIds);
		}
	}

	@Test
	public void testAddSubject() {
		subjectDao.addSubject(makeNewSubject(SUBJECT_ID1));

		assertTrue(subjectWithIdExists(SUBJECT_ID1));
	}

	@Test
	public void testDeleteSubjectById_id_exists() {
		// ensure subject XML file exists
		subjectDao.addSubject(makeNewSubject(SUBJECT_ID2));

		subjectDao.deleteSubjectsByIds(Collections.singletonList(SUBJECT_ID2));

		assertFalse(subjectWithIdExists(SUBJECT_ID2));
	}

	@Test
	public void testDeleteSubjectById_id_not_exists() {
		// ensure subject XML file exists
		subjectDao.addSubject(makeNewSubject(SUBJECT_ID3));

		// delete by non-existent ID
		subjectDao.deleteSubjectsByIds(Collections.singletonList(SUBJECT_ID_NON_EXISTENT));

		assertTrue(subjectWithIdExists(SUBJECT_ID3)
			&& !subjectWithIdExists(SUBJECT_ID_NON_EXISTENT));
	}

	@Test
	public void testGetAllSubjects() {
		subjectDao.addSubject(makeNewSubject(SUBJECT_ID1));
		subjectDao.addSubject(makeNewSubject(SUBJECT_ID2));
		subjectDao.addSubject(makeNewSubject(SUBJECT_ID3));

		assertEquals(subjectDao.getAllSubjects().size(), 3);
	}

	private Subject makeNewSubject(int id) {
		return new SubjectBuilder()
			.withId(id)
			.withTitle("subjectTitle")
			.withDateCreated(LocalDateTime.now())
			.build();
	}

	private boolean subjectWithIdExists(int id) {
		return subjectDao.getAllSubjects().stream()
			.filter(q -> q.getId() == id)
			.findFirst()
			.isPresent();
	}
}
