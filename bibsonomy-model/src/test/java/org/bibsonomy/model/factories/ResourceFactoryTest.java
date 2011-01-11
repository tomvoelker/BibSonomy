/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
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

package org.bibsonomy.model.factories;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
public class ResourceFactoryTest {
	
	private static ResourceFactory factory;
	
	/**
	 * inits the factory
	 */
	@BeforeClass
	public static void initFactory() {
		factory = new ResourceFactory();
	}
	
	/**
	 * creates a new bookmark
	 */
	@Test
	public void createBookmark() {
		final Resource createResource = factory.createResource(Bookmark.class);
		assertEquals(Bookmark.class, createResource.getClass());
	}
	
	/**
	 * creates a new publication
	 */
	@Test
	public void createPublication() {
		final Resource createResource = factory.createResource(BibTex.class);
		assertEquals(BibTex.class, createResource.getClass());
	}
	
	/**
	 * creates a new goldstandard publication
	 */
	@Test
	public void createGoldStandardPublication() {
		final Resource createResource = factory.createResource(GoldStandardPublication.class);
		assertEquals(GoldStandardPublication.class, createResource.getClass());
	}
	
	/**
	 * create a new resource (should throw an exception)
	 */
	@Test(expected = UnsupportedResourceTypeException.class)
	public void createResource() {
		factory.createResource(Resource.class);
	}
	
	/**
	 * null test
	 */
	@Test(expected = UnsupportedResourceTypeException.class)
	public void createNull() {
		factory.createResource(null);
	}
}
