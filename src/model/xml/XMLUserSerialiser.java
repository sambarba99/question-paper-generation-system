package model.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.persisted.User;

import view.enums.UserPrivilege;
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
 * Singleton class providing StAX (Streaming API for XML) read/write methods for Users.
 *
 * @author Sam Barba
 */
public class XMLUserSerialiser implements XMLSerialiser {

	private static XMLUserSerialiser instance;

	private XMLUserSerialiser() {
	}

	public synchronized static XMLUserSerialiser getInstance() {
		if (instance == null) {
			instance = new XMLUserSerialiser();
		}
		return instance;
	}

	/**
	 * Retrieve all users from users.xml.
	 * 
	 * @return list of all users
	 */
	@Override
	public List<?> readAll() throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
		XMLEventReader reader = XMLInputFactory.newInstance()
			.createXMLEventReader(new FileInputStream(Constants.USERS_FILE_PATH));

		List<User> users = new ArrayList<>();
		User user = new User();

		while (reader.hasNext()) {
			XMLEvent nextEvent = reader.nextEvent();

			if (nextEvent.isStartElement()) {
				StartElement startElement = nextEvent.asStartElement();

				switch (startElement.getName().getLocalPart()) {
					case "user":
						user = new User();
						Attribute usernameAtt = startElement.getAttributeByName(new QName("username"));
						user.setUsername(usernameAtt.getValue());
						break;
					case "password":
						Attribute passwordAtt = startElement.getAttributeByName(new QName("value"));
						user.setPassword(passwordAtt.getValue());
						break;
					case "privilege":
						Attribute privilegeAtt = startElement.getAttributeByName(new QName("value"));
						user.setPrivilege(UserPrivilege.getFromStr(privilegeAtt.getValue()));
						break;
					case "dateCreated":
						Attribute dateAtt = startElement.getAttributeByName(new QName("value"));
						LocalDateTime dateCreated = LocalDateTime.parse(dateAtt.getValue(), Constants.DATE_FORMATTER);
						user.setDateCreated(dateCreated);
						break;
				}
			} else if (nextEvent.isEndElement()
				&& nextEvent.asEndElement().getName().getLocalPart().equals("user")) {
				// if reached </user> tag
				users.add(user);
			}
		}
		reader.close();
		return users;
	}

	/**
	 * Write users to users.xml.
	 * 
	 * @param users - the list of users to write
	 */
	public void write(List<?> users) throws XMLStreamException, FactoryConfigurationError, IOException {
		XMLStreamWriter writer = XMLOutputFactory.newInstance()
			.createXMLStreamWriter(new FileWriter(Constants.USERS_FILE_PATH));

		writer.writeStartDocument();
		writer.writeStartElement("users");

		for (User user : (List<User>) users) {
			writeUserElement(writer, user);
		}

		writer.writeEndElement(); // write </users> tag
		writer.writeEndDocument();
		writer.flush();
		writer.close();
	}

	/**
	 * Write an individual user element.
	 * 
	 * @param writer - the XMLStreamWriter used
	 * @param user   - the user to write
	 */
	private void writeUserElement(XMLStreamWriter writer, User user) throws XMLStreamException {
		writer.writeStartElement("user");
		writer.writeAttribute("username", user.getUsername());

		writer.writeStartElement("password");
		writer.writeAttribute("value", user.getPassword());
		writer.writeEndElement(); // write </password> tag

		writer.writeStartElement("privilege");
		writer.writeAttribute("value", user.getPrivilege().toString());
		writer.writeEndElement(); // write </privilege> tag

		writer.writeStartElement("dateCreated");
		writer.writeAttribute("value", Constants.DATE_FORMATTER.format(user.getDateCreated()));
		writer.writeEndElement(); // write </dateCreated> tag

		writer.writeEndElement(); // write </user> tag
	}
}
