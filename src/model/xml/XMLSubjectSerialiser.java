package model.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.persisted.Subject;

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
 * Singleton class providing StAX (Streaming API for XML) read/write methods for Subjects.
 *
 * @author Sam Barba
 */
public class XMLSubjectSerialiser implements XMLSerialiser {

	private static XMLSubjectSerialiser instance;

	private XMLSubjectSerialiser() {
	}

	public synchronized static XMLSubjectSerialiser getInstance() {
		if (instance == null) {
			instance = new XMLSubjectSerialiser();
		}
		return instance;
	}

	/**
	 * Retrieve all subjects from subjects.xml.
	 * 
	 * @return list of all subjects
	 */
	@Override
	public List<?> readAll() throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
		XMLEventReader reader = XMLInputFactory.newInstance()
			.createXMLEventReader(new FileInputStream(Constants.SUBJECTS_FILE_PATH));

		List<Subject> subjects = new ArrayList<>();
		Subject subject = new Subject();

		while (reader.hasNext()) {
			XMLEvent nextEvent = reader.nextEvent();

			if (nextEvent.isStartElement()) {
				StartElement startElement = nextEvent.asStartElement();

				switch (startElement.getName().getLocalPart()) {
					case "subject":
						subject = new Subject();
						Attribute idAtt = startElement.getAttributeByName(new QName("id"));
						subject.setId(Integer.parseInt(idAtt.getValue()));
						break;
					case "title":
						Attribute titleAtt = startElement.getAttributeByName(new QName("value"));
						subject.setTitle(titleAtt.getValue());
						break;
					case "dateCreated":
						Attribute dateAtt = startElement.getAttributeByName(new QName("value"));
						LocalDateTime dateCreated = LocalDateTime.parse(dateAtt.getValue(), Constants.DATE_FORMATTER);
						subject.setDateCreated(dateCreated);
						break;
				}
			} else if (nextEvent.isEndElement()
				&& nextEvent.asEndElement().getName().getLocalPart().equals("subject")) {
				// if reached </subject> tag
				subjects.add(subject);
			}
		}
		reader.close();
		return subjects;
	}

	/**
	 * Write subjects to subjects.xml.
	 * 
	 * @param subjects - the list of subjects to write
	 */
	@Override
	public void write(List<?> subjects) throws XMLStreamException, FactoryConfigurationError, IOException {
		XMLStreamWriter writer = XMLOutputFactory.newInstance()
			.createXMLStreamWriter(new FileWriter(Constants.SUBJECTS_FILE_PATH));

		writer.writeStartDocument();
		writer.writeStartElement("subjects");

		for (Subject subject : (List<Subject>) subjects) {
			writeSubjectElement(writer, subject);
		}

		writer.writeEndElement(); // write </subjects> tag
		writer.writeEndDocument();
		writer.flush();
		writer.close();
	}

	/**
	 * Write an individual subject element.
	 * 
	 * @param writer  - the XMLStreamWriter used
	 * @param subject - the subject to write
	 */
	private void writeSubjectElement(XMLStreamWriter writer, Subject subject) throws XMLStreamException {
		writer.writeStartElement("subject");
		writer.writeAttribute("id", Integer.toString(subject.getId()));

		writer.writeStartElement("title");
		writer.writeAttribute("value", subject.getTitle());
		writer.writeEndElement(); // write </title> tag

		writer.writeStartElement("dateCreated");
		writer.writeAttribute("value", Constants.DATE_FORMATTER.format(subject.getDateCreated()));
		writer.writeEndElement(); // write </dateCreated> tag

		writer.writeEndElement(); // write </subject> tag
	}
}
