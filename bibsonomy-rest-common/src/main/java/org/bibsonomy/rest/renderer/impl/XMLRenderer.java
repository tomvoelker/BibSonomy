package org.bibsonomy.rest.renderer.impl;

import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.bibsonomy.common.exceptions.InternServerException;
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

	public void serializePosts( Writer writer, List<? extends Post<? extends Resource>> posts, ViewModel viewModel ) throws InternServerException
	{
		PostsType xmlPosts = new PostsType();
      if( viewModel != null )
      {
      	xmlPosts.setEnd( BigInteger.valueOf( viewModel.getEndValue() ) );
         if( viewModel.getUrlToNextResources() != null ) xmlPosts.setNext( viewModel.getUrlToNextResources() );
      	xmlPosts.setStart( BigInteger.valueOf( viewModel.getStartValue() ) );
      }
		for( Post<? extends Resource> post: posts )
		{
			PostType xmlPost = createXmlPost( post );
			xmlPosts.getPost().add( xmlPost );
		}
		BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setPosts( xmlPosts );
		serialize( writer, xmlDoc );
	}

   public void serializePost( Writer writer, Post<? extends Resource> post, ViewModel model ) throws InternServerException
   {
      BibsonomyXML xmlDoc = new BibsonomyXML();
      xmlDoc.setPost( createXmlPost( post ) );
      serialize( writer, xmlDoc );
   }

   private PostType createXmlPost( Post<? extends Resource> post ) throws InternServerException
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
      if( post.getTags() != null )
      {
	      for( Tag t: post.getTags() )
	      {
	         checkTag( t );
	      	TagType xmlTag = new TagType();
	      	xmlTag.setName( t.getName() );
	      	xmlPost.getTag().add( xmlTag );
	      }
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
      // there may be posts whithout tags
      // if( post.getTags() == null || post.getTags().size() == 0 ) throw new InternServerException( "error no tags assigned!" );
      if( post.getResource() == null ) throw new InternServerException( "error no ressource assigned!" );
   }

   public void serializeUsers( Writer writer, List<User> users, ViewModel viewModel ) throws InternServerException
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

   public void serializeTags( Writer writer, List<Tag> tags, ViewModel viewModel ) throws InternServerException
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
      xmlTag.setGlobalcount( BigInteger.valueOf( tag.getGlobalcount() ) );
      xmlTag.setUsercount( BigInteger.valueOf( tag.getUsercount() ) );
      return xmlTag;
   }

	public void serializeGroups( Writer writer, List<Group> groups, ViewModel viewModel ) throws InternServerException
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
   
   public void serializeError( Writer writer, String errorMessage )
   {
      BibsonomyXML xmlDoc = new BibsonomyXML();
      xmlDoc.setError( errorMessage );
      serialize( writer, xmlDoc );
   }

   public User parseUser( Reader reader ) throws BadRequestOrResponseException
	{
		if( reader == null ) throw new BadRequestOrResponseException( "The body part of the received document is missing" );
		
		BibsonomyXML xmlDoc = parse( reader );
		
		if( xmlDoc.getUser() != null )
		{
			return ModelFactory.getInstance().createUser( xmlDoc.getUser() );
		}
      if( xmlDoc.getError() != null ) throw new BadRequestOrResponseException( xmlDoc.getError() );
		throw new BadRequestOrResponseException( "The body part of the received document is erroneous - no user defined." );
	}

	public Post<? extends Resource> parsePost( Reader reader ) throws BadRequestOrResponseException
	{
		if( reader == null ) throw new BadRequestOrResponseException( "The body part of the received document is missing" );
		
		BibsonomyXML xmlDoc = parse( reader );
		
		if( xmlDoc.getPost() != null )
		{
			return ModelFactory.getInstance().createPost( xmlDoc.getPost() );
		}
      if( xmlDoc.getError() != null ) throw new BadRequestOrResponseException( xmlDoc.getError() );
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
      if( xmlDoc.getError() != null ) throw new BadRequestOrResponseException( xmlDoc.getError() );
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
      if( xmlDoc.getError() != null ) throw new BadRequestOrResponseException( xmlDoc.getError() );
		throw new BadRequestOrResponseException( "The body part of the received document is erroneous - no list of groups defined." );
	}
	
	public List<Post<? extends Resource>> parsePostList( Reader reader ) throws BadRequestOrResponseException
	{
		if( reader == null ) throw new BadRequestOrResponseException( "The body part of the received document is missing" );
		BibsonomyXML xmlDoc = parse( reader );
		if( xmlDoc.getPosts() != null )
		{
			List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
			for( PostType pt: xmlDoc.getPosts().getPost() )
			{
				Post<? extends Resource> p = ModelFactory.getInstance().createPost( pt );
				posts.add( p );
			}
			return posts;
		}
      if( xmlDoc.getError() != null ) throw new BadRequestOrResponseException( xmlDoc.getError() );
		throw new BadRequestOrResponseException( "The body part of the received document is erroneous - no list of posts defined." );
	}
	
	public List<Tag> parseTagList( Reader reader ) throws BadRequestOrResponseException 
	{
		if( reader == null ) throw new BadRequestOrResponseException( "The body part of the received document is missing" );
		BibsonomyXML xmlDoc = parse( reader );
		if( xmlDoc.getTags() != null )
		{
			List<Tag> tags = new LinkedList<Tag>();
			for( TagType tt: xmlDoc.getTags().getTag() )
			{
				Tag t = ModelFactory.getInstance().createTag( tt );
				tags.add( t );
			}
			return tags;
		}
      if( xmlDoc.getError() != null ) throw new BadRequestOrResponseException( xmlDoc.getError() );
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
      if( xmlDoc.getError() != null ) throw new BadRequestOrResponseException( xmlDoc.getError() );
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