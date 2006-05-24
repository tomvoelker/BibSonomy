package org.bibsonomy.rest.database;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.LogicInterface;


/**
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
		dbGroups = new TreeMap<String, Group>();
		dbUsers = new TreeMap<String, User>();
		dbTags = new TreeMap<String, Tag>();
		dbResources = new TreeMap<String, Resource>();
		fillDataBase();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getUsers(java.lang.String, int, int)
	 */
	public Set<User> getUsers( String authUser, int start, int end )
	{
		Set<User> users = new HashSet<User>();
		users.addAll( dbUsers.values() );
		return users;
	}
	
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getPostsByTags(java.lang.String, java.util.Set, int, int)
	 */
	public Set<Post> getPostsByTags( String authUser, Set<String> tags, int start, int end )
	{
		Set<Post> posts = new HashSet<Post>();
		for( String s: tags )
		{
			Tag t = dbTags.get( s );
			posts.addAll( t.getPosts() );
		}
		return posts;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getPostsOfUser(java.lang.String, java.lang.String, int, int)
	 */
	public Set<Post> getPostsOfUser( String authUser, String userName, int start, int end )
	{
		Set<Post> posts = new HashSet<Post>();
		User user = dbUsers.get( userName );
		if( user == null ) return posts; // TODO return null or return empty list?
		posts.addAll( user.getPosts() );
		return posts;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getPostsOfUserByTags(java.lang.String, java.lang.String, java.util.Set, int, int)
	 */
	public Set<Post> getPostsOfUserByTags( String authUser, String userName, Set<String> tags, int start, int end )
	{
		Set<Post> posts = new HashSet<Post>();
		User user = dbUsers.get( userName );
		if( user == null ) return posts; // TODO return null or return empty list?
		for( Post post: user.getPosts() )
		{
			boolean drin = false;
			for( String tagName: tags )
			{
				Tag tag = dbTags.get( tagName );
				if( post.getTags().contains( tag ) ) drin = true;
			}
			if( drin ) posts.add( post );
		}
		return posts;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getConceptOfUserByTags(java.lang.String, java.lang.String, java.util.Set, int, int)
	 */
	public Set<Post> getConceptOfUserByTags( String authUser, String userName, Set<String> tags, int start, int end )
	{
		// TODO
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getPostsOfBookmark(java.lang.String, java.lang.String, int, int)
	 */
	public Set<Post> getPostsOfBookmark( String authUser, String bookmarkHash, int start, int end )
	{
		Set<Post> posts = new HashSet<Post>();
		Resource resource = dbResources.get( bookmarkHash );
		if( !( resource instanceof Bookmark ) )
		{
			return posts; // TODO return null or return empty list?
		}
		if( resource == null ) return posts; // TODO return null or return empty list?
		posts.addAll( resource.getPosts() );
		return posts;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getPostOfBookmarkByUser(java.lang.String, java.lang.String, java.lang.String, int, int)
	 */
	public Set<Post> getPostOfBookmarkByUser( String authUser, String bookmarkHash, String userName, int start, int end )
	{
		Set<Post> posts = new HashSet<Post>();
		Resource resource = dbResources.get( bookmarkHash );
		if( resource == null ) return posts; // TODO return null or return empty list?
		if( !( resource instanceof Bookmark ) )
		{
			return posts; // TODO return null or return empty list?
		}
		User user = dbUsers.get( userName );
		if( user == null ) return posts; // TODO return null or return empty list?
		for( Post post: resource.getPosts() )
		{
			if( ( post).getUser() == user ) posts.add( post );
		}
		return posts;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getPostsOfBibtex(java.lang.String, java.lang.String, int, int)
	 */
	public Set<Post> getPostsOfBibtex( String authUser, String bibtexHash, int start, int end )
	{
		Set<Post> posts = new HashSet<Post>();
		Resource resource = dbResources.get( bibtexHash );
		if( !( resource instanceof BibTex ) )
		{
			return posts; // TODO return null or return empty list?
		}
		if( resource == null ) return posts; // TODO return null or return empty list?
		posts.addAll( resource.getPosts() );
		return posts;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getPostOfBibtexByUser(java.lang.String, java.lang.String, java.lang.String, int, int)
	 */
	public Set<Post> getPostOfBibtexByUser( String authUser, String bibtexHash, String userName, int start, int end )
	{
		Set<Post> posts = new HashSet<Post>();
		Resource resource = dbResources.get( bibtexHash );
		if( resource == null ) return posts; // TODO return null or return empty list?
		if( !( resource instanceof BibTex ) )
		{
			return posts; // TODO return null or return empty list?
		}
		User user = dbUsers.get( userName );
		if( user == null ) return posts; // TODO return null or return empty list?
		for( Post post: resource.getPosts() )
		{
			if( ( post).getUser() == user ) posts.add( post );
		}
		return posts;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getGroups()
	 */
	public Set<Group> getGroups()
	{
		return (Set<Group>)dbGroups.values();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getPostsOfGroup(java.lang.String, java.lang.String, int, int)
	 */
	public Set<Post> getPostsOfGroup( String authUser, String groupName, int start, int end )
	{
		Set<Post> posts = new HashSet<Post>();
		Group group = dbGroups.get( groupName );
		if( group == null )
		{
			return posts; // TODO return null or return empty list?
		}
		posts.addAll( group.getPosts() );
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getPostsOfGroupByTags(java.lang.String, java.lang.String, java.util.Set, int, int)
	 */
	public Set<Post> getPostsOfGroupByTags( String authUser, String groupName, Set<String> tags, int start, int end )
	{
		Set<Post> posts = new HashSet<Post>();
		Group group = dbGroups.get( groupName );
		if( group == null )
		{
			return posts; // TODO return null or return empty list?
		}
		for( Post post: group.getPosts() )
		{
			boolean drin = false;
			for( String tagName: tags )
			{
				Tag tag = dbTags.get( tagName );
				if( post.getTags().contains( tag ) ) drin = true;
			}
			if( drin ) posts.add( post );
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getViewablePostsForGroup(java.lang.String, java.lang.String, int, int)
	 */
	public Set<Post> getViewablePostsForGroup( String authUser, String groupName, int start, int end )
	{
		// TODO
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getViewablePostsTaggedWith(java.lang.String, java.lang.String, java.util.Set, int, int)
	 */
	public Set<Post> getViewablePostsTaggedWith( String authUser, String groupName, Set<String> tags, int start, int end )
	{
		// TODO
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getResourcesBySearch(java.lang.String, java.lang.String, int, int)
	 */
	public Set<Resource> getResourcesBySearch( String authUser, String query, int start, int end )
	{
		// TODO
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.DbInterface#getTagsOfUser(java.lang.String, java.lang.String, int, int)
	 */
	public  Set<Tag> getTagsOfUser( String authUser, String userName, int start, int end )
	{
		Set<Tag> tags = new HashSet<Tag>();
		User user = dbUsers.get( userName );
		if( user == null ) return tags; // TODO return null or return empty list?
		for( Post post: user.getPosts() )
		{
			tags.addAll( post.getTags() );
		}
		return tags;
	}
	
	/**
	 * inserts some test data into the local maps
	 */
	private void fillDataBase()
	{
		// a group
		Group group = new Group();
		group.setName( "public" );
		dbGroups.put( group.getName(), group );
		
		// dbUsers
		User userManu = new User();
		userManu.setEmail( "manuel.bork@uni-kassel.de" );
		userManu.setHomepage( "www.manuelbork.de" );
		userManu.setName( "mbork" );
		userManu.setRealname( "Manuel Bork" );
		userManu.setTimestamp( System.currentTimeMillis() );
		dbUsers.put( userManu.getName(), userManu );
		
		User userAndreas = new User();
		userAndreas.setEmail( "andreas.hotho@uni-kassel.de" );
		userAndreas.setHomepage( "www.bibsonomy.org" );
		userAndreas.setName( "hotho" );
		userAndreas.setRealname( "Andreas Hotho" );
		userAndreas.setTimestamp( System.currentTimeMillis() );
		dbUsers.put( userAndreas.getName(), userAndreas );
		
		User userButonic = new User();
		userButonic.setEmail( "joern.dreyer@uni-kassel.de" );
		userButonic.setHomepage( "www.butonic.org" );
		userButonic.setName( "butonic" );
		userButonic.setRealname( "Joern Dreyer" );
		userButonic.setTimestamp( System.currentTimeMillis() );
		dbUsers.put( userButonic.getName(), userButonic );
		
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
		post_1.getGroups().add( group );
		post_1.getTags().add( spiegelTag );
		post_1.getTags().add( nachrichtenTag );
		
		Post post_2 = new Post();
		post_2.setDescription( "Toller Webhoster und super Coder ;)" );
		post_2.setPostingDate( System.currentTimeMillis() );
		post_2.setResource( hostingprojectResource );
		post_2.setUser( userManu );
		post_2.getGroups().add( group );
		post_2.getTags().add( hostingTag  );
		
		Post post_3 = new Post();
		post_3.setDescription( "lustiger blog" );
		post_3.setPostingDate( System.currentTimeMillis() );
		post_3.setResource( klabusterbeereResource );
		post_3.setUser( userManu );
		post_3.getGroups().add( group );
		post_3.getTags().add( lustigTag );
		
		Post post_4 = new Post();
		post_4.setDescription( "lustiger mist ausm irc ^^" );
		post_4.setPostingDate( System.currentTimeMillis() );
		post_4.setResource( bildschirmarbeiterResource );
		post_4.setUser( userManu );
		post_4.getGroups().add( group );
		post_4.getTags().add( lustigTag );
		
		Post post_5 = new Post();
		post_5.setDescription( "Semantic Web Vorlesung im Wintersemester 0506" );
		post_5.setPostingDate( System.currentTimeMillis() );
		post_5.setResource( semwebResource );
		post_5.setUser( userManu );
		post_5.getGroups().add( group );
		post_5.getTags().add( semwebTag );
		post_5.getTags().add( vorlesungTag );
		post_5.getTags().add( ws0506Tag );
		
		Post post_6 = new Post();
		post_6.setDescription( "joerns blog" );
		post_6.setPostingDate( System.currentTimeMillis() );
		post_6.setResource( butonicResource  );
		post_6.setUser( userButonic );
		post_6.getGroups().add( group );
		post_6.getTags().add( mySiteTag  );
		
		Post post_7 = new Post();
		post_7.setDescription( "online game" );
		post_7.setPostingDate( System.currentTimeMillis() );
		post_7.setResource( wowResource );
		post_7.setUser( userButonic );
		post_7.getGroups().add( group );
		post_7.getTags().add( wowTag  );
		
		Post post_8 = new Post();
		post_8.setDescription( "wow clan" );
		post_8.setPostingDate( System.currentTimeMillis() );
		post_8.setResource( dunkleResource );
		post_8.setUser( userButonic );
		post_8.getGroups().add( group );
		post_8.getTags().add( wowTag );
		
		Post post_9 = new Post();
		post_9.setDescription( "w3c site zum semantic web" );
		post_9.setPostingDate( System.currentTimeMillis() );
		post_9.setResource( w3cResource );
		post_9.setUser( userAndreas  );
		post_9.getGroups().add( group );
		post_9.getTags().add( semwebTag  );

		Post post_10 = new Post();
		post_10.setDescription( "wikipedia site zum semantic web" );
		post_10.setPostingDate( System.currentTimeMillis() );
		post_10.setResource( wikipediaResource );
		post_10.setUser( userAndreas  );
		post_10.getGroups().add( group );
		post_10.getTags().add( semwebTag );
		
		Post post_11 = new Post();
		post_11.setDescription( "kdd vorlesung im ss06" );
		post_11.setPostingDate( System.currentTimeMillis() );
		post_11.setResource( kddResource );
		post_11.setUser( userAndreas  );
		post_11.getGroups().add( group );
		post_11.getTags().add( lehreTag );
		post_11.getTags().add( kddTag );
		
		Post post_12 = new Post();
		post_12.setDescription( "semantic web vorlesung im ws0506" );
		post_12.setPostingDate( System.currentTimeMillis() );
		post_12.setResource( semwebResource );
		post_12.setUser( userAndreas  );
		post_12.getGroups().add( group );
		post_12.getTags().add( lehreTag );
		post_12.getTags().add( semwebTag );
		
		// bibtex resource & post
		
		BibTex bibtexDemo = new BibTex();
		bibtexDemo.setAuthors( "Albert Einstein, Leonardo da Vinci" );
		bibtexDemo.setEditors( "Luke Skywalker, Yoda" );
		bibtexDemo.setIntraHash( "abcdef0123abcdef0123abcdef012345" );
		bibtexDemo.setTitle( "Die Weltformel" );
		bibtexDemo.setType( "Paper" );
		bibtexDemo.setYear( "2006" );
		dbResources.put( bibtexDemo.getIntraHash(), bibtexDemo );
		
		Post post_13 = new Post();
		post_13.setDescription("Beschreibung einer allumfassenden Weltformel. T�glich lesen!" );
		post_13.setPostingDate( System.currentTimeMillis() );
		post_13.setResource( bibtexDemo );
		post_13.setUser( userManu  );
		post_13.getGroups().add( group );
		post_13.getTags().add( weltformelTag );
		post_13.getTags().add( nachrichtenTag );
	}
	
}

/*
 * $Log$
 * Revision 1.1  2006-05-24 20:05:55  jillig
 * TestDatabase verschoben
 *
 * Revision 1.1  2006/05/19 21:01:08  mbork
 * started implementing rest api
 *
 */