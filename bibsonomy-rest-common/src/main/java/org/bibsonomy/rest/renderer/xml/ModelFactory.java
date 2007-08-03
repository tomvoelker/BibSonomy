package org.bibsonomy.rest.renderer.xml;

import static org.bibsonomy.model.util.ModelValidationUtils.checkBibTex;
import static org.bibsonomy.model.util.ModelValidationUtils.checkBookmark;
import static org.bibsonomy.model.util.ModelValidationUtils.checkGroup;
import static org.bibsonomy.model.util.ModelValidationUtils.checkPost;
import static org.bibsonomy.model.util.ModelValidationUtils.checkTag;
import static org.bibsonomy.model.util.ModelValidationUtils.checkUser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;

/**
 * Produces objects from the model based on objects from the XML model generated
 * with JAXB.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ModelFactory {

	private static ModelFactory modelFactory;

	private ModelFactory() {
	}

	public static ModelFactory getInstance() {
		if (ModelFactory.modelFactory == null) {
			ModelFactory.modelFactory = new ModelFactory();
		}
		return ModelFactory.modelFactory;
	}

	public User createUser(final UserType xmlUser) {
		checkUser(xmlUser);

		final User user = new User();
		user.setEmail(xmlUser.getEmail());
		try {
			// FIXME move into Factory
			user.setHomepage(new URL(xmlUser.getHomepage()));
		} catch (final MalformedURLException e) {
		}
		user.setName(xmlUser.getName());
		user.setRealname(xmlUser.getRealname());
		user.setPassword(xmlUser.getPassword());

		return user;
	}

	public Group createGroup(final GroupType xmlGroup) {
		checkGroup(xmlGroup);

		final Group group = new Group();
		group.setName(xmlGroup.getName());
		group.setDescription(xmlGroup.getDescription());
		if (xmlGroup.getUser().size() > 0) {
			group.setUsers(new ArrayList<User>());
			for (final UserType xmlUser : xmlGroup.getUser()) {
				group.getUsers().add(createUser(xmlUser));
			}
		}

		return group;
	}

	public Tag createTag(final TagType xmlTag) {
		return createTag(xmlTag, 1);
	}
	
	public Tag createTag(final TagType xmlTag, final int depth) {
		checkTag(xmlTag);

		final Tag tag = new Tag();
		tag.setName(xmlTag.getName());
		// TODO tag count  häh?
		if (xmlTag.getGlobalcount() != null) tag.setGlobalcount(xmlTag.getGlobalcount().intValue());
		// TODO tag count  häh?
		if (xmlTag.getUsercount() != null) tag.setUsercount(xmlTag.getUsercount().intValue());
		
		if (depth > 0) {
			if (xmlTag.getSubTags() != null) {
				tag.setSubTags(createTags(xmlTag.getSubTags(), depth - 1));
			}
			if (xmlTag.getSuperTags() != null) {
				tag.setSuperTags(createTags(xmlTag.getSuperTags(), depth - 1));
			}
		}
		return tag;
	}

	private List<Tag> createTags(final List<TagsType> xmlTags, final int depth) {
		final List<Tag> rVal = new ArrayList<Tag>();
		for (final TagsType xmlSubTags : xmlTags) {
			//tags.add(xmlSubTag);
			for (final TagType xmlSubTag : xmlSubTags.getTag()) {
				rVal.add(createTag(xmlSubTag, depth));
			}
		}
		return rVal;
	}

	public Post<Resource> createPost(final PostType xmlPost) {
		checkPost(xmlPost);

		// post itself
		final Post<Resource> post = new Post<Resource>();
		post.setDescription(xmlPost.getDescription());

		// user
		final User user = new User();
		final UserType xmlUser = xmlPost.getUser();
		checkUser(xmlUser);
		user.setName(xmlUser.getName());
		post.setUser(user);
		
		post.setDate(createDate(xmlPost.getPostingdate()));

		// tags
		for (final TagType xmlTag : xmlPost.getTag()) {
			checkTag(xmlTag);

			final Tag tag = new Tag();
			tag.setName(xmlTag.getName());
			post.getTags().add(tag);
		}

		// resource
		if (xmlPost.getBibtex() != null) {
			final BibtexType xmlBibtex = xmlPost.getBibtex();
			checkBibTex(xmlBibtex);

			final BibTex bibtex = new BibTex();

			bibtex.setAddress(xmlBibtex.getAddress());
			bibtex.setAnnote(xmlBibtex.getAnnote());
			bibtex.setAuthor(xmlBibtex.getAuthor());
			bibtex.setBibtexAbstract(xmlBibtex.getBibtexAbstract());
			bibtex.setBibtexKey(xmlBibtex.getBibtexKey());
			bibtex.setBKey(xmlBibtex.getBKey());
			bibtex.setBooktitle(xmlBibtex.getBooktitle());
			bibtex.setChapter(xmlBibtex.getChapter());
			bibtex.setCrossref(xmlBibtex.getCrossref());
			bibtex.setDay(xmlBibtex.getDay());
			bibtex.setEdition(xmlBibtex.getEdition());
			bibtex.setEditor(xmlBibtex.getEditor());
			bibtex.setEntrytype(xmlBibtex.getEntrytype());
			bibtex.setHowpublished(xmlBibtex.getHowpublished());
			bibtex.setInstitution(xmlBibtex.getInstitution());
			bibtex.setInterHash(xmlBibtex.getInterhash());
			bibtex.setIntraHash(xmlBibtex.getIntrahash());
			bibtex.setJournal(xmlBibtex.getJournal());
			bibtex.setMisc(xmlBibtex.getMisc());
			bibtex.setMonth(xmlBibtex.getMonth());
			bibtex.setNote(xmlBibtex.getNote());
			bibtex.setNumber(xmlBibtex.getNumber());
			bibtex.setOrganization(xmlBibtex.getOrganization());
			bibtex.setPages(xmlBibtex.getPages());
			bibtex.setPublisher(xmlBibtex.getPublisher());
			bibtex.setSchool(xmlBibtex.getSchool());
			// if (xmlBibtex.getScraperId() != null) bibtex.setScraperId(xmlBibtex.getScraperId().intValue());
			bibtex.setSeries(xmlBibtex.getSeries());
			bibtex.setTitle(xmlBibtex.getTitle());
			bibtex.setType(xmlBibtex.getType());
			bibtex.setUrl(xmlBibtex.getUrl());
			bibtex.setVolume(xmlBibtex.getVolume());
			bibtex.setYear(xmlBibtex.getYear());

			post.setResource(bibtex);
		}
		if (xmlPost.getBookmark() != null) {
			final BookmarkType xmlBookmark = xmlPost.getBookmark();
			checkBookmark(xmlBookmark);

			final Bookmark bookmark = new Bookmark();
			bookmark.setIntraHash(xmlBookmark.getIntrahash());
			bookmark.setTitle(xmlBookmark.getTitle());
			bookmark.setUrl(xmlBookmark.getUrl());

			post.setResource(bookmark);
		}
		if (xmlPost.getGroup() != null) {
			post.setGroups(new ArrayList<Group>());
			for (final GroupType xmlGroup : xmlPost.getGroup()) {
				checkGroup(xmlGroup);
				final Group group = new Group();
				group.setDescription(xmlGroup.getDescription());
				group.setName(xmlGroup.getName());
				post.getGroups().add(group);
			}
		}

		return post;
	}

	private Date createDate(XMLGregorianCalendar date) {
		
		return new Date(System.currentTimeMillis());
		
//		final Calendar cal = new GregorianCalendar(date.getYear(), date.getMonth() - 1, date.getDay(), date.getHour(), date.getMinute(), date.getSecond());
//		cal.set(Calendar.MILLISECOND, date.getMillisecond());
//		return cal.getTime();
	}
}