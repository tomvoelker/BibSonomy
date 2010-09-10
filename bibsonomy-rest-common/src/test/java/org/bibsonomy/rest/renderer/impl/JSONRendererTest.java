/**
 *
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group,
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

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.junit.BeforeClass;

/**
 * @author dzo
 * @version $Id$
 */
public class JSONRendererTest extends JAXBRendererTest {
	
	@BeforeClass
	public static void setRenderer() {
		renderer = new JSONRenderer();
		pathToTestFiles = "src/test/resources/jsonrenderer/";
		fileExt = ".json";
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testParseUser()
	 */
	@Override
	public void testParseUser() throws Exception {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testParseGroup()
	 */
	@Override
	public void testParseGroup() throws Exception {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testParsePost()
	 */
	@Override
	public void testParsePost() throws Exception {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testParseReferences()
	 */
	@Override
	public void testParseReferences() throws Exception {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testParseStandardPost()
	 */
	@Override
	public void testParseStandardPost() throws Exception {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#marshalToFile(org.bibsonomy.rest.renderer.xml.BibsonomyXML, java.io.File)
	 */
	@Override
	protected void marshalToFile(BibsonomyXML bibXML, File tmpFile) throws JAXBException, PropertyException, FileNotFoundException {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testSerializeTags()
	 */
	@Override
	public void testSerializeTags() throws Exception {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testSerializeUsers()
	 */
	@Override
	public void testSerializeUsers() throws Exception {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testSerializeUser()
	 */
	@Override
	public void testSerializeUser() throws Exception {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testSerializeGroups()
	 */
	@Override
	public void testSerializeGroups() throws Exception {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testSerializeGroup()
	 */
	@Override
	public void testSerializeGroup() throws Exception {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testSerializePosts()
	 */
	@Override
	public void testSerializePosts() throws Exception {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testSerializePost()
	 */
	@Override
	public void testSerializePost() {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testSerializeGoldStandardPost()
	 */
	@Override
	public void testSerializeGoldStandardPost() {
		// TODO: implement test
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.impl.JAXBRendererTest#testQuoting()
	 */
	@Override
	public void testQuoting() {
		// TODO: implement test
	}
}
