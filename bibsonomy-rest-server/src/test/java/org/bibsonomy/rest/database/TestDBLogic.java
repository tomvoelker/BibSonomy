package org.bibsonomy.rest.database;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.Author;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.junit.Ignore;

/**
 * This class is used for demonstrating purposes only. It is not designed to
 * verify any algorithm nor any strategy. Testing strategies with this class is
 * not possible, because one would only test the testcase's algorithm itself.<br/>
 * 
 * Furthermore the implementation is not complete; especially unimplemented are:
 * <ul>
 * <li>start and end value</li>
 * <li>class-relations of tags (subclassing/ superclassing)</li>
 * <li>popular- and added-flag at the posts-query</li>
 * <li>viewable-stuff</li>
 * </ul>
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @author Christian Kramer
 * @author Jens Illig
 * @version $Id$
 */
@Ignore
public class TestDBLogic implements LogicInterface {
	private final User loginUser;

	private final Map<String, Group> dbGroups;
	private final Map<String, User> dbUsers;
	private final Map<String, Tag> dbTags;
	private final Map<String, Resource> dbResources;
	private final Date date;

	/**
	 * a factory for this implementation
	 */
	public static final LogicInterfaceFactory factory = new LogicInterfaceFactory() {
		public LogicInterface getLogicAccess(String loginName, String apiKey) {
			return new TestDBLogic(loginName);
		}
	};

	/**
	 * creates a new LogicInterface Implementation for tests
	 * @param authUserName name of the user in whose name testoperations shall be performed 
	 */
	public TestDBLogic(final String authUserName) {
		this.loginUser = new User(authUserName);

		// use the linked map because ordering matters for the junit tests..
		this.dbGroups = new LinkedHashMap<String, Group>();
		this.dbUsers = new LinkedHashMap<String, User>();
		this.dbTags = new LinkedHashMap<String, Tag>();
		this.dbResources = new LinkedHashMap<String, Resource>();

		final Calendar cal = Calendar.getInstance();
		cal.clear();
		this.date = cal.getTime();

		fillDataBase();
	}


	public User getUserDetails(String userName) {
		return this.dbUsers.get(userName);
	}

	public Post<? extends Resource> getPostDetails(String resourceHash, String userName) {
		final User user = this.dbUsers.get(userName);
		if (user != null) {
			for (final Post<? extends Resource> p : user.getPosts()) {
				if (p.getResource().getInterHash().equals(resourceHash)) {
					return p;
				}
			}
		}
		return null;
	}

	public List<Group> getGroups(int start, int end) {
		final List<Group> groups = new LinkedList<Group>();
		groups.addAll(this.dbGroups.values());
		return groups;
	}

	public Group getGroupDetails(String groupName) {
		return this.dbGroups.get(groupName);
	}

	/**
	 * note: the regex is currently not considered
	 */
	public List<Tag> getTags(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags_, String hash, Order order, int start, int end, String search, TagSimilarity relation) {
		final List<Tag> tags = new LinkedList<Tag>();

		switch (grouping) {
		case VIEWABLE:
			// simply use groups
		case GROUP:
			if (this.dbGroups.get(groupingName) != null) {
				for (Post<? extends Resource> post : this.dbGroups.get(groupingName).getPosts()) {
					tags.addAll(post.getTags());
				}
			}
			break;
		case USER:
			if (this.dbUsers.get(groupingName) != null) {
				for (Post<? extends Resource> post : this.dbUsers.get(groupingName).getPosts()) {
					tags.addAll(post.getTags());
				}
			}
			break;
		default: // ALL
			tags.addAll(this.dbTags.values());
		break;
		}

		return tags;
	}

	public Tag getTagDetails(String tagName) {
		return this.dbTags.get(tagName);
	}

	/** note: popular and added are not considered */
	public <T extends Resource> List<Post<T>> getPosts(Class<T> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search) {
		final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
		// do grouping stuff
		switch (grouping) {
		case USER:
			if (this.dbUsers.get(groupingName) != null) {
				posts.addAll(this.dbUsers.get(groupingName).getPosts());
			}
			break;
		case VIEWABLE:
			// simply use groups
		case GROUP:
			if (this.dbGroups.get(groupingName) != null) {
				posts.addAll(this.dbGroups.get(groupingName).getPosts());
			}
			break;
		default: // ALL
			for (final User user : this.dbUsers.values()) {
				posts.addAll(user.getPosts());
			}
		break;
		}

		// check resourceType
		if (resourceType == Bookmark.class) {
			for (final Iterator<Post<? extends Resource>> it = posts.iterator(); it.hasNext();) {
				if (!(((Post<? extends Resource>) it.next()).getResource() instanceof Bookmark)) it.remove();
			}
		} else if (resourceType == BibTex.class) {
			for (final Iterator<Post<? extends Resource>> it = posts.iterator(); it.hasNext();) {
				if (!(((Post<? extends Resource>) it.next()).getResource() instanceof BibTex)) it.remove();
			}
		} else {
			// ALL
		}

		// now this cast is ok
		@SuppressWarnings("unchecked")
		final List<Post<T>> rVal = ((List) posts);
		// check hash
		if (hash != null) {
			for (final Iterator<Post<T>> it = rVal.iterator(); it.hasNext();) {
				if (!it.next().getResource().getInterHash().equals(hash)) it.remove();
			}
		}

		// do tag filtering
		if (tags.size() > 0) {
			for (final Iterator<Post<T>> it = rVal.iterator(); it.hasNext();) {
				boolean drin = false;
				for (final Tag tag : it.next().getTags()) {
					for (final String searchTag : tags) {
						if (tag.getName().equals(searchTag)) {
							drin = true;
							break;
						}
					}

				}
				if (!drin) it.remove();
			}
		}
		return rVal;
	}

	/**
	 * Inserts some test data into the local maps
	 */
	private void fillDataBase() {
		// a group
		final Group publicGroup = new Group();
		publicGroup.setName("public");
		this.dbGroups.put(publicGroup.getName(), publicGroup);

		// users
		final User userManu = new User();
		userManu.setEmail("manuel.bork@uni-kassel.de");
		try {
			userManu.setHomepage(new URL("http://www.manuelbork.de"));
		} catch (MalformedURLException e) {
		}
		userManu.setName("mbork");
		userManu.setRealname("Manuel Bork");
		userManu.setRegistrationDate(new Date(System.currentTimeMillis()));
		this.dbUsers.put(userManu.getName(), userManu);
		//publicGroup.getUsers().add(userManu);
		userManu.getGroups().add(publicGroup);

		final User userAndreas = new User();
		userAndreas.setEmail("andreas.hotho@uni-kassel.de");
		try {
			userAndreas.setHomepage(new URL("http://www.bibsonomy.org"));
		} catch (MalformedURLException e) {
		}
		userAndreas.setName("hotho");
		userAndreas.setRealname("Andreas Hotho");
		userAndreas.setRegistrationDate(new Date(System.currentTimeMillis()));
		this.dbUsers.put(userAndreas.getName(), userAndreas);
//		publicGroup.getUsers().add(userAndreas);
		userAndreas.getGroups().add(publicGroup);

		final User userButonic = new User();
		userButonic.setEmail("joern.dreyer@uni-kassel.de");
		try {
			userButonic.setHomepage(new URL("http://www.butonic.org"));
		} catch (MalformedURLException e) {
		}
		userButonic.setName("butonic");
		userButonic.setRealname("Joern Dreyer");
		userButonic.setRegistrationDate(new Date(System.currentTimeMillis()));
		this.dbUsers.put(userButonic.getName(), userButonic);
//		publicGroup.getUsers().add(userButonic);
		userButonic.getGroups().add(publicGroup);

		// tags
		final Tag spiegelTag = new Tag();
		spiegelTag.setName("spiegel");
		spiegelTag.setUsercount(1);
		spiegelTag.setGlobalcount(1);
		this.dbTags.put(spiegelTag.getName(), spiegelTag);

		final Tag hostingTag = new Tag();
		hostingTag.setName("hosting");
		hostingTag.setUsercount(1);
		hostingTag.setGlobalcount(1);
		this.dbTags.put(hostingTag.getName(), hostingTag);

		final Tag lustigTag = new Tag();
		lustigTag.setName("lustig");
		lustigTag.setUsercount(1);
		lustigTag.setGlobalcount(1);
		this.dbTags.put(lustigTag.getName(), lustigTag);

		final Tag nachrichtenTag = new Tag();
		nachrichtenTag.setName("nachrichten");
		nachrichtenTag.setUsercount(1);
		nachrichtenTag.setGlobalcount(2);
		this.dbTags.put(nachrichtenTag.getName(), nachrichtenTag);

		final Tag semwebTag = new Tag();
		semwebTag.setName("semweb");
		semwebTag.setUsercount(1);
		semwebTag.setGlobalcount(4);
		this.dbTags.put(semwebTag.getName(), semwebTag);

		final Tag vorlesungTag = new Tag();
		vorlesungTag.setName("vorlesung");
		vorlesungTag.setUsercount(1);
		vorlesungTag.setGlobalcount(1);
		this.dbTags.put(vorlesungTag.getName(), vorlesungTag);

		final Tag ws0506Tag = new Tag();
		ws0506Tag.setName("ws0506");
		ws0506Tag.setUsercount(1);
		ws0506Tag.setGlobalcount(1);
		this.dbTags.put(ws0506Tag.getName(), ws0506Tag);

		final Tag weltformelTag = new Tag();
		weltformelTag.setName("weltformel");
		weltformelTag.setUsercount(1);
		weltformelTag.setGlobalcount(1);
		this.dbTags.put(weltformelTag.getName(), weltformelTag);

		final Tag mySiteTag = new Tag();
		mySiteTag.setName("mySite");
		mySiteTag.setUsercount(1);
		mySiteTag.setGlobalcount(1);
		this.dbTags.put(mySiteTag.getName(), mySiteTag);

		final Tag wowTag = new Tag();
		wowTag.setName("wow");
		wowTag.setUsercount(2);
		wowTag.setGlobalcount(2);
		this.dbTags.put(wowTag.getName(), wowTag);

		final Tag lehreTag = new Tag();
		lehreTag.setName("lehre");
		lehreTag.setUsercount(2);
		lehreTag.setGlobalcount(2);
		this.dbTags.put(lehreTag.getName(), lehreTag);

		final Tag kddTag = new Tag();
		kddTag.setName("kdd");
		kddTag.setUsercount(1);
		kddTag.setGlobalcount(1);
		this.dbTags.put(kddTag.getName(), kddTag);

		final Tag wwwTag = new Tag();
		wwwTag.setName("www");
		wwwTag.setUsercount(1);
		wwwTag.setGlobalcount(3);
		this.dbTags.put(wwwTag.getName(), wwwTag);

		// this.dbResources
		final Bookmark spiegelOnlineResource = new Bookmark();
		spiegelOnlineResource.setIntraHash("111111111111111111111111111111111");
		spiegelOnlineResource.setTitle("Spiegel");
		spiegelOnlineResource.setUrl("http://www.spiegel.de");
		this.dbResources.put(spiegelOnlineResource.getIntraHash(), spiegelOnlineResource);

		final Bookmark hostingprojectResource = new Bookmark();
		hostingprojectResource.setIntraHash("22222222222222222222222222222222");
		hostingprojectResource.setTitle("Hostingproject");
		hostingprojectResource.setUrl("http://www.hostingproject.de");
		this.dbResources.put(hostingprojectResource.getIntraHash(), hostingprojectResource);

		final Bookmark klabusterbeereResource = new Bookmark();
		klabusterbeereResource.setIntraHash("33333333333333333333333333333333");
		klabusterbeereResource.setTitle("Klabusterbeere");
		klabusterbeereResource.setUrl("http://www.klabusterbeere.net");
		this.dbResources.put(klabusterbeereResource.getIntraHash(), klabusterbeereResource);

		final Bookmark bildschirmarbeiterResource = new Bookmark();
		bildschirmarbeiterResource.setIntraHash("44444444444444444444444444444444");
		bildschirmarbeiterResource.setTitle("Bildschirmarbeiter");
		bildschirmarbeiterResource.setUrl("http://www.bildschirmarbeiter.com");
		this.dbResources.put(bildschirmarbeiterResource.getIntraHash(), bildschirmarbeiterResource);

		final Bookmark semwebResource = new Bookmark();
		semwebResource.setIntraHash("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
		semwebResource.setTitle("Semantic Web Seminar");
		semwebResource.setUrl("http://www.kde.cs.uni-kassel.de/lehre/ws2005-06/Semantic_Web");
		this.dbResources.put(semwebResource.getIntraHash(), semwebResource);

		final Bookmark butonicResource = new Bookmark();
		butonicResource.setIntraHash("55555555555555555555555555555555");
		butonicResource.setTitle("Butonic");
		butonicResource.setUrl("http://www.butonic.de");
		this.dbResources.put(butonicResource.getIntraHash(), butonicResource);

		final Bookmark wowResource = new Bookmark();
		wowResource.setIntraHash("66666666666666666666666666666666");
		wowResource.setTitle("Worldofwarcraft");
		wowResource.setUrl("http://www.worldofwarcraft.com");
		this.dbResources.put(wowResource.getIntraHash(), wowResource);

		final Bookmark dunkleResource = new Bookmark();
		dunkleResource.setIntraHash("77777777777777777777777777777777");
		dunkleResource.setTitle("Dunkleherzen");
		dunkleResource.setUrl("http://www.dunkleherzen.de");
		this.dbResources.put(dunkleResource.getIntraHash(), dunkleResource);

		final Bookmark w3cResource = new Bookmark();
		w3cResource.setIntraHash("88888888888888888888888888888888");
		w3cResource.setTitle("W3C");
		w3cResource.setUrl("http://www.w3.org/2001/sw/");
		this.dbResources.put(w3cResource.getIntraHash(), w3cResource);

		final Bookmark wikipediaResource = new Bookmark();
		wikipediaResource.setIntraHash("99999999999999999999999999999999");
		wikipediaResource.setTitle("Wikipedia");
		wikipediaResource.setUrl("http://de.wikipedia.org/wiki/Semantic_Web");
		this.dbResources.put(wikipediaResource.getIntraHash(), wikipediaResource);

		final Bookmark kddResource = new Bookmark();
		kddResource.setIntraHash("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		kddResource.setTitle("KDD Seminar");
		kddResource.setUrl("http://www.kde.cs.uni-kassel.de/lehre/ss2006/kdd");
		this.dbResources.put(kddResource.getIntraHash(), kddResource);

		// posts
		final Post<Resource> post_1 = new Post<Resource>();
		post_1.setDescription("Neueste Nachrichten aus aller Welt.");
		post_1.setDate(this.date);
		post_1.setResource(spiegelOnlineResource);
		spiegelOnlineResource.getPosts().add(post_1);
		post_1.setUser(userManu);
		userManu.getPosts().add(post_1);
		post_1.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_1);
		post_1.getTags().add(spiegelTag);
		spiegelTag.getPosts().add(post_1);
		post_1.getTags().add(nachrichtenTag);
		nachrichtenTag.getPosts().add(post_1);

		final Post<Resource> post_2 = new Post<Resource>();
		post_2.setDescription("Toller Webhoster und super Coder ;)");
		post_2.setDate(this.date);
		post_2.setResource(hostingprojectResource);
		hostingprojectResource.getPosts().add(post_2);
		post_2.setUser(userManu);
		userManu.getPosts().add(post_2);
		post_2.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_2);
		post_2.getTags().add(hostingTag);
		hostingTag.getPosts().add(post_2);

		final Post<Resource> post_3 = new Post<Resource>();
		post_3.setDescription("lustiger blog");
		post_3.setDate(this.date);
		post_3.setResource(klabusterbeereResource);
		klabusterbeereResource.getPosts().add(post_3);
		post_3.setUser(userManu);
		userManu.getPosts().add(post_3);
		post_3.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_3);
		post_3.getTags().add(lustigTag);
		lustigTag.getPosts().add(post_3);

		final Post<Resource> post_4 = new Post<Resource>();
		post_4.setDescription("lustiger mist ausm irc ^^");
		post_4.setDate(this.date);
		post_4.setResource(bildschirmarbeiterResource);
		bildschirmarbeiterResource.getPosts().add(post_4);
		post_4.setUser(userManu);
		userManu.getPosts().add(post_4);
		post_4.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_4);
		post_4.getTags().add(lustigTag);
		lustigTag.getPosts().add(post_4);

		final Post<Resource> post_5 = new Post<Resource>();
		post_5.setDescription("Semantic Web Vorlesung im Wintersemester 0506");
		post_5.setDate(this.date);
		post_5.setResource(semwebResource);
		semwebResource.getPosts().add(post_5);
		post_5.setUser(userManu);
		userManu.getPosts().add(post_5);
		post_5.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_5);
		post_5.getTags().add(semwebTag);
		semwebTag.getPosts().add(post_5);
		post_5.getTags().add(vorlesungTag);
		vorlesungTag.getPosts().add(post_5);
		post_5.getTags().add(ws0506Tag);
		ws0506Tag.getPosts().add(post_5);

		final Post<Resource> post_6 = new Post<Resource>();
		post_6.setDescription("joerns blog");
		post_6.setDate(this.date);
		post_6.setResource(butonicResource);
		butonicResource.getPosts().add(post_6);
		post_6.setUser(userButonic);
		userButonic.getPosts().add(post_6);
		post_6.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_6);
		post_6.getTags().add(mySiteTag);
		mySiteTag.getPosts().add(post_6);

		final Post<Resource> post_7 = new Post<Resource>();
		post_7.setDescription("online game");
		post_7.setDate(this.date);
		post_7.setResource(wowResource);
		wowResource.getPosts().add(post_7);
		post_7.setUser(userButonic);
		userButonic.getPosts().add(post_7);
		post_7.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_7);
		post_7.getTags().add(wowTag);
		wowTag.getPosts().add(post_7);

		final Post<Resource> post_8 = new Post<Resource>();
		post_8.setDescription("wow clan");
		post_8.setDate(this.date);
		post_8.setResource(dunkleResource);
		dunkleResource.getPosts().add(post_8);
		post_8.setUser(userButonic);
		userButonic.getPosts().add(post_8);
		post_8.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_8);
		post_8.getTags().add(wowTag);
		wowTag.getPosts().add(post_8);

		final Post<Resource> post_9 = new Post<Resource>();
		post_9.setDescription("w3c site zum semantic web");
		post_9.setDate(this.date);
		post_9.setResource(w3cResource);
		w3cResource.getPosts().add(post_9);
		post_9.setUser(userAndreas);
		userAndreas.getPosts().add(post_9);
		post_9.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_9);
		post_9.getTags().add(semwebTag);
		semwebTag.getPosts().add(post_9);

		final Post<Resource> post_10 = new Post<Resource>();
		post_10.setDescription("wikipedia site zum semantic web");
		post_10.setDate(this.date);
		post_10.setResource(wikipediaResource);
		wikipediaResource.getPosts().add(post_10);
		post_10.setUser(userAndreas);
		userAndreas.getPosts().add(post_10);
		post_10.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_10);
		post_10.getTags().add(semwebTag);
		semwebTag.getPosts().add(post_10);

		final Post<Resource> post_11 = new Post<Resource>();
		post_11.setDescription("kdd vorlesung im ss06");
		post_11.setDate(this.date);
		post_11.setResource(kddResource);
		kddResource.getPosts().add(post_11);
		post_11.setUser(userAndreas);
		userAndreas.getPosts().add(post_11);
		post_11.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_11);
		post_11.getTags().add(lehreTag);
		lehreTag.getPosts().add(post_11);
		post_11.getTags().add(kddTag);
		kddTag.getPosts().add(post_11);

		final Post<Resource> post_12 = new Post<Resource>();
		post_12.setDescription("semantic web vorlesung im ws0506");
		post_12.setDate(this.date);
		post_12.setResource(semwebResource);
		semwebResource.getPosts().add(post_12);
		post_12.setUser(userAndreas);
		userAndreas.getPosts().add(post_12);
		post_12.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_12);
		post_12.getTags().add(lehreTag);
		lehreTag.getPosts().add(post_12);
		post_12.getTags().add(semwebTag);
		semwebTag.getPosts().add(post_12);

		// bibtex resource & post

		BibTex bibtexDemo = new BibTex();
		bibtexDemo.setAuthor("Albert Einstein, Leonardo da Vinci");
		bibtexDemo.setEditor("Luke Skywalker, Yoda");
		bibtexDemo.setIntraHash("abcdef0123abcdef0123abcdef012345");
		bibtexDemo.setInterHash("abcdef0123abcdef0123abcdef012345");
		bibtexDemo.setTitle("Die Weltformel");
		bibtexDemo.setType("Paper");
		bibtexDemo.setYear("2006");
		this.dbResources.put(bibtexDemo.getIntraHash(), bibtexDemo);

		BibTex bibtexDemo1 = new BibTex();
		bibtexDemo1.setAuthor("R. Fielding and J. Gettys and J. Mogul and H. Frystyk and L. Masinter and P. Leach and T. Berners-Lee");
		bibtexDemo1.setEditor("");
		bibtexDemo1.setIntraHash("aaaaaaaabbbbbbbbccccccccaaaaaaaa");
		bibtexDemo1.setInterHash("aaaaaaaabbbbbbbbccccccccaaaaaaaa");
		bibtexDemo1.setTitle("RFC 2616, Hypertext Transfer Protocol -- HTTP/1.1");
		bibtexDemo1.setType("Paper");
		bibtexDemo1.setYear("1999");
		this.dbResources.put(bibtexDemo1.getIntraHash(), bibtexDemo1);

		BibTex bibtexDemo2 = new BibTex();
		bibtexDemo2.setAuthor("Roy T. Fielding");
		bibtexDemo2.setEditor("");
		bibtexDemo2.setIntraHash("abcdabcdabcdabcdaaaaaaaaaaaaaaaa");
		bibtexDemo2.setInterHash("abcdabcdabcdabcdaaaaaaaaaaaaaaaa");
		bibtexDemo2.setTitle("Architectural Styles and the Design of Network-based Software Architectures");
		bibtexDemo2.setType("Paper");
		bibtexDemo2.setYear("2000");
		this.dbResources.put(bibtexDemo2.getIntraHash(), bibtexDemo2);

		BibTex bibtexDemo3 = new BibTex();
		bibtexDemo3.setAuthor("Tim Berners-Lee and Mark Fischetti");
		bibtexDemo3.setEditor("");
		bibtexDemo3.setIntraHash("ddddddddccccccccbbbbbbbbaaaaaaaa");
		bibtexDemo3.setInterHash("ddddddddccccccccbbbbbbbbaaaaaaaa");
		bibtexDemo3.setTitle("Weaving the web");
		bibtexDemo3.setType("Paper");
		bibtexDemo3.setYear("1999");
		this.dbResources.put(bibtexDemo3.getIntraHash(), bibtexDemo3);

		final Post<Resource> post_13 = new Post<Resource>();
		post_13.setDescription("Beschreibung einer allumfassenden Weltformel. Taeglich lesen!");
		post_13.setDate(this.date);
		post_13.setResource(bibtexDemo);
		bibtexDemo.getPosts().add(post_13);
		post_13.setUser(userManu);
		userManu.getPosts().add(post_13);
		post_13.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_13);
		post_13.getTags().add(weltformelTag);
		weltformelTag.getPosts().add(post_13);
		post_13.getTags().add(nachrichtenTag);
		nachrichtenTag.getPosts().add(post_13);

		final Post<Resource> post_14 = new Post<Resource>();
		post_14.setDescription("Grundlagen des www");
		post_14.setDate(this.date);
		post_14.setResource(bibtexDemo1);
		bibtexDemo1.getPosts().add(post_14);
		post_14.setUser(userManu);
		userManu.getPosts().add(post_14);
		post_14.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_14);
		post_14.getTags().add(wwwTag);
		wwwTag.getPosts().add(post_14);

		final Post<Resource> post_15 = new Post<Resource>();
		post_15.setDescription("So ist unsers api konstruiert.");
		post_15.setDate(this.date);
		post_15.setResource(bibtexDemo2);
		bibtexDemo2.getPosts().add(post_15);
		post_15.setUser(userManu);
		userManu.getPosts().add(post_15);
		post_15.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_15);
		post_15.getTags().add(wwwTag);
		wwwTag.getPosts().add(post_15);

		final Post<Resource> post_16 = new Post<Resource>();
		post_16.setDescription("das ist nur ein beispiel.");
		post_16.setDate(this.date);
		post_16.setResource(bibtexDemo3);
		bibtexDemo3.getPosts().add(post_16);
		post_16.setUser(userManu);
		userManu.getPosts().add(post_16);
		post_16.getGroups().add(publicGroup);
		publicGroup.getPosts().add(post_16);
		post_16.getTags().add(wwwTag);
		wwwTag.getPosts().add(post_16);
	}

	public void addUserToGroup(String groupName, String userName) {
	}

	public void deleteGroup(String groupName) {
	}

	public void deletePosts(String userName, List<String> resourceHashes) {
	}

	public void deleteUser(String userName) {
	}

	public void deleteUserFromGroup(String groupName, String userName) {
	}

	public String createGroup(Group group) {
		return null;
	}

	public String createUser(User user) {
		this.dbUsers.put(user.getName(), user);
		return null;
	}

	public User getAuthenticatedUser() {
		return loginUser;
	}

	public String updateGroup(Group group, final GroupUpdateOperation operation) {
		// TODO Auto-generated method stub
		return null;
	}

	public String updateUser(User user, final UserUpdateOperation operation) {
		this.dbUsers.put(user.getName(), user);
		return null;
	}

	public List<String> createPosts(List<Post<?>> posts) {
		return null;
	}

	public List<String> updatePosts(List<Post<?>> posts, PostUpdateOperation operation) {
		return null;
	}

	public String createDocument(Document doc, String resourceHash) {
		return null;
	}

	public Document getDocument(final String userName, final String fileHash) {
		
		return null;
	}
	
	public Document getDocument(final String userName, final String resourceHash, final String fileName) {
		return null;
	}

	public void deleteDocument(Document document, String resourceHash) {
		// TODO Auto-generated method stub

	}

	public void createInetAddressStatus(InetAddress address, InetAddressStatus status) {
		// TODO Auto-generated method stub

	}

	public void deleteInetAdressStatus(InetAddress address) {
		// TODO Auto-generated method stub

	}

	public InetAddressStatus getInetAddressStatus(InetAddress address) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, StatisticsConstraint constraint, String search, List<String> tags) {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<Tag> getConcepts(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags, ConceptStatus status, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	public String createConcept(Tag concept, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteConcept(Tag concept, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub

	}

	public String updateConcept(Tag concept, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteConcept(String concept, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub

	}

	public void deleteRelation(String upper, String lower, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub

	}

	public Tag getConceptDetails(String conceptName, GroupingEntity grouping, String groupingName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getUsers(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, UserRelation relation, String search, int start, int end) {
		final List<User> users = new LinkedList<User>();
		if (GroupingEntity.ALL.equals(grouping)) {
			users.addAll(this.dbUsers.values());
		}
		if (GroupingEntity.GROUP.equals(grouping) && groupingName != null && !groupingName.equals("")) {
			final Group group = this.dbGroups.get(groupingName);
			if (group != null) {
				users.addAll(group.getUsers());
			}
		}
		return users;		

	}

	public String getClassifierSettings(ClassifierSettings key) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateClassifierSettings(ClassifierSettings key, String value) {
		// TODO Auto-generated method stub

	}

	public int getClassifiedUserCount(Classifier classifier, SpamStatus status, int interval) {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<User> getClassifiedUsers(Classifier classifier, SpamStatus status, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getClassifierHistory(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getClassifierComparison(int interval) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPostStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search, StatisticsConstraint constraint) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getOpenIDUser(String openID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int updateTags(final User user, final List<Tag> tagsToReplace, final List<Tag> replacementTags) {
		return 0;
	}

	public int getTagStatistics(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingName, String regex, List<String> tags, ConceptStatus status, int start, int end) {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<User> getFriendsOfUser(User loginUser) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> getUserFriends(User loginUser) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Author> getAuthors(GroupingEntity grouping, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, int start, int end, String search) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public List<User> getUserRelationship(String sourceUser, UserRelation relation) {
		// TODO Auto-generated method stub
		return new ArrayList<User>();
	}

	@Override
	public void deleteUserRelationship(String sourceUser, String targetUser, UserRelation relation) {
		// TODO Auto-generated method stub
	}


	@Override
	public void createUserRelationship(String sourceUser, String targetUser, UserRelation relation) {
		// TODO Auto-generated method stub
	}


	@Override
	public int createBasketItems(List<Post<? extends Resource>> posts) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int deleteBasketItems(List<Post<? extends Resource>> posts, boolean clearAll) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteInboxMessages(List<Post<? extends Resource>> posts, final boolean clearInbox) {
		return 0;
	}


	@Override
	public String getLdapUserByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getUsernameByLdapUser(String ldapUser) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void updateLastLdapRequest(String loginName) {
		// TODO Auto-generated method stub
		
	}


}