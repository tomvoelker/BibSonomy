package org.bibsonomy.rest.renderer.impl.json;

import java.io.IOException;

import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author dzo
 * @version $Id$
 */
public class ISO8601DateTimeSerializer extends StdSerializer<XMLGregorianCalendar> {

	/**
	 * constuctor
	 */
	public ISO8601DateTimeSerializer() {
		super(XMLGregorianCalendar.class);
	}
	
	@Override
	public void serialize(final XMLGregorianCalendar value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
		jgen.writeString(value.toXMLFormat());
	}

}
