package model.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import model.builders.SubjectBuilder;
import model.dto.SubjectDTO;
import model.persisted.Subject;

public class SubjectServiceTest {

	private static final int SUBJECT_ID1 = 22;

	private static final int SUBJECT_ID2 = 23;

	private static final int SUBJECT_ID_NON_EXISTENT = -1;

	private static final String DISPLAY_STR = "Computer Science (ID 42)";

	private SubjectService subjectService = SubjectService.getInstance();

	// reset subject file before each test
	@Before
	public void resetFile() {
		List<Integer> allIds = subjectService.getAllSubjects().stream()
			.map(Subject::getId)
			.collect(Collectors.toList());

		if (!allIds.isEmpty()) {
			subjectService.deleteSubjectsByIds(allIds);
		}

		subjectService.addSubject(makeNewSubject(SUBJECT_ID1));
		subjectService.addSubject(makeNewSubject(SUBJECT_ID2));
	}

	@Test
	public void testGetSubjectById_id_exists() {
		Optional<Subject> subject = subjectService.getSubjectById(SUBJECT_ID1);

		assertTrue(subject.isPresent());
	}

	@Test
	public void testGetSubjectById_id_not_exists() {
		Optional<Subject> subject = subjectService.getSubjectById(SUBJECT_ID_NON_EXISTENT);

		assertFalse(subject.isPresent());
	}

	@Test
	public void testGetNewSubjectId() {
		int newId = subjectService.getNewSubjectId();

		assertEquals(newId, SUBJECT_ID2 + 1);
	}

	@Test
	public void testGetAllSubjectDTOs() {
		List<SubjectDTO> subjectDtos = subjectService.getAllSubjectDTOs();

		boolean correctListSize = subjectDtos.size() == 2;
		boolean correctIDs = subjectDtos.get(0).getId() == SUBJECT_ID1
			&& subjectDtos.get(1).getId() == SUBJECT_ID2;

		assertTrue(correctListSize && correctIDs);
	}

	@Test
	public void testGetSubjectIdFromDisplayStr() {
		int subjectId = subjectService.getSubjectIdFromDisplayStr(DISPLAY_STR);

		assertEquals(subjectId, 42);
	}

	private Subject makeNewSubject(int id) {
		return new SubjectBuilder()
			.withId(id)
			.withTitle("subjectTitle")
			.withDateCreated(LocalDateTime.now())
			.build();
	}
}
