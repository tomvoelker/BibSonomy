package org.bibsonomy.rest.renderer.xml;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.exceptions.InvalidXMLException;

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
		validateUser(xmlUser);

		final User user = new User();
		user.setEmail(xmlUser.getEmail());
		try {
			user.setHomepage(new URL(xmlUser.getHomepage())); // FIXME move
			// into Factory
		} catch (final MalformedURLException e) {
		}
		user.setName(xmlUser.getName());
		user.setRealname(xmlUser.getRealname());

		return user;
	}

	public Group createGroup(final GroupType xmlGroup) {
		validateGroup(xmlGroup);

		final Group group = new Group();
		group.setName(xmlGroup.getName());
		group.setDescription(xmlGroup.getDescription());

		return group;
	}

	public Tag createTag(final TagType xmlTag) {
		validateTag(xmlTag);

		final Tag tag = new Tag();
		tag.setName(xmlTag.getName());
		// TODO tag count
		if (xmlTag.getGlobalcount() != null) tag.setGlobalcount(xmlTag.getGlobalcount().intValue());
		// TODO tag count
		if (xmlTag.getUsercount() != null) tag.setUsercount(xmlTag.getUsercount().intValue());

		return tag;
	}

	public Post<Resource> createPost(final PostType xmlPost) {
		validatePost(xmlPost);

		// post itself
		final Post<Resource> post = new Post<Resource>();
		post.setDescription(xmlPost.getDescription());

		// user
		final User user = new User();
		final UserType xmlUser = xmlPost.getUser();
		validateUser(xmlUser);
		user.setName(xmlUser.getName());
		post.setUser(user);

		// tags
		for (final TagType xmlTag : xmlPost.getTag()) {
			validateTag(xmlTag);

			final Tag tag = new Tag();
			tag.setName(xmlTag.getName());
			post.getTags().add(tag);
		}

		// resource
		if (xmlPost.getBibtex() != null) {
			final BibtexType xmlBibtex = xmlPost.getBibtex();
			validateBibTex(xmlBibtex);

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
			bibtex.setSchool(xmlBibtex.getPublisher());
			if (xmlBibtex.getScraperId() != null) bibtex.setScraperId(xmlBibtex.getScraperId().intValue());
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
			validateBookmark(xmlBookmark);

			final Bookmark bookmark = new Bookmark();
			bookmark.setIntraHash(xmlBookmark.getIntrahash());
			bookmark.setUrl(xmlBookmark.getUrl());

			post.setResource(bookmark);
		}

		return post;
	}

	private void validateUser(final UserType xmlUser) {
		if (xmlUser.getName() == null || xmlUser.getName().length() == 0) throw new InvalidXMLException("username is missing");
	}

	private void validateGroup(final GroupType xmlGroup) {
		if (xmlGroup.getName() == null || xmlGroup.getName().length() == 0) throw new InvalidXMLException("groupname is missing");
	}

	private void validateTag(final TagType xmlTag) {
		if (xmlTag.getName() == null || xmlTag.getName().length() == 0) throw new InvalidXMLException("tag name is missing");
	}

	private void validatePost(final PostType xmlPost) {
		if (xmlPost.getTag() == null) throw new InvalidXMLException("list of tags is missing");
		if (xmlPost.getTag().size() == 0) throw new InvalidXMLException("no tags specified");
		if (xmlPost.getUser() == null) throw new InvalidXMLException("user is missing");

		final BibtexType xmlBibtex = xmlPost.getBibtex();
		final BookmarkType xmlBookmark = xmlPost.getBookmark();
		if (xmlBibtex == null && xmlBookmark == null) {
			throw new InvalidXMLException("resource is missing");
		} else if (xmlBibtex != null && xmlBookmark != null) {
			throw new InvalidXMLException("only one resource is allowed");
		} else {
			// just fine (bibtex xor bookmark):
			// ( xmlBibtex == null && xmlBookmark != null ) || ( xmlBibtex != null || xmlBookmark == null )
		}
	}

	private void validateBookmark(final BookmarkType xmlBookmark) {
		if (xmlBookmark.getUrl() == null) throw new InvalidXMLException("url is missing");
		// do not test hash value - it depends on the request if its available,
		// so we check it later
		// if( xmlBookmark.getIntrahash() == null ) throw new InvalidXMLException( "hash is missing" );
	}

	private void validateBibTex(BibtexType xmlBibtex) {
		if (xmlBibtex.getTitle() == null) throw new InvalidXMLException("title is missing");
		// do not test hash value - it depends on the request if its available,
		// so we check it later
		// if( xmlBibtex.getIntrahash() == null ) throw new InvalidXMLException( "hash is missing" );
	}
}