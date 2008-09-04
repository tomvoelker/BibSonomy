package org.bibsonomy.webapp.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.util.RequestWrapperContext;

/**
 * @author mwa
 * @version $Id$
 */
public class PopularResourceViewCommand extends ResourceViewCommand{

	//list of lists of popular bibtex posts for the last XX days
	private List<ListCommand<Post<BibTex>>> popularListsBibTex = new ArrayList<ListCommand<Post<BibTex>>>();
	//list of lists of popular bookmark posts for the last XX days
	private List<ListCommand<Post<Bookmark>>> popularListsBookmark = new ArrayList<ListCommand<Post<Bookmark>>>();
	//the days for bibtex
	private List<Integer> popularBibtexDays = new ArrayList<Integer>();
	//the days for bookmark
	private List<Integer> popularBookmarkDays = new ArrayList<Integer>();
	
	//Getters and Setters
	public List<ListCommand<Post<BibTex>>> getPopularListsBibTex() {
		return this.popularListsBibTex;
	}

	public void setPopularListsBibTex(List<ListCommand<Post<BibTex>>> popularListsBibTex) {
		this.popularListsBibTex = popularListsBibTex;
	}

	public List<ListCommand<Post<Bookmark>>> getPopularListsBookmark() {
		return this.popularListsBookmark;
	}

	public void setPopularListsBookmark(List<ListCommand<Post<Bookmark>>> popularListsBookmark) {
		this.popularListsBookmark = popularListsBookmark;
	}

	public List<Integer> getPopularBibtexDays() {
		return this.popularBibtexDays;
	}

	public void setPopularBibtexDays(List<Integer> popularBibtexDays) {
		this.popularBibtexDays = popularBibtexDays;
	}

	public List<Integer> getPopularBookmarkDays() {
		return this.popularBookmarkDays;
	}

	public void setPopularBookmarkDays(List<Integer> popularBookmarkDays) {
		this.popularBookmarkDays = popularBookmarkDays;
	}
	
}
