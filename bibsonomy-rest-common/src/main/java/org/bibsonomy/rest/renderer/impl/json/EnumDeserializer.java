package org.bibsonomy.rest.renderer.impl.json;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.util.EnumResolver;

/**
 * @author dzo
 * @version $Id$
 */
public class EnumDeserializer extends com.fasterxml.jackson.databind.deser.std.EnumDeserializer {
	private static final long serialVersionUID = 6805828183224444369L;
	
	/**
	 * default constructor
	 * @param enumResolver 
	 */
	public EnumDeserializer(final EnumResolver<?> enumResolver) {
		super(enumResolver);
	}

	@Override
	public Enum<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		final JsonToken curr = jp.getCurrentToken();
		if (curr == JsonToken.VALUE_STRING || curr == JsonToken.FIELD_NAME) {
			final String name = jp.getText().toUpperCase();
			
			final Enum<?> result = this._resolver.findEnum(name);
			if (present(result)) {
				return result;
			}
		}
		
		return super.deserialize(jp, ctxt);
	}
}
