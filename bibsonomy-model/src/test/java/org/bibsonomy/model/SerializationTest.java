/**  
 * 
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.model;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO: more tests
 * 
 * @author dzo
 * @version $Id$
 */
public class SerializationTest {
	
	private static List<Object> testValues;
	
	/**
	 * adds the test values
	 */
	@BeforeClass
	public static void setUpTestValues() {
		testValues = new LinkedList<Object>();
		testValues.add(new Tag());
		testValues.add(new BibTex());
		testValues.add(new Bookmark());
		testValues.add(new Post<Resource>());
		testValues.add(new User());
		testValues.add(new Group());
	}
	
	private static void writeObject(final Object object) throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
	    final ObjectOutputStream oos = new ObjectOutputStream(out);
	    oos.writeObject(object);
	    oos.close();
	}
	
	/**
	 * just try to write each test value
	 * 
	 * @throws Exception
	 */
	@Test
	public void serializePublication() throws Exception {
		for (final Object object : testValues) {
			try {
				writeObject(object);
			} catch (final Exception e) {
				fail("exception while serializing " + object.getClass().getSimpleName());
			}
		}
	}
}
