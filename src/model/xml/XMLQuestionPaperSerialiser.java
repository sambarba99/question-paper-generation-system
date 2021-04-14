package model.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.persisted.QuestionPaper;

import view.enums.BloomSkillLevel;
import view.utils.Constants;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Singleton class providing StAX (Streaming API for XML) read/write methods for Question Papers.
 *
 * @author Sam Barba
 */
public class XMLQuestionPaperSerialiser implements XMLSerialiser {

	private static XMLQuestionPaperSerialiser instance;

	private XMLQuestionPaperSerialiser() {
	}

	public synchronized static XMLQuestionPaperSerialiser getInstance() {
		if (instance == null) {
			instance = new XMLQuestionPaperSerialiser();
		}
		return instance;
	}

	/**
	 * Retrieve all papers from questionpapers.xml.
	 * 
	 * @return list of all question papers
	 */
	@Override
	public List<?> readAll() throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
		XMLEventReader reader = XMLInputFactory.newInstance()
			.createXMLEventReader(new FileInputStream(Constants.QUESTION_PAPERS_FILE_PATH));

		List<QuestionPaper> questionPapers = new ArrayList<>();
		QuestionPaper questionPaper = new QuestionPaper();

		while (reader.hasNext()) {
			XMLEvent nextEvent = reader.nextEvent();

			if (nextEvent.isStartElement()) {
				StartElement startElement = nextEvent.asStartElement();

				switch (startElement.getName().getLocalPart()) {
					case "questionPaper":
						questionPaper = new QuestionPaper();
						Attribute idAtt = startElement.getAttributeByName(new QName("id"));
						questionPaper.setId(Integer.parseInt(idAtt.getValue()));
						break;
					case "subjectId":
						Attribute subjectIdAtt = startElement.getAttributeByName(new QName("value"));
						questionPaper.setSubjectId(Integer.parseInt(subjectIdAtt.getValue()));
						break;
					case "title":
						Attribute titleAtt = startElement.getAttributeByName(new QName("value"));
						questionPaper.setTitle(titleAtt.getValue());
						break;
					case "courseTitle":
						Attribute courseTitleAtt = startElement.getAttributeByName(new QName("value"));
						questionPaper.setCourseTitle(courseTitleAtt.getValue());
						break;
					case "courseCode":
						Attribute courseCodeAtt = startElement.getAttributeByName(new QName("value"));
						questionPaper.setCourseCode(courseCodeAtt.getValue());
						break;
					case "questionId":
						Attribute questionIdAtt = startElement.getAttributeByName(new QName("value"));
						int questionId = Integer.parseInt(questionIdAtt.getValue());
						questionPaper.getQuestionIds().add(questionId);
						break;
					case "bloomSkillLevel":
						Attribute skillLvlAtt = startElement.getAttributeByName(new QName("value"));
						questionPaper.setSkillLevel(BloomSkillLevel.getFromStr(skillLvlAtt.getValue()));
						break;
					case "marks":
						Attribute marksAtt = startElement.getAttributeByName(new QName("value"));
						questionPaper.setMarks(Integer.parseInt(marksAtt.getValue()));
						break;
					case "minutesRequired":
						Attribute minutesAtt = startElement.getAttributeByName(new QName("value"));
						questionPaper.setMinutesRequired(Integer.parseInt(minutesAtt.getValue()));
						break;
					case "dateCreated":
						Attribute dateAtt = startElement.getAttributeByName(new QName("value"));
						LocalDateTime dateCreated = LocalDateTime.parse(dateAtt.getValue(), Constants.DATE_FORMATTER);
						questionPaper.setDateCreated(dateCreated);
						break;
				}
			} else if (nextEvent.isEndElement()
				&& nextEvent.asEndElement().getName().getLocalPart().equals("questionPaper")) {
				// if reached </questionPaper> tag
				questionPapers.add(questionPaper);
			}
		}
		reader.close();
		return questionPapers;
	}

	/**
	 * Write question papers to questionpapers.xml.
	 * 
	 * @param questionPapers - the list of papers to write
	 */
	@Override
	public void write(List<?> questionPapers) throws XMLStreamException, FactoryConfigurationError, IOException {
		XMLStreamWriter writer = XMLOutputFactory.newInstance()
			.createXMLStreamWriter(new FileWriter(Constants.QUESTION_PAPERS_FILE_PATH));

		writer.writeStartDocument();
		writer.writeStartElement("questionPapers");

		for (QuestionPaper paper : (List<QuestionPaper>) questionPapers) {
			writePaperElement(writer, paper);
		}

		writer.writeEndElement(); // write </questionPapers> tag
		writer.writeEndDocument();
		writer.flush();
		writer.close();
	}

	/**
	 * Write an individual question paper element.
	 * 
	 * @param writer        - the XMLStreamWriter used
	 * @param questionPaper - the paper to write
	 */
	private void writePaperElement(XMLStreamWriter writer, QuestionPaper questionPaper) throws XMLStreamException {
		writer.writeStartElement("questionPaper");
		writer.writeAttribute("id", Integer.toString(questionPaper.getId()));

		writer.writeStartElement("subjectId");
		writer.writeAttribute("value", Integer.toString(questionPaper.getSubjectId()));
		writer.writeEndElement(); // write </subjectId> tag

		writer.writeStartElement("title");
		writer.writeAttribute("value", questionPaper.getTitle());
		writer.writeEndElement(); // write </title> tag

		writer.writeStartElement("courseTitle");
		writer.writeAttribute("value", questionPaper.getCourseTitle());
		writer.writeEndElement(); // write </courseTitle> tag

		writer.writeStartElement("courseCode");
		writer.writeAttribute("value", questionPaper.getCourseCode());
		writer.writeEndElement(); // write </courseCode> tag

		writer.writeStartElement("questionIds");
		for (Integer id : questionPaper.getQuestionIds()) {
			writer.writeStartElement("questionId");
			writer.writeAttribute("value", Integer.toString(id));
			writer.writeEndElement(); // write </questionId> tag
		}
		writer.writeEndElement(); // write </questionIds> tag

		writer.writeStartElement("bloomSkillLevel");
		writer.writeAttribute("value", questionPaper.getSkillLevel().toString());
		writer.writeEndElement(); // write </bloomSkillLevel> tag

		writer.writeStartElement("marks");
		writer.writeAttribute("value", Integer.toString(questionPaper.getMarks()));
		writer.writeEndElement(); // write </marks> tag

		writer.writeStartElement("minutesRequired");
		writer.writeAttribute("value", Integer.toString(questionPaper.getMinutesRequired()));
		writer.writeEndElement(); // write </minutesRequired> tag

		writer.writeStartElement("dateCreated");
		writer.writeAttribute("value", Constants.DATE_FORMATTER.format(questionPaper.getDateCreated()));
		writer.writeEndElement(); // write </dateCreated> tag

		writer.writeEndElement(); // write </questionPaper> tag
	}
}
