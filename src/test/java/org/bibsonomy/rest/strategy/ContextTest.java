package org.bibsonomy.rest.strategy;

import java.util.HashMap;
import java.util.Set;

import junit.framework.TestCase;

import org.bibsonomy.rest.LogicInterface;
import org.bibsonomy.rest.database.TestDatabase;
import org.bibsonomy.rest.enums.HttpMethod;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id $
 */
public class ContextTest extends TestCase 
{
	private LogicInterface db;
	private HashMap<String, String[]> parameterMap;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		parameterMap = new HashMap<String, String[]> ();
		this.db = new TestDatabase();
	}
	
	public void testGetSimpleTags()
	{
		parameterMap.put( "tags", new String[]{ "foo+bar" } );
		Context c = new Context( db, HttpMethod.GET.toString(), "/users/egal/posts", parameterMap );
		
		Set<String> tags = c.getTags( "tags" );
		assertTrue( "tag parameters are not correctly splitted!", tags.contains( "foo" ) );
		assertTrue( "tag parameters are not correctly splitted!", tags.contains( "bar" ) );
		assertTrue( "tag parameters are not correctly splitted!", tags.size() == 2 );
	}
	
	public void testGetTags()
	{
		parameterMap.put( "tags", new String[]{ "foo+bar+->subtags+-->transitiveSubtags+supertags->+transitiveSupertags-->+<->correlated" } );
		Context c = new Context( db, HttpMethod.GET.toString(), "/users/egal/posts", parameterMap );
		
		Set<String> tags = c.getTags( "tags" );
		assertTrue( "tag parameters are not correctly splitted!", tags.contains( "foo" ) );
		assertTrue( "tag parameters are not correctly splitted!", tags.contains( "bar" ) );
		assertTrue( "tag parameters are not correctly splitted!", tags.contains( "->subtags" ) );
		assertTrue( "tag parameters are not correctly splitted!", tags.contains( "-->transitiveSubtags" ) );
		assertTrue( "tag parameters are not correctly splitted!", tags.contains( "supertags->" ) );
		assertTrue( "tag parameters are not correctly splitted!", tags.contains( "transitiveSupertags-->" ) );
		assertTrue( "tag parameters are not correctly splitted!", tags.contains( "<->correlated" ) );
		assertTrue( "tag parameters are not correctly splitted!", tags.size() == 7 );
	}
}

/*
 * $Log$
 * Revision 1.3  2006-06-05 14:14:12  mbork
 * implemented GET strategies
 *
 */