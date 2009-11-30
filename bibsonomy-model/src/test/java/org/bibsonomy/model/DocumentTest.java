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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class DocumentTest {

	/**
	 * tests that userName is always set to lowercase
	 * 
	 * @see UserTest#name()
	 */
	@Test
	public void userName() {
		assertEquals(null, new Document().getUserName());

		final Document doc = new Document();
		doc.setUserName("TeStUsEr");
		assertEquals("testuser", doc.getUserName());
		doc.setUserName(null);
		assertNull(doc.getUserName());
	}
}