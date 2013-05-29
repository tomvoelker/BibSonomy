package org.bibsonomy.recommender.connector.database.params;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.PersonNameParser;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;

/**
 * @author Lukas
 * @version $Id$
 */
public class BibTexResultParam {

	private String contentId;
	private String intrahash;
	private String interhash;
	private String title;
	private String bookTitle;
	private String bibtexAbstract;
	private List<Tag> tags;
	private String journal;
	private String bibtexKey;
	private String authors;
	private String date;
	private int group;
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the bookTitle
	 */
	public String getBookTitle() {
		return this.bookTitle;
	}
	/**
	 * @param bookTitle the bookTitle to set
	 */
	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}
	/**
	 * @return the bibtexAbstract
	 */
	public String getBibtexAbstract() {
		return this.bibtexAbstract;
	}
	/**
	 * @param bibtexAbstract the bibtexAbstract to set
	 */
	public void setBibtexAbstract(String bibtexAbstract) {
		this.bibtexAbstract = bibtexAbstract;
	}
	/**
	 * @return the journal
	 */
	public String getJournal() {
		return this.journal;
	}
	/**
	 * @param journal the journal to set
	 */
	public void setJournal(String journal) {
		this.journal = journal;
	}
	/**
	 * @return the bibtexKey
	 */
	public String getBibtexKey() {
		return this.bibtexKey;
	}
	/**
	 * @param bibtexKey the bibtexKey to set
	 */
	public void setBibtexKey(String bibtexKey) {
		this.bibtexKey = bibtexKey;
	}
	/**
	 * @return the group
	 */
	public int getGroup() {
		return this.group;
	}
	/**
	 * @param group the group to set
	 */
	public void setGroup(int group) {
		this.group = group;
	}
	
	/**
	 * @return the contentId
	 */
	public String getContentId() {
		return this.contentId;
	}
	/**
	 * @param contentId the contentId to set
	 */
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	/**
	 * @return the intrahash
	 */
	public String getIntrahash() {
		return this.intrahash;
	}
	/**
	 * @param intrahash the intrahash to set
	 */
	public void setIntrahash(String intrahash) {
		this.intrahash = intrahash;
	}
	/**
	 * @return the interhash
	 */
	public String getInterhash() {
		return this.interhash;
	}
	/**
	 * @param interhash the interhash to set
	 */
	public void setInterhash(String interhash) {
		this.interhash = interhash;
	}
	/**
	 * @return the authors
	 */
	public String getAuthors() {
		return this.authors;
	}
	/**
	 * @param authors the authors to set
	 */
	public void setAuthors(String authors) {
		this.authors = authors;
	}
	/**
	 * @return a post created with the database information
	 */
	public Post<BibTex> getCorrespondingPost() {
		Post<BibTex> post = new Post<BibTex>();
		List<Post<? extends Resource>> correspondingPosts = new ArrayList<Post<?>>();
		BibTex bib = new BibTex();
		Set<Group> groups = new HashSet<Group>();
		
		correspondingPosts.add(post);
		groups.add(new Group(this.group));
		
		bib.setAbstract(this.bibtexAbstract);
		bib.setBooktitle(this.bookTitle);
		bib.setBibtexKey(this.bibtexKey);
		bib.setIntraHash(this.intrahash);
		bib.setInterHash(this.interhash);
		bib.setJournal(this.journal);
		bib.setTitle(this.title);
		bib.setPosts(correspondingPosts);
		bib.setYear(date.split("-")[0]);
		bib.setMonth(date.split("-")[1]);
		bib.setDay(date.split("-")[2]);
		try {
			bib.setAuthor(PersonNameParser.parse(authors));
		} catch (PersonListParserException ex) {
			//do nothing, no author available
		}

		post.setGroups(groups);
		post.setContentId(Integer.parseInt(contentId));
		post.setTags(new HashSet<Tag>(tags));
		
		post.setResource(bib);
		return post;
	}
	/**
	 * @return the tags
	 */
	public List<Tag> getTags() {
		return this.tags;
	}
	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
	
}
