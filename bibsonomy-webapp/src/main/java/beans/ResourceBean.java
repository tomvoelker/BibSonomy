package beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

import resources.Bibtex;
import resources.BibtexYearComparator;
import resources.Bookmark;


public class ResourceBean implements Serializable {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257854931056922929L;
	
	private static final int MAX_ELEMENTS_TO_SORT = 500;
	
	private LinkedList <Bookmark>bookmarks;
	private LinkedList <Bibtex>bibtex;
	private int bookmarkTotalCount = 0;
	private int bibtexTotalCount = 0;
	private String title = "";
	
	public ResourceBean() {
		bookmarks = new LinkedList<Bookmark>();
		bibtex = new LinkedList<Bibtex>();
		/*
		 * TODO: here maybe some initialization, e.g. StartBook, EndBook, NoOfBookRows
		 */
	}
	public void addBookmark (Bookmark b) {
		bookmarks.add(b);
	}
	public void setBookmarks (LinkedList<Bookmark> b) {
		bookmarks = b;
	}
	public int getBookmarkCount () {
		return bookmarks.size();
	}
	public LinkedList getBookmarks () {
		return bookmarks;
	}
	public String toString() {
		if (bookmarks != null && bookmarks.size() > 0) {
			return bookmarks.getFirst() + " " + bookmarks.getLast();
		} else {
			return "(empty)";
		}
	}
	//bibtex part
	public void addBibtex (Bibtex bib) {
		bibtex.add(bib);
	}
	public void setBibtex (LinkedList<Bibtex> bib) {
		bibtex = bib;
	}
	public int getBibtexCount () {
		return bibtex.size();
	}
	public LinkedList<Bibtex> getBibtex () {
		return bibtex;
	}
	public Collection<Bibtex> getBibtexSortedByYear () {
		if (bibtex.size() < MAX_ELEMENTS_TO_SORT) {
			TreeSet<Bibtex> temp = new TreeSet<Bibtex>(new BibtexYearComparator());
			temp.addAll(bibtex);
			return temp;
		}
		return bibtex;
	}
	
	public String toStringBib() {
		if (bibtex != null && bibtex.size() > 0) {
			return bibtex.getFirst() + " " + bibtex.getLast();
		} else {
			return "(empty)";
		}
	}
	public int getBibtexTotalCount() {
		return bibtexTotalCount;
	}
	public void setBibtexTotalCount(int bibtexTotalCount) {
		this.bibtexTotalCount = bibtexTotalCount;
	}
	public int getBookmarkTotalCount() {
		return bookmarkTotalCount;
	}
	public void setBookmarkTotalCount(int bookmarkTotalCount) {
		this.bookmarkTotalCount = bookmarkTotalCount;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getbookmarkHash() {
		
		if (bookmarks == null || bookmarks.size() == 0) {		
			return null;
		} else {
			return bookmarks.getFirst().getHash();
		}
	}

	public String getbibHash() {
		if (bibtex == null || bibtex.size() == 0) {		
			return null;
		} else {
			return bibtex.getFirst().getSimHash(Bibtex.INTER_HASH);
		}
	}
	
	
}
