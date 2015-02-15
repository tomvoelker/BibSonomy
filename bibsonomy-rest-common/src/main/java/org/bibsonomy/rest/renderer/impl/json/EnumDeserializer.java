/**
 * BibSonomy-Rest-Common - Common things for the REST-client and server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
