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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;

/**
 * @author dzo
 */
public class DeserializerFactory extends BeanDeserializerFactory {
	private static final long serialVersionUID = -3016780764419888216L;
	
	/**
	 * @param config
	 */
	public DeserializerFactory(DeserializerFactoryConfig config) {
		super(config);
	}
	
	@Override
	public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
		final Class<?> enumClass = type.getRawClass();
		return new EnumDeserializer(constructEnumResolver(enumClass, ctxt.getConfig(), beanDesc.findJsonValueMethod()));
	}
	
	@Override
	public com.fasterxml.jackson.databind.deser.DeserializerFactory withConfig(DeserializerFactoryConfig config) {
		return new DeserializerFactory(config);
	}
}
