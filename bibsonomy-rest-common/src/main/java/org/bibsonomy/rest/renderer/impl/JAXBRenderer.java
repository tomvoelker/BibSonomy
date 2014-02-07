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

package org.bibsonomy.rest.renderer.impl;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Reader;
import java.io.Writer;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.AbstractRenderer;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.ObjectFactory;
import org.xml.sax.SAXParseException;

/**
 * @author dzo
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * 
 */
public abstract class JAXBRenderer extends AbstractRenderer {
	private static final Log log = LogFactory.getLog(JAXBRenderer.class);
	
	private static void handleJAXBException(final JAXBException e) throws InternServerException {
		final Throwable linkedException = e.getLinkedException();
		if (present(linkedException) && (linkedException.getClass() == SAXParseException.class)) {
			final SAXParseException ex = (SAXParseException) linkedException;
			throw new BadRequestOrResponseException(
					"Error while parsing XML (Line " 
					+ ex.getLineNumber() + ", Column "
					+ ex.getColumnNumber() + ": "
					+ ex.getMessage()
			);
		}
		throw new InternServerException(e.toString());
	}
	
	private Schema schema;
	private boolean validateXMLInput;
	private boolean validateXMLOutput;
	
	/**
	 * defialt constructor
	 * @param urlRenderer
	 */
	protected JAXBRenderer(final UrlRenderer urlRenderer) {
		super(urlRenderer);
	}
	
	/**
	 * loads the {@link #schema} iff neccessary
	 */
	protected void loadSchema() {
		// we only need to load the XML schema if we validate input or output
		if ((this.validateXMLInput || this.validateXMLOutput) && this.schema == null) {
			try {
				schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(this.getClass().getClassLoader().getResource("xschema.xsd"));
			} catch (final Exception e) {
				log.error("Failed to load XML schema", e);
				schema = null;
			}
		} else {
			schema = null;
		}
	}

	/**
	 * Unmarshalls the document from the reader to the generated java
	 * model.
	 * 
	 * @return A BibsonomyXML object that contains the unmarshalled content
	 * @throws InternServerException
	 *             if the content can't be unmarshalled
	 */
	@Override
	protected BibsonomyXML parse(final Reader reader) throws InternServerException {
		// first: check the reader 
		this.checkReader(reader);
		try {
			// initialize JAXB context. We provide the classloader here because we experienced that under
			// certain circumstances (e.g. when used within JabRef as a JPF-Plugin), the wrong classloader is
			// used which has the following exception as consequence:
			//
			//   javax.xml.bind.JAXBException: "org.bibsonomy.rest.renderer.xml" doesnt contain ObjectFactory.class or jaxb.index
			//
			// (see also http://ws.apache.org/jaxme/apidocs/javax/xml/bind/JAXBContext.html)
			final JAXBContext jc = this.getJAXBContext();
			
			// create an Unmarshaller
			final Unmarshaller u = jc.createUnmarshaller();
			
			// set schema to validate input documents
			if (this.validateXMLInput) {
				u.setSchema(schema);
			}
			
			/*
			 * unmarshal a xml instance document into a tree of Java content
			 * objects composed of classes from the restapi package.
			 */
			final JAXBElement<BibsonomyXML> xmlDoc = this.unmarshal(u, reader);
			return xmlDoc.getValue();
		} catch (final JAXBException e) {
			handleJAXBException(e);
			return null; // never reached (handleJAXBExceptions throws an exception
		}
	}

	@SuppressWarnings("unchecked")
	protected JAXBElement<BibsonomyXML> unmarshal(final Unmarshaller u, final Reader reader) throws JAXBException {
		return (JAXBElement<BibsonomyXML>) u.unmarshal(reader);
	}

	/**
	 * Initializes java xml bindings, builds the document and then marshalls
	 * it to the writer.
	 * @param writer 
	 * @param xmlDoc 
	 * 
	 * @throws InternServerException
	 *             if the document can't be marshalled
	 */
	@Override
	protected void serialize(final Writer writer, final BibsonomyXML xmlDoc) throws InternServerException {
		try {
			// initialize context for java xml bindings
			final JAXBContext jc = this.getJAXBContext();

			// buildup document model
			final JAXBElement<BibsonomyXML> webserviceElement = new ObjectFactory().createBibsonomy(xmlDoc);

			// create a marshaller
			final Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			if (this.validateXMLOutput) {
				// validate the XML produced by the marshaller
				marshaller.setSchema(schema);
			}

			// marshal to the writer
			this.marshal(marshaller, webserviceElement, writer);
		} catch (final JAXBException e) {
			handleJAXBException(e);
		}
	}

	protected void marshal(final Marshaller marshaller, final JAXBElement<BibsonomyXML> webserviceElement, final Writer writer) throws JAXBException {
		marshaller.marshal(webserviceElement, writer);
	}

	protected abstract JAXBContext getJAXBContext() throws JAXBException;
	

	/**
	 * @param validateXMLInput the validateXMLInput to set
	 */
	public void setValidateXMLInput(final boolean validateXMLInput) {
		this.validateXMLInput = validateXMLInput;
		this.loadSchema();
	}

	/**
	 * @param validateXMLOutput the validateXMLOutput to set
	 */
	public void setValidateXMLOutput(final boolean validateXMLOutput) {
		this.validateXMLOutput = validateXMLOutput;
		this.loadSchema();
	}
}
