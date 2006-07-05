package org.bibsonomy.rest.renderer.xml;

import java.math.BigInteger;

import junit.framework.TestCase;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.exceptions.InvalidXMLException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ModelFactoryTest extends TestCase
{
	private ModelFactory modelFactory;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		modelFactory = ModelFactory.getInstance();
	}
	
	public void testCreateUser()
	{
		// check invalid user
		UserType xmlUser = new UserType();
		try 
		{
			modelFactory.createUser( xmlUser );
			fail( "exception should have been thrown." );
		}
		catch( InvalidXMLException e )
		{
			if( !"The body part of the received XML document is not valid: username is missing".equals( e.getMessage() ) )
				fail( "wrong exception thrown: " + e.getMessage() );
		}
		// check valid user
		xmlUser.setName( "test" );
		User user = modelFactory.createUser( xmlUser );
		assertTrue( "model not correctly initialized", "test".equals( user.getName() ) );
	}
	
	public void testCreateGroup()
	{
		// check invalid user
		GroupType xmlGroup = new GroupType();
		try 
		{
			modelFactory.createGroup( xmlGroup );
			fail( "exception should have been thrown." );
		}
		catch( InvalidXMLException e )
		{
			if( !"The body part of the received XML document is not valid: groupname is missing".equals( e.getMessage() ) )
				fail( "wrong exception thrown: " + e.getMessage() );
		}
		// check valid user
		xmlGroup.setName( "test" );
		Group group = modelFactory.createGroup( xmlGroup );
		assertTrue( "model not correctly initialized", "test".equals( group.getName() ) );
	}
   
   public void testCreateTag()
   {
      // check invalid tag
      TagType xmlTag = new TagType();
      try
      {
         modelFactory.createTag( xmlTag );
      }
      catch( InvalidXMLException e )
      {
         if( !"The body part of the received XML document is not valid: tag name is missing".equals( e.getMessage() ) )
            fail( "wrong exception thrown: " + e.getMessage() );
      }
      // check valid tag
      xmlTag.setName( "foo" );
      Tag tag = modelFactory.createTag( xmlTag );
      assertTrue( "tag not correctly initailized", "foo".equals( tag.getName() ) );
      xmlTag.setGlobalcount( BigInteger.ONE );
      xmlTag.setUsercount( BigInteger.TEN );
      tag = modelFactory.createTag( xmlTag );
      assertTrue( "tag not correctly initailized", tag.getGlobalcount() == 1 );
      assertTrue( "tag not correctly initailized", tag.getUsercount() == 10 );
   }
	
	public void testCreatePost()
	{
		// check invalid posts
		PostType xmlPost = new PostType();
		checkInvalidPost( xmlPost, "The body part of the received XML document is not valid: no tags specified" );
		TagType xmlTag = new TagType();
		xmlPost.getTag().add( xmlTag );
		checkInvalidPost( xmlPost, "The body part of the received XML document is not valid: user is missing" );
		UserType xmlUser = new UserType();
		xmlUser.setName( "tuser" );
		xmlPost.setUser( xmlUser );
		checkInvalidPost( xmlPost, "The body part of the received XML document is not valid: resource is missing" );
		BookmarkType xmlBookmark = new BookmarkType();
		xmlPost.setBookmark( xmlBookmark );
		checkInvalidPost( xmlPost, "The body part of the received XML document is not valid: tag name is missing" );
		xmlTag.setName( "testtag" );
		checkInvalidPost( xmlPost, "The body part of the received XML document is not valid: url is missing" );
		xmlBookmark.setUrl( "http://www.google.de" );
		xmlPost.setBibtex( new BibtexType() );
		checkInvalidPost( xmlPost, "The body part of the received XML document is not valid: only one resource is allowed" );
		xmlPost.setBibtex( null );
		
		// check valid post with bookmark
		Post post = modelFactory.createPost( xmlPost );
		assertTrue( "model not correctly initialized", "tuser".equals( post.getUser().getName() ) );
		assertTrue( "model not correctly initialized", post.getResource() instanceof Bookmark );
		assertTrue( "model not correctly initialized", "http://www.google.de".equals( ((Bookmark)post.getResource()).getUrl() ) );
		assertTrue( "model not correctly initialized", "testtag".equals( ((Tag)post.getTags().iterator().next()).getName() ) );
		
		xmlPost.setBookmark( null );
		BibtexType xmlBibtex = new BibtexType();
		xmlPost.setBibtex( xmlBibtex );
		checkInvalidPost( xmlPost, "The body part of the received XML document is not valid: title is missing" );
		xmlBibtex.setTitle( "foo bar" );
		
		// check valid post with bibtex
		post = modelFactory.createPost( xmlPost );
		assertTrue( "model not correctly initialized", "tuser".equals( post.getUser().getName() ) );
		assertTrue( "model not correctly initialized", post.getResource() instanceof BibTex );
		assertTrue( "model not correctly initialized", "foo bar".equals( ((BibTex)post.getResource()).getTitle() ) );
		assertTrue( "model not correctly initialized", "testtag".equals( ((Tag)post.getTags().iterator().next()).getName() ) );
	}
	
	private void checkInvalidPost( PostType xmlPost, String exceptionMessage )
	{
		try 
		{
			modelFactory.createPost( xmlPost );
			fail( "exception should have been thrown." );
		}
		catch( InvalidXMLException e )
		{
			if( !e.getMessage().equals( exceptionMessage ) )
			{
				System.out.println( e.getMessage() );
				fail( "wrong exception thrown: " + e.getMessage() );
			}
		}
	}
}

/*
 * $Log$
 * Revision 1.4  2006-07-05 15:27:51  mbork
 * place constants on left side of comparison
 *
 * Revision 1.3  2006/07/05 15:20:14  mbork
 * implemented missing strategies, little changes on datamodel --> alpha :)
 *
 * Revision 1.2  2006/06/08 16:14:35  mbork
 * Implemented some XMLRenderer functions, including unit-tests. introduced djunitplugin (see http://works.dgic.co.jp/djunit/index.html)
 *
 * Revision 1.1  2006/06/06 17:39:30  mbork
 * implemented a modelfactory which parses incoming xml-requests and then generates the intern model
 *
 */