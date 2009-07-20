/**
 *  
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.rest.renderer.impl;

import static org.bibsonomy.model.util.ModelValidationUtils.checkBibtex;
import static org.bibsonomy.model.util.ModelValidationUtils.checkBookmark;
import static org.bibsonomy.model.util.ModelValidationUtils.checkGroup;
import static org.bibsonomy.model.util.ModelValidationUtils.checkTag;
import static org.bibsonomy.model.util.ModelValidationUtils.checkUser;
import static org.bibsonomy.rest.RestProperties.Property.VALIDATE_XML_INPUT;
import static org.bibsonomy.rest.RestProperties.Property.VALIDATE_XML_OUTPUT;

import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.BibtexType;
import org.bibsonomy.rest.renderer.xml.BookmarkType;
import org.bibsonomy.rest.renderer.xml.DocumentType;
import org.bibsonomy.rest.renderer.xml.DocumentsType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.GroupsType;
import org.bibsonomy.rest.renderer.xml.ModelFactory;
import org.bibsonomy.rest.renderer.xml.ObjectFactory;
import org.bibsonomy.rest.renderer.xml.PostType;
import org.bibsonomy.rest.renderer.xml.PostsType;
import org.bibsonomy.rest.renderer.xml.StatType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.rest.renderer.xml.TagsType;
import org.bibsonomy.rest.renderer.xml.UserType;
import org.bibsonomy.rest.renderer.xml.UsersType;
import org.xml.sax.SAXParseException;

/**
 * This class creates xml documents valid to the xsd schema and vice-versa.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class XMLRenderer implements Renderer {
	private static final Logger log = Logger.getLogger(XMLRenderer.class);
	private static final String JAXB_PACKAGE_DECLARATION = "org.bibsonomy.rest.renderer.xml";
	private static XMLRenderer renderer;
	
	private final DatatypeFactory datatypeFactory;
	private final UrlRenderer urlRenderer;
	private final Boolean validateXMLInput;
	private final Boolean validateXMLOutput;
	private static Schema schema;

	private XMLRenderer() {
		final RestProperties properties = RestProperties.getInstance();
		this.urlRenderer = UrlRenderer.getInstance();

		try {
			this.datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException ex) {
			throw new RuntimeException("Could not instantiate data type factory.", ex);
		}

		this.validateXMLInput = "true".equals( properties.get(VALIDATE_XML_INPUT) );
		this.validateXMLOutput = "true".equals( properties.get(VALIDATE_XML_OUTPUT) );
		ModelFactory.getInstance().setModelValidator(properties.geModelValidator());

		// we only need to load the XML schema if we validate input or output
		if (this.validateXMLInput || this.validateXMLOutput) {
			try {
				schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(this.getClass().getClassLoader().getResource("xschema.xsd"));
			} catch (Exception e) {
				log.error("Failed to load XML schema", e);
				schema = null;
			}
		} else {
			schema = null;
		}
	}

	public static Renderer getInstance() {
		if (renderer == null) {
			renderer = new XMLRenderer();
		}
		return renderer;
	}

	public void serializePosts(final Writer writer, final List<? extends Post<? extends Resource>> posts, final ViewModel viewModel) throws InternServerException {
		final PostsType xmlPosts = new PostsType();
		if (viewModel != null) {
			xmlPosts.setEnd(BigInteger.valueOf(viewModel.getEndValue()));
			if (viewModel.getUrlToNextResources() != null) xmlPosts.setNext(viewModel.getUrlToNextResources());
			xmlPosts.setStart(BigInteger.valueOf(viewModel.getStartValue()));
		}
		for (final Post<? extends Resource> post : posts) {
			final PostType xmlPost = createXmlPost(post);
			xmlPosts.getPost().add(xmlPost);
		}
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setPosts(xmlPosts);
		serialize(writer, xmlDoc);
	}

	public void serializePost(final Writer writer, final Post<? extends Resource> post, final ViewModel model) throws InternServerException {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setPost(createXmlPost(post));
		serialize(writer, xmlDoc);
	}

	private PostType createXmlPost(final Post<? extends Resource> post) throws InternServerException {
		final PostType xmlPost = new PostType();
		checkPost(post);

		// set user
		checkUser(post.getUser());
		final UserType xmlUser = new UserType();
		xmlUser.setName(post.getUser().getName());
		xmlUser.setHref(urlRenderer.createHrefForUser(post.getUser().getName()));
		xmlPost.setUser(xmlUser);
		if (post.getDate() != null)
			xmlPost.setPostingdate(createXmlCalendar(post.getDate()));

		// add tags
		if (post.getTags() != null) {
			for (final Tag t : post.getTags()) {
				checkTag(t);
				final TagType xmlTag = new TagType();
				xmlTag.setName(t.getName());
				xmlTag.setHref(urlRenderer.createHrefForTag(t.getName()));
				xmlPost.getTag().add(xmlTag);
			}
		}

		// check if the resource is an instance of bibtex
		if(post.getResource() instanceof BibTex){
			final BibTex bibtex = (BibTex) post.getResource();
			// if the resource is a bibtex object and has documents ...
			if(bibtex.getDocuments() != null){

				checkBibtex(bibtex);
				// put them into the xml output
				final DocumentsType xmlDocuments = new DocumentsType();
				for (final Document document : bibtex.getDocuments()){
					final DocumentType xmlDocument = new DocumentType();
					xmlDocument.setFilename(document.getFileName());
					xmlDocument.setMd5Hash(document.getMd5hash());
					xmlDocument.setHref(urlRenderer.createHrefForResourceDocument(post.getUser().getName(), bibtex.getIntraHash(), document.getFileName()));
					xmlDocuments.getDocument().add(xmlDocument);
				}
				xmlPost.setDocuments(xmlDocuments);
			}
		}

		// add groups
		for (final Group group : post.getGroups()) {
			checkGroup(group);
			final GroupType xmlGroup = new GroupType();
			xmlGroup.setName(group.getName());
			xmlGroup.setHref(urlRenderer.createHrefForGroup(group.getName()));
			xmlPost.getGroup().add(xmlGroup);
		}

		xmlPost.setDescription(post.getDescription());

		if (post.getResource() instanceof Bookmark) {
			final Bookmark bookmark = (Bookmark) post.getResource();
			checkBookmark(bookmark);
			final BookmarkType xmlBookmark = new BookmarkType();
			xmlBookmark.setHref(urlRenderer.createHrefForResource(post.getUser().getName(), bookmark.getIntraHash()));
			xmlBookmark.setInterhash(bookmark.getInterHash());
			xmlBookmark.setIntrahash(bookmark.getIntraHash());
			xmlBookmark.setTitle(bookmark.getTitle());
			xmlBookmark.setUrl(bookmark.getUrl());
			xmlPost.setBookmark(xmlBookmark);
		}
		if (post.getResource() instanceof BibTex) {
			final BibTex bibtex = (BibTex) post.getResource();
			checkBibtex(bibtex);
			final BibtexType xmlBibtex = new BibtexType();

			xmlBibtex.setHref(urlRenderer.createHrefForResource(post.getUser().getName(), bibtex.getIntraHash()));

			xmlBibtex.setAddress(bibtex.getAddress());
			xmlBibtex.setAnnote(bibtex.getAnnote());
			xmlBibtex.setAuthor(bibtex.getAuthor());
			xmlBibtex.setBibtexAbstract(bibtex.getAbstract());
			xmlBibtex.setBibtexKey(bibtex.getBibtexKey());
			xmlBibtex.setBKey(bibtex.getKey());
			xmlBibtex.setBooktitle(bibtex.getBooktitle());
			xmlBibtex.setChapter(bibtex.getChapter());
			xmlBibtex.setCrossref(bibtex.getCrossref());
			xmlBibtex.setDay(bibtex.getDay());
			xmlBibtex.setEdition(bibtex.getEdition());
			xmlBibtex.setEditor(bibtex.getEditor());
			xmlBibtex.setEntrytype(bibtex.getEntrytype());
			xmlBibtex.setHowpublished(bibtex.getHowpublished());
			xmlBibtex.setInstitution(bibtex.getInstitution());
			xmlBibtex.setInterhash(bibtex.getInterHash());
			xmlBibtex.setIntrahash(bibtex.getIntraHash());
			xmlBibtex.setJournal(bibtex.getJournal());
			xmlBibtex.setMisc(bibtex.getMisc());
			xmlBibtex.setMonth(bibtex.getMonth());
			xmlBibtex.setNote(bibtex.getNote());
			xmlBibtex.setNumber(bibtex.getNumber());
			xmlBibtex.setOrganization(bibtex.getOrganization());
			xmlBibtex.setPages(bibtex.getPages());
			xmlBibtex.setPublisher(bibtex.getPublisher());
			xmlBibtex.setSchool(bibtex.getSchool());
			// xmlBibtex.setScraperId(BigInteger.valueOf(bibtex.getScraperId()));
			xmlBibtex.setSeries(bibtex.getSeries());
			xmlBibtex.setTitle(bibtex.getTitle());
			xmlBibtex.setType(bibtex.getType());
			xmlBibtex.setUrl(bibtex.getUrl());
			xmlBibtex.setVolume(bibtex.getVolume());
			xmlBibtex.setYear(bibtex.getYear());
			xmlBibtex.setPrivnote(bibtex.getPrivnote());


			xmlPost.setBibtex(xmlBibtex);
		}
		return xmlPost;
	}

	private XMLGregorianCalendar createXmlCalendar(final Date date) {
		final GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return datatypeFactory.newXMLGregorianCalendar(cal);
	}

	private void checkPost(final Post<? extends Resource> post) throws InternServerException {
		if (post.getUser() == null) throw new InternServerException("error no user assigned!");
		// there may be posts whithout tags
		// 2009/01/07, fei: as stated above - there are situations where posts don't have tags
		//                  for example, when serializing a post for submission to a remote 
		//                  recommender -> commenting out
		// if( post.getTags() == null || post.getTags().size() == 0 ) throw new InternServerException( "error no tags assigned!" );
		if (post.getResource() == null) throw new InternServerException("error no ressource assigned!");
	}

	public void serializeUsers(final Writer writer, final List<User> users, final ViewModel viewModel) throws InternServerException {
		final UsersType xmlUsers = new UsersType();
		if (viewModel != null) {
			xmlUsers.setEnd(BigInteger.valueOf(viewModel.getEndValue()));
			if (viewModel.getUrlToNextResources() != null) xmlUsers.setNext(viewModel.getUrlToNextResources());
			xmlUsers.setStart(BigInteger.valueOf(viewModel.getStartValue()));
		}
		for (final User user : users) {
			xmlUsers.getUser().add(createXmlUser(user));
		}
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setUsers(xmlUsers);
		serialize(writer, xmlDoc);
	}

	public void serializeUser(final Writer writer, final User user, final ViewModel viewModel) throws InternServerException {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setUser(createXmlUser(user));
		serialize(writer, xmlDoc);
	}

	private UserType createXmlUser(final User user) throws InternServerException {
		checkUser(user);
		final UserType xmlUser = new UserType();
		xmlUser.setEmail(user.getEmail());
		if (user.getHomepage() != null) {
			xmlUser.setHomepage(user.getHomepage().toString());
		}
		xmlUser.setName(user.getName());
		xmlUser.setRealname(user.getRealname());
		xmlUser.setHref(urlRenderer.createHrefForUser(user.getName()));
		xmlUser.setPassword(user.getPassword());
		if (user.getSpammer() != null)
			xmlUser.setSpammer(user.getSpammer());
		if (user.getPrediction() != null)
			xmlUser.setPrediction(BigInteger.valueOf(user.getPrediction()));
		if (user.getConfidence() != null)
			xmlUser.setConfidence(Double.valueOf(user.getConfidence()));
		xmlUser.setAlgorithm(user.getAlgorithm());
		xmlUser.setClassifierMode(user.getMode());
		if (user.getToClassify() != null)
			xmlUser.setToClassify(BigInteger.valueOf(user.getToClassify()));

		/*
		 * copy groups
		 */
		final List<Group> groups = user.getGroups();
		xmlUser.setGroups(new GroupsType());
		if (groups != null) {
			final List<GroupType> group2 = xmlUser.getGroups().getGroup();
			for (final Group group: groups) {
				group2.add(createXmlGroup(group));
			}
		}
		return xmlUser;
	}

	public void serializeTags(final Writer writer, final List<Tag> tags, final ViewModel viewModel) throws InternServerException {
		final TagsType xmlTags = new TagsType();
		if (viewModel != null) {
			xmlTags.setEnd(BigInteger.valueOf(viewModel.getEndValue()));
			if (viewModel.getUrlToNextResources() != null) xmlTags.setNext(viewModel.getUrlToNextResources());
			xmlTags.setStart(BigInteger.valueOf(viewModel.getStartValue()));
		}
		for (final Tag tag : tags) {
			xmlTags.getTag().add(createXmlTag(tag));
		}
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setTags(xmlTags);
		serialize(writer, xmlDoc);
	}

	public void serializeTag(final Writer writer, final Tag tag, final ViewModel model) throws InternServerException {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setTag(createXmlTag(tag));
		serialize(writer, xmlDoc);
	}

	private TagType createXmlTag(final Tag tag) throws InternServerException {
		final TagType xmlTag = new TagType();
		checkTag(tag);
		xmlTag.setName(tag.getName());
		xmlTag.setHref(urlRenderer.createHrefForTag(tag.getName()));
		// if (tag.getGlobalcount() > 0) {
		xmlTag.setGlobalcount(BigInteger.valueOf(tag.getGlobalcount()));
		// }
		// if (tag.getUsercount() > 0) {
		xmlTag.setUsercount(BigInteger.valueOf(tag.getUsercount()));
		// }

		// add sub-/supertags - dbe, 20070718
		if (tag.getSubTags() != null && tag.getSubTags().size() > 0) {			
			xmlTag.getSubTags().add(createXmlTags(tag.getSubTags()));		
		}
		if (tag.getSuperTags() != null && tag.getSuperTags().size() > 0) {
			xmlTag.getSuperTags().add(createXmlTags(tag.getSuperTags()));
		}
		return xmlTag;
	}

	private TagsType createXmlTags(final List<Tag> tags) {
		final TagsType xmlTags = new TagsType();
		for (final Tag tag : tags) {
			xmlTags.getTag().add(createXmlTag(tag));				
		}
		xmlTags.setStart(BigInteger.valueOf(0));
		xmlTags.setEnd(BigInteger.valueOf(tags.size()));		
		return xmlTags;
	}

	/**
	 * Creates an xml-representation for a given {@link RecommendedTag}.
	 * @param tag recommended tag to encode
	 * @return an {@link TagType} for given {@link RecommendedTag}
	 * @throws InternServerException
	 */
	private TagType createXmlRecommendedTag(final RecommendedTag tag) throws InternServerException {
		final TagType xmlTag = new TagType();
		checkTag(tag);
		xmlTag.setName(tag.getName());
		xmlTag.setHref(urlRenderer.createHrefForTag(tag.getName()));
		// if (tag.getGlobalcount() > 0) {
		xmlTag.setGlobalcount(BigInteger.valueOf(tag.getGlobalcount()));
		// }
		// if (tag.getUsercount() > 0) {
		xmlTag.setUsercount(BigInteger.valueOf(tag.getUsercount()));
		// }
		xmlTag.setConfidence(tag.getConfidence());
		xmlTag.setScore(tag.getScore());

		return xmlTag;
	}

	public void serializeGroups(final Writer writer, final List<Group> groups, final ViewModel viewModel) throws InternServerException {
		final GroupsType xmlGroups = new GroupsType();
		if (viewModel != null) {
			xmlGroups.setEnd(BigInteger.valueOf(viewModel.getEndValue()));
			if (viewModel.getUrlToNextResources() != null) xmlGroups.setNext(viewModel.getUrlToNextResources());
			xmlGroups.setStart(BigInteger.valueOf(viewModel.getStartValue()));
		}
		for (final Group group : groups) {
			xmlGroups.getGroup().add(createXmlGroup(group));
		}
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setGroups(xmlGroups);
		serialize(writer, xmlDoc);
	}

	public void serializeGroup(final Writer writer, final Group group, final ViewModel model) throws InternServerException {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setGroup(createXmlGroup(group));
		serialize(writer, xmlDoc);
	}

	private GroupType createXmlGroup(final Group group) {
		checkGroup(group);
		final GroupType xmlGroup = new GroupType();
		xmlGroup.setName(group.getName());
		xmlGroup.setDescription(group.getDescription());
		xmlGroup.setRealname(group.getRealname());
		if (group.getHomepage() != null) {
			xmlGroup.setHomepage(group.getHomepage().toString());
		}
		xmlGroup.setHref(urlRenderer.createHrefForGroup(group.getName()));
		xmlGroup.setDescription(group.getDescription());
		if (group.getUsers() != null) {
			for (final User user : group.getUsers()) {
				xmlGroup.getUser().add(createXmlUser(user));
			}
		}
		return xmlGroup;
	}

	public void serializeOK(final Writer writer) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		serialize(writer, xmlDoc);
	}

	public void serializeFail(final Writer writer) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.FAIL);
		serialize(writer, xmlDoc);
	}	

	public void serializeError(final Writer writer, final String errorMessage) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.FAIL);
		xmlDoc.setError(errorMessage);
		serialize(writer, xmlDoc);
	}

	public void serializeGroupId(Writer writer, String groupId) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setGroupid(groupId);
		serialize(writer, xmlDoc);		
	}

	public void serializeResourceHash(Writer writer, String hash) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setResourcehash(hash);
		serialize(writer, xmlDoc);		
	}

	public void serializeUserId(Writer writer, String userId) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setUserid(userId);
		serialize(writer, xmlDoc);		
	}

	public void serializeURI(Writer writer, String uri) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setUri(uri);
		serialize(writer, xmlDoc);		
	}	

	public String parseError(final Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getError() != null) {
			return xmlDoc.getError();
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no error defined.");
	}

	public User parseUser(final Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);

		final BibsonomyXML xmlDoc = parse(reader);

		if (xmlDoc.getUser() != null) {
			return ModelFactory.getInstance().createUser(xmlDoc.getUser());
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no user defined.");
	}

	public Post<? extends Resource> parsePost(final Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);

		final BibsonomyXML xmlDoc = parse(reader);

		if (xmlDoc.getPost() != null) {
			return ModelFactory.getInstance().createPost(xmlDoc.getPost());
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no post defined.");
	}

	public Group parseGroup(final Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);

		final BibsonomyXML xmlDoc = parse(reader);

		if (xmlDoc.getGroup() != null) {
			return ModelFactory.getInstance().createGroup(xmlDoc.getGroup());
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no group defined.");
	}

	public List<Group> parseGroupList(final Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getGroups() != null) {
			final List<Group> groups = new LinkedList<Group>();
			for (final GroupType gt : xmlDoc.getGroups().getGroup()) {
				final Group g = ModelFactory.getInstance().createGroup(gt);
				groups.add(g);
			}
			return groups;
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no list of groups defined.");
	}

	public List<Post<? extends Resource>> parsePostList(final Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getPosts() != null) {
			final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
			for (final PostType pt : xmlDoc.getPosts().getPost()) {
				final Post<? extends Resource> p = ModelFactory.getInstance().createPost(pt);
				posts.add(p);
			}
			return posts;
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no list of posts defined.");
	}

	public List<Tag> parseTagList(final Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getTags() != null) {
			final List<Tag> tags = new LinkedList<Tag>();
			for (final TagType tt : xmlDoc.getTags().getTag()) {
				final Tag t = ModelFactory.getInstance().createTag(tt);
				tags.add(t);
			}
			return tags;
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no list of tags defined.");
	}

	public List<User> parseUserList(final Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getUsers() != null) {
			final List<User> users = new LinkedList<User>();
			for (final UserType ut : xmlDoc.getUsers().getUser()) {
				final User u = ModelFactory.getInstance().createUser(ut);
				users.add(u);
			}
			return users;
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no list of users defined.");
	}

	/**
	 * Initializes java xml bindings, builds the xml document and then marshalls
	 * it to the writer.
	 * 
	 * @throws InternServerException
	 *             if the document can't be marshalled
	 */
	private void serialize(final Writer writer, final BibsonomyXML xmlDoc) throws InternServerException {
		try {
			// initialize context for java xml bindings (see comment inside method parse of this class
			// why we provide a classloader here)
			final JAXBContext jc = JAXBContext.newInstance(JAXB_PACKAGE_DECLARATION, this.getClass().getClassLoader());

			// buildup xml document
			final JAXBElement<BibsonomyXML> webserviceElement = new ObjectFactory().createBibsonomy(xmlDoc);

			// create a marshaller
			final Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			if (this.validateXMLOutput) {
				// validate the XML produced by the marshaller
				marshaller.setSchema(schema);
			}

			// marshal to the writer
			marshaller.marshal(webserviceElement, writer);
			// TODO log
			// marshaller.marshal( webserviceElement, System.out );
		} catch (final JAXBException e) {
			if (e.getLinkedException().getClass() == SAXParseException.class) {
				SAXParseException ex = (SAXParseException) e.getLinkedException();
				throw new BadRequestOrResponseException(
						"Error while parsing XML (Line " 
						+ ex.getLineNumber() + ", Column "
						+ ex.getColumnNumber() + ": "
						+ ex.getMessage()
				);				
			}						
			throw new InternServerException(e.toString());
		}
	}

	/**
	 * Unmarshalls the xml document from the reader to the generated java
	 * model.
	 * 
	 * @return A BibsonomyXML object that contains the unmarshalled content
	 * @throws InternServerException
	 *             if the content can't be unmarshalled
	 */
	private BibsonomyXML parse(Reader reader) throws InternServerException {
		try {

//			if (log.isDebugEnabled() == true) {
//			char[] chars = new char[65536];
//			String s;
//			try {
//			int read = reader.read(chars);
//			s = new String(chars,0,read); 
//			log.debug("request-body:\n[" + s + "]");
//			reader = new StringReader(s);
//			} catch (IOException ex) {
//			log.error(ex,ex);
//			}
//			}

			// initialize JAXB context. We provide the classloader here because we experienced that under
			// certain circumstances (e.g. when used within JabRef as a JPF-Plugin), the wrong classloader is
			// used which has the following exception as consequence:
			//
			//   javax.xml.bind.JAXBException: "org.bibsonomy.rest.renderer.xml" doesnt contain ObjectFactory.class or jaxb.index
			//
			// (see also http://ws.apache.org/jaxme/apidocs/javax/xml/bind/JAXBContext.html)
			final JAXBContext jc = JAXBContext.newInstance(JAXB_PACKAGE_DECLARATION, this.getClass().getClassLoader());

			// create an Unmarshaller
			final Unmarshaller u = jc.createUnmarshaller();

			// set schema to validate input documents
			if (this.validateXMLInput) {
				u.setSchema(schema);
			}

			/*
			 * unmarshal a xml instance document into a tree of Java content
			 * objects composed of classes from the restapi package.
			 */
			final JAXBElement<?> xmlDoc = (JAXBElement<?>) u.unmarshal(reader);
			return (BibsonomyXML) xmlDoc.getValue();
		} catch (final JAXBException e) {
			if (e.getLinkedException() != null && e.getLinkedException().getClass() == SAXParseException.class) {
				SAXParseException ex = (SAXParseException) e.getLinkedException();
				throw new BadRequestOrResponseException(
						"Error while parsing XML (Line " 
						+ ex.getLineNumber() + ", Column "
						+ ex.getColumnNumber() + ": "
						+ ex.getMessage()
				);				
			}			
			throw new InternServerException(e.toString());
		}
	}


	public Tag parseTag(Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getTag() != null) {
			return ModelFactory.getInstance().createTag(xmlDoc.getTag());
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no tag defined.");
	}

	public String parseStat(Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);		
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getStat() != null) {
			return xmlDoc.getStat().value();
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no status defined.");
	}

	public String parseGroupId(Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getGroupid() != null) {
			return xmlDoc.getGroupid();
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no group id.");
	}

	public String parseResourceHash(Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getResourcehash() != null) {
			return xmlDoc.getResourcehash();
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no resource hash defined.");
	}

	public String parseUserId(Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getUserid() != null) {
			return xmlDoc.getUserid();
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no user id defined.");
	}

	private void checkReader(Reader reader) throws BadRequestOrResponseException {
		if (reader == null) throw new BadRequestOrResponseException("The body part of the received document is missing");
	}

	public RecommendedTag parseRecommendedTag(Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getTag() != null) {
			return ModelFactory.getInstance().createRecommendedTag(xmlDoc.getTag());
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no tag defined.");
	}

	public SortedSet<RecommendedTag> parseRecommendedTagList(Reader reader) throws BadRequestOrResponseException {
		checkReader(reader);
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getTags() != null) {
			final SortedSet<RecommendedTag> tags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
			for (final TagType tt : xmlDoc.getTags().getTag()) {
				final RecommendedTag t = ModelFactory.getInstance().createRecommendedTag(tt);
				tags.add(t);
			}
			return tags;
		}
		if (xmlDoc.getError() != null) throw new BadRequestOrResponseException(xmlDoc.getError());
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no list of tags defined.");
	}

	public void serializeRecommendedTag(Writer writer, RecommendedTag tag) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setTag(createXmlRecommendedTag(tag));
		serialize(writer, xmlDoc);		
	}

	public void serializeRecommendedTags(Writer writer, Collection<RecommendedTag> tags) {
		final TagsType xmlTags = new TagsType();
		if (tags != null) {
			for (final RecommendedTag tag : tags) {
				xmlTags.getTag().add(createXmlRecommendedTag(tag));
			}
		}
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setTags(xmlTags);
		serialize(writer, xmlDoc);	
	}
}