/**
 *
 *  BibSonomy-Rest-Common - Common things for the REST-client and server.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

package org.bibsonomy.rest.renderer;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.sync.SynchronizationAction;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.model.util.ModelValidationUtils;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.data.Data;
import org.bibsonomy.model.util.data.DataAccessor;
import org.bibsonomy.model.util.data.NoDataAccessor;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.xml.AbstractPublicationType;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.BibtexType;
import org.bibsonomy.rest.renderer.xml.BookmarkType;
import org.bibsonomy.rest.renderer.xml.DocumentType;
import org.bibsonomy.rest.renderer.xml.DocumentsType;
import org.bibsonomy.rest.renderer.xml.ExtraUrlType;
import org.bibsonomy.rest.renderer.xml.ExtraUrlsType;
import org.bibsonomy.rest.renderer.xml.GoldStandardPublicationType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.GroupsType;
import org.bibsonomy.rest.renderer.xml.PostType;
import org.bibsonomy.rest.renderer.xml.PostsType;
import org.bibsonomy.rest.renderer.xml.ReferenceType;
import org.bibsonomy.rest.renderer.xml.ReferencesType;
import org.bibsonomy.rest.renderer.xml.StatType;
import org.bibsonomy.rest.renderer.xml.SyncDataType;
import org.bibsonomy.rest.renderer.xml.SyncPostType;
import org.bibsonomy.rest.renderer.xml.SyncPostsType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.rest.renderer.xml.TagsType;
import org.bibsonomy.rest.renderer.xml.UploadDataType;
import org.bibsonomy.rest.renderer.xml.UserType;
import org.bibsonomy.rest.renderer.xml.UsersType;
import org.bibsonomy.rest.validation.ModelValidator;
import org.bibsonomy.rest.validation.StandardModelValidator;
import org.bibsonomy.rest.validation.StandardXMLModelValidator;
import org.bibsonomy.rest.validation.XMLModelValidator;
import org.bibsonomy.util.ValidationUtils;

/**
 * @author dzo
 */
public abstract class AbstractRenderer implements Renderer {
	private static final Log log = LogFactory.getLog(AbstractRenderer.class);
	
	protected ModelValidator modelValidator = new StandardModelValidator();
	protected XMLModelValidator xmlModelValidator = new StandardXMLModelValidator();
	protected final UrlRenderer urlRenderer;
	protected final DatatypeFactory datatypeFactory;
	
	protected AbstractRenderer(final UrlRenderer urlRenderer) {
		this.urlRenderer = urlRenderer;

		try {
			this.datatypeFactory = DatatypeFactory.newInstance();
		} catch (final DatatypeConfigurationException ex) {
			throw new RuntimeException("Could not instantiate data type factory.", ex);
		}
	}
	
	protected abstract void serialize(Writer writer, BibsonomyXML xmlDoc);
	
	protected abstract BibsonomyXML parse(Reader reader);

	@Override
	public void serializePosts(final Writer writer, final List<? extends Post<? extends Resource>> posts, final ViewModel viewModel) throws InternServerException {
		final PostsType xmlPosts = new PostsType();
		if (viewModel != null) {
			xmlPosts.setEnd(BigInteger.valueOf(viewModel.getEndValue()));
			if (viewModel.getUrlToNextResources() != null) {
				xmlPosts.setNext(viewModel.getUrlToNextResources());
			}
			xmlPosts.setStart(BigInteger.valueOf(viewModel.getStartValue()));
		} else if (posts != null) {
			xmlPosts.setStart(BigInteger.valueOf(0));
			xmlPosts.setEnd(BigInteger.valueOf(posts.size()));
		} else {
			xmlPosts.setStart(BigInteger.valueOf(0));
			xmlPosts.setEnd(BigInteger.valueOf(0));
		}
	
		if (present(posts)) {
			for (final Post<? extends Resource> post : posts) {
				final PostType xmlPost = this.createXmlPost(post);
				xmlPosts.getPost().add(xmlPost);
			}
		}
	
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setPosts(xmlPosts);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializePost(final Writer writer, final Post<? extends Resource> post, final ViewModel xxx) throws InternServerException {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setPost(this.createXmlPost(post));
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeDocument(Writer writer, Document document) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setDocument(this.createXmlDocument(document));
		this.serialize(writer, xmlDoc);
	}
	
	protected PostType createXmlPost(final Post<? extends Resource> post) throws InternServerException {
		final PostType xmlPost = new PostType();
		this.fillXmlPost(xmlPost, post);
		return xmlPost;
	}

	protected void checkReader(final Reader reader) throws BadRequestOrResponseException {
		if (reader == null) {
			throw new BadRequestOrResponseException("The body part of the received document is missing");
		}
	}

	protected void fillXmlPost(final PostType xmlPost, final Post<? extends Resource> post) {
		this.modelValidator.checkPost(post);
		this.modelValidator.checkUser(post.getUser());
		
		// set user
		final UserType xmlUser = new UserType();
		xmlUser.setName(post.getUser().getName());
		xmlUser.setHref(this.urlRenderer.createHrefForUser(post.getUser().getName()));
		xmlPost.setUser(xmlUser);
		if (post.getDate() != null) {
			xmlPost.setPostingdate(this.createXmlCalendar(post.getDate()));
		}
		if (post.getChangeDate() != null) {
			xmlPost.setChangedate(this.createXmlCalendar(post.getChangeDate()));
		}
	
		// add tags
		if (post.getTags() != null) {
			for (final Tag t : post.getTags()) {
				this.modelValidator.checkTag(t);
				final TagType xmlTag = new TagType();
				xmlTag.setName(t.getName());
				xmlTag.setHref(this.urlRenderer.createHrefForTag(t.getName()));
				xmlPost.getTag().add(xmlTag);
			}
		}
	
		// add groups
		for (final Group group : post.getGroups()) {
			this.modelValidator.checkGroup(group);
			final GroupType xmlGroup = new GroupType();
			xmlGroup.setName(group.getName());
			xmlGroup.setHref(this.urlRenderer.createHrefForGroup(group.getName()));
			xmlPost.getGroup().add(xmlGroup);
		}
	
		xmlPost.setDescription(post.getDescription());
	
		// check if the resource is a publication
		final Resource resource = post.getResource();
		if ((resource instanceof BibTex) && !(resource instanceof GoldStandardPublication)) {
			final BibTex publication = (BibTex) post.getResource();
			this.modelValidator.checkPublication(publication);
			final String userName = post.getUser().getName();
			final BibtexType xmlPublication = new BibtexType();
	
			xmlPublication.setHref(this.urlRenderer.createHrefForResource(userName, publication.getIntraHash()));
	
			this.fillXmlPublicationDetails(publication, xmlPublication);
	
			xmlPost.setBibtex(xmlPublication);
	
			// if the publication has documents …
			final List<Document> documents = publication.getDocuments();
			if (present(documents)) {
				// … put them into the xml output
				final DocumentsType xmlDocuments = new DocumentsType();
				for (final Document document : documents){
					final DocumentType xmlDocument = createXmlDocument(document);
					xmlDocument.setHref(this.urlRenderer.createHrefForResourceDocument(userName, publication.getIntraHash(), document.getFileName()));
					xmlDocuments.getDocument().add(xmlDocument);
				}
				xmlPost.setDocuments(xmlDocuments);
			}
			
			/*
			 * add extra URLs (if they exist)
			 */
			final List<BibTexExtra> extraUrls = publication.getExtraUrls();
			if (ValidationUtils.present(extraUrls)) {
				final ExtraUrlsType xmlExtraUrls = new ExtraUrlsType();
				xmlPublication.setExtraurls(xmlExtraUrls);
				
				final List<ExtraUrlType> urlList = xmlExtraUrls.getUrl();
				
				for (final BibTexExtra bibtexExtra: extraUrls) {
					final ExtraUrlType xmlExtraUrl = new ExtraUrlType();
					xmlExtraUrl.setTitle(bibtexExtra.getText());
					xmlExtraUrl.setHref(bibtexExtra.getUrl().toExternalForm());
					xmlExtraUrl.setDate(this.createXmlCalendar(bibtexExtra.getDate()));
					
					urlList.add(xmlExtraUrl);
				}
				
			}
			
		}
		// if resource is a bookmark create a xml representation
		if (resource instanceof Bookmark) {
			final Bookmark bookmark = (Bookmark) post.getResource();
			this.modelValidator.checkBookmark(bookmark);
			final BookmarkType xmlBookmark = new BookmarkType();
			xmlBookmark.setHref(this.urlRenderer.createHrefForResource(post.getUser().getName(), bookmark.getIntraHash()));
			xmlBookmark.setInterhash(bookmark.getInterHash());
			xmlBookmark.setIntrahash(bookmark.getIntraHash());
			xmlBookmark.setTitle(bookmark.getTitle());
			xmlBookmark.setUrl(bookmark.getUrl());
			xmlPost.setBookmark(xmlBookmark);
		}
	
		if (resource instanceof GoldStandardPublication) {
			/*
			 * first clear tags; gold standard publications have (currently) no tags
			 */
			xmlPost.getTag().clear();
	
			final GoldStandardPublication publication = (GoldStandardPublication) post.getResource();
	
			final GoldStandardPublicationType xmlPublication = new GoldStandardPublicationType();
			this.fillXmlPublicationDetails(publication, xmlPublication);
	
			/*
			 * add references
			 */
			final ReferencesType xmlReferences = new ReferencesType();
			xmlPublication.setReferences(xmlReferences);
	
			final List<ReferenceType> referenceList = xmlReferences.getReference();
	
			for (final BibTex reference : publication.getReferences()) {
				final ReferenceType xmlReference = new ReferenceType();
				xmlReference.setInterhash(reference.getInterHash());
	
				referenceList.add(xmlReference);
			}
	
			xmlPost.setGoldStandardPublication(xmlPublication);
		}
	}

	protected DocumentType createXmlDocument(final Document document) {
		final DocumentType xmlDocument = new DocumentType();
		xmlDocument.setFilename(document.getFileName());
		xmlDocument.setMd5Hash(document.getMd5hash());
		return xmlDocument;
	}

	protected void fillXmlPublicationDetails(final BibTex publication, final AbstractPublicationType xmlPublication) {
		xmlPublication.setAddress(publication.getAddress());
		xmlPublication.setAnnote(publication.getAnnote());
		xmlPublication.setAuthor(PersonNameUtils.serializePersonNames(publication.getAuthor()));
		xmlPublication.setBibtexAbstract(publication.getAbstract());
		xmlPublication.setBibtexKey(publication.getBibtexKey());
		xmlPublication.setBKey(publication.getKey());
		xmlPublication.setBooktitle(publication.getBooktitle());
		xmlPublication.setChapter(publication.getChapter());
		xmlPublication.setCrossref(publication.getCrossref());
		xmlPublication.setDay(publication.getDay());
		xmlPublication.setEdition(publication.getEdition());
		xmlPublication.setEditor(PersonNameUtils.serializePersonNames(publication.getEditor()));
		xmlPublication.setEntrytype(publication.getEntrytype());
		xmlPublication.setHowpublished(publication.getHowpublished());
		xmlPublication.setInstitution(publication.getInstitution());
		xmlPublication.setInterhash(publication.getInterHash());
		xmlPublication.setIntrahash(publication.getIntraHash());
		xmlPublication.setJournal(publication.getJournal());
		xmlPublication.setMisc(publication.getMisc());
		xmlPublication.setMonth(publication.getMonth());
		xmlPublication.setNote(publication.getNote());
		xmlPublication.setNumber(publication.getNumber());
		xmlPublication.setOrganization(publication.getOrganization());
		xmlPublication.setPages(publication.getPages());
		xmlPublication.setPublisher(publication.getPublisher());
		xmlPublication.setSchool(publication.getSchool());
		xmlPublication.setSeries(publication.getSeries());
		xmlPublication.setTitle(publication.getTitle());
		xmlPublication.setType(publication.getType());
		xmlPublication.setUrl(publication.getUrl());
		xmlPublication.setVolume(publication.getVolume());
		xmlPublication.setYear(publication.getYear());
		xmlPublication.setPrivnote(publication.getPrivnote());
	}

	private XMLGregorianCalendar createXmlCalendar(final Date date) {
		final GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return this.datatypeFactory.newXMLGregorianCalendar(cal);
	}

	@Override
	public void serializeUsers(final Writer writer, final List<User> users, final ViewModel viewModel) throws InternServerException {
		final UsersType xmlUsers = new UsersType();
		if (viewModel != null) {
			xmlUsers.setEnd(BigInteger.valueOf(viewModel.getEndValue()));
			if (viewModel.getUrlToNextResources() != null) {
				xmlUsers.setNext(viewModel.getUrlToNextResources());
			}
			xmlUsers.setStart(BigInteger.valueOf(viewModel.getStartValue()));
		} else if (users != null) {
			xmlUsers.setStart(BigInteger.valueOf(0));
			xmlUsers.setEnd(BigInteger.valueOf(users.size()));
		} else {
			xmlUsers.setStart(BigInteger.valueOf(0));
			xmlUsers.setEnd(BigInteger.valueOf(0));
		}
	
		if (present(users)) {
			for (final User user : users) {
				xmlUsers.getUser().add(this.createXmlUser(user));
			}
		}
	
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setUsers(xmlUsers);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeUser(final Writer writer, final User user, final ViewModel viewModel) throws InternServerException {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setUser(this.createXmlUser(user));
		this.serialize(writer, xmlDoc);
	}

	private UserType createXmlUser(final User user) throws InternServerException {
		this.modelValidator.checkUser(user);
		final UserType xmlUser = new UserType();
		xmlUser.setEmail(user.getEmail());
		if (user.getHomepage() != null) {
			xmlUser.setHomepage(user.getHomepage().toString());
		}
		xmlUser.setName(user.getName());
		xmlUser.setRealname(user.getRealname());
		xmlUser.setHref(this.urlRenderer.createHrefForUser(user.getName()));
		if (user.getSpammer() != null) {
			xmlUser.setSpammer(user.getSpammer());
		}
		if (user.getPrediction() != null) {
			xmlUser.setPrediction(BigInteger.valueOf(user.getPrediction()));
		}
		if (user.getConfidence() != null) {
			xmlUser.setConfidence(Double.valueOf(user.getConfidence()));
		}
		xmlUser.setAlgorithm(user.getAlgorithm());
		xmlUser.setClassifierMode(user.getMode());
		if (user.getToClassify() != null) {
			xmlUser.setToClassify(BigInteger.valueOf(user.getToClassify()));
		}
	
		/*
		 * copy groups
		 */
		final List<Group> groups = user.getGroups();
		xmlUser.setGroups(new GroupsType());
		if (groups != null) {
			final List<GroupType> group2 = xmlUser.getGroups().getGroup();
			for (final Group group: groups) {
				group2.add(this.createXmlGroup(group));
			}
			xmlUser.getGroups().setStart(BigInteger.valueOf(0));
			xmlUser.getGroups().setEnd(BigInteger.valueOf(groups.size()));
		}
		return xmlUser;
	}

	@Override
	public void serializeTags(final Writer writer, final List<Tag> tags, final ViewModel viewModel) throws InternServerException {
		final TagsType xmlTags = new TagsType();
		if (viewModel != null) {
			xmlTags.setEnd(BigInteger.valueOf(viewModel.getEndValue()));
			if (viewModel.getUrlToNextResources() != null) {
				xmlTags.setNext(viewModel.getUrlToNextResources());
			}
			xmlTags.setStart(BigInteger.valueOf(viewModel.getStartValue()));
		} else if( tags!=null ) {
			xmlTags.setStart(BigInteger.valueOf(0));
			xmlTags.setEnd(BigInteger.valueOf(tags.size()));
		} else {
			xmlTags.setStart(BigInteger.valueOf(0));
			xmlTags.setEnd(BigInteger.valueOf(0));
		}
	
		if (present(tags)) {
			for (final Tag tag : tags) {
				xmlTags.getTag().add(this.createXmlTag(tag));
			}
		}
	
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setTags(xmlTags);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeTag(final Writer writer, final Tag tag, final ViewModel model) throws InternServerException {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setTag(this.createXmlTag(tag));
		this.serialize(writer, xmlDoc);
	}

	private TagType createXmlTag(final Tag tag) throws InternServerException {
		final TagType xmlTag = new TagType();
		this.modelValidator.checkTag(tag);
		xmlTag.setName(tag.getName());
		xmlTag.setHref(this.urlRenderer.createHrefForTag(tag.getName()));
		// if (tag.getGlobalcount() > 0) {
		xmlTag.setGlobalcount(BigInteger.valueOf(tag.getGlobalcount()));
		// }
		// if (tag.getUsercount() > 0) {
		xmlTag.setUsercount(BigInteger.valueOf(tag.getUsercount()));
		// }
	
		// add sub-/supertags - dbe, 20070718
		if (present(tag.getSubTags())) {
			xmlTag.getSubTags().add(this.createXmlTags(tag.getSubTags()));
		}
		if (present(tag.getSuperTags())) {
			xmlTag.getSuperTags().add(this.createXmlTags(tag.getSuperTags()));
		}
		return xmlTag;
	}

	private TagsType createXmlTags(final List<Tag> tags) {
		final TagsType xmlTags = new TagsType();
		for (final Tag tag : tags) {
			xmlTags.getTag().add(this.createXmlTag(tag));
		}
		xmlTags.setStart(BigInteger.valueOf(0));
		xmlTags.setEnd(BigInteger.valueOf(tags.size()));
		return xmlTags;
	}

	@Override
	public void serializeGroups(final Writer writer, final List<Group> groups, final ViewModel viewModel) throws InternServerException {
		final GroupsType xmlGroups = new GroupsType();
		if (viewModel != null) {
			xmlGroups.setEnd(BigInteger.valueOf(viewModel.getEndValue()));
			if (viewModel.getUrlToNextResources() != null) {
				xmlGroups.setNext(viewModel.getUrlToNextResources());
			}
			xmlGroups.setStart(BigInteger.valueOf(viewModel.getStartValue()));
		} else if (groups!=null) {
			xmlGroups.setStart(BigInteger.valueOf(0));
			xmlGroups.setEnd(BigInteger.valueOf(groups.size()));
		} else {
			xmlGroups.setStart(BigInteger.valueOf(0));
			xmlGroups.setEnd(BigInteger.valueOf(0));
		}
	
		if (present(groups)) {
			for (final Group group : groups) {
				xmlGroups.getGroup().add(this.createXmlGroup(group));
			}
		}
	
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setGroups(xmlGroups);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeGroup(final Writer writer, final Group group, final ViewModel model) throws InternServerException {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setGroup(this.createXmlGroup(group));
		this.serialize(writer, xmlDoc);
	}

	private GroupType createXmlGroup(final Group group) {
		this.modelValidator.checkGroup(group);
		final GroupType xmlGroup = new GroupType();
		xmlGroup.setName(group.getName());
		xmlGroup.setDescription(group.getDescription());
		xmlGroup.setRealname(group.getRealname());
		if (group.getHomepage() != null) {
			xmlGroup.setHomepage(group.getHomepage().toString());
		}
		xmlGroup.setHref(this.urlRenderer.createHrefForGroup(group.getName()));
		xmlGroup.setDescription(group.getDescription());
		if (group.getUsers() != null) {
			for (final User user : group.getUsers()) {
				xmlGroup.getUser().add(this.createXmlUser(user));
			}
		}
		return xmlGroup;
	}

	@Override
	public void serializeOK(final Writer writer) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeFail(final Writer writer) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.FAIL);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeError(final Writer writer, final String errorMessage) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.FAIL);
		xmlDoc.setError(errorMessage);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeGroupId(final Writer writer, final String groupId) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setGroupid(groupId);
		this.serialize(writer, xmlDoc);		
	}

	@Override
	public void serializeResourceHash(final Writer writer, final String hash) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setResourcehash(hash);
		this.serialize(writer, xmlDoc);		
	}

	@Override
	public void serializeUserId(final Writer writer, final String userId) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setUserid(userId);
		this.serialize(writer, xmlDoc);		
	}

	@Override
	public void serializeURI(final Writer writer, final String uri) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		xmlDoc.setUri(uri);
		this.serialize(writer, xmlDoc);		
	}

	@Override
	public void serializeSynchronizationPosts(final Writer writer, final List<? extends SynchronizationPost> posts) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		final SyncPostsType xmlSyncPosts = new SyncPostsType();
		for (final SynchronizationPost post : posts) {
			final SyncPostType xmlSyncPost = this.createXmlSyncPost(post);
			xmlSyncPosts.getSyncPost().add(xmlSyncPost);
		}
		xmlDoc.setSyncPosts(xmlSyncPosts);
		this.serialize(writer, xmlDoc);
	}

	/**
	 * @param post
	 * @return SyncPostType representation of given post
	 */
	private SyncPostType createXmlSyncPost(final SynchronizationPost post) {
		final SyncPostType xmlSyncpost = new SyncPostType();
		if(present(post.getAction())) {
			xmlSyncpost.setAction(post.getAction().toString());
		} if (present(post.getChangeDate())) {
			xmlSyncpost.setChangeDate(this.createXmlCalendar(post.getChangeDate()));
		}
		if (present(post.getCreateDate())) {
			xmlSyncpost.setCreateDate(this.createXmlCalendar(post.getCreateDate()));
		}
		xmlSyncpost.setHash(post.getIntraHash());
		if(present(post.getPost())) {
			xmlSyncpost.setPost(this.createXmlPost(post.getPost()));
		}
		return xmlSyncpost;
	}

	@Override
	public void serializeSynchronizationData(final Writer writer, final SynchronizationData syncData) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		
		final SyncDataType xmlSyncData = new SyncDataType();
		xmlSyncData.setLastSyncDate(this.createXmlCalendar(syncData.getLastSyncDate()));
		xmlSyncData.setResourceType(ResourceFactory.getResourceName(syncData.getResourceType()));
		xmlSyncData.setService(syncData.getService().toString());
		xmlSyncData.setSynchronizationStatus(syncData.getStatus().toString());
		xmlSyncData.setInfo(syncData.getInfo());
		
		xmlDoc.setSyncData(xmlSyncData);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public List<SynchronizationPost> parseSynchronizationPostList(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (xmlDoc.getSyncPosts() != null) {
			final List<SynchronizationPost> syncPosts = new LinkedList<SynchronizationPost>();
			for (final SyncPostType spt : xmlDoc.getSyncPosts().getSyncPost()) {
				syncPosts.add(this.createSynchronizationPost(spt));
			}
			return syncPosts;
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no synchronization posts defined.");
	}

	@Override
	public SynchronizationData parseSynchronizationData(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (xmlDoc.getSyncData() != null) {
			return this.createSynchronizationData(xmlDoc.getSyncData());
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no  defined.");
	}

	@Override
	public void serializeReference(final Writer writer, final String referenceHash) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(StatType.OK);
		final ReferencesType refsType = new ReferencesType();
		final ReferenceType type = new ReferenceType();
		type.setInterhash(referenceHash);
		refsType.getReference().add(type);
		xmlDoc.setReferences(refsType);
		this.serialize(writer, xmlDoc);
	}
	
	@Override
	public String parseError(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (xmlDoc.getError() != null) {
			return xmlDoc.getError();
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no error defined.");
	}

	@Override
	public User parseUser(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
	
		if (xmlDoc.getUser() != null) {
			return this.createUser(xmlDoc.getUser());
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no user defined.");
	}

	@Override
	public Post<? extends Resource> parsePost(final Reader reader, DataAccessor uploadedFileAccessor) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
	
		final PostType post = xmlDoc.getPost();
		if (post != null) {
			try {
				return this.createPost(post, uploadedFileAccessor);
			} catch (final PersonListParserException ex) {
				xmlDoc.setError("Error parsing the person names for entry with BibTeXKey '" + post.getBibtex().getBibtexKey() + "': " + ex.getMessage());
			}
		}
	
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no post defined.");
	}

	@Override
	public Document parseDocument(Reader reader, DataAccessor uploadFileAccessor) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		
		final DocumentType docType = xmlDoc.getDocument();
		if (docType!= null) {
			
			final Document document = new Document();
			document.setFileName(docType.getFilename());
			document.setMd5hash(docType.getMd5Hash());
				
			return document;
		}
	
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no valid document data defined.");
	}
	
	@Override
	public Post<? extends Resource> parseCommunityPost(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
	
		final PostType post = xmlDoc.getPost();
		if (post != null) {
			try {
				return this.createCommunityPost(post);
			} catch (final PersonListParserException ex) {
				xmlDoc.setError("Error parsing the person names for entry with BibTeX key '" + post.getBibtex().getBibtexKey() + "': " + ex.getMessage());
			}
		}
	
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no post defined.");
	}

	@Override
	public Group parseGroup(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
	
		if (xmlDoc.getGroup() != null) {
			return this.createGroup(xmlDoc.getGroup());
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no group defined.");
	}

	@Override
	public List<Group> parseGroupList(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (xmlDoc.getGroups() != null) {
			final List<Group> groups = new LinkedList<Group>();
			for (final GroupType gt : xmlDoc.getGroups().getGroup()) {
				final Group g = this.createGroup(gt);
				groups.add(g);
			}
			return groups;
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no list of groups defined.");
	}

	@Override
	public List<Post<? extends Resource>> parsePostList(final Reader reader, DataAccessor uploadedFileAcessor) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (xmlDoc.getPosts() != null) {
			final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
			for (final PostType post : xmlDoc.getPosts().getPost()) {
				try {
					final Post<? extends Resource> p = this.createPost(post, uploadedFileAcessor);
					posts.add(p);
				} catch (final PersonListParserException ex) {
					throw new BadRequestOrResponseException("Error parsing the person names for entry with BibTeX key '" + post.getBibtex().getBibtexKey() + "': " + ex.getMessage());
				}
			}
			return posts;
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no list of posts defined.");
	}
	
	@Override
	public List<Tag> parseTagList(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (xmlDoc.getTags() != null) {
			final List<Tag> tags = new LinkedList<Tag>();
			for (final TagType tt : xmlDoc.getTags().getTag()) {
				final Tag t = this.createTag(tt);
				tags.add(t);
			}
			return tags;
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no list of tags defined.");
	}

	@Override
	public List<User> parseUserList(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (xmlDoc.getUsers() != null) {
			final List<User> users = new LinkedList<User>();
			for (final UserType ut : xmlDoc.getUsers().getUser()) {
				final User u = this.createUser(ut);
				users.add(u);
			}
			return users;
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no list of users defined.");
	}

	@Override
	public Set<String> parseReferences(final Reader reader) {
		final BibsonomyXML xmlDoc = this.parse(reader);
		final ReferencesType referencesType = xmlDoc.getReferences();
	
		if (present(referencesType)) {
			final Set<String> references = new HashSet<String>();
			final List<ReferenceType> referenceList = referencesType.getReference();
	
			if (present(referenceList)) {
				for (final ReferenceType referenceType : referenceList) {
					references.add(referenceType.getInterhash());
				}
			}
	
			return references;
		}		
	
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no list of references defined.");
	}

	@Override
	public Tag parseTag(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (xmlDoc.getTag() != null) {
			return this.createTag(xmlDoc.getTag());
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no tag defined.");
	}

	@Override
	public String parseStat(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (xmlDoc.getStat() != null) {
			return xmlDoc.getStat().value();
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no status defined.");
	}

	@Override
	public String parseGroupId(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (xmlDoc.getGroupid() != null) {
			return xmlDoc.getGroupid();
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no group id.");
	}

	@Override
	public String parseResourceHash(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (xmlDoc.getResourcehash() != null) {
			return xmlDoc.getResourcehash();
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no resource hash defined.");
	}

	@Override
	public String parseUserId(final Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (xmlDoc.getUserid() != null) {
			return xmlDoc.getUserid();
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no user id defined.");
	}
	
	/**
	 * creates a user based on the xml user
	 * 
	 * @param xmlUser
	 * @return the converted user
	 */
	public User createUser(final UserType xmlUser) {
		this.xmlModelValidator.checkUser(xmlUser);

		final User user = new User();
		user.setEmail(xmlUser.getEmail());
		user.setHomepage(this.createURL(xmlUser.getHomepage()));
		user.setName(xmlUser.getName());
		user.setRealname(xmlUser.getRealname());
		user.setPassword(xmlUser.getPassword());
		if (xmlUser.isSpammer() != null) {
			user.setSpammer(xmlUser.isSpammer());
		}
		if (xmlUser.getPrediction() != null) {
			user.setPrediction(xmlUser.getPrediction().intValue());
		}
		if (xmlUser.getConfidence() != null) {
			user.setConfidence(xmlUser.getConfidence());
		}
		user.setAlgorithm(xmlUser.getAlgorithm());
		user.setMode(xmlUser.getClassifierMode());
		if (xmlUser.getToClassify() != null) {
			user.setToClassify(xmlUser.getToClassify().intValue());
		}
		/*
		 * copy groups
		 */
		final GroupsType groups = xmlUser.getGroups();
		if (groups != null) {
			final List<Group> groups2 = user.getGroups();
			for (final GroupType xmlGroup: groups.getGroup()) {
				groups2.add(this.createGroup(xmlGroup));
			}
		}
		return user;
	}

	/**
	 * creates a {@link Group} based on the xml group
	 * 
	 * @param xmlGroup
	 * @return the converted group
	 */
	public Group createGroup(final GroupType xmlGroup) {
		this.xmlModelValidator.checkGroup(xmlGroup);

		final Group group = new Group();
		group.setName(xmlGroup.getName());
		group.setDescription(xmlGroup.getDescription());
		group.setRealname(xmlGroup.getRealname());
		group.setHomepage(this.createURL(xmlGroup.getHomepage()));
		if (xmlGroup.getUser().size() > 0) {
			group.setUsers(new ArrayList<User>());
			for (final UserType xmlUser : xmlGroup.getUser()) {
				group.getUsers().add(this.createUser(xmlUser));
			}
		}

		return group;
	}

	/**
	 * converts a xml tag to the model representation
	 * 
	 * @param xmlTag
	 * @return the created tag
	 */
	public Tag createTag(final TagType xmlTag) {
		return this.createTag(xmlTag, 1);
	}

	/**
	 * TODO: improve documentation
	 * 
	 * @param xmlTag
	 * @param depth
	 * @return the created tag
	 */
	public Tag createTag(final TagType xmlTag, final int depth) {
		this.xmlModelValidator.checkTag(xmlTag);

		final Tag tag = new Tag();
		tag.setName(xmlTag.getName());
		// TODO tag count  häh?
		if (xmlTag.getGlobalcount() != null) {
			tag.setGlobalcount(xmlTag.getGlobalcount().intValue());
		}
		// TODO tag count  häh?
		if (xmlTag.getUsercount() != null) {
			tag.setUsercount(xmlTag.getUsercount().intValue());
		}

		if (depth > 0) {
			if (xmlTag.getSubTags() != null) {
				tag.setSubTags(this.createTags(xmlTag.getSubTags(), depth - 1));
			}
			if (xmlTag.getSuperTags() != null) {
				tag.setSuperTags(this.createTags(xmlTag.getSuperTags(), depth - 1));
			}
		}
		return tag;
	}

	private List<Tag> createTags(final List<TagsType> xmlTags, final int depth) {
		final List<Tag> rVal = new ArrayList<Tag>();
		for (final TagsType xmlSubTags : xmlTags) {
			for (final TagType xmlSubTag : xmlSubTags.getTag()) {
				rVal.add(this.createTag(xmlSubTag, depth));
			}
		}
		return rVal;
	}

	/**
	 * creates a {@link GoldStandard} post based on the xml post
	 * 
	 * @param xmlPost
	 * @return the converted post
	 * @throws PersonListParserException 
	 */
	public Post<Resource> createCommunityPost(final PostType xmlPost) throws PersonListParserException {
		this.xmlModelValidator.checkStandardPost(xmlPost);

		final Post<Resource> post = this.createPostWithUserAndDate(xmlPost);

		final GoldStandardPublicationType xmlPublication = xmlPost.getGoldStandardPublication();
		if (present(xmlPublication)) {
			ModelValidationUtils.checkPublication(xmlPublication);
			final GoldStandardPublication publication = new GoldStandardPublication();
			this.fillPublicationWithInformation(xmlPublication, publication);

			this.modelValidator.checkPublication(publication);

			post.setResource(publication);
		} else {
			// TODO: add goldstandard bookmark
			throw new InvalidModelException("resource is not supported");
		}

		return post;
	}
	
	/**
	 * converts an xml post to the model post
	 * 
	 * @param xmlPost
	 * @param uploadedFileAccessor 
	 * @return the converted post
	 * @throws PersonListParserException 
	 */
	protected Post<Resource> createPost(final PostType xmlPost, DataAccessor uploadedFileAccessor) throws PersonListParserException {
		this.xmlModelValidator.checkPost(xmlPost);

		// create post, user and date
		final Post<Resource> post = this.createPostWithUserAndDate(xmlPost);

		// create tags
		for (final TagType xmlTag : xmlPost.getTag()) {
			this.xmlModelValidator.checkTag(xmlTag);

			final Tag tag = new Tag();
			tag.setName(xmlTag.getName());
			post.getTags().add(tag);
		}

		// create resource
		final BibtexType xmlPublication = xmlPost.getBibtex();
		if (xmlPublication != null) {
			this.xmlModelValidator.checkPublicationXML(xmlPublication);

			final BibTex publication = new BibTex();
			this.fillPublicationWithInformation(xmlPublication, publication);

			/*
			 * check, of the post contains documents
			 */
			final DocumentsType xmlDocuments = xmlPost.getDocuments();
			if (xmlDocuments != null) {
				final List<Document> documents = new LinkedList<Document>();
				for (final DocumentType xmlDocument : xmlDocuments.getDocument()) {
					final Document document = new Document();
					document.setFileName(xmlDocument.getFilename());
					document.setMd5hash(xmlDocument.getMd5Hash());
					documents.add(document);
				}
				publication.setDocuments(documents);
			}
			
			this.modelValidator.checkPublication(publication);

			post.setResource(publication);
		}

		final BookmarkType xmlBookmark = xmlPost.getBookmark();
		if (xmlBookmark != null) {
			this.xmlModelValidator.checkBookmarkXML(xmlBookmark);
			
			final Bookmark bookmark = new Bookmark();
			bookmark.setIntraHash(xmlBookmark.getIntrahash());
			bookmark.setTitle(xmlBookmark.getTitle());
			bookmark.setUrl(xmlBookmark.getUrl());

			post.setResource(bookmark);
		}
		
		final UploadDataType upload = xmlPost.getPublicationFileUpload();
		if (upload != null) {
			final String name = upload.getMultipartName();
			if (present(name)) {
				Data data = uploadedFileAccessor.getData(name);
				if (data == null) {
					log.warn("missing data in API");
				} else {
					BibTex alreadyParsedBibtex = null;
					if (post.getResource() instanceof BibTex) {
						alreadyParsedBibtex = (BibTex) post.getResource();
					}
					post.setResource(new ImportResource(alreadyParsedBibtex, data));
				}
			} else {
				log.warn("missing multipartname  in API");
			}
		}

		if (xmlPost.getGroup() != null) {
			post.setGroups(new HashSet<Group>());
			for (final GroupType xmlGroup : xmlPost.getGroup()) {
				this.xmlModelValidator.checkGroup(xmlGroup);
				final Group group = new Group();
				group.setDescription(xmlGroup.getDescription());
				group.setName(xmlGroup.getName());
				post.getGroups().add(group);
			}
		}

		return post;
	}

	/**
	 * @param xmlPost
	 * @return
	 */
	private Post<Resource> createPostWithUserAndDate(final PostType xmlPost) {
		final Post<Resource> post = new Post<Resource>();
		post.setDescription(xmlPost.getDescription());

		// user
		final User user = this.createUser(xmlPost);
		post.setUser(user);
		post.setDate(this.createDate(xmlPost.getPostingdate()));
		post.setChangeDate(this.createDate(xmlPost.getChangedate()));
		return post;
	}

	/**
	 * @param xmlPost
	 * @return the user
	 */
	private User createUser(final PostType xmlPost) {
		final User user = new User();
		final UserType xmlUser = xmlPost.getUser();
		this.xmlModelValidator.checkUser(xmlUser);
		user.setName(xmlUser.getName());

		return user;
	}

	/**
	 * Creates a {@link SynchronizationPost} from its xml representation
	 * @param xmlSyncPost
	 * @return synchronization post
	 */
	public SynchronizationPost createSynchronizationPost(final SyncPostType xmlSyncPost) {
		final SynchronizationPost post = new SynchronizationPost();
		if (present(xmlSyncPost.getAction())) {
			final SynchronizationAction action = Enum.valueOf(SynchronizationAction.class, xmlSyncPost.getAction().toUpperCase());
			post.setAction(action);
		}
		if (present(xmlSyncPost.getChangeDate())) {
			post.setChangeDate(this.createDate(xmlSyncPost.getChangeDate()));
		}
		if (present(xmlSyncPost.getPost())) {
			try {
				post.setPost(this.createPost(xmlSyncPost.getPost(), NoDataAccessor.getInstance()));
			} catch (final PersonListParserException ex) {
				throw new BadRequestOrResponseException("Error parsing the person names for entry with BibTeX key '" + xmlSyncPost.getPost().getBibtex().getBibtexKey() + "': " + ex.getMessage());
			}
		}
		if (present(xmlSyncPost.getCreateDate())) {
			post.setCreateDate(this.createDate(xmlSyncPost.getCreateDate()));
		} else {
			throw new InvalidModelException("create date not present"); 
		}
		if (present(xmlSyncPost.getHash())) {
			post.setIntraHash(xmlSyncPost.getHash());
		} else {
			throw new InvalidModelException("hash not present");
		}
		return post;
	}

	/**
	 * Creates a {@link SynchronizationData} from xml representation
	 * @param xmlSyncData
	 * @return synchronization data
	 */
	private SynchronizationData createSynchronizationData(final SyncDataType xmlSyncData) {
		final SynchronizationData syncData = new SynchronizationData();
		final String errors = this.fillSyncData(xmlSyncData, syncData);
		if (!present(errors)) {
			return syncData;
		}

		throw new InvalidModelException(errors.trim());
	}

	private String fillSyncData(final SyncDataType xmlSyncData, final SynchronizationData syncData) {
		final StringBuilder errors = new StringBuilder();
		
		syncData.setInfo(xmlSyncData.getInfo());
		
		final XMLGregorianCalendar lastSyncDate = xmlSyncData.getLastSyncDate();
		if (present(lastSyncDate)) {
			syncData.setLastSyncDate(this.createDate(lastSyncDate));
		} else {
			errors.append("last sync date is not present\n");
		}
		
		final String resourceType = xmlSyncData.getResourceType();
		if(present(resourceType)) {
			syncData.setResourceType(ResourceFactory.getResourceClass(resourceType.toLowerCase()));
		} else {
			errors.append("resource type is not present\n");
		}
		
		final String service = xmlSyncData.getService();
		if (present(service)) {
			try {
				syncData.setService(new URI(service));
			} catch (final URISyntaxException ex) {
				errors.append("service uri is malformed: " + ex.getMessage() + "\n");
			}
		} else {
			errors.append("service URI is not present\n");
		}

		final String synchronizationStatus = xmlSyncData.getSynchronizationStatus();
		if (present(synchronizationStatus)) {
			syncData.setStatus(Enum.valueOf(SynchronizationStatus.class, synchronizationStatus.toUpperCase()));
		} else {
			errors.append("synchronization status not present\n");
		}
		
		if (!present(errors)) {
			return "";
		}
		return errors.toString();
	}

	/**
	 * @param xmlPublication
	 * @param publication
	 * @throws PersonListParserException 
	 */
	private void fillPublicationWithInformation(final AbstractPublicationType xmlPublication, final BibTex publication) throws PersonListParserException {
		publication.setAddress(xmlPublication.getAddress());
		publication.setAnnote(xmlPublication.getAnnote());
		publication.setAuthor(PersonNameUtils.discoverPersonNames(xmlPublication.getAuthor()));
		publication.setAbstract(xmlPublication.getBibtexAbstract());
		publication.setBibtexKey(xmlPublication.getBibtexKey());
		publication.setKey(xmlPublication.getBKey());
		publication.setBooktitle(xmlPublication.getBooktitle());
		publication.setChapter(xmlPublication.getChapter());
		publication.setCrossref(xmlPublication.getCrossref());
		publication.setDay(xmlPublication.getDay());
		publication.setEdition(xmlPublication.getEdition());
		publication.setEditor(PersonNameUtils.discoverPersonNames(xmlPublication.getEditor()));
		publication.setEntrytype(xmlPublication.getEntrytype());
		publication.setHowpublished(xmlPublication.getHowpublished());
		publication.setInstitution(xmlPublication.getInstitution());
		publication.setInterHash(xmlPublication.getInterhash());
		publication.setIntraHash(xmlPublication.getIntrahash());
		publication.setJournal(xmlPublication.getJournal());
		publication.setMisc(xmlPublication.getMisc());
		publication.setMonth(xmlPublication.getMonth());
		publication.setNote(xmlPublication.getNote());
		publication.setNumber(xmlPublication.getNumber());
		publication.setOrganization(xmlPublication.getOrganization());
		publication.setPages(xmlPublication.getPages());
		publication.setPublisher(xmlPublication.getPublisher());
		publication.setSchool(xmlPublication.getSchool());
		publication.setSeries(xmlPublication.getSeries());
		publication.setTitle(xmlPublication.getTitle());
		publication.setType(xmlPublication.getType());
		publication.setUrl(xmlPublication.getUrl());
		publication.setVolume(xmlPublication.getVolume());
		publication.setYear(xmlPublication.getYear());
		publication.setPrivnote(xmlPublication.getPrivnote());
		
		// extra URLs
		final ExtraUrlsType extraurls = xmlPublication.getExtraurls();
		if (ValidationUtils.present(extraurls)) {
			final List<ExtraUrlType> urls = extraurls.getUrl();
			final List<BibTexExtra> eurls = new ArrayList<BibTexExtra>(urls.size());
			
			for (final ExtraUrlType extraUrl : urls) {
				eurls.add(new BibTexExtra(this.createURL(extraUrl.getHref()), extraUrl.getTitle(), this.createDate(extraUrl.getDate())));
			}
			publication.setExtraUrls(eurls);
		}
		
	}
	
	/**
	 * Helper method to create a new URL object with ignoring exceptions.
	 * 
	 * @param s The string to be converted to a URL
	 * @return <code>null</code> if the string could not be converted
	 */
	private URL createURL(final String s) {
		try {
			return new URL(s);
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	
	/**
	 * Helper method to create a date when parsing a post. Two situations may occur:
	 * 
	 * 1/ The post is parsed on client side. Then the date is the one as sent by
	 *    the BibSonomy API.
	 *    
	 * 2/ The post is parsed on server side; this only happens in the two strategies
	 *       {@link org.bibsonomy.rest.strategy.users.PutPostStragegy}} and 
	 *       {@link org.bibsonomy.rest.strategy.users.PostPostStragegy}.
	 *    In both strategies, the date is overwritten in order to prevent malicious users
	 *    from posting posts with faked dates (e.g. from the future)
	 *    
	 * @param date - the date of the XML post
	 * @return a date for this post
	 */
	private Date createDate(final XMLGregorianCalendar date) {
		/*
		 * If there is no date, use the current date. 
		 */
		if (date == null) {
			return new Date();
		}
		/*
		 * this is save because the postingdate is overwritten in the corresponding
		 * strategies when creating or updating a post (see above) 
		 */
		return date.toGregorianCalendar().getTime();
	}

	/**
	 * @param modelValidator the modelValidator to set
	 */
	public void setModelValidator(final ModelValidator modelValidator) {
		this.modelValidator = modelValidator;
	}

	/**
	 * @param xmlModelValidator the xmlModelValidator to set
	 */
	public void setXmlModelValidator(XMLModelValidator xmlModelValidator) {
		this.xmlModelValidator = xmlModelValidator;
	}
}