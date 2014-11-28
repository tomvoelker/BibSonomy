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
package org.bibsonomy.rest.renderer.impl.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.rest.renderer.impl.JAXBRenderer;

/**
 * This class creates xml documents valid to the xsd schema and vice-versa.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class XMLRenderer extends JAXBRenderer {
	
	private static final String JAXB_PACKAGE_DECLARATION = "org.bibsonomy.rest.renderer.xml";
	
	/**
	 * @param urlRenderer the url renderer to use
	 */
	public XMLRenderer(final UrlRenderer urlRenderer) {
		super(urlRenderer);
	}
	
	@Override
	protected JAXBContext getJAXBContext() throws JAXBException {
		/*
		 * XXX: initialize JAXB context. We provide the classloader here because
		 * we experienced that under certain circumstances (e.g. when used
		 * within JabRef as a JPF-Plugin), the wrong classloader is used which
		 * has the following exception as consequence:
		 * 	javax.xml.bind.JAXBException: "org.bibsonomy.rest.renderer.xml" doesnt contain ObjectFactory.class or jaxb.index
		 * 
		 * (see also http://ws.apache.org/jaxme/apidocs/javax/xml/bind/JAXBContext.html)
		 */
		return JAXBContext.newInstance(JAXB_PACKAGE_DECLARATION, this.getClass().getClassLoader());
	}
}