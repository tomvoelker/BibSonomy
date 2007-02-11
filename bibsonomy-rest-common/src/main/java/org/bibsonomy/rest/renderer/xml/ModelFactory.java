package org.bibsonomy.rest.renderer.xml;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.exceptions.InvalidXMLException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ModelFactory
{
	private static ModelFactory modelFactory;
	
	private ModelFactory()
	{}
	
	public static ModelFactory getInstance()
	{
		if( ModelFactory.modelFactory == null )
		{
			ModelFactory.modelFactory = new ModelFactory();
		}
		return ModelFactory.modelFactory;
	}

	/**
	 * @param xmlUser
	 * @return
	 */
	public User createUser( UserType xmlUser )
	{
		validateUser( xmlUser );
		
		User user = new User();
		user.setEmail( xmlUser.getEmail() );
		try
      {
         user.setHomepage( new URL( xmlUser.getHomepage() ) ); // FIXME move into Factory
      }
      catch( MalformedURLException e )
      {
      }
		user.setName( xmlUser.getName() );
		user.setRealname( xmlUser.getRealname() );
		
		return user;
	}
	
	/**
	 * @param xmlUser
	 * @return
	 */
	public Group createGroup( GroupType xmlGroup )
	{
		validateGroup( xmlGroup );
		
		Group group = new Group();
		group.setName( xmlGroup.getName() );
		group.setDescription( xmlGroup.getDescription() );
		
		return group;
	}
	
	public Tag createTag( TagType xmlTag )
	{
		validateTag( xmlTag );
		
		Tag tag = new Tag();
		tag.setName( xmlTag.getName() );
      if( xmlTag.getGlobalcount() != null ) tag.setCount( xmlTag.getGlobalcount().intValue() ); //TODO tag count
      if( xmlTag.getUsercount() != null ) tag.setUsercount( xmlTag.getUsercount().intValue() ); //TODO tag count
		
		return tag;
	}

	/**
	 * @param xmlPost
	 * @return
	 */
	public Post<Resource> createPost( PostType xmlPost )
	{
		validatePost( xmlPost );
		
		// post itself
		Post<Resource> post = new Post<Resource>();
		post.setDescription( xmlPost.getDescription() );
		
		// user
		User user = new User();
		UserType xmlUser = xmlPost.getUser();
		validateUser( xmlUser );
		user.setName( xmlUser.getName() );
		post.setUser( user );
		
		// tags
		for( TagType xmlTag: xmlPost.getTag() )
		{
			validateTag( xmlTag );
			
			Tag tag = new Tag();
			tag.setName( xmlTag.getName() );
			post.getTags().add( tag );
		}
		
		// resource
		if( xmlPost.getBibtex() != null )
		{
			BibtexType xmlBibtex = xmlPost.getBibtex();
			validateBibTex( xmlBibtex );
			
			BibTex bibtex = new BibTex();
			bibtex.setAuthor( xmlBibtex.getAuthors() );
			bibtex.setEditor( xmlBibtex.getEditors() );
			bibtex.setIntraHash( xmlBibtex.getIntrahash() );
         bibtex.setInterHash( xmlBibtex.getIntrahash() );
			bibtex.setTitle( xmlBibtex.getTitle() );
			bibtex.setType( xmlBibtex.getType() );
			bibtex.setYear( xmlBibtex.getYear() );
			
			post.setResource( bibtex );
		}
		if( xmlPost.getBookmark() != null )
		{
			BookmarkType xmlBookmark = xmlPost.getBookmark();
			validateBookmark( xmlBookmark );
			
			Bookmark bookmark = new Bookmark();
			bookmark.setIntraHash( xmlBookmark.getIntrahash() );
			bookmark.setUrl( xmlBookmark.getUrl() );
			
			post.setResource( bookmark );
		}
		return post;
	}
	

	private void validateUser( UserType xmlUser )
	{
		if( xmlUser.getName() == null || xmlUser.getName().length() == 0 ) throw new InvalidXMLException( "username is missing" );
	}

	private void validateGroup( GroupType xmlGroup )
	{
		if( xmlGroup.getName() == null || xmlGroup.getName().length() == 0  ) throw new InvalidXMLException( "groupname is missing" );
	}
	
	private void validateTag( TagType xmlTag )
	{
		if( xmlTag.getName() == null || xmlTag.getName().length() == 0 ) throw new InvalidXMLException( "tag name is missing" );
	}
	
	private void validatePost( PostType xmlPost )
	{
		if( xmlPost.getTag() == null ) throw new InvalidXMLException( "list of tags is missing" );
		if( xmlPost.getTag().size() == 0 ) throw new InvalidXMLException( "no tags specified" );
		if( xmlPost.getUser() == null ) throw new InvalidXMLException( "user is missing" );
		BibtexType xmlBibtex = xmlPost.getBibtex();
		BookmarkType xmlBookmark = xmlPost.getBookmark();
		if( xmlBibtex == null && xmlBookmark == null )
		{
			throw new InvalidXMLException( "resource is missing" );
		}
		else if( xmlBibtex != null && xmlBookmark != null )
		{
			throw new InvalidXMLException( "only one resource is allowed" );
		}
		else
		{
			 // just fine: ( xmlBibtex == null && xmlBookmark != null ) || ( xmlBibtex != null || xmlBookmark == null ) <=> bibtex xor bookmark
		}
	}
	
	private void validateBookmark( BookmarkType xmlBookmark )
	{
		if( xmlBookmark.getUrl() == null ) throw new InvalidXMLException( "url is missing" );
		// do not test hash value - it depends on the request if its available, so we check it later
		// if( xmlBookmark.getIntrahash() == null ) throw new InvalidXMLException( "hash is missing" );
	}

	private void validateBibTex( BibtexType xmlBibtex )
	{
		if( xmlBibtex.getTitle() == null ) throw new InvalidXMLException( "title is missing" );
		// do not test hash value - it depends on the request if its available, so we check it later
		// if( xmlBibtex.getIntrahash() == null ) throw new InvalidXMLException( "hash is missing" );
	}
}

/*
 * $Log$
 * Revision 1.4  2007-02-11 18:35:20  mbork
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
 * Revision 1.1  2006/10/10 12:42:16  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.6  2006/07/05 15:20:14  mbork
 * implemented missing strategies, little changes on datamodel --> alpha :)
 *
 * Revision 1.5  2006/06/28 14:50:50  mbork
 * bugfix: missing interhash
 *
 * Revision 1.4  2006/06/09 14:18:44  mbork
 * implemented xml renderer
 *
 * Revision 1.3  2006/06/08 16:14:36  mbork
 * Implemented some XMLRenderer functions, including unit-tests. introduced djunitplugin (see http://works.dgic.co.jp/djunit/index.html)
 *
 * Revision 1.2  2006/06/07 18:22:31  mbork
 * client api: finished implementing get and delete requests
 *
 * Revision 1.1  2006/06/06 17:39:30  mbork
 * implemented a modelfactory which parses incoming xml-requests and then generates the intern model
 *
 */