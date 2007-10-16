/*
 * Created on 14.10.2007
 */
package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListView<T> {
	private int numPreviousPages = 2;
	private int numNextPages = 2;
	private int entriesPerPage = 20;
	private final Page curPage = new Page();
	private List<Page> previousPages;
	private List<Page> nextPages;
	private int totalCount = 100; // TODO: 0 nehmen?
	private List<T> list;
	
	public List<T> getList() {
		return this.list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	public int getStart() {
		return this.curPage.getStart();
	}
	public void setStart(int start) {
		this.curPage.setStart(start);
		this.previousPages = null;
		this.nextPages = null;
	}
	
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		this.previousPages = null;
		this.nextPages = null;
	}
	
	public List<Page> getPreviousPages() {
		if (this.previousPages == null) {
			this.previousPages = new ArrayList<Page>();
			for (int i = this.curPage.getNumber() - this.numPreviousPages; i > 0; --i) {
				final int start = this.curPage.getStart() - i * this.entriesPerPage;
				if (start >= 0) {
					this.previousPages.add(new Page(this.curPage.getNumber() - i, start));
				}
			}
		}
		return this.previousPages;
	}
	public List<Page> getNextPages() {
		if (this.nextPages == null) {
			this.nextPages = new ArrayList<Page>();
			for (int i = 1; i <= this.numNextPages; ++i) {
				final int start = this.curPage.getStart() + i * this.entriesPerPage;
				if (start < this.totalCount) {
					this.nextPages.add(new Page(this.curPage.getNumber() + i, start));
				}
			}
		}
		return this.nextPages;
	}
	
	public Page getPreviousPage() {
		final List<Page> prev = this.getPreviousPages();
		if (prev.size() > 0) {
			return prev.get(prev.size() - 1);
		}
		return null;
	}
	
	public Page getNextPage() {
		final List<Page> next = this.getNextPages();
		if (next.size() > 0) {
			return next.get(0);
		}
		return null;
	}
	
	public void setNumNextPages(int numNextPages) {
		this.numNextPages = numNextPages;
	}
	public void setNumPreviousPages(int numPreviousPages) {
		this.numPreviousPages = numPreviousPages;
	}
	public void setEntriesPerPage(int entriesPerPage) {
		this.entriesPerPage = entriesPerPage;
	}
	public int getTotalCount() {
		return this.totalCount;
	}
	public Page getCurPage() {
		return this.curPage;
	}
}
