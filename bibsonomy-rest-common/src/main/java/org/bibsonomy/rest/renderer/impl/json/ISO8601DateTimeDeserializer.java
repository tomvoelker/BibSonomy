package org.bibsonomy.rest.renderer.impl.json;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author dzo
 * @version $Id$
 */
public class ISO8601DateTimeDeserializer extends StdDeserializer<XMLGregorianCalendar> {
	private static final long serialVersionUID = -5734348718664258211L;
	
	/**
	 * constuctor
	 */
	public ISO8601DateTimeDeserializer() {
		super(XMLGregorianCalendar.class);
	}

	@Override
	public XMLGregorianCalendar deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
		final Calendar date = DatatypeConverter.parseDate(jp.getValueAsString());
		final GregorianCalendar c = new GregorianCalendar();
		c.setTime(date.getTime());
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (final DatatypeConfigurationException ex) {
			throw new JsonMappingException("can't init datatype factory", ex);
		}
	}

}
