package org.bibsonomy.rest.renderer.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import junit.framework.TestCase;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.BookmarkType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.ObjectFactory;
import org.bibsonomy.rest.renderer.xml.PostType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.rest.renderer.xml.UserType;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class XMLRendererTest extends TestCase
{
	private Renderer renderer;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		renderer = XMLRenderer.getInstance();
	}

	public void testParseUser() throws Exception
	{
		// check null behavior
		try 
		{
			renderer.parseUser( null );
			fail( "exception should have been thrown." );
		}
		catch( BadRequestOrResponseException e )
		{
		}
		
		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile( "bibsonomy", "junit" );
		marshalToFile( bibXML, tmpFile );
		
		try 
		{
			renderer.parseUser( new FileReader( tmpFile ) );
			fail( "exception should have been thrown." );
		}
		catch( BadRequestOrResponseException e )
		{
			if( !"The body part of the received document is erroneous - no user defined.".equals( e.getMessage() ) )
				fail( "wrong exception thrown: " + e.getMessage() );
		}
		
		// check valid user
		bibXML = new BibsonomyXML();
		UserType userType = new UserType();
		userType.setName( "test" );
		bibXML.setUser( userType );
		tmpFile = File.createTempFile( "bibsonomy", "junit" );
		marshalToFile( bibXML, tmpFile );
		User user = renderer.parseUser( new FileReader( tmpFile ) );
		assertTrue( "model not correctly initialized", "test".equals( user.getName() ) );
	}
	
	public void testParseGroup() throws Exception
	{
		// check null behavior
		try 
		{
			renderer.parseGroup( null );
			fail( "exception should have been thrown." );
		}
		catch( BadRequestOrResponseException e )
		{
		}
		
		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile( "bibsonomy", "junit" );
		marshalToFile( bibXML, tmpFile );
		
		try 
		{
			renderer.parseGroup( new FileReader( tmpFile ) );
			fail( "exception should have been thrown." );
		}
		catch( BadRequestOrResponseException e )
		{
			if( !"The body part of the received document is erroneous - no group defined.".equals( e.getMessage() ) )
				fail( "wrong exception thrown: " + e.getMessage() );
		}
		
		// check valid user
		bibXML = new BibsonomyXML();
		GroupType groupType = new GroupType();
		groupType.setName( "test" );
		bibXML.setGroup( groupType );
		tmpFile = File.createTempFile( "bibsonomy", "junit" );
		marshalToFile( bibXML, tmpFile );
		Group group = renderer.parseGroup( new FileReader( tmpFile ) );
		assertTrue( "model not correctly initialized", "test".equals( group.getName() ) );
	}
	
	public void testParsePost() throws Exception
	{
		// check null behavior
		try 
		{
			renderer.parsePost( null );
			fail( "exception should have been thrown." );
		}
		catch( BadRequestOrResponseException e )
		{
		}
		
		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile( "bibsonomy", "junit" );
		marshalToFile( bibXML, tmpFile );
		
		try 
		{
			renderer.parsePost( new FileReader( tmpFile ) );
			fail( "exception should have been thrown." );
		}
		catch( BadRequestOrResponseException e )
		{
			if( !"The body part of the received document is erroneous - no post defined.".equals( e.getMessage() ) )
			{
				System.out.println( e.getMessage() );
				fail( "wrong exception thrown: " + e.getMessage() );
			}
		}
		
		// check valid post
		/** 
		 * this is just a rudimentary test.</p>
		 * tests of the created post object belong to {@link org.bibsonomy.rest.renderer.xml.ModelFactoryTest} 
		 */
		bibXML = new BibsonomyXML();
		PostType xmlPost = new PostType();
		TagType xmlTag = new TagType();
		xmlPost.getTag().add( xmlTag );
		UserType xmlUser = new UserType();
		xmlUser.setName( "tuser" );
		xmlPost.setUser( xmlUser );
		BookmarkType xmlBookmark = new BookmarkType();
		xmlPost.setBookmark( xmlBookmark );
		xmlTag.setName( "testtag" );
		xmlBookmark.setUrl( "http://www.google.de" );
		bibXML.setPost( xmlPost );
		tmpFile = File.createTempFile( "bibsonomy", "junit" );
		marshalToFile( bibXML, tmpFile );
		renderer.parsePost( new FileReader( tmpFile ) );
	}
	
	/**
	 * @param bibXML
	 * @param tmpFile
	 * @throws JAXBException
	 * @throws PropertyException
	 * @throws FileNotFoundException
	 */
	private void marshalToFile( BibsonomyXML bibXML, File tmpFile ) throws JAXBException, PropertyException, FileNotFoundException
	{
		JAXBContext jc = JAXBContext.newInstance( "org.bibsonomy.rest.renderer.xml" );
		JAXBElement<BibsonomyXML> webserviceElement = ( new ObjectFactory() ).createBibsonomyXMLInterchangeDocument( bibXML );
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		
		marshaller.marshal( webserviceElement, new FileOutputStream( tmpFile ) );
	}
	
   public void testSerializeTags() throws Exception
   {
      // empty list
      StringWriter sw = new StringWriter( 100 );
      LinkedList<Tag> tags = new LinkedList<Tag>();
      renderer.serializeTags( sw, tags, null );
      compareWithFile( sw, "ExampleResultTags0.txt" );
      // empty list 2
      ViewModel vm = new ViewModel();
      vm.setStartValue( 0 );
      vm.setEndValue( 10 );
      vm.setUrlToNextResources( "http://www.bibsonomy.org/foo/bar" );
      sw = new StringWriter( 100 );
      renderer.serializeTags( sw, tags, vm );
      compareWithFile( sw, "ExampleResultTags1.txt" );
      // with tags
      sw = new StringWriter( 100 );
      Tag t1 = new Tag();
      tags.add( t1 );
      try
      {
         renderer.serializeTags( sw, tags, vm );
         fail( "exception should have been thrown: no tagname specified" );
      }
      catch( InternServerException e )
      {
      }
      t1.setName( "foo" );
      sw = new StringWriter( 100 );
      renderer.serializeTags( sw, tags, vm );
      compareWithFile( sw, "ExampleResultTags2.txt" );
      // with multiple tags
      Tag t2 = new Tag();
      t2.setName( "bar" );
      t2.setUsercount( 5 );
      t2.setCount( 10 );
      tags.add( t2 );
      sw = new StringWriter( 100 );
      renderer.serializeTags( sw, tags, vm );
      compareWithFile( sw, "ExampleResultTags3.txt" );
   }
   
   public void testSerializeTag() throws Exception
   {
      // empty tag
      StringWriter sw = new StringWriter( 100 );
      Tag tag = new Tag();
      try
      {
         renderer.serializeTag( sw, tag, null );
         fail( "exception should have been thrown: no tagname specified" );
      }
      catch( InternServerException e )
      {
      }
      tag.setName( "foo" );
      renderer.serializeTag( sw, tag, null );
      compareWithFile( sw, "ExampleResultTag.txt" );
   }
   
   public void testSerializeUsers() throws Exception
   {
      // empty user
      StringWriter sw = new StringWriter( 100 );
      LinkedList<User> users = new LinkedList<User>();
      renderer.serializeUsers( sw, users, null );
      compareWithFile( sw, "ExampleResultUsers0.txt" );
      //
      ViewModel vm = new ViewModel();
      vm.setStartValue( 20 );
      vm.setEndValue( 30 );
      vm.setUrlToNextResources( "http://www.bibsonomy.org/api/foo/bar" );
      User u1 = new User();
      users.add( u1 );
      try
      {
         renderer.serializeUsers( sw, users, null );
         fail( "exception should have been thrown: no username specified" );
      }
      catch( InternServerException e )
      {
      }
      sw = new StringWriter( 100 );
      u1.setName( "testName" );
      u1.setEmail( "mail@foo.bar" );
      u1.setHomepage( new URL( "http://foo.bar.com" ) );
      u1.setPassword( "raboof" );
      u1.setRealname( "Dr. FOO BaR" );
      User u2 = new User();
      u2.setName( "fooBar" );
      users.add( u2 );
      renderer.serializeUsers( sw, users, vm );
      compareWithFile( sw, "ExampleResultUsers1.txt" );
   }
   
   public void testSerializeUser() throws Exception
   {
      // empty user
      StringWriter sw = new StringWriter( 100 );
      User user = new User();
      try
      {
         renderer.serializeUser( sw, user, null );
         fail( "exception should have been thrown: no username specified" );
      }
      catch( InternServerException e )
      {
      }
      user.setName( "foo" );
      renderer.serializeUser( sw, user, null );
      compareWithFile( sw, "ExampleResultUser.txt" );
   }
   
   public void testSerializeGroups() throws Exception
   {
      // empty group
      StringWriter sw = new StringWriter( 100 );
      LinkedList<Group> groups = new LinkedList<Group>();
      renderer.serializeGroups( sw, groups, null );
      compareWithFile( sw, "ExampleResultGroups0.txt" );
      //
      ViewModel vm = new ViewModel();
      vm.setStartValue( 20 );
      vm.setEndValue( 30 );
      vm.setUrlToNextResources( "http://www.bibsonomy.org/api/foo/bar" );
      Group g1 = new Group();
      groups.add( g1 );
      try
      {
         renderer.serializeGroups( sw, groups, null );
         fail( "exception should have been thrown: no groupname specified" );
      }
      catch( InternServerException e )
      {
      }
      sw = new StringWriter( 100 );
      g1.setName( "testName" );
      g1.setDescription( "foo bar ..." );
      Group g2 = new Group();
      g2.setName( "testName2" );
      groups.add( g2 );
      renderer.serializeGroups( sw, groups, vm );
      compareWithFile( sw, "ExampleResultGroups1.txt" );
   }
   
   public void testSerializeGroup() throws Exception
   {
      // empty group
      StringWriter sw = new StringWriter( 100 );
      Group group = new Group();
      try
      {
         renderer.serializeGroup( sw, group, null );
         fail( "exception should have been thrown: no groupname specified" );
      }
      catch( InternServerException e )
      {
      }
      group.setName( "foo" );
      group.setDescription( "foo bar :)" );
      renderer.serializeGroup( sw, group, null );
      compareWithFile( sw, "ExampleResultGroup.txt" );
   }
   
   public void testSerializePosts() throws Exception
   {
      StringWriter sw = new StringWriter( 100 );
      List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
      renderer.serializePosts( sw, posts, null );
      sw = new StringWriter( 100 );
      ViewModel vm = new ViewModel();
      vm.setStartValue( 0 );
      vm.setEndValue( 10 );
      vm.setUrlToNextResources( "www.bibsonomy.org/foo/bar" );
      Post<Resource> post = new Post<Resource>();
      User user = new User();
      user.setName( "foo" );
      Group group = new Group();
      group.setName( "bar" );
      Tag tag = new Tag();
      tag.setName( "foobar" );
      post.setUser( user );
      post.getGroups().add( group );
      post.getTags().add( tag );
      BibTex bib = new BibTex();
      bib.setTitle( "foo and bar" );
      bib.setIntraHash( "abc" );
      bib.setInterHash( "abc" );
      post.setResource( bib );
      posts.add( post );
      Bookmark b = new Bookmark();
      b.setInterHash( "12345678" );
      b.setIntraHash( "12345678" );
      b.setUrl( "www.foobar.de" );
      Post<Resource> post2 = new Post<Resource>();
      post2.setResource( b );
      post2.setUser( user );
      post2.getTags().add( tag );
      posts.add( post2 );
      renderer.serializePosts( sw, posts, vm );
      compareWithFile( sw, "ExampleResultPosts.txt" );
   }
   
   public void testSerializePost() throws Exception
   {
      StringWriter sw = new StringWriter( 100 );
      Post<Resource> post = new Post<Resource>();
      try
      {
         renderer.serializePost( sw, post, null );
         fail( "exception should have been thrown: no user specified" );
      }
      catch( InternServerException e )
      {
      }
      User u = new User();
      u.setName( "foo" );
      post.setUser( u );
      try
      {
         renderer.serializePost( sw, post, null );
         fail( "exception should have been thrown: no tags assigned" );
      }
      catch( InternServerException e )
      {
      }
      Tag t = new Tag();
      t.setName( "bar" );
      post.getTags().add( t );
      try
      {
         renderer.serializePost( sw, post, null );
         fail( "exception should have been thrown: no ressource assigned" );
      }
      catch( InternServerException e )
      {
      }
      Bookmark b = new Bookmark();
      post.setResource( b );
      try
      {
         renderer.serializePost( sw, post, null );
         fail( "exception should have been thrown: bookmark has no url assigned" );
      }
      catch( InternServerException e )
      {
      }
      b.setUrl( "www.foobar.org" );
      b.setIntraHash( "aabbcc" );
      b.setInterHash( "1324356789" );
      renderer.serializePost( sw, post, null );
      compareWithFile( sw, "ExampleResultPost.txt" );
   }
   
   public void testQuoting() throws IOException
   {
      StringWriter sw = new StringWriter( 100 );
      List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
      Post<Resource> post = new Post<Resource>();
      posts.add( post );
      User u = new User();
      u.setName( "foo" );
      post.setUser( u );
      Tag t = new Tag();
      t.setName( "bar" );
      post.getTags().add( t );
      Bookmark b = new Bookmark();
      post.setResource( b );
      b.setUrl( "www.foobar.org" );
      b.setIntraHash( "aabbcc" );
      b.setInterHash( "1324356789" );
      ViewModel vm = new ViewModel();
      vm.setStartValue( 0 );
      vm.setEndValue( 1 );
      vm.setUrlToNextResources( "http://foo.bar/posts?start=1&end=2&resourcetype=bookmark&tags=a+->b+<-c+<->d&hash=asd&&&kjalsjdf" );
      renderer.serializePosts( sw, posts, vm );
      compareWithFile( sw, "QuotingTest.txt" );
   }
   
   private void compareWithFile( StringWriter sw, String filename ) throws IOException
   {
      StringBuffer sb = new StringBuffer( 200 );
      File file = new File( "src/test/java/org/bibsonomy/rest/renderer/impl/" + filename );
      BufferedReader br = new BufferedReader( new FileReader( file ) );
      String s;
      while( (s = br.readLine() ) != null )
      {
         sb.append( s + "\n" );
      }
      assertTrue( "output not as expected", sw.toString().equals( sb.toString() ) );
   }
}

/*
 * $Log$
 * Revision 1.7  2007-02-21 14:08:33  mbork
 * - included code generation of the schema in the maven2 build-lifecycle
 * - removed circular dependencies among the modules
 * - cleaned up the poms of the modules
 * - fixed failing unit-tests
 *
 * Revision 1.6  2007/02/16 16:12:42  mbork
 * fixed tests broken by the updates
 * added a test testing quotation of the urls in the xml
 *
 * Revision 1.5  2007/02/15 10:29:08  mbork
 * the LogicInterface now uses Lists instead of Sets
 * fixed use of generics
 *
 * Revision 1.4  2007/02/11 18:35:20  mbork
 * lazy instantiation of lists in the model.
 * we definitely need bidirectional links for the api to work proper!
 * fixed all unit tests, every test performs well.
 *
 * Revision 1.3  2007/02/11 17:55:39  mbork
 * switched REST-api to the 'new' datamodel, which does not deserve the name...
 *
 * Revision 1.2  2007/02/05 10:35:55  cschenk
 * Distributed code from the spielwiese among the modules
 *
 * Revision 1.1  2006/10/10 12:42:13  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.8  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.7  2006/09/16 18:17:50  mbork
 * added some new fake bibtex entries to demonstrate jabref plugin :)
 * fix of tests depiending on fake bibtex entries
 *
 * Revision 1.6  2006/07/05 15:27:51  mbork
 * place constants on left side of comparison
 *
 * Revision 1.5  2006/07/05 15:20:13  mbork
 * implemented missing strategies, little changes on datamodel --> alpha :)
 *
 * Revision 1.4  2006/06/11 11:42:47  mbork
 * added unit tests for rendering posts
 *
 * Revision 1.3  2006/06/08 16:14:35  mbork
 * Implemented some XMLRenderer functions, including unit-tests. introduced djunitplugin (see http://works.dgic.co.jp/djunit/index.html)
 *
 * Revision 1.2  2006/06/07 19:37:28  mbork
 * implemented post queries
 *
 * Revision 1.1  2006/06/06 17:39:29  mbork
 * implemented a modelfactory which parses incoming xml-requests and then generates the intern model
 *
 */