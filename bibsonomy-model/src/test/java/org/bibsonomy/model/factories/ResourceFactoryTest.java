/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.model.factories;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Resource;
import org.hamcrest.Matcher;
import org.junit.Test;

/**
 * tests for {@link ResourceFactory}
 *
 * @author dzo
 */
public class ResourceFactoryTest {
	
	private static final ResourceFactory factory = new ResourceFactory();
	
	/**
	 * tests getResourceClass
	 */
	@Test
	public void testGetResourceClass() {
		assertEquals(Resource.class, ResourceFactory.getResourceClass("all"));
		assertEquals(Resource.class, ResourceFactory.getResourceClass("ALL"));
		
		assertEquals(Bookmark.class, ResourceFactory.getResourceClass("bookmark"));
		assertEquals(Bookmark.class, ResourceFactory.getResourceClass("BOOKMARK"));
		
		assertEquals(BibTex.class, ResourceFactory.getResourceClass("bibtex"));
		assertEquals(BibTex.class, ResourceFactory.getResourceClass("BIBTEX"));
		
		assertEquals(BibTex.class, ResourceFactory.getResourceClass("publication"));
		assertEquals(BibTex.class, ResourceFactory.getResourceClass("PUBLICATION"));
		
		assertEquals(GoldStandardPublication.class, ResourceFactory.getResourceClass("goldstandardPublication"));
		assertEquals(GoldStandardPublication.class, ResourceFactory.getResourceClass("GOLDSTANDARDPUBLICATION"));
		
		assertEquals(GoldStandardBookmark.class, ResourceFactory.getResourceClass("goldStandardBookmark"));
		assertEquals(GoldStandardBookmark.class, ResourceFactory.getResourceClass("GOLDSTANDARDBOOKMARK"));
	}
	
	/**
	 * creates a new bookmark
	 */
	@Test
	public void testCreateBookmark() {
		final Resource createResource = factory.createResource(Bookmark.class);
		assertEquals(Bookmark.class, createResource.getClass());
	}
	
	/**
	 * creates a new publication
	 */
	@Test
	public void testCreatePublication() {
		final Resource createResource = factory.createResource(BibTex.class);
		assertEquals(BibTex.class, createResource.getClass());
	}
	
	/**
	 * creates a new goldstandard publication
	 */
	@Test
	public void testCreateGoldStandardPublication() {
		final Resource createResource = factory.createResource(GoldStandardPublication.class);
		assertEquals(GoldStandardPublication.class, createResource.getClass());
	}
	
	/**
	 * tests {@link ResourceFactory#createGoldStandardBookmark()}
	 */
	@Test
	public void testCreateGoldStandardBookmark() {
		final Resource createResource = factory.createResource(GoldStandardBookmark.class);
		assertEquals(GoldStandardBookmark.class, createResource.getClass());
	}
	
	/**
	 * create a new resource (should throw an exception)
	 */
	@Test(expected = UnsupportedResourceTypeException.class)
	public void testCreateResource() {
		factory.createResource(Resource.class);
	}
	
	/**
	 * null test
	 */
	@Test(expected = UnsupportedResourceTypeException.class)
	public void testCreateNull() {
		factory.createResource(null);
	}

	/**
	 * tests {@link ResourceFactory#findSuperiorResourceClass(Class)}
	 */
	@Test
	public void testFindSuperiorResourceClass() {
		final Class<? extends Resource> superiorResourceClass = ResourceFactory.findSuperiorResourceClass(GoldStandardPublication.class);
		final Matcher<Class<? extends Resource>> publicationClassMatcher = is(equalTo(BibTex.class));
		assertThat(superiorResourceClass, publicationClassMatcher);

		final Class<? extends Resource> publicationSuperior = ResourceFactory.findSuperiorResourceClass(BibTex.class);
		assertThat(publicationSuperior, publicationClassMatcher);
	}
}
