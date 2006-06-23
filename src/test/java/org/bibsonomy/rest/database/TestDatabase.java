package org.bibsonomy.rest.database;

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

	public Post getPostDetails( String authUser, String resourceHash, String userName )
	{
		User user = dbUsers.get( userName );
		if( user != null )
		{
			for( Post p: user.getPosts() )
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
				for( Post post: dbGroups.get( groupingName ).getPosts() )
				{
					tags.addAll( post.getTags() );
				}
			}
			break;
		case USER:
			if( dbUsers.get( groupingName ) != null )
			{
				for( Post post: dbUsers.get( groupingName ).getPosts() )
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
	public Set<Post> getPosts( String authUser, ResourceType resourceType, GroupingEntity grouping, String groupingName, Set<String> tags, String hash, boolean popular, boolean added, int start, int end )
	{
		Set<Post> posts = new LinkedHashSet<Post>();
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
			for( Iterator<Post> it = posts.iterator(); it.hasNext(); )
			{
				if( !( ( (Post)it.next() ).getResource() instanceof Bookmark ) ) it.remove();
			}
			break;
		case BIBTEX:
			for( Iterator<Post> it = posts.iterator(); it.hasNext(); )
			{
				if( !( ( (Post)it.next() ).getResource() instanceof BibTex ) ) it.remove();
			}
			break;
		default: // ALL
			break;
		}
		// check hash
		if( !"".equals( hash ) )
		{
			for( Iterator<Post> it = posts.iterator(); it.hasNext(); )
			{
				if( !( (Post)it.next() ).getResource().getInterHash().equals( hash ) ) it.remove();
			}
		}
		// do tag filtering
      if( tags.size() > 0 )
      {
   		for( Iterator<Post> it = posts.iterator(); it.hasNext(); )
   		{
   			boolean drin = false;
   			for( Tag tag: ( (Post)it.next() ).getTags() )
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
		userManu.setHomepage( "www.manuelbork.de" );
		userManu.setName( "mbork" );
		userManu.setRealname( "Manuel Bork" );
		userManu.setTimestamp( System.currentTimeMillis() );
		dbUsers.put( userManu.getName(), userManu );
      publicGroup.getUsers().add( userManu );
		
		User userAndreas = new User();
		userAndreas.setEmail( "andreas.hotho@uni-kassel.de" );
		userAndreas.setHomepage( "www.bibsonomy.org" );
		userAndreas.setName( "hotho" );
		userAndreas.setRealname( "Andreas Hotho" );
		userAndreas.setTimestamp( System.currentTimeMillis() );
		dbUsers.put( userAndreas.getName(), userAndreas );
      publicGroup.getUsers().add( userAndreas );
		
		User userButonic = new User();
		userButonic.setEmail( "joern.dreyer@uni-kassel.de" );
		userButonic.setHomepage( "www.butonic.org" );
		userButonic.setName( "butonic" );
		userButonic.setRealname( "Joern Dreyer" );
		userButonic.setTimestamp( System.currentTimeMillis() );
		dbUsers.put( userButonic.getName(), userButonic );
      publicGroup.getUsers().add( userButonic );
      
		// dbTags
		Tag spiegelTag = new Tag(); 
		spiegelTag.setName( "spiegel" );
		spiegelTag.setCount( 1 );
		dbTags.put( spiegelTag.getName(), spiegelTag );
		
		Tag hostingTag = new Tag(); 
		hostingTag.setName( "hosting" );
		hostingTag.setCount( 1 );
		dbTags.put( hostingTag.getName(), hostingTag );
		
		Tag lustigTag = new Tag(); 
		lustigTag.setName( "lustig" );
		lustigTag.setCount( 2 );
		dbTags.put( lustigTag.getName(), lustigTag );
		
		Tag nachrichtenTag = new Tag(); 
		nachrichtenTag.setName( "nachrichten" );
		nachrichtenTag.setCount( 2 );
		dbTags.put( nachrichtenTag.getName(), nachrichtenTag );
		
		Tag semwebTag = new Tag(); 
		semwebTag.setName( "semweb" );
		semwebTag.setCount( 1 );
		dbTags.put( semwebTag.getName(), semwebTag );
		
		Tag vorlesungTag = new Tag(); 
		vorlesungTag.setName( "vorlesung" );
		vorlesungTag.setCount( 1 );
		dbTags.put( vorlesungTag.getName(), vorlesungTag );
		
		Tag ws0506Tag = new Tag();
		ws0506Tag.setName( "ws0506" );
		ws0506Tag.setCount( 1 );
		dbTags.put( ws0506Tag.getName(), ws0506Tag );
		
		Tag weltformelTag = new Tag();
		weltformelTag.setName( "weltformel" );
		weltformelTag.setCount( 1 );
		dbTags.put( weltformelTag.getName(), weltformelTag );
		
		Tag mySiteTag = new Tag(); 
		mySiteTag.setName( "mySite" );
		mySiteTag.setCount( 1 );
		dbTags.put( mySiteTag.getName(), mySiteTag );
		
		Tag wowTag = new Tag();
		wowTag.setName( "wow" );
		wowTag.setCount( 2 );
		dbTags.put( wowTag.getName(), wowTag );
		
		Tag lehreTag = new Tag(); 
		lehreTag.setName( "lehre" );
		lehreTag.setCount( 2 );
		dbTags.put( lehreTag.getName(), lehreTag );
		
		Tag kddTag = new Tag();
		kddTag.setName( "kdd" );
		kddTag.setCount( 1 );
		dbTags.put( kddTag.getName(), kddTag );
		
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
		Post post_1 = new Post();
		post_1.setDescription( "Neueste Nachrichten aus aller Welt." );
		post_1.setPostingDate( System.currentTimeMillis() );
		post_1.setResource( spiegelOnlineResource );
		post_1.setUser( userManu );
		post_1.getGroups().add( publicGroup );
		post_1.getTags().add( spiegelTag );
		post_1.getTags().add( nachrichtenTag );
		
		Post post_2 = new Post();
		post_2.setDescription( "Toller Webhoster und super Coder ;)" );
		post_2.setPostingDate( System.currentTimeMillis() );
		post_2.setResource( hostingprojectResource );
		post_2.setUser( userManu );
		post_2.getGroups().add( publicGroup );
		post_2.getTags().add( hostingTag  );
		
		Post post_3 = new Post();
		post_3.setDescription( "lustiger blog" );
		post_3.setPostingDate( System.currentTimeMillis() );
		post_3.setResource( klabusterbeereResource );
		post_3.setUser( userManu );
		post_3.getGroups().add( publicGroup );
		post_3.getTags().add( lustigTag );
		
		Post post_4 = new Post();
		post_4.setDescription( "lustiger mist ausm irc ^^" );
		post_4.setPostingDate( System.currentTimeMillis() );
		post_4.setResource( bildschirmarbeiterResource );
		post_4.setUser( userManu );
		post_4.getGroups().add( publicGroup );
		post_4.getTags().add( lustigTag );
		
		Post post_5 = new Post();
		post_5.setDescription( "Semantic Web Vorlesung im Wintersemester 0506" );
		post_5.setPostingDate( System.currentTimeMillis() );
		post_5.setResource( semwebResource );
		post_5.setUser( userManu );
		post_5.getGroups().add( publicGroup );
		post_5.getTags().add( semwebTag );
		post_5.getTags().add( vorlesungTag );
		post_5.getTags().add( ws0506Tag );
		
		Post post_6 = new Post();
		post_6.setDescription( "joerns blog" );
		post_6.setPostingDate( System.currentTimeMillis() );
		post_6.setResource( butonicResource  );
		post_6.setUser( userButonic );
		post_6.getGroups().add( publicGroup );
		post_6.getTags().add( mySiteTag  );
		
		Post post_7 = new Post();
		post_7.setDescription( "online game" );
		post_7.setPostingDate( System.currentTimeMillis() );
		post_7.setResource( wowResource );
		post_7.setUser( userButonic );
		post_7.getGroups().add( publicGroup );
		post_7.getTags().add( wowTag  );
		
		Post post_8 = new Post();
		post_8.setDescription( "wow clan" );
		post_8.setPostingDate( System.currentTimeMillis() );
		post_8.setResource( dunkleResource );
		post_8.setUser( userButonic );
		post_8.getGroups().add( publicGroup );
		post_8.getTags().add( wowTag );
		
		Post post_9 = new Post();
		post_9.setDescription( "w3c site zum semantic web" );
		post_9.setPostingDate( System.currentTimeMillis() );
		post_9.setResource( w3cResource );
		post_9.setUser( userAndreas  );
		post_9.getGroups().add( publicGroup );
		post_9.getTags().add( semwebTag  );

		Post post_10 = new Post();
		post_10.setDescription( "wikipedia site zum semantic web" );
		post_10.setPostingDate( System.currentTimeMillis() );
		post_10.setResource( wikipediaResource );
		post_10.setUser( userAndreas  );
		post_10.getGroups().add( publicGroup );
		post_10.getTags().add( semwebTag );
		
		Post post_11 = new Post();
		post_11.setDescription( "kdd vorlesung im ss06" );
		post_11.setPostingDate( System.currentTimeMillis() );
		post_11.setResource( kddResource );
		post_11.setUser( userAndreas  );
		post_11.getGroups().add( publicGroup );
		post_11.getTags().add( lehreTag );
		post_11.getTags().add( kddTag );
		
		Post post_12 = new Post();
		post_12.setDescription( "semantic web vorlesung im ws0506" );
		post_12.setPostingDate( System.currentTimeMillis() );
		post_12.setResource( semwebResource );
		post_12.setUser( userAndreas  );
		post_12.getGroups().add( publicGroup );
		post_12.getTags().add( lehreTag );
		post_12.getTags().add( semwebTag );
		
		// bibtex resource & post
		
		BibTex bibtexDemo = new BibTex();
		bibtexDemo.setAuthors( "Albert Einstein, Leonardo da Vinci" );
		bibtexDemo.setEditors( "Luke Skywalker, Yoda" );
		bibtexDemo.setIntraHash( "abcdef0123abcdef0123abcdef012345" );
      bibtexDemo.setInterHash( "abcdef0123abcdef0123abcdef012345" );
		bibtexDemo.setTitle( "Die Weltformel" );
		bibtexDemo.setType( "Paper" );
		bibtexDemo.setYear( "2006" );
		dbResources.put( bibtexDemo.getIntraHash(), bibtexDemo );
		
		Post post_13 = new Post();
		post_13.setDescription("Beschreibung einer allumfassenden Weltformel. Taeglich lesen!" );
		post_13.setPostingDate( System.currentTimeMillis() );
		post_13.setResource( bibtexDemo );
		post_13.setUser( userManu  );
		post_13.getGroups().add( publicGroup );
		post_13.getTags().add( weltformelTag );
		post_13.getTags().add( nachrichtenTag );
	}
}

/*
 * $Log$
 * Revision 1.5  2006-06-23 20:47:45  mbork
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