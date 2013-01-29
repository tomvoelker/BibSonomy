package org.bibsonomy.rest.renderer.impl.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

/**
 * @author dzo
 * @version $Id$
 */
public class EnumSerializer extends StdScalarSerializer<Enum<?>> {
	
	/**
	 * default constructor
	 */
	public EnumSerializer() {
		super(Enum.class, false);
	}

	@Override
	public void serialize(final Enum<?> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeString(value.toString().toLowerCase()); // note: toLowerCase
	}

}
