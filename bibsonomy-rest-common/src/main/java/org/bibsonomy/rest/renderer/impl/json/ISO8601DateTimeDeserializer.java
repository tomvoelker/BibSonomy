/**
 *
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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
