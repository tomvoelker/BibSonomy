package org.bibsonomy.rest.renderer.impl;

import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
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
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.BibtexType;
import org.bibsonomy.rest.renderer.xml.BookmarkType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.GroupsType;
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
	public static final String JAXB_PACKAGE_DECLARATION = "org.bibsonomy.rest.renderer.xml";
   private static XMLRenderer renderer;

	private XMLRenderer()
	{
	}

	public void serializePosts( Writer writer, Set<Post<Resource>> posts, ViewModel viewModel ) throws InternServerException
	{
		PostsType xmlPosts = new PostsType();
      if( viewModel != null )
      {
      	xmlPosts.setEnd( BigInteger.valueOf( viewModel.getEndValue() ) );
         if( viewModel.getUrlToNextResources() != null ) xmlPosts.setNext( viewModel.getUrlToNextResources() );
      	xmlPosts.setStart( BigInteger.valueOf( viewModel.getStartValue() ) );
      }
		for( Post<Resource> post: posts )
		{
			PostType xmlPost = createXmlPost( post );
			xmlPosts.getPost().add( xmlPost );
		}
		BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setPosts( xmlPosts );
		serialize( writer, xmlDoc );
	}

   public void serializePost( Writer writer, Post<Resource> post, ViewModel model ) throws InternServerException
   {
      BibsonomyXML xmlDoc = new BibsonomyXML();
      xmlDoc.setPost( createXmlPost( post ) );
      serialize( writer, xmlDoc );
   }

   private PostType createXmlPost( Post<Resource> post ) throws InternServerException
   {
      PostType xmlPost = new PostType();
      checkPost( post );
      // set user
      checkUser( post.getUser() );
      UserType xmlUser = new UserType();
      xmlUser.setName( post.getUser().getName() );
      xmlUser.setHref( createHrefForUser( post.getUser().getName() ) );
      xmlPost.setUser( xmlUser );
      
      // add tags
      for( Tag t: post.getTags() )
      {
         checkTag( t );
      	TagType xmlTag = new TagType();
      	xmlTag.setName( t.getName() );
      	xmlPost.getTag().add( xmlTag );
      }
      
      // add groups
      for( Group group: post.getGroups() )
      {
         checkGroup( group );
      	GroupType xmlGroup = new GroupType();
      	xmlGroup.setName( group.getName() );
      	xmlGroup.setHref( createHrefForGroup( group.getName() ) );
      	xmlPost.getGroup().add( xmlGroup );
      }
      
      xmlPost.setDescription( post.getDescription() );
      
      if( post.getResource() instanceof Bookmark )
      {
      	Bookmark bookmark = (Bookmark)post.getResource();
         checkBookmark( bookmark );
      	BookmarkType xmlBookmark = new BookmarkType();
      	xmlBookmark.setHref( createHrefForRessource( post.getUser().getName(), bookmark.getIntraHash() ) );
      	xmlBookmark.setInterhash( bookmark.getInterHash() );
         xmlBookmark.setIntrahash( bookmark.getIntraHash() );
      	xmlBookmark.setUrl( bookmark.getUrl() );
      	xmlPost.setBookmark( xmlBookmark );
      }
      if( post.getResource() instanceof BibTex )
      {
      	BibTex bibtex = (BibTex)post.getResource();
         checkBibtex( bibtex );
      	BibtexType xmlBibtex = new BibtexType();
      	xmlBibtex.setAuthors( bibtex.getAuthor() );
      	xmlBibtex.setEditors( bibtex.getEditor() );
      	xmlBibtex.setHref( createHrefForRessource( post.getUser().getName(), bibtex.getIntraHash() ) );
      	xmlBibtex.setInterhash( bibtex.getInterHash() );
         xmlBibtex.setIntrahash( bibtex.getIntraHash() );
      	xmlBibtex.setTitle( bibtex.getTitle() );
      	xmlBibtex.setType( bibtex.getType() );
      	xmlBibtex.setYear( bibtex.getYear() );
      	xmlPost.setBibtex( xmlBibtex );
      }
      return xmlPost;
   }

   private void checkPost( Post post ) throws InternServerException
   {
      if( post.getUser() == null ) throw new InternServerException( "error no user assigned!" );
      if( post.getTags() == null || post.getTags().size() == 0 ) throw new InternServerException( "error no tags assigned!" );
      if( post.getResource() == null ) throw new InternServerException( "error no ressource assigned!" );
   }

   public void serializeUsers( Writer writer, Set<User> users, ViewModel viewModel ) throws InternServerException
	{
		UsersType xmlUsers = new UsersType();
      if( viewModel != null )
      {
   		xmlUsers.setEnd( BigInteger.valueOf( viewModel.getEndValue() ) );
   		if( viewModel.getUrlToNextResources() != null ) xmlUsers.setNext( viewModel.getUrlToNextResources() );
   		xmlUsers.setStart( BigInteger.valueOf( viewModel.getStartValue() ) );
      }
		for( User user: users )
		{
			xmlUsers.getUser().add( createXmlUser( user ) );
		}
		BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setUsers( xmlUsers );
		serialize( writer, xmlDoc );
	}
   
   public void serializeUser( Writer writer, User user, ViewModel viewModel ) throws InternServerException
   {
      BibsonomyXML xmlDoc = new BibsonomyXML();
      xmlDoc.setUser( createXmlUser( user ) );
      serialize( writer, xmlDoc );
   }
   
   private UserType createXmlUser( User user ) throws InternServerException
   {
      checkUser( user );
      UserType xmlUser = new UserType();
      xmlUser.setEmail( user.getEmail() );
      if( user.getHomepage() != null )
      {
         xmlUser.setHomepage( user.getHomepage().toString() );
      }
      xmlUser.setName( user.getName() );
      xmlUser.setRealname( user.getRealname() );
      xmlUser.setHref( createHrefForUser( user.getName() ) );
      return xmlUser;
   }

   public void serializeTags( Writer writer, Set<Tag> tags, ViewModel viewModel ) throws InternServerException
	{
		TagsType xmlTags = new TagsType();
      if( viewModel != null )
      {
   		xmlTags.setEnd( BigInteger.valueOf( viewModel.getEndValue() ) );
         if( viewModel.getUrlToNextResources() != null ) xmlTags.setNext( viewModel.getUrlToNextResources() );
   		xmlTags.setStart( BigInteger.valueOf( viewModel.getStartValue() ) );
      }
		for( Tag tag: tags )
		{
			xmlTags.getTag().add( createXmlTag( tag ) );
		}
		BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setTags( xmlTags );
		serialize( writer, xmlDoc );
	}

	public void serializeTag( Writer writer, Tag tag, ViewModel model ) throws InternServerException
	{
      BibsonomyXML xmlDoc = new BibsonomyXML();
      xmlDoc.setTag( createXmlTag( tag ) );
      serialize( writer, xmlDoc );
	}
   
   private TagType createXmlTag( Tag tag ) throws InternServerException
   {
      TagType xmlTag = new TagType();
      checkTag( tag );
      xmlTag.setName( tag.getName() );
      xmlTag.setGlobalcount( BigInteger.valueOf( tag.getCount() ) );
      xmlTag.setUsercount( BigInteger.valueOf( tag.getUsercount() ) );
      return xmlTag;
   }

	public void serializeGroups( Writer writer, Set<Group> groups, ViewModel viewModel ) throws InternServerException
	{
      GroupsType xmlGroups = new GroupsType();
      if( viewModel != null )
      {
         xmlGroups.setEnd( BigInteger.valueOf( viewModel.getEndValue() ) );
         if( viewModel.getUrlToNextResources() != null ) xmlGroups.setNext( viewModel.getUrlToNextResources() );
         xmlGroups.setStart( BigInteger.valueOf( viewModel.getStartValue() ) );
      }
      for( Group group: groups )
      {
         xmlGroups.getGroup().add( createXmlGroup( group ) );
      }
      BibsonomyXML xmlDoc = new BibsonomyXML();
      xmlDoc.setGroups( xmlGroups );
      serialize( writer, xmlDoc );
	}

	public void serializeGroup( Writer writer, Group group, ViewModel model ) throws InternServerException
   {
      BibsonomyXML xmlDoc = new BibsonomyXML();
      xmlDoc.setGroup( createXmlGroup( group ) );
      serialize( writer, xmlDoc );
   }
	
	private GroupType createXmlGroup( Group group )
   {
      checkGroup( group );
      GroupType xmlGroup = new GroupType();
      xmlGroup.setName( group.getName() );
      xmlGroup.setHref( createHrefForGroup( group.getName() ) );
      xmlGroup.setDescription( group.getDescription() );
      return xmlGroup;
   }

   public User parseUser( Reader reader ) throws BadRequestOrResponseException
	{
		if( reader == null ) throw new BadRequestOrResponseException( "The body part of the received document is missing" );
		
		BibsonomyXML xmlDoc = parse( reader );
		
		if( xmlDoc.getUser() != null )
		{
			return ModelFactory.getInstance().createUser( xmlDoc.getUser() );
		}
		throw new BadRequestOrResponseException( "The body part of the received document is erroneous - no user defined." );
	}

	public Post<Resource> parsePost( Reader reader ) throws BadRequestOrResponseException
	{
		if( reader == null ) throw new BadRequestOrResponseException( "The body part of the received document is missing" );
		
		BibsonomyXML xmlDoc = parse( reader );
		
		if( xmlDoc.getPost() != null )
		{
			return ModelFactory.getInstance().createPost( xmlDoc.getPost() );
		}
		throw new BadRequestOrResponseException( "The body part of the received document is erroneous - no post defined." );
	}

	public Group parseGroup( Reader reader ) throws BadRequestOrResponseException
	{
		if( reader == null ) throw new BadRequestOrResponseException( "The body part of the received document is missing" );
		
		BibsonomyXML xmlDoc = parse( reader );
		
		if( xmlDoc.getGroup() != null )
		{
			return ModelFactory.getInstance().createGroup( xmlDoc.getGroup() );
		}
		throw new BadRequestOrResponseException( "The body part of the received document is erroneous - no group defined." );
	}
	
	public List<Group> parseGroupList( Reader reader ) throws BadRequestOrResponseException 
	{
		if( reader == null ) throw new BadRequestOrResponseException( "The body part of the received document is missing" );
		BibsonomyXML xmlDoc = parse( reader );
		if( xmlDoc.getGroups() != null )
		{
			List<Group> groups = new LinkedList<Group>();
			for( GroupType gt: xmlDoc.getGroups().getGroup() )
			{
				Group g = ModelFactory.getInstance().createGroup( gt );
				groups.add( g );
			}
			return groups;
		}
		throw new BadRequestOrResponseException( "The body part of the received document is erroneous - no list of groups defined." );
	}
	
	public List<Post<Resource>> parsePostList( Reader reader ) throws BadRequestOrResponseException
	{
		if( reader == null ) throw new BadRequestOrResponseException( "The body part of the received document is missing" );
		BibsonomyXML xmlDoc = parse( reader );
		if( xmlDoc.getPosts() != null )
		{
			List<Post<Resource>> posts = new LinkedList<Post<Resource>>();
			for( PostType pt: xmlDoc.getPosts().getPost() )
			{
				Post<Resource> p = ModelFactory.getInstance().createPost( pt );
				posts.add( p );
			}
			return posts;
		}
		throw new BadRequestOrResponseException( "The body part of the received document is erroneous - no list of posts defined." );
	}
	
	public List<Tag> parseTagList( Reader reader ) throws BadRequestOrResponseException 
	{
		if( reader == null ) throw new BadRequestOrResponseException( "The body part of the received document is missing" );
		BibsonomyXML xmlDoc = parse( reader );
		if( xmlDoc.getGroups() != null )
		{
			List<Tag> tags = new LinkedList<Tag>();
			for( TagType tt: xmlDoc.getTags().getTag() )
			{
				Tag t = ModelFactory.getInstance().createTag( tt );
				tags.add( t );
			}
			return tags;
		}
		throw new BadRequestOrResponseException( "The body part of the received document is erroneous - no list of tags defined." );
	}
	
	public List<User> parseUserList( Reader reader ) throws BadRequestOrResponseException 
	{
		if( reader == null ) throw new BadRequestOrResponseException( "The body part of the received document is missing" );
		BibsonomyXML xmlDoc = parse( reader );
		if( xmlDoc.getUsers() != null )
		{
			List<User> users = new LinkedList<User>();
			for( UserType ut: xmlDoc.getUsers().getUser() )
			{
				User u = ModelFactory.getInstance().createUser( ut );
				users.add( u );
			}
			return users;
		}
		throw new BadRequestOrResponseException( "The body part of the received document is erroneous - no list of users defined." );
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
			JAXBContext jc = JAXBContext.newInstance( JAXB_PACKAGE_DECLARATION );
			
			// buildup xml document
			JAXBElement<BibsonomyXML> webserviceElement = ( new ObjectFactory() ).createBibsonomyXMLInterchangeDocument( xmlDoc );
	
			// create a marshaller
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			
			// marshal to the writer
			marshaller.marshal( webserviceElement, writer );
//			marshaller.marshal( webserviceElement, System.out ); // TODO log
		}
		catch( JAXBException e )
		{
			throw new InternServerException( e.toString() );
		}
	}
	
	private BibsonomyXML parse( Reader reader ) throws InternServerException
	{
        try
		{
			JAXBContext jc = JAXBContext.newInstance( JAXB_PACKAGE_DECLARATION );
			
			// create an Unmarshaller
			Unmarshaller u = jc.createUnmarshaller();

			/*
			 * unmarshal a xml instance document into a tree of Java content
			 * objects composed of classes from the restapi package. 
			 */
			JAXBElement<?> xmlDoc = ( JAXBElement<?> )u.unmarshal( reader );
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
   
   private void checkTag( Tag tag ) throws InternServerException
   {
      if( tag.getName() == null || tag.getName().length() == 0 )
      {
         throw new InternServerException( "found a tag without tagname assigned." );
      }
   }
   
   private void checkUser( User user ) throws InternServerException
   {
      if( user.getName() == null || user.getName().length() == 0 )
      {
         throw new InternServerException( "found an user without username assigned." );
      }
   }
   
   private void checkGroup( Group group ) throws InternServerException
   {
      if( group.getName() == null || group.getName().length() == 0 )
      {
         throw new InternServerException( "found a group without username assigned." );
      }
   }
   
   private void checkBookmark( Bookmark bookmark ) throws InternServerException
   {
      if( bookmark.getUrl() == null || bookmark.getUrl().length() == 0 )
      {
         throw new InternServerException( "found a bookmark without url assigned." );
      }
      if( bookmark.getInterHash() == null || bookmark.getInterHash().length() == 0 ||
            bookmark.getIntraHash() == null || bookmark.getIntraHash().length() == 0 )
      {
         throw new InternServerException( "found a bookmark without hash assigned." );
      }
   }

   private void checkBibtex( BibTex bibtex )
   {
      if( bibtex.getTitle() == null || bibtex.getTitle().length() == 0 )
      {
         throw new InternServerException( "found a bibtex without title assigned." );
      }
   }
   
   private String createHrefForUser( String name )
   {
      return RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getUsersUrl() + "/" + name;
   }
   
   private String createHrefForGroup( String name )
   {
      return RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getGroupsUrl() + "/" + name;
   }
   
   private String createHrefForRessource( String userName, String intraHash )
   {
      return RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getUsersUrl() + "/" + userName + "/" + RestProperties.getInstance().getPostsUrl() + "/" + intraHash;
   }
}

/*
 * $Log$
 * Revision 1.5  2007-02-11 18:35:20  mbork
 * lazy instantiation of lists in the model.
 * we definitely need bidirectional links for the api to work proper!
 * fixed all unit tests, every test performs well.
 *
 * Revision 1.4  2007/02/11 17:55:39  mbork
 * switched REST-api to the 'new' datamodel, which does not deserve the name...
 *
 * Revision 1.3  2007/02/05 10:35:54  cschenk
 * Distributed code from the spielwiese among the modules
 *
 * Revision 1.2  2006/10/24 21:39:28  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:16  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.15  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.14  2006/09/16 18:19:16  mbork
 * completed client side api: client api now supports multiple renderers (currently only an implementation for the xml-renderer exists).
 *
 * Revision 1.13  2006/07/09 19:07:12  mbork
 * moved check for hash from renderer to ChangePostQuery, because some queries must not test for the hash
 *
 * Revision 1.12  2006/07/05 16:27:57  mbork
 * fixed issues with link to next list of resources
 *
 * Revision 1.11  2006/07/05 15:20:14  mbork
 * implemented missing strategies, little changes on datamodel --> alpha :)
 *
 * Revision 1.10  2006/06/13 21:30:41  mbork
 * implemented unit tests for get-strategies; fixed some minor bugs
 *
 * Revision 1.9  2006/06/11 11:42:47  mbork
 * added unit tests for rendering posts
 *
 * Revision 1.8  2006/06/09 14:18:44  mbork
 * implemented xml renderer
 *
 * Revision 1.7  2006/06/08 16:14:36  mbork
 * Implemented some XMLRenderer functions, including unit-tests. introduced djunitplugin (see http://works.dgic.co.jp/djunit/index.html)
 *
 * Revision 1.6  2006/06/08 13:23:48  mbork
 * improved documentation, added throws statements even for runtimeexceptions, moved abstractquery to prevent users to call execute directly
 *
 * Revision 1.5  2006/06/07 19:37:28  mbork
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