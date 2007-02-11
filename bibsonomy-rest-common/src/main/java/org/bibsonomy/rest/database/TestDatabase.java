package org.bibsonomy.rest.database;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.LogicInterface;
import org.bibsonomy.rest.enums.GroupingEntity;
import org.bibsonomy.rest.enums.ResourceType;


/**
 * note: this class is only used on demonstrating purpose. it is not designed to
 * verify any algorithm, not to verify any strategy. Testing strategies with
 * this class is not possible, because one would only test the testcase's
 * algorithm itself..<p/> furthermore the implementation is not complete;
 * especially unimplemented are:
 * <ul>
 * <li>start and end value</li>
 * <li>class-relations of tags (subclassing/ superclassing)</li>
 * <li>popular- and added-flag at the posts-query</li>
 * <li>viewable-stuff</li>
 * </ul>
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class TestDatabase implements LogicInterface
{
	private Map<String, Group> dbGroups;
	private Map<String, User> dbUsers;
	private Map<String, Tag> dbTags;
	private Map<String, Resource> dbResources;
	
	public TestDatabase()
	{
		// use the linked map because ordering matters for the junit tests..
		dbGroups = new LinkedHashMap<String, Group>();
		dbUsers = new LinkedHashMap<String, User>();
		dbTags = new LinkedHashMap<String, Tag>();
		dbResources = new LinkedHashMap<String, Resource>();
		fillDataBase();
	}
   
   public boolean validateUserAccess( String username, String password )
   {
      return true;
   }
   
	public Set<User> getUsers( String authUser, int start, int end )
	{
		Set<User> users = new LinkedHashSet<User>();
		users.addAll( dbUsers.values() );
		return users;
	}

	public Set<User> getUsers( String authUser, String groupName, int start, int end )
	{
		Set<User> users = new LinkedHashSet<User>();
		Group group = dbGroups.get( groupName );
		if( group != null )
		{
			users.addAll( group.getUsers() );
		}
		return users;
	}

	public User getUserDetails( String authUserName, String userName )
	{
		return dbUsers.get( userName );
	}

	public Post<Resource> getPostDetails( String authUser, String resourceHash, String userName )
	{
		User user = dbUsers.get( userName );
		if( user != null )
		{
			for( Post<Resource> p: user.getPosts() )
			{
				if( p.getResource().getInterHash().equals( resourceHash ) )
				{
					return p;
				}
			}
		}
		return null;
	}

	public Set<Group> getGroups( String string, int start, int end )
	{
      Set<Group> groups = new LinkedHashSet<Group>();
      groups.addAll( dbGroups.values() );
      return groups;
	}

	public Group getGroupDetails( String authUserName, String groupName )
	{
		return dbGroups.get( groupName );
	}

	/**
	 * note: the regex is currently not considered
	 */
	public Set<Tag> getTags( String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end )
	{
		Set<Tag> tags = new LinkedHashSet<Tag>();
		switch( grouping )
		{
		case VIEWABLE:
			// simply use groups
		case GROUP:
			if( dbGroups.get( groupingName ) != null )
			{
				for( Post<Resource> post: dbGroups.get( groupingName ).getPosts() )
				{
					tags.addAll( post.getTags() );
				}
			}
			break;
		case USER:
			if( dbUsers.get( groupingName ) != null )
			{
				for( Post<Resource> post: dbUsers.get( groupingName ).getPosts() )
				{
					tags.addAll( post.getTags() );
				}
			}
			break;
		default: // ALL
			tags.addAll( dbTags.values() );
			break;
		}
		return tags;
	}

	public Tag getTagDetails( String authUserName, String tagName )
	{
		return dbTags.get( tagName );
	}

	/**
	 * note: popular and added are not considered
	 */
	public Set<Post<Resource>> getPosts( String authUser, ResourceType resourceType, GroupingEntity grouping, String groupingName, Set<String> tags, String hash, boolean popular, boolean added, int start, int end )
	{
		Set<Post<Resource>> posts = new LinkedHashSet<Post<Resource>>();
		// do grouping stuff
		switch( grouping )
		{
		case USER:
			if( dbUsers.get( groupingName ) != null )
			{
				posts.addAll( dbUsers.get( groupingName).getPosts() );
			}
			break;
		case VIEWABLE:
			// simply use groups
		case GROUP:
			if( dbGroups.get( groupingName ) != null )
			{
				posts.addAll( dbGroups.get( groupingName).getPosts() );
			}
			break;
		default: // ALL
			for( User user: dbUsers.values() )
			{
				posts.addAll( user.getPosts() );
			}
			break;
		}
		// check resourceType
		switch( resourceType )
		{
		case BOOKMARK:
			for( Iterator<Post<Resource>> it = posts.iterator(); it.hasNext(); )
			{
				if( !( ( (Post<Resource>)it.next() ).getResource() instanceof Bookmark ) ) it.remove();
			}
			break;
		case BIBTEX:
			for( Iterator<Post<Resource>> it = posts.iterator(); it.hasNext(); )
			{
				if( !( ( (Post<Resource>)it.next() ).getResource() instanceof BibTex ) ) it.remove();
			}
			break;
		default: // ALL
			break;
		}
		// check hash
		if( !"".equals( hash ) )
		{
			for( Iterator<Post<Resource>> it = posts.iterator(); it.hasNext(); )
			{
				if( !( (Post<Resource>)it.next() ).getResource().getInterHash().equals( hash ) ) it.remove();
			}
		}
		// do tag filtering
      if( tags.size() > 0 )
      {
   		for( Iterator<Post<Resource>> it = posts.iterator(); it.hasNext(); )
   		{
   			boolean drin = false;
   			for( Tag tag: ( (Post<Resource>)it.next() ).getTags() )
   			{
   				for( String searchTag: tags )
   				{
   					if( tag.getName().equals( searchTag ) )
   					{
   						drin = true;
   						break;
   					}
   				}
   				
   			}
   			if( !drin ) it.remove();
         }
		}
		return posts;
	}
	
	/**
	 * inserts some test data into the local maps
	 */
	private void fillDataBase()
	{
		// a group
		Group publicGroup = new Group();
		publicGroup.setName( "public" );
		dbGroups.put( publicGroup.getName(), publicGroup );
		
		// dbUsers
		User userManu = new User();
		userManu.setEmail( "manuel.bork@uni-kassel.de" );
		try
      {
         userManu.setHomepage( new URL( "http://www.manuelbork.de" ) );
      }
      catch( MalformedURLException e1 )
      {
      }
		userManu.setName( "mbork" );
		userManu.setRealname( "Manuel Bork" );
		userManu.setRegistrationDate( new Date( System.currentTimeMillis() ) );
		dbUsers.put( userManu.getName(), userManu );
      publicGroup.getUsers().add( userManu );
      userManu.getGroups().add( publicGroup );
		
		User userAndreas = new User();
		userAndreas.setEmail( "andreas.hotho@uni-kassel.de" );
		try
      {
         userAndreas.setHomepage( new URL( "http://www.bibsonomy.org" ) );
      }
      catch( MalformedURLException e )
      {
      }
		userAndreas.setName( "hotho" );
		userAndreas.setRealname( "Andreas Hotho" );
		userAndreas.setRegistrationDate( new Date( System.currentTimeMillis() ) );
		dbUsers.put( userAndreas.getName(), userAndreas );
      publicGroup.getUsers().add( userAndreas );
      userAndreas.getGroups().add( publicGroup );
		
		User userButonic = new User();
		userButonic.setEmail( "joern.dreyer@uni-kassel.de" );
		try
      {
         userButonic.setHomepage( new URL( "http://www.butonic.org" ) );
      }
      catch( MalformedURLException e )
      {
      }
		userButonic.setName( "butonic" );
		userButonic.setRealname( "Joern Dreyer" );
		userButonic.setRegistrationDate( new Date( System.currentTimeMillis() ) );
		dbUsers.put( userButonic.getName(), userButonic );
      publicGroup.getUsers().add( userButonic );
      userButonic.getGroups().add( publicGroup );
      
		// dbTags
		Tag spiegelTag = new Tag(); 
		spiegelTag.setName( "spiegel" );
		spiegelTag.setUsercount( 1 );
      spiegelTag.setCount( 1 );
		dbTags.put( spiegelTag.getName(), spiegelTag );
		
		Tag hostingTag = new Tag(); 
		hostingTag.setName( "hosting" );
		hostingTag.setUsercount( 1 );
      hostingTag.setCount( 1 );
		dbTags.put( hostingTag.getName(), hostingTag );
		
		Tag lustigTag = new Tag(); 
		lustigTag.setName( "lustig" );
		lustigTag.setUsercount( 1 );
      lustigTag.setCount( 1 );
		dbTags.put( lustigTag.getName(), lustigTag );
		
		Tag nachrichtenTag = new Tag(); 
		nachrichtenTag.setName( "nachrichten" );
		nachrichtenTag.setUsercount( 1 );
      nachrichtenTag.setCount( 2 );
		dbTags.put( nachrichtenTag.getName(), nachrichtenTag );
		
		Tag semwebTag = new Tag(); 
		semwebTag.setName( "semweb" );
		semwebTag.setUsercount( 1 );
      semwebTag.setCount( 4 );
		dbTags.put( semwebTag.getName(), semwebTag );
		
		Tag vorlesungTag = new Tag(); 
		vorlesungTag.setName( "vorlesung" );
		vorlesungTag.setUsercount( 1 );
      vorlesungTag.setCount( 1 );
		dbTags.put( vorlesungTag.getName(), vorlesungTag );
		
		Tag ws0506Tag = new Tag();
		ws0506Tag.setName( "ws0506" );
		ws0506Tag.setUsercount( 1 );
      ws0506Tag.setCount( 1 );
		dbTags.put( ws0506Tag.getName(), ws0506Tag );
		
		Tag weltformelTag = new Tag();
		weltformelTag.setName( "weltformel" );
		weltformelTag.setUsercount( 1 );
      weltformelTag.setCount( 1 );
		dbTags.put( weltformelTag.getName(), weltformelTag );
		
		Tag mySiteTag = new Tag(); 
		mySiteTag.setName( "mySite" );
		mySiteTag.setUsercount( 1 );
      mySiteTag.setCount( 1 );
		dbTags.put( mySiteTag.getName(), mySiteTag );
		
		Tag wowTag = new Tag();
		wowTag.setName( "wow" );
		wowTag.setUsercount( 2 );
      wowTag.setCount( 2 );
		dbTags.put( wowTag.getName(), wowTag );
		
		Tag lehreTag = new Tag(); 
		lehreTag.setName( "lehre" );
		lehreTag.setUsercount( 2 );
      lehreTag.setCount( 2 );
		dbTags.put( lehreTag.getName(), lehreTag );
		
		Tag kddTag = new Tag();
		kddTag.setName( "kdd" );
		kddTag.setUsercount( 1 );
      kddTag.setCount( 1 );
		dbTags.put( kddTag.getName(), kddTag );
		
		Tag wwwTag = new Tag();
		wwwTag.setName( "www" );
		wwwTag.setUsercount( 1 );
		wwwTag.setCount( 3 );
		dbTags.put( wwwTag.getName(), wwwTag );
		
		// dbResources
		Bookmark spiegelOnlineResource = new Bookmark();
		spiegelOnlineResource.setIntraHash( "111111111111111111111111111111111" );
		spiegelOnlineResource.setUrl( "http://www.spiegel.de" );
		dbResources.put( spiegelOnlineResource.getIntraHash(), spiegelOnlineResource );
		
		Bookmark hostingprojectResource = new Bookmark();
		hostingprojectResource.setIntraHash( "22222222222222222222222222222222" );
		hostingprojectResource.setUrl( "http://www.hostingproject.de" );
		dbResources.put( hostingprojectResource.getIntraHash(), hostingprojectResource );
		
		Bookmark klabusterbeereResource = new Bookmark();
		klabusterbeereResource.setIntraHash( "33333333333333333333333333333333" );
		klabusterbeereResource.setUrl( "http://www.klabusterbeere.net" );
		dbResources.put( klabusterbeereResource.getIntraHash(), klabusterbeereResource );
		
		Bookmark bildschirmarbeiterResource = new Bookmark();
		bildschirmarbeiterResource.setIntraHash( "44444444444444444444444444444444" );
		bildschirmarbeiterResource.setUrl( "http://www.bildschirmarbeiter.com" );
		dbResources.put( bildschirmarbeiterResource.getIntraHash(), bildschirmarbeiterResource );
		
		Bookmark semwebResource = new Bookmark();
		semwebResource.setIntraHash( "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" );
		semwebResource.setUrl( "http://www.kde.cs.uni-kassel.de/lehre/ws2005-06/Semantic_Web" );
		dbResources.put( semwebResource.getIntraHash(), semwebResource );
		
		Bookmark butonicResource = new Bookmark();
		butonicResource.setIntraHash( "55555555555555555555555555555555" );
		butonicResource.setUrl( "http://www.butonic.de" );
		dbResources.put( butonicResource.getIntraHash(), butonicResource );
		
		Bookmark wowResource = new Bookmark();
		wowResource.setIntraHash( "66666666666666666666666666666666" );
		wowResource.setUrl( "http://www.worldofwarcraft.com" );
		dbResources.put( wowResource.getIntraHash(), wowResource );
		
		Bookmark dunkleResource = new Bookmark();
		dunkleResource.setIntraHash( "77777777777777777777777777777777" );
		dunkleResource.setUrl( "http://www.dunkleherzen.de" );
		dbResources.put( dunkleResource.getIntraHash(), dunkleResource );
		
		Bookmark w3cResource = new Bookmark();
		w3cResource.setIntraHash( "88888888888888888888888888888888" );
		w3cResource.setUrl( "http://www.w3.org/2001/sw/" );
		dbResources.put( w3cResource.getIntraHash(), w3cResource );
		
		Bookmark wikipediaResource = new Bookmark();
		wikipediaResource.setIntraHash( "99999999999999999999999999999999" );
		wikipediaResource.setUrl( "http://de.wikipedia.org/wiki/Semantic_Web" );
		dbResources.put( wikipediaResource.getIntraHash(), wikipediaResource );
		
		Bookmark kddResource = new Bookmark();
		kddResource.setIntraHash( "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" );
		kddResource.setUrl( "http://www.kde.cs.uni-kassel.de/lehre/ss2006/kdd" );
		dbResources.put( kddResource.getIntraHash(), kddResource );
		
		// posts
		Post<Resource> post_1 = new Post<Resource>();
		post_1.setDescription( "Neueste Nachrichten aus aller Welt." );
		post_1.setPostingDate( System.currentTimeMillis() );
		post_1.setResource( spiegelOnlineResource );
      spiegelOnlineResource.getPosts().add( post_1 );
		post_1.setUser( userManu );
      userManu.getPosts().add( post_1 );
		post_1.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_1 );
		post_1.getTags().add( spiegelTag );
      spiegelTag.getPosts().add( post_1 );
		post_1.getTags().add( nachrichtenTag );
      nachrichtenTag.getPosts().add( post_1 );
		
		Post<Resource> post_2 = new Post<Resource>();
		post_2.setDescription( "Toller Webhoster und super Coder ;)" );
		post_2.setPostingDate( System.currentTimeMillis() );
		post_2.setResource( hostingprojectResource );
      hostingprojectResource.getPosts().add( post_2 );
		post_2.setUser( userManu );
      userManu.getPosts().add( post_2 );
		post_2.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_2 );
		post_2.getTags().add( hostingTag  );
      hostingTag.getPosts().add( post_2 );
		
		Post<Resource> post_3 = new Post<Resource>();
		post_3.setDescription( "lustiger blog" );
		post_3.setPostingDate( System.currentTimeMillis() );
		post_3.setResource( klabusterbeereResource );
      klabusterbeereResource.getPosts().add( post_3 );
		post_3.setUser( userManu );
      userManu.getPosts().add( post_3 );
		post_3.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_3 );
		post_3.getTags().add( lustigTag );
      lustigTag.getPosts().add( post_3 );
		
		Post<Resource> post_4 = new Post<Resource>();
		post_4.setDescription( "lustiger mist ausm irc ^^" );
		post_4.setPostingDate( System.currentTimeMillis() );
		post_4.setResource( bildschirmarbeiterResource );
      bildschirmarbeiterResource.getPosts().add( post_4 );
		post_4.setUser( userManu );
      userManu.getPosts().add( post_4 );
		post_4.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_4 );
		post_4.getTags().add( lustigTag );
      lustigTag.getPosts().add( post_4 );
		
		Post<Resource> post_5 = new Post<Resource>();
		post_5.setDescription( "Semantic Web Vorlesung im Wintersemester 0506" );
		post_5.setPostingDate( System.currentTimeMillis() );
		post_5.setResource( semwebResource );
      semwebResource.getPosts().add( post_5 );
		post_5.setUser( userManu );
      userManu.getPosts().add( post_5 );
		post_5.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_5 );
		post_5.getTags().add( semwebTag );
      semwebTag.getPosts().add( post_5 );
		post_5.getTags().add( vorlesungTag );
      vorlesungTag.getPosts().add( post_5 );
		post_5.getTags().add( ws0506Tag );
      ws0506Tag.getPosts().add( post_5 );
		
		Post<Resource> post_6 = new Post<Resource>();
		post_6.setDescription( "joerns blog" );
		post_6.setPostingDate( System.currentTimeMillis() );
		post_6.setResource( butonicResource  );
      butonicResource.getPosts().add( post_6 );
		post_6.setUser( userButonic );
      userButonic.getPosts().add( post_6 );
		post_6.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_6 );
		post_6.getTags().add( mySiteTag  );
      mySiteTag.getPosts().add( post_6 );
		
		Post<Resource> post_7 = new Post<Resource>();
		post_7.setDescription( "online game" );
		post_7.setPostingDate( System.currentTimeMillis() );
		post_7.setResource( wowResource );
      wowResource.getPosts().add( post_7 );
		post_7.setUser( userButonic );
      userButonic.getPosts().add( post_7 );
		post_7.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_7 );
		post_7.getTags().add( wowTag  );
      wowTag.getPosts().add( post_7 );
		
		Post<Resource> post_8 = new Post<Resource>();
		post_8.setDescription( "wow clan" );
		post_8.setPostingDate( System.currentTimeMillis() );
		post_8.setResource( dunkleResource );
      dunkleResource.getPosts().add( post_8 );
		post_8.setUser( userButonic );
      userButonic.getPosts().add( post_8 );
		post_8.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_8 );
		post_8.getTags().add( wowTag );
      wowTag.getPosts().add( post_8 );
		
		Post<Resource> post_9 = new Post<Resource>();
		post_9.setDescription( "w3c site zum semantic web" );
		post_9.setPostingDate( System.currentTimeMillis() );
		post_9.setResource( w3cResource );
      w3cResource.getPosts().add( post_9 );
		post_9.setUser( userAndreas  );
      userAndreas.getPosts().add( post_9 );
		post_9.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_9 );
		post_9.getTags().add( semwebTag  );
      semwebTag.getPosts().add( post_9 );

		Post<Resource> post_10 = new Post<Resource>();
		post_10.setDescription( "wikipedia site zum semantic web" );
		post_10.setPostingDate( System.currentTimeMillis() );
		post_10.setResource( wikipediaResource );
      wikipediaResource.getPosts().add( post_10 );
		post_10.setUser( userAndreas  );
      userAndreas.getPosts().add( post_10 );
		post_10.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_10 );
		post_10.getTags().add( semwebTag );
      semwebTag.getPosts().add( post_10 );
		
		Post<Resource> post_11 = new Post<Resource>();
		post_11.setDescription( "kdd vorlesung im ss06" );
		post_11.setPostingDate( System.currentTimeMillis() );
		post_11.setResource( kddResource );
      kddResource.getPosts().add( post_11 );
		post_11.setUser( userAndreas  );
      userAndreas.getPosts().add( post_11 );
		post_11.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_11 );
		post_11.getTags().add( lehreTag );
      lehreTag.getPosts().add( post_11 );
		post_11.getTags().add( kddTag );
		kddTag.getPosts().add( post_11 );
      
		Post<Resource> post_12 = new Post<Resource>();
		post_12.setDescription( "semantic web vorlesung im ws0506" );
		post_12.setPostingDate( System.currentTimeMillis() );
		post_12.setResource( semwebResource );
      semwebResource.getPosts().add( post_12 );
		post_12.setUser( userAndreas  );
      userAndreas.getPosts().add( post_12 );
		post_12.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_12 );
		post_12.getTags().add( lehreTag );
      lehreTag.getPosts().add( post_12 );
		post_12.getTags().add( semwebTag );
      semwebTag.getPosts().add( post_12 );
		
		// bibtex resource & post
		
		BibTex bibtexDemo = new BibTex();
		bibtexDemo.setAuthor( "Albert Einstein, Leonardo da Vinci" );
		bibtexDemo.setEditor( "Luke Skywalker, Yoda" );
		bibtexDemo.setIntraHash( "abcdef0123abcdef0123abcdef012345" );
		bibtexDemo.setInterHash( "abcdef0123abcdef0123abcdef012345" );
		bibtexDemo.setTitle( "Die Weltformel" );
		bibtexDemo.setType( "Paper" );
		bibtexDemo.setYear( "2006" );
		dbResources.put( bibtexDemo.getIntraHash(), bibtexDemo );
		
		BibTex bibtexDemo1 = new BibTex();
		bibtexDemo1.setAuthor( "R. Fielding and J. Gettys and J. Mogul and H. Frystyk and L. Masinter and P. Leach and T. Berners-Lee" );
		bibtexDemo1.setEditor( "" );
		bibtexDemo1.setIntraHash( "aaaaaaaabbbbbbbbccccccccaaaaaaaa" );
		bibtexDemo1.setInterHash( "aaaaaaaabbbbbbbbccccccccaaaaaaaa" );
		bibtexDemo1.setTitle( "RFC 2616, Hypertext Transfer Protocol -- HTTP/1.1" );
		bibtexDemo1.setType( "Paper" );
		bibtexDemo1.setYear( "1999" );
		dbResources.put( bibtexDemo1.getIntraHash(), bibtexDemo1 );
		
		BibTex bibtexDemo2 = new BibTex();
		bibtexDemo2.setAuthor( "Roy T. Fielding" );
		bibtexDemo2.setEditor( "" );
		bibtexDemo2.setIntraHash( "abcdabcdabcdabcdaaaaaaaaaaaaaaaa" );
		bibtexDemo2.setInterHash( "abcdabcdabcdabcdaaaaaaaaaaaaaaaa" );
		bibtexDemo2.setTitle( "Architectural Styles and the Design of Network-based Software Architectures" );
		bibtexDemo2.setType( "Paper" );
		bibtexDemo2.setYear( "2000" );
		dbResources.put( bibtexDemo2.getIntraHash(), bibtexDemo2 );
		
		BibTex bibtexDemo3 = new BibTex();
		bibtexDemo3.setAuthor( "Tim Berners-Lee and Mark Fischetti" );
		bibtexDemo3.setEditor( "" );
		bibtexDemo3.setIntraHash( "ddddddddccccccccbbbbbbbbaaaaaaaa" );
		bibtexDemo3.setInterHash( "ddddddddccccccccbbbbbbbbaaaaaaaa" );
		bibtexDemo3.setTitle( "Weaving the web" );
		bibtexDemo3.setType( "Paper" );
		bibtexDemo3.setYear( "1999" );
		dbResources.put( bibtexDemo3.getIntraHash(), bibtexDemo3 );
		
		Post<Resource> post_13 = new Post<Resource>();
		post_13.setDescription("Beschreibung einer allumfassenden Weltformel. Taeglich lesen!" );
		post_13.setPostingDate( System.currentTimeMillis() );
		post_13.setResource( bibtexDemo );
      bibtexDemo.getPosts().add( post_13 );
		post_13.setUser( userManu  );
      userManu.getPosts().add( post_13 );
		post_13.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_13 );
		post_13.getTags().add( weltformelTag );
      weltformelTag.getPosts().add( post_13 );
		post_13.getTags().add( nachrichtenTag );
      nachrichtenTag.getPosts().add( post_13 );
		
		Post<Resource> post_14 = new Post<Resource>();
		post_14.setDescription("Grundlagen des www" );
		post_14.setPostingDate( System.currentTimeMillis() );
		post_14.setResource( bibtexDemo1 );
      bibtexDemo1.getPosts().add( post_14 );
		post_14.setUser( userManu  );
      userManu.getPosts().add( post_14 );
		post_14.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_14 );
		post_14.getTags().add( wwwTag );
      wwwTag.getPosts().add( post_14 );
		
		Post<Resource> post_15 = new Post<Resource>();
		post_15.setDescription("So ist unsers api konstruiert." );
		post_15.setPostingDate( System.currentTimeMillis() );
		post_15.setResource( bibtexDemo2 );
      bibtexDemo2.getPosts().add( post_15 );
		post_15.setUser( userManu  );
      userManu.getPosts().add( post_15 );
		post_15.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_15 );
		post_15.getTags().add( wwwTag );
      wwwTag.getPosts().add( post_15 );
		
		Post<Resource> post_16 = new Post<Resource>();
		post_16.setDescription("das ist nur ein beispiel." );
		post_16.setPostingDate( System.currentTimeMillis() );
		post_16.setResource( bibtexDemo3 );
      bibtexDemo3.getPosts().add( post_16 );
		post_16.setUser( userManu  );
      userManu.getPosts().add( post_16 );
		post_16.getGroups().add( publicGroup );
      publicGroup.getPosts().add( post_16 );
		post_16.getTags().add( wwwTag );
      wwwTag.getPosts().add( post_16 );
	}

   public void addUserToGroup( String groupName, String userName )
   {
      // TODO Auto-generated method stub
      
   }

   public void deleteGroup( String groupName )
   {
      // TODO Auto-generated method stub
      
   }

   public void deletePost( String userName, String resourceHash )
   {
      // TODO Auto-generated method stub
      
   }

   public void deleteUser( String userName )
   {
      // TODO Auto-generated method stub
      
   }

   public void removeUserFromGroup( String groupName, String userName )
   {
      // TODO Auto-generated method stub
      
   }

   public void storeGroup( Group group, boolean update )
   {
      // TODO Auto-generated method stub
      
   }

   public void storePost( String userName, Post post, boolean update )
   {
      // TODO Auto-generated method stub
      
   }

   public void storeUser( User user, boolean update )
   {
      // TODO Auto-generated method stub
      
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
 * Revision 1.1  2006/10/25 19:14:55  mbork
 * moved TestDatabase because other junit tests depend on it
 *
 * Revision 1.1  2006/10/10 12:42:16  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.7  2006/09/16 18:17:50  mbork
 * added some new fake bibtex entries to demonstrate jabref plugin :)
 * fix of tests depiending on fake bibtex entries
 *
 * Revision 1.6  2006/07/05 15:20:13  mbork
 * implemented missing strategies, little changes on datamodel --> alpha :)
 *
 * Revision 1.5  2006/06/23 20:47:45  mbork
 * bugfix: wrong usage of iterator
 *
 * Revision 1.4  2006/06/13 21:30:40  mbork
 * implemented unit tests for get-strategies; fixed some minor bugs
 *
 * Revision 1.3  2006/06/11 15:25:25  mbork
 * removed gatekeeper, changed authentication process
 *
 * Revision 1.2  2006/06/05 14:14:11  mbork
 * implemented GET strategies
 *
 * Revision 1.1  2006/05/24 20:05:55  jillig
 * TestDatabase verschoben
 *
 * Revision 1.1  2006/05/19 21:01:08  mbork
 * started implementing rest api
 *
 */
