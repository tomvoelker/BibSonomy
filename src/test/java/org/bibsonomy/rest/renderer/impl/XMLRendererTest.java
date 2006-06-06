package org.bibsonomy.rest.renderer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import junit.framework.TestCase;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.exceptions.BadRequestException;
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
		renderer = new XMLRenderer();
	}

	public void testParseUser() throws Exception
	{
		// check null behavior
		try 
		{
			renderer.parseUser( null );
			fail( "exception should have been thrown." );
		}
		catch( BadRequestException e )
		{
		}
		
		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile( "bibsonomy", "junit" );
		marshalToFile( bibXML, tmpFile );
		
		try 
		{
			renderer.parseUser( new FileInputStream( tmpFile ) );
			fail( "exception should have been thrown." );
		}
		catch( BadRequestException e )
		{
			if( !e.getMessage().equals( "The body part of the received document is erroneous - no user defined." ) )
				fail( "wrong exception thrown: " + e.getMessage() );
		}
		
		// check valid user
		bibXML = new BibsonomyXML();
		UserType userType = new UserType();
		userType.setName( "test" );
		bibXML.setUser( userType );
		tmpFile = File.createTempFile( "bibsonomy", "junit" );
		marshalToFile( bibXML, tmpFile );
		User user = renderer.parseUser( new FileInputStream( tmpFile ) );
		assertTrue( "model not correctly initialized", user.getName().equals( "test" ) );
	}
	
	public void testParseGroup() throws Exception
	{
		// check null behavior
		try 
		{
			renderer.parseGroup( null );
			fail( "exception should have been thrown." );
		}
		catch( BadRequestException e )
		{
		}
		
		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile( "bibsonomy", "junit" );
		marshalToFile( bibXML, tmpFile );
		
		try 
		{
			renderer.parseGroup( new FileInputStream( tmpFile ) );
			fail( "exception should have been thrown." );
		}
		catch( BadRequestException e )
		{
			if( !e.getMessage().equals( "The body part of the received document is erroneous - no group defined." ) )
				fail( "wrong exception thrown: " + e.getMessage() );
		}
		
		// check valid user
		bibXML = new BibsonomyXML();
		GroupType groupType = new GroupType();
		groupType.setName( "test" );
		bibXML.setGroup( groupType );
		tmpFile = File.createTempFile( "bibsonomy", "junit" );
		marshalToFile( bibXML, tmpFile );
		Group group = renderer.parseGroup( new FileInputStream( tmpFile ) );
		assertTrue( "model not correctly initialized", group.getName().equals( "test" ) );
	}
	
	public void testParsePost() throws Exception
	{
		// check null behavior
		try 
		{
			renderer.parsePost( null );
			fail( "exception should have been thrown." );
		}
		catch( BadRequestException e )
		{
		}
		
		// check empty/ wrong document
		BibsonomyXML bibXML = new BibsonomyXML();
		File tmpFile = File.createTempFile( "bibsonomy", "junit" );
		marshalToFile( bibXML, tmpFile );
		
		try 
		{
			renderer.parsePost( new FileInputStream( tmpFile ) );
			fail( "exception should have been thrown." );
		}
		catch( BadRequestException e )
		{
			if( !e.getMessage().equals( "The body part of the received document is erroneous - no post defined." ) )
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
		renderer.parsePost( new FileInputStream( tmpFile ) );
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
	
}

/*
 * $Log$
 * Revision 1.1  2006-06-06 17:39:29  mbork
 * implemented a modelfactory which parses incoming xml-requests and then generates the intern model
 *
 */