package model.service;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import model.dao.QuestionPaperDAO;
import model.dto.QuestionPaperDTO;
import model.persisted.Question;
import model.persisted.QuestionPaper;
import model.persisted.Subject;

import view.SystemNotification;
import view.enums.SystemNotificationType;
import view.utils.Constants;

/**
 * This class is a singleton, the use of which is to perform any functionality regarding question
 * papers.
 *
 * @author Sam Barba
 */
public class QuestionPaperService {

    private static QuestionPaperService instance;

    private QuestionPaperDAO questionPaperDao = QuestionPaperDAO.getInstance();

    private QuestionPaperService(QuestionPaperDAO questionPaperDao) {
        if (questionPaperDao == null) {
            throw new IllegalArgumentException("Question paper DAO cannot be null!");
        }
        this.questionPaperDao = questionPaperDao;
    }

    public synchronized static QuestionPaperService getInstance() {
        if (instance == null) {
            instance = new QuestionPaperService(QuestionPaperDAO.getInstance());
        }
        return instance;
    }

    /**
     * Add a question paper to the question papers CSV file.
     * 
     * @param questionPaper - the question paper to add
     */
    public void addQuestionPaper(QuestionPaper questionPaper) {
        questionPaper.setDateCreated(LocalDateTime.now());
        questionPaperDao.addQuestionPaper(questionPaper);
    }

    /**
     * Delete a question paper by its unique ID.
     * 
     * @param id - the ID of the paper to delete
     */
    public void deleteQuestionPaperById(int id) {
        questionPaperDao.deleteQuestionPaperById(id);
    }

    /**
     * Retrieve all question papers from question papers CSV file.
     */
    public List<QuestionPaper> getAllQuestionPapers() {
        return questionPaperDao.getAllQuestionPapers();
    }

    /**
     * Retrieve question paper by its unique ID.
     * 
     * @param id - the ID of the paper to retrieve
     * @return the question paper with the specified ID
     */
    public Optional<QuestionPaper> getQuestionPaperById(int id) {
        return getAllQuestionPapers().stream()
            .filter(qp -> qp.getId() == id)
            .findFirst();
    }

    /**
     * Retrieve all papers containing specified question ID.
     * 
     * @param questionId - ID of the question to search for
     * @return list of papers containing question ID
     */
    public List<QuestionPaper> getQuestionPapersByQuestionId(int questionId) {
        return getAllQuestionPapers().stream()
            .filter(qp -> qp.getQuestionIds().contains(questionId))
            .collect(Collectors.toList());
    }

    /**
     * Get a new question paper ID, to be used when generating a new question paper to ensure
     * uniqueness.
     * 
     * @returns highest existing paper ID
     */
    public int getNewQuestionPaperId() {
        List<QuestionPaper> allQuestionPapers = getAllQuestionPapers();
        return allQuestionPapers.isEmpty() ? 1
            : allQuestionPapers.stream().max(Comparator.comparing(QuestionPaper::getId)).get().getId() + 1;
    }

    /**
     * Get all question papers converted to DTOs for using in TableViews, with applied subject ID
     * filters (if any selected in AcademicMaterialManagement).
     * 
     * @param subjectIds - subject IDs to filter by
     * @return (filtered) list of all question papers as DTOs
     */
    public List<QuestionPaperDTO> getQuestionPaperDTOsWithSubjectFilter(List<Integer> subjectIds) {
        /*
         * If the subject IDs list is empty, then it means the user does not want to filter by
         * subject. This is why the subjectIds.isEmpty() condition in a logical disjunction (||).
         */
        return getAllQuestionPapers().stream()
            .filter(qp -> subjectIds.isEmpty() || subjectIds.contains(qp.getSubjectId()))
            .map(this::convertToQuestionPaperDTO)
            .collect(Collectors.toList());
    }

    /**
     * Convert a question paper to its DTO equivalent.
     * 
     * @param questionPaper - the paper to convert
     * @return the equivalent QuestionPaperDTO
     */
    private QuestionPaperDTO convertToQuestionPaperDTO(QuestionPaper questionPaper) {
        Optional<Subject> subjectOpt = SubjectService.getInstance().getSubjectById(questionPaper.getSubjectId());
        String subjectTitle = subjectOpt.isPresent() ? subjectOpt.get().getTitle() : Constants.SUBJECT_DELETED;

        QuestionPaperDTO questionPaperDto = new QuestionPaperDTO();
        questionPaperDto.setId(questionPaper.getId());
        questionPaperDto.setTitle(questionPaper.getTitle());
        questionPaperDto.setSubjectTitle(subjectTitle);
        questionPaperDto.setCourse(questionPaper.getCourseTitle() + " (" + questionPaper.getCourseCode() + ")");
        questionPaperDto.setDateCreated(Constants.DATE_FORMATTER.format(questionPaper.getDateCreated()));

        return questionPaperDto;
    }

    /**
     * Get a formatted question paper string for question paper TextArea object, or to export to
     * '.txt'.
     * 
     * @param questionPaper - the paper to format
     * @return question paper as a string
     */
    public String getQuestionPaperDisplayStr(QuestionPaper questionPaper) {
        Optional<Subject> subjectOpt = SubjectService.getInstance().getSubjectById(questionPaper.getSubjectId());
        String subjectTitle = subjectOpt.isPresent() ? subjectOpt.get().getTitle() : Constants.SUBJECT_DELETED;

        StringBuilder resultBld = new StringBuilder();
        resultBld.append(questionPaper.toString());
        resultBld.append("\nSubject: " + subjectTitle);
        resultBld.append("\nCourse: " + questionPaper.getCourseTitle() + " (" + questionPaper.getCourseCode() + ")");
        resultBld.append("\nApprox. Bloom skill level: " + questionPaper.getSkillLevel().getIntVal() + "/6");
        resultBld.append("\nMarks: " + questionPaper.getMarks());
        resultBld.append("\nApprox. duration: " + questionPaper.getMinutesRequired() + " mins");

        List<Integer> questionIds = questionPaper.getQuestionIds();
        int numQ = questionIds.size();
        for (int i = 0; i < questionIds.size(); i++) {
            resultBld.append("\n\nQuestion " + (i + 1) + "/" + numQ);

            Optional<Question> questionOpt = QuestionService.getInstance().getQuestionById(questionIds.get(i));

            if (questionOpt.isPresent()) {
                Question question = questionOpt.get();
                resultBld.append(" (" + question.getMarks() + " marks). " + question.getStatement() + "\n");

                for (int j = 0; j < question.getAnswers().size(); j++) {
                    resultBld.append(
                        "\n(" + ((char) (Constants.ASCII_A + j)) + ") " + question.getAnswers().get(j).getValue());
                }
            } else {
                resultBld.append("\n" + Constants.QUESTION_DELETED);
            }
        }

        return resultBld.toString();
    }

    /**
     * Save a question paper as a text file.
     * 
     * @param questionPaper - the paper to export
     * @param directory     - the destination directory
     * @return whether or not the operation was successful
     */
    public boolean exportToTxt(QuestionPaper questionPaper, String directory) {
        try {
            String[] dateTimeCreated = Constants.DATE_FORMATTER.format(questionPaper.getDateCreated()).split(" ");
            String date = dateTimeCreated[0].replace("/", "-");

            String fileName = directory + "\\" + questionPaper.getId() + "-"
                + questionPaper.getTitle().replace(" ", "-") + "-" + date + Constants.TXT_EXT;

            File txtFile = new File(fileName);
            if (!txtFile.exists()) {
                txtFile.getParentFile().mkdirs();
                txtFile.createNewFile();

                FileWriter writer = new FileWriter(txtFile, false); // append = false
                writer.write(getQuestionPaperDisplayStr(questionPaper));
                writer.flush();
                writer.close();
                return true;
            } else {
                SystemNotification.display(SystemNotificationType.ERROR,
                    "A paper with that name already exists at that directory.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            SystemNotification.display(SystemNotificationType.ERROR,
                Constants.UNEXPECTED_ERROR + e.getClass().getName());
        }
        return false;
    }
}
