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

package org.bibsonomy.rest.renderer.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.ObjectFactory;

/**
 * @author dzo
 * @version $Id$
 */
public class XMLRendererTest extends JAXBRendererTest {
	
	private final static Renderer RENDERER = new XMLRenderer(new UrlRenderer("http://www.bibsonomy.org/api/"));

	@Override
	public String getPathToTestFiles() {
		return "src/test/resources/xmlrenderer/";
	}

	@Override
	public String getFileExt() {
		return ".xml";
	}

	@Override
	public Renderer getRenderer() {
		return RENDERER;
	}

	@Override
	protected void marshalToFile(final BibsonomyXML bibXML, final File tmpFile) throws JAXBException, PropertyException, FileNotFoundException {
		final JAXBContext jc = JAXBContext.newInstance(JAXBRenderer.JAXB_PACKAGE_DECLARATION);
		final JAXBElement<BibsonomyXML> webserviceElement = new ObjectFactory().createBibsonomy(bibXML);
		final Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(webserviceElement, new FileOutputStream(tmpFile));
	}

	@Override
	protected String getQuotingTestString() {
		return "http://foo.bar/posts?start=1&end=2&resourcetype=bookmark&tags=a+->b+<-c+<->d&hash=asd&&&kjalsjdf";
	}
}
