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
