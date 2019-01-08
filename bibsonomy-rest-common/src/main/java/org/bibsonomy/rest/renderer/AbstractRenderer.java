/**
 * BibSonomy-Rest-Common - Common things for the REST-client and server.
 * <p>
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 * University of Kassel, Germany
 * http://www.kde.cs.uni-kassel.de/
 * Data Mining and Information Retrieval Group,
 * University of Würzburg, Germany
 * http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 * L3S Research Center,
 * Leibniz University Hannover, Germany
 * http://www.l3s.de/
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.renderer;

import static org.bibsonomy.util.ValidationUtils.present;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.InvalidModelException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.GroupRequest;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.CRISLinkDataSource;
import org.bibsonomy.model.cris.CRISLinkType;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.cris.ProjectPersonLinkType;
import org.bibsonomy.model.enums.Gender;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.sync.SynchronizationAction;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStatus;
import org.bibsonomy.model.user.remote.RemoteUserId;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.model.user.remote.SimpleRemoteUserId;
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
import org.bibsonomy.rest.renderer.xml.CRISLinkDataSourceType;
import org.bibsonomy.rest.renderer.xml.CRISLinkTypeType;
import org.bibsonomy.rest.renderer.xml.DocumentType;
import org.bibsonomy.rest.renderer.xml.DocumentsType;
import org.bibsonomy.rest.renderer.xml.ExtraUrlType;
import org.bibsonomy.rest.renderer.xml.ExtraUrlsType;
import org.bibsonomy.rest.renderer.xml.GenderType;
import org.bibsonomy.rest.renderer.xml.GoldStandardPublicationType;
import org.bibsonomy.rest.renderer.xml.GroupMembershipType;
import org.bibsonomy.rest.renderer.xml.GroupMembershipsType;
import org.bibsonomy.rest.renderer.xml.GroupRequestType;
import org.bibsonomy.rest.renderer.xml.GroupRoleType;
import org.bibsonomy.rest.renderer.xml.GroupType;
import org.bibsonomy.rest.renderer.xml.GroupsType;
import org.bibsonomy.rest.renderer.xml.LinkableType;
import org.bibsonomy.rest.renderer.xml.PersonMatchType;
import org.bibsonomy.rest.renderer.xml.PersonNameType;
import org.bibsonomy.rest.renderer.xml.PersonType;
import org.bibsonomy.rest.renderer.xml.PostType;
import org.bibsonomy.rest.renderer.xml.PostsType;
import org.bibsonomy.rest.renderer.xml.ProjectPersonLinkTypeType;
import org.bibsonomy.rest.renderer.xml.ProjectType;
import org.bibsonomy.rest.renderer.xml.ProjectsType;
import org.bibsonomy.rest.renderer.xml.PublicationType;
import org.bibsonomy.rest.renderer.xml.PublicationsType;
import org.bibsonomy.rest.renderer.xml.PublishedInType;
import org.bibsonomy.rest.renderer.xml.ReferenceType;
import org.bibsonomy.rest.renderer.xml.ReferencesType;
import org.bibsonomy.rest.renderer.xml.RelationType;
import org.bibsonomy.rest.renderer.xml.RemoteUserIdType;
import org.bibsonomy.rest.renderer.xml.ResourceLinkType;
import org.bibsonomy.rest.renderer.xml.ResourcePersonRelationType;
import org.bibsonomy.rest.renderer.xml.ResourcePersonRelationsType;
import org.bibsonomy.rest.renderer.xml.StatType;
import org.bibsonomy.rest.renderer.xml.SyncDataType;
import org.bibsonomy.rest.renderer.xml.SyncPostType;
import org.bibsonomy.rest.renderer.xml.SyncPostsType;
import org.bibsonomy.rest.renderer.xml.TagType;
import org.bibsonomy.rest.renderer.xml.TagsType;
import org.bibsonomy.rest.renderer.xml.UploadDataType;
import org.bibsonomy.rest.renderer.xml.UserType;
import org.bibsonomy.rest.renderer.xml.UsersType;
import org.bibsonomy.rest.validation.StandardXMLModelValidator;
import org.bibsonomy.rest.validation.XMLModelValidator;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author dzo
 */
public abstract class AbstractRenderer implements Renderer {
	private static final Log log = LogFactory.getLog(AbstractRenderer.class);

	private static final Map<String, Function<LinkableType, Linkable>> TO_LINKABLE_MAPPERS = new HashMap<>();
	private static final Map<String, Function<Linkable, LinkableType>> TO_LINKABLE_TYPE_MAPPERS = new HashMap<>();

	static {
		TO_LINKABLE_MAPPERS.put(ProjectType.class.getName(), l -> {
			final Project project = new Project();
			project.setExternalId(((ProjectType) l).getExternalId());
			return project;
		});
		TO_LINKABLE_MAPPERS.put(PersonType.class.getName(), l -> {
			final Person person = new Person();
			person.setPersonId(((PersonType) l).getPersonId());
			return person;
		});
		TO_LINKABLE_MAPPERS.put(PostType.class.getName(), l -> {
			final Post<? extends Resource> post = new Post<>();
			final BibTex resource = new ResourceFactory().createPublication();
			resource.setInterHash(((PostType) l).getBibtex().getInterhash());
			return post;
		});
	}

	static {
		TO_LINKABLE_TYPE_MAPPERS.put(Project.class.getName(), l -> {
			final ProjectType projectType = new ProjectType();
			projectType.setExternalId(((Project) l).getExternalId());
			return projectType;
		});
		TO_LINKABLE_TYPE_MAPPERS.put(Person.class.getName(), l -> {
			final PersonType personType = new PersonType();
			personType.setPersonId(((Person) l).getPersonId());
			return personType;
		});
		TO_LINKABLE_TYPE_MAPPERS.put(Post.class.getName(), l -> {
			final PostType postType = new PostType();
			final BibtexType bibtexType = new BibtexType();
			bibtexType.setInterhash(((Post<? extends Resource>) l).getResource().getInterHash());
			postType.setBibtex(bibtexType);
			return postType;
		});
	}

	protected final UrlRenderer urlRenderer;
	protected final DatatypeFactory datatypeFactory;
	protected XMLModelValidator xmlModelValidator = new StandardXMLModelValidator();

	protected AbstractRenderer(final UrlRenderer urlRenderer) {
		this.urlRenderer = urlRenderer;

		try {
			this.datatypeFactory = DatatypeFactory.newInstance();
		} catch (final DatatypeConfigurationException ex) {
			throw new RuntimeException("Could not instantiate data type factory.", ex);
		}
	}

	private static BibsonomyXML buildEmptyBibsonomyXMLWithOK() {
		return buildEmptyBibsonomyXML(StatType.OK);
	}

	private static BibsonomyXML buildEmptyBibsonomyXMLWithFAIL() {
		return buildEmptyBibsonomyXML(StatType.FAIL);
	}

	private static BibsonomyXML buildEmptyBibsonomyXML(StatType statType) {
		final BibsonomyXML xmlDoc = new BibsonomyXML();
		xmlDoc.setStat(statType);
		return xmlDoc;
	}

	/**
	 * Helper method to create a new URL object with ignoring exceptions.
	 *
	 * @param s The string to be converted to a URL
	 * @return <code>null</code> if the string could not be converted
	 */
	private static URL createURL(final String s) {
		try {
			return new URL(s);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * Helper method to create a date when parsing a post. Two situations may occur:
	 * <p>
	 * 1/ The post is parsed on client side. Then the date is the one as sent by
	 * the BibSonomy API.
	 * <p>
	 * 2/ The post is parsed on server side; the date is overwritten in order to prevent malicious users
	 * from posting posts with faked dates (e.g. from the future)
	 *
	 * @param date - the date of the XML post
	 * @return a date for this post
	 */
	private static Date createDate(final XMLGregorianCalendar date) {
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

	public static <T> void setValue(Consumer<T> consumer, Supplier<T> supplier) {
		setValue(consumer, supplier, Function.identity());
	}

	public static <T, E> void setValue(Consumer<T> consumer, Supplier<E> supplier, Function<E, T> transformer) {
		final E value = supplier.get();
		if (!present(value)) {
			return;
		}
		final T transformedValue = transformer.apply(value);
		if (present(transformedValue)) {
			consumer.accept(transformedValue);
		}
	}

	private static <T, E> void setCollectionValue(Collection<T> consumer, Collection<E> producer,
																								Function<E, T> transformer) {
		if (present(producer)) {
			consumer.addAll(producer.stream().map(transformer).collect(Collectors.toList()));
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
			xmlPosts.setStart(BigInteger.ZERO);
			xmlPosts.setEnd(BigInteger.valueOf(posts.size()));
		} else {
			xmlPosts.setStart(BigInteger.ZERO);
			xmlPosts.setEnd(BigInteger.ZERO);
		}

		if (present(posts)) {
			for (final Post<? extends Resource> post : posts) {
				final PostType xmlPost = this.createXmlPost(post);
				xmlPosts.getPost().add(xmlPost);
			}
		}

		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setPosts(xmlPosts);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializePost(final Writer writer, final Post<? extends Resource> post, final ViewModel xxx) throws InternServerException {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setPost(this.createXmlPost(post));
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeDocument(Writer writer, Document document) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setDocument(this.createXmlDocument(document));
		this.serialize(writer, xmlDoc);
	}

	private PostType createXmlPost(final Post<? extends Resource> post) throws InternServerException {
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
		// set user
		final String userName = post.getUser().getName();
		final UserType xmlUser = new UserType();
		xmlUser.setName(userName);
		xmlUser.setHref(this.urlRenderer.createHrefForUser(userName));
		xmlPost.setUser(xmlUser);

		// date infos
		final Date date = post.getDate();
		if (date != null) {
			xmlPost.setPostingdate(this.createXmlCalendar(date));
		}
		final Date changeDate = post.getChangeDate();
		if (changeDate != null) {
			xmlPost.setChangedate(this.createXmlCalendar(changeDate));
		}

		// add tags
		final Set<Tag> tags = post.getTags();
		if (tags != null) {
			for (final Tag t : tags) {
				final TagType xmlTag = new TagType();
				xmlTag.setName(t.getName());
				xmlTag.setHref(this.urlRenderer.createHrefForTag(t.getName()));
				xmlPost.getTag().add(xmlTag);
			}
		}

		// add groups
		for (final Group group : post.getGroups()) {
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
			final BibtexType xmlPublication = new BibtexType();

			final String intraHash = publication.getIntraHash();

			// new post do not have an intrahash => set no url
			if (present(intraHash)) {
				xmlPublication.setHref(this.urlRenderer.createHrefForResource(userName, intraHash));
			}

			this.fillXmlPublicationDetails(publication, xmlPublication);

			xmlPost.setBibtex(xmlPublication);

			// if the publication has documents …
			final List<Document> documents = publication.getDocuments();
			if (present(documents)) {
				// … put them into the xml output
				final DocumentsType xmlDocuments = new DocumentsType();
				for (final Document document : documents) {
					final DocumentType xmlDocument = createXmlDocument(document);
					xmlDocument.setHref(this.urlRenderer.createHrefForResourceDocument(userName, intraHash, document.getFileName()));
					xmlDocuments.getDocument().add(xmlDocument);
				}
				xmlPost.setDocuments(xmlDocuments);
			}

			/*
			 * add extra URLs (if they exist)
			 */
			final List<BibTexExtra> extraUrls = publication.getExtraUrls();
			if (present(extraUrls)) {
				final ExtraUrlsType xmlExtraUrls = new ExtraUrlsType();
				xmlPublication.setExtraurls(xmlExtraUrls);

				final List<ExtraUrlType> urlList = xmlExtraUrls.getUrl();

				for (final BibTexExtra bibtexExtra : extraUrls) {
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
				final String interHash = reference.getInterHash();
				xmlReference.setInterhash(interHash);
				xmlReference.setHref(this.urlRenderer.createHrefForCommunityPost(interHash));

				referenceList.add(xmlReference);
			}

			final Set<BibTex> publishedInSet = publication.getReferenceThisPublicationIsPublishedIn();
			if (present(publishedInSet)) {
				final BibTex publishedIn = publishedInSet.iterator().next();
				final PublishedInType publisedInXml = new PublishedInType();
				final String interHash = publishedIn.getInterHash();
				publisedInXml.setInterhash(interHash);
				publisedInXml.setHref(this.urlRenderer.createHrefForCommunityPost(interHash));
				xmlPublication.setPublishedIn(publisedInXml);
			}

			final Set<BibTex> publicationsPartOfPublication = publication.getSubGoldStandards();
			final PublicationsType partOfList = new PublicationsType();
			xmlPublication.setPublications(partOfList);

			final List<PublicationType> publications = partOfList.getPublication();
			for (final BibTex publicationPart : publicationsPartOfPublication) {
				final PublicationType xmlPublicationPart = new PublicationType();
				final String interHash = publicationPart.getInterHash();

				xmlPublicationPart.setInterhash(interHash);
				xmlPublicationPart.setHref(this.urlRenderer.createHrefForCommunityPost(interHash));
				publications.add(xmlPublicationPart);
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
			xmlUsers.setStart(BigInteger.ZERO);
			xmlUsers.setEnd(BigInteger.valueOf(users.size()));
		} else {
			xmlUsers.setStart(BigInteger.ZERO);
			xmlUsers.setEnd(BigInteger.ZERO);
		}

		if (present(users)) {
			for (final User user : users) {
				xmlUsers.getUser().add(this.createXmlUser(user));
			}
		}

		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setUsers(xmlUsers);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeUser(final Writer writer, final User user, final ViewModel viewModel) throws InternServerException {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setUser(createXmlUser(user));
		this.serialize(writer, xmlDoc);
	}

	private UserType createXmlUser(final User user) throws InternServerException {
		final UserType xmlUser = new UserType();
		setValue(xmlUser::setEmail, user::getEmail);
		setValue(xmlUser::setHomepage, user::getHomepage, URL::toString);
		setValue(xmlUser::setName, user::getName);
		setValue(xmlUser::setRealname, user::getRealname);
		setValue(xmlUser::setHref, user::getName, urlRenderer::createHrefForUser);
		setValue(xmlUser::setSpammer, user::getSpammer);
		setValue(xmlUser::setConfidence, user::getConfidence);
		setValue(xmlUser::setAlgorithm, user::getAlgorithm);
		setValue(xmlUser::setClassifierMode, user::getMode);
		setValue(xmlUser::setPassword, user::getPassword);
		setValue(xmlUser::setPrediction, user::getPrediction, i -> BigInteger.valueOf(i.longValue()));
		setValue(xmlUser::setToClassify, user::getToClassify, i -> BigInteger.valueOf(i.longValue()));
		setCollectionValue(xmlUser.getRemoteUserId(), user.getRemoteUserIds(), this::createXmlRemoteUserIdType);

		/*
		 * copy groups
		 */
		final List<Group> groups = user.getGroups();
		xmlUser.setGroups(new GroupsType());
		if (groups != null) {
			final List<GroupType> group2 = xmlUser.getGroups().getGroup();
			for (final Group group : groups) {
				group2.add(this.createXmlGroup(group));
			}
			xmlUser.getGroups().setStart(BigInteger.ZERO);
			xmlUser.getGroups().setEnd(BigInteger.valueOf(groups.size()));
		}
		return xmlUser;
	}

	private RemoteUserIdType createXmlRemoteUserIdType(RemoteUserId remoteUserId) {
		final RemoteUserIdType remoteUserIdType = new RemoteUserIdType();
		remoteUserIdType.setUserId(remoteUserId.getSimpleId());
		if (remoteUserId instanceof SamlRemoteUserId) {
			remoteUserIdType.setIdentityProvider(((SamlRemoteUserId) remoteUserId).getIdentityProviderId());
		}
		return remoteUserIdType;
	}

	@Override
	public void serializeCRISLink(Writer writer, CRISLink crisLink, ViewModel viewModel) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setCrisLink(createXmlCRISLink(crisLink));
		serialize(writer, xmlDoc);
	}

	private CRISLinkTypeType createXmlCRISLink(CRISLink crisLink) {
		final CRISLinkTypeType crisLinkTypeType = new CRISLinkTypeType();
		setValue(crisLinkTypeType::setStartDate, crisLink::getStartDate, this::createXmlCalendar);
		setValue(crisLinkTypeType::setEndDate, crisLink::getEndDate, this::createXmlCalendar);
		if (present(crisLink.getLinkType())) {
			crisLinkTypeType.setLinkType(ProjectPersonLinkTypeType.
							valueOf(((ProjectPersonLinkType) crisLink.getLinkType()).name()));
		}
		if (present(crisLink.getDataSource())) {
			crisLinkTypeType.setDataSource(CRISLinkDataSourceType.valueOf(crisLink.getDataSource().name()));
		}
		setValue(crisLinkTypeType::setSource, crisLink::getSource, this::createXmlLinkable);
		setValue(crisLinkTypeType::setTarget, crisLink::getTarget, this::createXmlLinkable);
		return crisLinkTypeType;
	}

	private LinkableType createXmlLinkable(Linkable linkable) {
		return TO_LINKABLE_TYPE_MAPPERS.getOrDefault(linkable.getClass().getName(), null).apply(linkable);
	}

	@Override
	public void serializeProjects(Writer writer, List<Project> projects, ViewModel viewModel) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setProjects(createXmlCRISProjects(projects));
		serialize(writer, xmlDoc);
	}

	private ProjectsType createXmlCRISProjects(List<Project> projects) {
		ProjectsType projectsType = new ProjectsType();
		setCollectionValue(projectsType.getProject(), projects, this::createXmlCRISProject);
		return projectsType;
	}

	@Override
	public void serializeProject(Writer writer, Project project, ViewModel viewModel) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setProject(createXmlCRISProject(project));
		serialize(writer, xmlDoc);
	}

	private ProjectType createXmlCRISProject(Project project) {
		final ProjectType projectType = new ProjectType();
		setValue(projectType::setExternalId, project::getExternalId);
		setValue(projectType::setBudget, project::getBudget);
		setValue(projectType::setDescription, project::getDescription);
		setValue(projectType::setSubTitle, project::getSubTitle);
		setValue(projectType::setInternalId, project::getInternalId);
		setValue(projectType::setType, project::getType);
		setValue(projectType::setTitle, project::getTitle);
		setValue(projectType::setStartDate, project::getStartDate, this::createXmlCalendar);
		setValue(projectType::setEndDate, project::getEndDate, this::createXmlCalendar);
		setValue(projectType::setSponsor, project::getSponsor);
		setValue(projectType::setParentProject, project::getParentProject, this::createXmlCRISProject);
		setCollectionValue(projectType.getCrisLinks(), project.getCrisLinks(), this::createXmlCRISLink);
		setCollectionValue(projectType.getSubProjects(), project.getSubProjects(), this::createXmlCRISProject);
		return projectType;
	}

	@Override
	public void serializePersonMatch(Writer writer, PersonMatch match, ViewModel viewModel) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setPersonMatch(createXmlPersonMatch(match));
		serialize(writer, xmlDoc);
	}

	private PersonMatchType createXmlPersonMatch(PersonMatch match) {
		final PersonMatchType xmlPersonMatch = new PersonMatchType();
		setValue(xmlPersonMatch::setMatchId, match::getMatchID);
		setValue(xmlPersonMatch::setPerson1, match::getPerson1, this::createXmlPerson);
		setValue(xmlPersonMatch::setPerson2, match::getPerson2, this::createXmlPerson);
		setValue(xmlPersonMatch::setState, match::getState);
		setCollectionValue(xmlPersonMatch.getPerson1Posts(), match.getPerson1Posts(), this::createXmlPost);
		setCollectionValue(xmlPersonMatch.getPerson2Posts(), match.getPerson2Posts(), this::createXmlPost);
		setValue(xmlPersonMatch::setMatchId, match::getMatchID);
		setValue(xmlPersonMatch::setState, match::getState);
		setValue(xmlPersonMatch.getUserDenies()::addAll, match::getUserDenies);
		return xmlPersonMatch;
	}

	@Override
	public void serializePerson(Writer writer, Person person, ViewModel viewModel) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setPerson(createXmlPerson(person));
		serialize(writer, xmlDoc);
	}

	private UserType createXmlUser(String userName) {
		UserType userType = new UserType();
		userType.setName(userName);
		return userType;
	}

	private PersonType createXmlPerson(Person person) throws InternServerException {
		final PersonType xmlPerson = new PersonType();
		setValue(xmlPerson::setAcademicDegree, person::getAcademicDegree);
		setValue(xmlPerson::setCollege, person::getCollege);
		setValue(xmlPerson::setPersonId, person::getPersonId);
		setValue(xmlPerson::setHomepage, person::getHomepage, URL::toString);
		setValue(xmlPerson::setEmail, person::getEmail);
		setValue(xmlPerson::setOrcid, person::getOrcid);
		setValue(xmlPerson::setGender, person::getGender, p -> GenderType.valueOf(p.name().toUpperCase()));
		setValue(xmlPerson::setMainName, person::getMainName, this::createXmlPersonName);

		/*
		 * here we have to remove the main name from the names because it was already set as the mainName attribute of the type
		 */
		final List<PersonName> otherNames = person.getNames().stream().filter(personName -> !personName.isMain()).collect(Collectors.toList());
		setCollectionValue(xmlPerson.getNames(), otherNames, this::createXmlPersonName);
		setValue(xmlPerson::setUser, person::getUser, this::createXmlUser);
		return xmlPerson;
	}

	private PersonNameType createXmlPersonName(PersonName personName) {
		final PersonNameType xmlName = new PersonNameType();
		xmlName.setFirstName(personName.getFirstName());
		xmlName.setLastName(personName.getLastName());
		return xmlName;
	}

	@Override
	public void serializeResourcePersonRelation(final Writer writer, ResourcePersonRelation resourcePersonRelation, ViewModel viewModel) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setResourcePersonRelation(createXmlResourcePersonRelation(resourcePersonRelation));
		serialize(writer, xmlDoc);
	}

	private ResourcePersonRelationType createXmlResourcePersonRelation(ResourcePersonRelation resourcePersonRelation) {
		final ResourcePersonRelationType xmlResourcePersonRelation = new ResourcePersonRelationType();
		setValue(xmlResourcePersonRelation::setPerson, resourcePersonRelation::getPerson, this::createXmlPerson);
		setValue(xmlResourcePersonRelation::setResource, resourcePersonRelation::getPost, this::createXmlResourceLink);
		setValue(xmlResourcePersonRelation::setRelationType, resourcePersonRelation::getRelationType,
						this::createXmlRelationType);
		setValue(xmlResourcePersonRelation::setPersonIndex, resourcePersonRelation::getPersonIndex,
						i -> BigInteger.valueOf(i.longValue()));
		return xmlResourcePersonRelation;
	}

	@Override
	public void serializeResourcePersonRelations(Writer writer, List<ResourcePersonRelation> relations) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();

		final ResourcePersonRelationsType listWrapper = new ResourcePersonRelationsType();
		setCollectionValue(listWrapper.getResourcePersonRelation(), relations, this::createXmlResourcePersonRelation);

		xmlDoc.setResourcePersonRelations(listWrapper);
		serialize(writer, xmlDoc);
	}

	private RelationType createXmlRelationType(PersonResourceRelationType personResourceRelationType) {
		return RelationType.valueOf(personResourceRelationType.name());
	}

	private ResourceLinkType createXmlResourceLink(Post<? extends BibTex> post) {
		final ResourceLinkType xmlLinkType = new ResourceLinkType();
		setValue(xmlLinkType::setInterHash, post::getResource, Resource::getInterHash);
		setValue(xmlLinkType::setIntraHash, post::getResource, Resource::getIntraHash);
		return xmlLinkType;
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
		} else if (tags != null) {
			xmlTags.setStart(BigInteger.ZERO);
			xmlTags.setEnd(BigInteger.valueOf(tags.size()));
		} else {
			xmlTags.setStart(BigInteger.ZERO);
			xmlTags.setEnd(BigInteger.ZERO);
		}

		if (present(tags)) {
			for (final Tag tag : tags) {
				xmlTags.getTag().add(this.createXmlTag(tag));
			}
		}

		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setTags(xmlTags);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeTag(final Writer writer, final Tag tag, final ViewModel model) throws InternServerException {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setTag(this.createXmlTag(tag));
		this.serialize(writer, xmlDoc);
	}

	private TagType createXmlTag(final Tag tag) throws InternServerException {
		final TagType xmlTag = new TagType();
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
		xmlTags.setStart(BigInteger.ZERO);
		xmlTags.setEnd(BigInteger.valueOf(tags.size()));
		return xmlTags;
	}

	@Override
	public void serializeGroupMemberships(Writer writer, Collection<GroupMembership> groupMemberships, ViewModel viewModel) {
		final GroupMembershipsType groupMembershipsType = new GroupMembershipsType();
		groupMembershipsType.getGroupMembership().addAll(
						groupMemberships.stream().map(this::createXmlGroupMembership).collect(Collectors.toList()));
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setGroupMemberships(groupMembershipsType);
		this.serialize(writer, xmlDoc);
	}

	private GroupMembershipType createXmlGroupMembership (GroupMembership groupMembership) {
		final GroupMembershipType xmlGroupMembership = new GroupMembershipType();
		setValue(xmlGroupMembership::setJoinDate, groupMembership::getJoinDate, this::createXmlCalendar);
		setValue(xmlGroupMembership::setUser, groupMembership::getUser, this::createXmlUser);
		setValue(xmlGroupMembership::setUserSharedDocuments, groupMembership::isUserSharedDocuments);
		setValue(xmlGroupMembership::setGroupRole, groupMembership::getGroupRole,
						r -> GroupRoleType.valueOf(r.name().toUpperCase()));
		return xmlGroupMembership;
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
		} else if (groups != null) {
			xmlGroups.setStart(BigInteger.ZERO);
			xmlGroups.setEnd(BigInteger.valueOf(groups.size()));
		} else {
			xmlGroups.setStart(BigInteger.ZERO);
			xmlGroups.setEnd(BigInteger.ZERO);
		}

		if (present(groups)) {
			for (final Group group : groups) {
				xmlGroups.getGroup().add(this.createXmlGroup(group));
			}
		}

		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setGroups(xmlGroups);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeGroup(final Writer writer, final Group group, final ViewModel model) throws InternServerException {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setGroup(this.createXmlGroup(group));
		this.serialize(writer, xmlDoc);
	}

	private GroupType createXmlGroup(final Group group) {
		final GroupType xmlGroup = new GroupType();
		setValue(xmlGroup::setName, group::getName);
		setValue(xmlGroup::setDescription, group::getDescription);
		setValue(xmlGroup::setRealname, group::getRealname);
		setValue(xmlGroup::setHomepage, group::getHomepage, URL::toString);
		setValue(xmlGroup::setInternalId, group::getInternalId);
		setValue(xmlGroup::setParent, group::getParent, this::createXmlGroup);
		setValue(xmlGroup::setHref, group::getName, urlRenderer::createHrefForGroup);
		setValue(xmlGroup::setGroupRequest, group::getGroupRequest, this::createXmlGroupRequest);
		if (group.isAllowJoin()) {
			setValue(xmlGroup::setAllowJoin, group::isAllowJoin);
		}
		setCollectionValue(xmlGroup.getUser(), group.getMemberships(), g -> createXmlUser(g.getUser()));
		if (group.isOrganization()) {
			xmlGroup.setOrganization(Boolean.TRUE.toString());
		}
		return xmlGroup;
	}

	private GroupRequestType createXmlGroupRequest(final GroupRequest groupRequest) {
		final GroupRequestType groupRequestType = new GroupRequestType();
		setValue(groupRequestType::setReason, groupRequest::getReason);
		setValue(groupRequestType::setSubmissionDate, groupRequest::getSubmissionDate, this::createXmlCalendar);
		setValue(groupRequestType::setRequestedUser, groupRequest::getUserName, this::createXmlUser);
		return groupRequestType;
	}

	@Override
	public void serializeOK(final Writer writer) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeFail(final Writer writer) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithFAIL();
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeError(final Writer writer, final String errorMessage) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithFAIL();
		xmlDoc.setError(errorMessage);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeGroupId(final Writer writer, final String groupId) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setGroupid(groupId);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeResourceHash(final Writer writer, final String hash) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setResourcehash(hash);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeUserId(final Writer writer, final String userId) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setUserid(userId);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializePersonId(Writer writer, String personId) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setPersonid(personId);
		serialize(writer, xmlDoc);
	}

	@Override
	public void serializeProjectId(Writer writer, String projectId) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setProjectid(projectId);
		serialize(writer, xmlDoc);
	}

	@Override
	public void serializeCRISLinkId(Writer writer, String linkId) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setCrislinkid(linkId);
		serialize(writer, xmlDoc);
	}

	@Override
	public void serializeURI(final Writer writer, final String uri) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		xmlDoc.setUri(uri);
		this.serialize(writer, xmlDoc);
	}

	@Override
	public void serializeSynchronizationPosts(final Writer writer, final List<? extends SynchronizationPost> posts) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
		final SyncPostsType xmlSyncPosts = new SyncPostsType();
		setCollectionValue(xmlSyncPosts.getSyncPost(), posts, this::createXmlSyncPost);
		xmlDoc.setSyncPosts(xmlSyncPosts);
		this.serialize(writer, xmlDoc);
	}

	/**
	 * @param post the post to convert
	 * @return SyncPostType representation of given post
	 */
	private SyncPostType createXmlSyncPost(final SynchronizationPost post) {
		final SyncPostType xmlSyncpost = new SyncPostType();
		setValue(xmlSyncpost::setAction, post::getAction, SynchronizationAction::toString);
		setValue(xmlSyncpost::setChangeDate, post::getChangeDate, this::createXmlCalendar);
		setValue(xmlSyncpost::setCreateDate, post::getCreateDate, this::createXmlCalendar);
		setValue(xmlSyncpost::setHash, post::getIntraHash);
		setValue(xmlSyncpost::setPost, post::getPost, this::createXmlPost);
		return xmlSyncpost;
	}

	@Override
	public void serializeSynchronizationData(final Writer writer, final SynchronizationData syncData) {
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();

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
			final List<SynchronizationPost> syncPosts = new LinkedList<>();
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
		final BibsonomyXML xmlDoc = buildEmptyBibsonomyXMLWithOK();
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
	public CRISLink parseCRISLink(Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getCrisLink() != null) {
			return createCRISLink(xmlDoc.getCrisLink());
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no project defined.");
	}

	private CRISLink createCRISLink(CRISLinkTypeType crisLinkType) {
		final CRISLink crisLink = new CRISLink();
		setValue(crisLink::setEndDate, crisLinkType::getEndDate, AbstractRenderer::createDate);
		setValue(crisLink::setStartDate, crisLinkType::getStartDate, AbstractRenderer::createDate);
		setValue(crisLink::setDataSource, crisLinkType::getDataSource, this::createCRISLinkDataSource);
		setValue(crisLink::setLinkType, crisLinkType::getLinkType, this::createCRISLinkType);
		setValue(crisLink::setSource, crisLinkType::getSource, this::createCRISLinkable);
		setValue(crisLink::setTarget, crisLinkType::getTarget, this::createCRISLinkable);
		return crisLink;
	}

	private CRISLinkType createCRISLinkType(ProjectPersonLinkTypeType projectPersonLinkTypeType) {
		return ProjectPersonLinkType.valueOf(projectPersonLinkTypeType.name());
	}

	private Linkable createCRISLinkable(LinkableType linkableType) {
		return TO_LINKABLE_MAPPERS.getOrDefault(linkableType.getClass().getName(), l -> null).apply(linkableType);
	}

	private CRISLinkDataSource createCRISLinkDataSource(CRISLinkDataSourceType crisLinkTypeDataSource) {
		return CRISLinkDataSource.valueOf(crisLinkTypeDataSource.name());
	}

	@Override
	public List<Project> parseProjects(Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getProjects() != null) {
			return xmlDoc.getProjects().getProject().stream().parallel().map(this::createProject).collect(Collectors.toList());
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no projects defined.");
	}

	@Override
	public Project parseProject(Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getProject() != null) {
			return createProject(xmlDoc.getProject());
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no project defined.");
	}

	private Project createProject(ProjectType projectType) {
		final Project project = new Project();
		setValue(project::setParentProject, projectType::getParentProject, this::createProject);
		setCollectionValue(project.getSubProjects(), projectType.getSubProjects(), this::createProject);
		setCollectionValue(project.getCrisLinks(), projectType.getCrisLinks(), this::createCRISLink);
		setValue(project::setBudget, projectType::getBudget);
		setValue(project::setTitle, projectType::getTitle);
		setValue(project::setSubTitle, projectType::getSubTitle);
		setValue(project::setDescription, projectType::getDescription);
		setValue(project::setExternalId, projectType::getExternalId);
		setValue(project::setInternalId, projectType::getInternalId);
		setValue(project::setStartDate, projectType::getStartDate, AbstractRenderer::createDate);
		setValue(project::setEndDate, projectType::getEndDate, AbstractRenderer::createDate);
		setValue(project::setSponsor, projectType::getSponsor);
		setValue(project::setType, projectType::getType);
		return project;
	}

	@Override
	public Person parsePerson(Reader reader) {
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getPerson() != null) {
			return createPerson(xmlDoc.getPerson());
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no person defined.");
	}

	private Person createPerson(PersonType personType) {
		final Person person = new Person();
		setValue(person::setMainName, personType::getMainName, this::createPersonMainName);
		setValue(person::setAcademicDegree, personType::getAcademicDegree);
		setValue(person::setCollege, personType::getCollege);
		setValue(person::setEmail, personType::getEmail);
		setValue(person::setHomepage, personType::getHomepage, AbstractRenderer::createURL);
		setValue(person::setGender, personType::getGender, g -> Gender.valueOf(g.name()));
		setValue(person::setOrcid, personType::getOrcid);
		setCollectionValue(person.getNames(), personType.getNames(), this::createPersonName);
		setValue(person::setPersonId, personType::getPersonId);
		setValue(person::setUser, personType::getUser, UserType::getName);
		return person;
	}

	private PersonName createPersonMainName(PersonNameType personNameType) {
		final PersonName personName = createPersonName(personNameType);
		personName.setMain(true);
		return personName;
	}

	private PersonName createPersonName(PersonNameType personNameType) {
		final PersonName name = new PersonName();
		setValue(name::setFirstName, personNameType::getFirstName);
		setValue(name::setLastName, personNameType::getLastName);
		return name;
	}

	@Override
	public ResourcePersonRelation parseResourcePersonRelation(Reader reader) {
		final BibsonomyXML xmlDoc = parse(reader);
		if (xmlDoc.getResourcePersonRelation() != null) {
			return createResourcePersonRelation(xmlDoc.getResourcePersonRelation());
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no resource-person-relation defined.");
	}

	private ResourcePersonRelation createResourcePersonRelation(ResourcePersonRelationType resourcePersonRelationType) {
		final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
		resourcePersonRelation.setPost(createDummyResource(resourcePersonRelationType.getResource()));
		resourcePersonRelation.setPersonIndex(resourcePersonRelationType.getPersonIndex().intValue());
		resourcePersonRelation.setRelationType(PersonResourceRelationType.valueOf(
						resourcePersonRelationType.getRelationType().name()));
		return resourcePersonRelation;
	}

	private Post<BibTex> createDummyResource(ResourceLinkType resourceLinkType) {
		final BibTex bibTex = new ResourceFactory().createPublication();
		bibTex.setInterHash(resourceLinkType.getInterHash());
		bibTex.setIntraHash(resourceLinkType.getIntraHash());
		final Post<BibTex> post = new Post<>();
		post.setResource(bibTex);
		return post;
	}

	@Override
	public List<ResourcePersonRelation> parseResourcePersonRelations(Reader reader) {
		final BibsonomyXML xmlDoc = this.parse(reader);
		final ResourcePersonRelationsType xmlRelations = xmlDoc.getResourcePersonRelations();
		if (xmlRelations != null) {

			return xmlRelations.getResourcePersonRelation().stream().map(this::createResourcePersonRelation).collect(Collectors.toList());
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no resource-person-relation defined.");
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
		if (docType != null) {

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
	public Collection<GroupMembership> parseGroupMemberships(Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);

		if (xmlDoc.getGroupMemberships() != null) {
			return xmlDoc.getGroupMemberships().getGroupMembership().stream().
							map(this::createGroupMembership).collect(Collectors.toList());
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no group memberships defined.");
	}

	private GroupMembership createGroupMembership(GroupMembershipType groupMembershipType) {
		final GroupMembership groupMembership = new GroupMembership();
		setValue(groupMembership::setUser, groupMembershipType::getUser, this::createUser);
		setValue(groupMembership::setGroupRole, groupMembershipType::getGroupRole,
						r -> GroupRole.valueOf(r.name().toUpperCase()));
		setValue(groupMembership::setUserSharedDocuments, groupMembershipType::isUserSharedDocuments);
		setValue(groupMembership::setJoinDate, groupMembershipType::getJoinDate, AbstractRenderer::createDate);
		return groupMembership;
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
			return xmlDoc.getGroups().getGroup().stream().map(this::createGroup).collect(Collectors.toList());
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
			final List<Post<? extends Resource>> posts = new LinkedList<>();
			for (final PostType post : xmlDoc.getPosts().getPost()) {
				try {
					final Post<? extends Resource> p = this.createPost(post, uploadedFileAcessor);
					posts.add(p);
				} catch (final PersonListParserException ex) {
					throw new BadRequestOrResponseException("Error parsing the person names for entry with BibTeX key '" +
									post.getBibtex().getBibtexKey() + "': " + ex.getMessage());
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
			return xmlDoc.getTags().getTag().stream().map(this::createTag).collect(Collectors.toList());
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
			final List<User> users = new LinkedList<>();
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
			final Set<String> references = new HashSet<>();
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

	@Override
	public String parseProjectId(Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (present(xmlDoc.getProjectid())) {
			return xmlDoc.getProjectid();
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no project id defined.");
	}

	@Override
	public String parsePersonId(Reader reader) throws BadRequestOrResponseException {
		final BibsonomyXML xmlDoc = this.parse(reader);
		if (present(xmlDoc.getPersonid())) {
			return xmlDoc.getPersonid();
		}
		if (xmlDoc.getError() != null) {
			throw new BadRequestOrResponseException(xmlDoc.getError());
		}
		throw new BadRequestOrResponseException("The body part of the received document is erroneous - no person id defined.");
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
		setValue(user::setEmail, xmlUser::getEmail);
		setValue(user::setHomepage, xmlUser::getHomepage, AbstractRenderer::createURL);
		setValue(user::setName, xmlUser::getName);
		setValue(user::setRealname, xmlUser::getRealname);
		setValue(user::setPassword, xmlUser::getPassword);
		setValue(user::setSpammer, xmlUser::isSpammer);
		setValue(user::setPrediction, xmlUser::getPrediction, BigInteger::intValue);
		setValue(user::setConfidence, xmlUser::getConfidence);
		setValue(user::setAlgorithm, xmlUser::getAlgorithm);
		setValue(user::setMode, xmlUser::getClassifierMode);
		setValue(user::setToClassify, xmlUser::getToClassify, BigInteger::intValue);
		if (present(xmlUser.getRemoteUserId())) {
			for (RemoteUserIdType remoteUserIdType : xmlUser.getRemoteUserId()) {
				user.setRemoteUserId(createRemoteUserId(remoteUserIdType));
			}
		}
		if (present(xmlUser.getGroups())) {
			setCollectionValue(user.getGroups(), xmlUser.getGroups().getGroup(), this::createGroup);
		}
		return user;
	}

	private RemoteUserId createRemoteUserId(RemoteUserIdType remoteUserIdType) {
		if (present(remoteUserIdType.getIdentityProvider())) {
			return new SamlRemoteUserId(remoteUserIdType.getIdentityProvider(), remoteUserIdType.getUserId());
		}
		return new SimpleRemoteUserId(remoteUserIdType.getUserId());
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
		setValue(group::setName, xmlGroup::getName);
		setValue(group::setDescription, xmlGroup::getDescription);
		setValue(group::setRealname, xmlGroup::getRealname);
		setValue(group::setHomepage, xmlGroup::getHomepage, AbstractRenderer::createURL);
		setCollectionValue(group.getMemberships(), xmlGroup.getUser(), u -> createGroupMembership(createUser(u)));
		setValue(group::setOrganization, xmlGroup::getOrganization, Boolean::parseBoolean);
		setValue(group::setInternalId, xmlGroup::getInternalId);
		setValue(group::setParent, xmlGroup::getParent, this::createGroup);
		setValue(group::setGroupRequest, xmlGroup::getGroupRequest, this::createGroupRequest);
		setValue(group::setAllowJoin, xmlGroup::isAllowJoin);
		return group;
	}

	private GroupMembership createGroupMembership(User user) {
		GroupMembership groupMembership = new GroupMembership();
		groupMembership.setUser(user);
		return groupMembership;
	}

	private GroupRequest createGroupRequest(GroupRequestType xmlGroupRequest) {
		GroupRequest groupRequest = new GroupRequest();
		setValue(groupRequest::setReason, xmlGroupRequest::getReason);
		setValue(groupRequest::setSubmissionDate, xmlGroupRequest::getSubmissionDate, AbstractRenderer::createDate);
		setValue(groupRequest::setUserName, xmlGroupRequest::getRequestedUser, UserType::getName);
		return groupRequest;
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
		final List<Tag> rVal = new ArrayList<>();
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
				final List<Document> documents = new LinkedList<>();
				for (final DocumentType xmlDocument : xmlDocuments.getDocument()) {
					final Document document = new Document();
					document.setFileName(xmlDocument.getFilename());
					document.setMd5hash(xmlDocument.getMd5Hash());
					documents.add(document);
				}
				publication.setDocuments(documents);
			}

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
				final Data data = uploadedFileAccessor.getData(name);
				if (data == null) {
					log.warn("missing data in API");
				} else {
					final BibTex alreadyParsedBibtex;
					if (post.getResource() instanceof BibTex) {
						alreadyParsedBibtex = (BibTex) post.getResource();
					} else {
						alreadyParsedBibtex = null;
					}
					post.setResource(new ImportResource(alreadyParsedBibtex, data));
				}
			} else {
				log.warn("missing multipartname in API");
			}
		}

		final List<GroupType> xmlGroups = xmlPost.getGroup();
		if (xmlGroups != null) {
			post.setGroups(new HashSet<>());
			for (final GroupType xmlGroup : xmlGroups) {
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
		final Post<Resource> post = new Post<>();
		post.setDescription(xmlPost.getDescription());

		// user
		final User user = this.createUser(xmlPost);
		post.setUser(user);
		post.setDate(createDate(xmlPost.getPostingdate()));
		post.setChangeDate(createDate(xmlPost.getChangedate()));
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
	 *
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
			post.setChangeDate(createDate(xmlSyncPost.getChangeDate()));
		}
		if (present(xmlSyncPost.getPost())) {
			try {
				post.setPost(this.createPost(xmlSyncPost.getPost(), NoDataAccessor.getInstance()));
			} catch (final PersonListParserException ex) {
				throw new BadRequestOrResponseException("Error parsing the person names for entry with BibTeX key '" + xmlSyncPost.getPost().getBibtex().getBibtexKey() + "': " + ex.getMessage());
			}
		}
		if (present(xmlSyncPost.getCreateDate())) {
			post.setCreateDate(createDate(xmlSyncPost.getCreateDate()));
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
	 *
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
			syncData.setLastSyncDate(createDate(lastSyncDate));
		} else {
			errors.append("last sync date is not present\n");
		}

		final String resourceType = xmlSyncData.getResourceType();
		if (present(resourceType)) {
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
		if (present(extraurls)) {
			final List<ExtraUrlType> urls = extraurls.getUrl();
			final List<BibTexExtra> eurls = new ArrayList<>(urls.size());

			for (final ExtraUrlType extraUrl : urls) {
				eurls.add(new BibTexExtra(createURL(extraUrl.getHref()), extraUrl.getTitle(), createDate(extraUrl.getDate())));
			}
			publication.setExtraUrls(eurls);
		}

	}

	/**
	 * @param xmlModelValidator the xmlModelValidator to set
	 */
	public void setXmlModelValidator(XMLModelValidator xmlModelValidator) {
		this.xmlModelValidator = xmlModelValidator;
	}
}