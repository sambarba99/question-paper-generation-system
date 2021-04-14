package model.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

/**
 * Provides methods to implement by other XML read/write classes.
 *
 * @author Sam Barba
 */
public interface XMLSerialiser {

	List<?> readAll() throws FileNotFoundException, XMLStreamException, FactoryConfigurationError;

	void write(List<?> elements) throws XMLStreamException, FactoryConfigurationError, IOException;
}
