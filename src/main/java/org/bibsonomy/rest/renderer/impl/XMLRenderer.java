package org.bibsonomy.rest.renderer.impl;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestException;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.BibtexType;
import org.bibsonomy.rest.renderer.xml.BookmarkType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.ModelFactory;
import org.bibsonomy.rest.renderer.xml.ObjectFactory;
import org.bibsonomy.rest.renderer.xml.PostType;
import org.bibsonomy.rest.renderer.xml.PostsType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.rest.renderer.xml.TagsType;
import org.bibsonomy.rest.renderer.xml.UserType;
import org.bibsonomy.rest.renderer.xml.UsersType;

/**
 * this class creates xml documents valid to the xsd schema and vice-versa.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class XMLRenderer implements Renderer
{
	private static XMLRenderer renderer;

	private XMLRenderer()
	{
	}

	public void serializePosts( Writer writer, Set<Post> posts, ViewModel viewModel ) throws InternServerException
	{
		PostsType xmlPosts = new PostsType();
		xmlPosts.setEnd( BigInteger.valueOf( viewModel.getEndValue() ) );
		xmlPosts.setNext( viewModel.getUrlToNextResources() );
		xmlPosts.setStart( BigInteger.valueOf( viewModel.getStartValue() ) );
		
		for( Post post: posts )
		{
			PostType xmlPost = new PostType();
			// set user
			UserType xmlUser = new UserType();
			xmlUser.setName( post.getUser().getName() );
//			xmlUser.setHref( createHrefForUser( post.getUser().getName() ) ); TODO
			xmlPost.setUser( xmlUser );
			
			// add tags
			for( Tag t: post.getTags() )
			{
				TagType xmlTag = new TagType();
				xmlTag.setName( t.getName() );
				xmlPost.getTag().add( xmlTag );
			}
			
			// add groups
			for( Group group: post.getGroups() )
			{
				GroupType xmlGroup = new GroupType();
				xmlGroup.setName( group.getName() );
//				xmlGroup.setHref( createHrefForGroup() ); // TODO
				xmlPost.getGroup().add( xmlGroup );
			}
			
			xmlPost.setDescription( post.getDescription() );
			
			if( post.getResource() instanceof Bookmark )
			{
				Bookmark bookmark = (Bookmark)post.getResource();
				BookmarkType xmlBookmark = new BookmarkType();
//				xmlBookmark.setHref( ); // TODO
				xmlBookmark.setIntrahash( bookmark.getIntraHash() );
//				xmlBookmark.setTimestamp( ); // TODO
				xmlBookmark.setUrl( bookmark.getUrl() );
				xmlPost.setBookmark( xmlBookmark );
			}
			if( post.getResource() instanceof BibTex )
			{
				BibTex bibtex = (BibTex)post.getResource();
				BibtexType xmlBibtex = new BibtexType();
				xmlBibtex.setAuthors( bibtex.getAuthors() );
				xmlBibtex.setEditors( bibtex.getEditors() );
//				xmlBibtex.setHref( ); // TODO
				xmlBibtex.setIntrahash( bibtex.getIntraHash() );
//				xmlBibtex.setTimestamp(); // TODO
				xmlBibtex.setTitle( bibtex.getTitle() );
				xmlBibtex.setType( bibtex.getType() );
				xmlBibtex.setYear( bibtex.getYear() );
				xmlPost.setBibtex( xmlBibtex );
			}
			xmlPosts.getPost().add( xmlPost );
		}
		BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setPosts( xmlPosts );
		serialize( writer, xmlDoc );
	}

	public void serializePost( Writer writer, Post post, ViewModel model )
	{
		// TODO Auto-generated method stub
	}

	public void serializeUsers( Writer writer, Set<User> users, ViewModel viewModel )
	{
		UsersType xmlUsers = new UsersType();
		xmlUsers.setEnd( BigInteger.valueOf( viewModel.getEndValue() ) );
		xmlUsers.setNext( viewModel.getUrlToNextResources() );
		xmlUsers.setStart( BigInteger.valueOf( viewModel.getStartValue() ) );
		
		for( User user: users )
		{
			UserType xmlUser = new UserType();
			xmlUser.setEmail( user.getEmail() );
			xmlUser.setHomepage( user.getHomepage() );
			xmlUser.setName( user.getName() );
			xmlUser.setRealname( user.getRealname() );
			xmlUser.setVersion( user.getTimestamp() );
//			xmlUser.setHref( createHrefForUser( user.getName() ) ); TODO
			
			xmlUsers.getUser().add( xmlUser );
		}
		BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setUsers( xmlUsers );
		serialize( writer, xmlDoc );
	}

	public void serializeUser( Writer writer, User user, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	public void serializeTags( Writer writer, Set<Tag> tags, ViewModel viewModel )
	{
		TagsType xmlTags = new TagsType();
		xmlTags.setEnd( BigInteger.valueOf( viewModel.getEndValue() ) );
		xmlTags.setNext( viewModel.getUrlToNextResources() );
		xmlTags.setStart( BigInteger.valueOf( viewModel.getStartValue() ) );
		
		for( Tag tag: tags )
		{
			TagType xmlTag = new TagType();
			xmlTag.setName( tag.getName() );
			xmlTag.setCount( BigInteger.valueOf( tag.getCount() ) );
			
			xmlTags.getTag().add( xmlTag );
		}
		BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setTags( xmlTags );
		serialize( writer, xmlDoc );
	}

	public void serializeTag( Writer writer, Tag tag, ViewModel model )
	{
		// TODO Auto-generated method stub
	}

	public void serializeGroups( PrintWriter writer, Set<Group> groups, ViewModel viewModel )
	{
		// TODO Auto-generated method stub
	}

	public void serializeGroup( Writer writer, Group group, ViewModel model )
	{
		// TODO Auto-generated method stub
	}
	
	public User parseUser( InputStream is ) // throws BadRequestException
	{
		if( is == null ) throw new BadRequestException( "The body part of the received document is missing" );
		
		BibsonomyXML xmlDoc = parse( is );
		
		if( xmlDoc.getUser() != null )
		{
			return ModelFactory.getInstance().createUser( xmlDoc.getUser() );
		}
		throw new BadRequestException( "The body part of the received document is erroneous - no user defined." );
	}

	public Post parsePost( InputStream is )
	{
		if( is == null ) throw new BadRequestException( "The body part of the received document is missing" );
		
		BibsonomyXML xmlDoc = parse( is );
		
		if( xmlDoc.getPost() != null )
		{
			return ModelFactory.getInstance().createPost( xmlDoc.getPost() );
		}
		throw new BadRequestException( "The body part of the received document is erroneous - no post defined." );
	}

	public Group parseGroup( InputStream is )
	{
		if( is == null ) throw new BadRequestException( "The body part of the received document is missing" );
		
		BibsonomyXML xmlDoc = parse( is );
		
		if( xmlDoc.getGroup() != null )
		{
			return ModelFactory.getInstance().createGroup( xmlDoc.getGroup() );
		}
		throw new BadRequestException( "The body part of the received document is erroneous - no group defined." );
	}
	
	/**
	 * initializes java xml bindings, builds the xml document and then marshalls it to the writer
	 * 
	 * @param writer
	 * @param xmlDoc
	 * @throws InternServerException
	 */
	private void serialize( Writer writer, BibsonomyXML xmlDoc ) throws InternServerException
	{
		try
		{
			// initialize context for java xml bindings
			JAXBContext jc = JAXBContext.newInstance( "org.bibsonomy.rest.renderer.xml" );
			
			// buildup xml document
			JAXBElement<BibsonomyXML> webserviceElement = ( new ObjectFactory() ).createBibsonomyXMLInterchangeDocument( xmlDoc );
	
			// create a marshaller
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			
			// marshal to the writer
			marshaller.marshal( webserviceElement, writer );
			marshaller.marshal( webserviceElement, System.out ); // TODO
		}
		catch( JAXBException e )
		{
			throw new InternServerException( e.toString() );
		}
	}
	
	private BibsonomyXML parse( InputStream is ) throws InternServerException
	{
        try
		{
			JAXBContext jc = JAXBContext.newInstance( "org.bibsonomy.rest.renderer.xml" );
			
			// create an Unmarshaller
			Unmarshaller u = jc.createUnmarshaller();

			/*
			 * unmarshal a xml instance document into a tree of Java content
			 * objects composed of classes from the restapi package. 
			 */
			JAXBElement<?> xmlDoc = ( JAXBElement<?> )u.unmarshal( is );
			return (BibsonomyXML)xmlDoc.getValue();
		}
		catch( JAXBException e )
		{
			throw new InternServerException( e.toString() );
		}
	}

	public static Renderer getInstance()
	{
		if( XMLRenderer.renderer == null )
		{
			renderer = new XMLRenderer();
		}
		return XMLRenderer.renderer;
	}
}

/*
 * $Log$
 * Revision 1.5  2006-06-07 19:37:28  mbork
 * implemented post queries
 *
 * Revision 1.4  2006/06/06 20:11:04  mbork
 * docu
 *
 * Revision 1.3  2006/06/06 17:39:29  mbork
 * implemented a modelfactory which parses incoming xml-requests and then generates the intern model
 *
 * Revision 1.2  2006/06/05 14:14:11  mbork
 * implemented GET strategies
 *
 * Revision 1.1  2006/05/24 15:18:08  cschenk
 * Introduced a rendering format and a factory that produces renderers (for xml, rdf, html)
 *
 * Revision 1.3  2006/05/24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.2  2006/05/21 20:31:51  mbork
 * continued implementing context
 *
 * Revision 1.1  2006/05/19 21:01:08  mbork
 * started implementing rest api
 *
 */