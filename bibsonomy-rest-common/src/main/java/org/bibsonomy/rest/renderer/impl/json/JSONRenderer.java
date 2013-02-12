/**
 *
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

import java.io.Reader;
import java.io.Writer;

import javax.xml.datatype.XMLGregorianCalendar;

import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.AbstractRenderer;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * {@link Renderer} rendering JSON using Jackson
 * 
 * @author dzo
 * @version $Id$
 */
public class JSONRenderer extends AbstractRenderer {

	private final ObjectMapper mapper;
	
	/**
	 * default constructor with an {@link UrlRenderer} to build links
	 * @param urlRenderer
	 */
	public JSONRenderer(final UrlRenderer urlRenderer) {
		super(urlRenderer);
		this.mapper = new ObjectMapper();
		this.mapper.setSerializationInclusion(Include.NON_NULL);
		this.mapper.setSerializationInclusion(Include.NON_EMPTY);
		
		final SimpleModule module = new SimpleModule("BibSonomy Module", new Version(2, 1, 0, null, null, null));
		module.addSerializer(new EnumSerializer());
		module.addSerializer(new ISO8601DateTimeSerializer());
		module.addDeserializer(XMLGregorianCalendar.class, new ISO8601DateTimeDeserializer());
		
		this.mapper.registerModule(module);
	}

	@Override
	protected void serialize(final Writer writer, final BibsonomyXML xmlDoc) {
		try {
			this.mapper.writeValue(writer, xmlDoc);
		} catch (final Exception ex) {
			throw new BadRequestOrResponseException(ex);
		}
	}

	@Override
	protected BibsonomyXML parse(final Reader reader) {
		this.checkReader(reader);
		try {
			return this.mapper.readValue(reader, BibsonomyXML.class);
		} catch (final Exception ex) {
			throw new BadRequestOrResponseException(ex);
		}
	}

}
