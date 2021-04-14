package model.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.persisted.Answer;
import model.persisted.Question;

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
 * Singleton class providing StAX (Streaming API for XML) read/write methods for Questions.
 *
 * @author Sam Barba
 */
public class XMLQuestionSerialiser implements XMLSerialiser {

	private static XMLQuestionSerialiser instance;

	private XMLQuestionSerialiser() {
	}

	public synchronized static XMLQuestionSerialiser getInstance() {
		if (instance == null) {
			instance = new XMLQuestionSerialiser();
		}
		return instance;
	}

	/**
	 * Retrieve all questions from questions.xml.
	 * 
	 * @return list of all questions
	 */
	@Override
	public List<?> readAll() throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
		XMLEventReader reader = XMLInputFactory.newInstance()
			.createXMLEventReader(new FileInputStream(Constants.QUESTIONS_FILE_PATH));

		List<Question> questions = new ArrayList<>();
		Question question = new Question();
		Answer answer = new Answer();

		while (reader.hasNext()) {
			XMLEvent nextEvent = reader.nextEvent();

			if (nextEvent.isStartElement()) {
				StartElement startElement = nextEvent.asStartElement();

				switch (startElement.getName().getLocalPart()) {
					case "question":
						question = new Question();
						Attribute idAtt = startElement.getAttributeByName(new QName("id"));
						question.setId(Integer.parseInt(idAtt.getValue()));
						break;
					case "subjectId":
						Attribute subjectIdAtt = startElement.getAttributeByName(new QName("value"));
						question.setSubjectId(Integer.parseInt(subjectIdAtt.getValue()));
						break;
					case "statement":
						nextEvent = reader.nextEvent();
						question.setStatement(nextEvent.asCharacters().getData());
						break;
					case "answer":
						answer = new Answer();
						Attribute correctAtt = startElement.getAttributeByName(new QName("correct"));
						answer.setCorrect(Boolean.parseBoolean(correctAtt.getValue()));
						break;
					case "value":
						nextEvent = reader.nextEvent();
						answer.setValue(nextEvent.asCharacters().getData());
						break;
					case "bloomSkillLevel":
						Attribute skillLvlAtt = startElement.getAttributeByName(new QName("value"));
						question.setSkillLevel(BloomSkillLevel.getFromStr(skillLvlAtt.getValue()));
						break;
					case "marks":
						Attribute marksAtt = startElement.getAttributeByName(new QName("value"));
						question.setMarks(Integer.parseInt(marksAtt.getValue()));
						break;
					case "minutesRequired":
						Attribute minutesAtt = startElement.getAttributeByName(new QName("value"));
						question.setMinutesRequired(Integer.parseInt(minutesAtt.getValue()));
						break;
					case "dateCreated":
						Attribute dateAtt = startElement.getAttributeByName(new QName("value"));
						LocalDateTime dateCreated = LocalDateTime.parse(dateAtt.getValue(), Constants.DATE_FORMATTER);
						question.setDateCreated(dateCreated);
						break;
				}
			} else if (nextEvent.isEndElement()) {
				String elementName = nextEvent.asEndElement().getName().getLocalPart();

				switch (elementName) {
					case "answer": // if reached </answer> tag
						question.getAnswers().add(answer);
						break;
					case "question": // if reached </question> tag
						questions.add(question);
						break;
				}
			}
		}
		reader.close();
		return questions;
	}

	/**
	 * Write questions to questions.xml.
	 * 
	 * @param questions - the list of questions to write
	 */
	@Override
	public void write(List<?> questions) throws XMLStreamException, FactoryConfigurationError, IOException {
		XMLStreamWriter writer = XMLOutputFactory.newInstance()
			.createXMLStreamWriter(new FileWriter(Constants.QUESTIONS_FILE_PATH));

		writer.writeStartDocument();
		writer.writeStartElement("questions");

		for (Question question : (List<Question>) questions) {
			writeQuestionElement(writer, question);
		}

		writer.writeEndElement(); // write </questions> tag
		writer.writeEndDocument();
		writer.flush();
		writer.close();
	}

	/**
	 * Write an individual question element.
	 * 
	 * @param writer   - the XMLStreamWriter used
	 * @param question - the question to write
	 */
	private void writeQuestionElement(XMLStreamWriter writer, Question question) throws XMLStreamException {
		writer.writeStartElement("question");
		writer.writeAttribute("id", Integer.toString(question.getId()));

		writer.writeStartElement("subjectId");
		writer.writeAttribute("value", Integer.toString(question.getSubjectId()));
		writer.writeEndElement(); // write </subjectId> tag

		writer.writeStartElement("statement");
		writer.writeCharacters(question.getStatement());
		writer.writeEndElement(); // write </statement> tag

		writer.writeStartElement("answers");
		for (Answer answer : question.getAnswers()) {
			writer.writeStartElement("answer");
			writer.writeAttribute("correct", Boolean.toString(answer.isCorrect()));

			writer.writeStartElement("value");
			writer.writeCharacters(answer.getValue());
			writer.writeEndElement(); // write </value> tag

			writer.writeEndElement(); // write </answer> tag
		}
		writer.writeEndElement(); // write </answers> tag

		writer.writeStartElement("bloomSkillLevel");
		writer.writeAttribute("value", question.getSkillLevel().toString());
		writer.writeEndElement(); // write </bloomSkillLevel> tag

		writer.writeStartElement("marks");
		writer.writeAttribute("value", Integer.toString(question.getMarks()));
		writer.writeEndElement(); // write </marks> tag

		writer.writeStartElement("minutesRequired");
		writer.writeAttribute("value", Integer.toString(question.getMinutesRequired()));
		writer.writeEndElement(); // write </minutesRequired> tag

		writer.writeStartElement("dateCreated");
		writer.writeAttribute("value", Constants.DATE_FORMATTER.format(question.getDateCreated()));
		writer.writeEndElement(); // write </dateCreated> tag

		writer.writeEndElement(); // write </question> tag
	}
}
