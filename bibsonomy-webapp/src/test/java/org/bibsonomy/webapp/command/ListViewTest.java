/*
 * Created on 01.11.2007
 */
package org.bibsonomy.webapp.command;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;


/**
 * Tests some pageNumber and pageContext calculations
 * 
 * @author Jens Illig
 */
public class ListViewTest {
	/**
	 * tests the correctness of some (curPage.start,entriesPerpage)->curPage.number values
	 */
	@Test
	public void testCurPageNumber() {
		ListCommand<Integer> lv = new ListCommand<Integer>();
		lv.setEntriesPerPage(15);
		lv.setStart(0);
		Assert.assertEquals(new Integer(1), lv.getCurPage().getNumber());
		lv.setStart(15);
		Assert.assertEquals(new Integer(2), lv.getCurPage().getNumber());
		lv.setStart(16);
		Assert.assertEquals(new Integer(3), lv.getCurPage().getNumber());
		lv.setStart(29);
		Assert.assertEquals(new Integer(3), lv.getCurPage().getNumber());
	}
	
	/**
	 * tests the correctness of the previous page context
	 */
	@Test
	public void testPreviousPages() {
		ListCommand<Integer> lv = new ListCommand<Integer>();
		lv.setEntriesPerPage(15);
		lv.setStart(60);
		lv.setNumPreviousPages(2);
		List<PageCommand> prevPages = lv.getPreviousPages();
		Assert.assertEquals(2, prevPages.size());
		
		Assert.assertEquals(30, prevPages.get(0).getStart());
		Assert.assertEquals(new Integer(3), prevPages.get(0).getNumber());
		
		Assert.assertEquals(45, prevPages.get(1).getStart());
		Assert.assertEquals(new Integer(4), prevPages.get(1).getNumber());

		lv.setStart(15);
		prevPages = lv.getPreviousPages();		
		Assert.assertEquals(1, prevPages.size());
		
		Assert.assertEquals(0, prevPages.get(0).getStart());
		Assert.assertEquals(new Integer(1), prevPages.get(0).getNumber());
	}
	
	/**
	 * tests the correctness of the next page context
	 */
	@Test
	public void testNextPages() {
		ListCommand<Integer> lv = new ListCommand<Integer>();
		lv.setEntriesPerPage(15);
		lv.setTotalCount(91);
		lv.setStart(60);
		lv.setNumNextPages(2);
		List<PageCommand> nextPages = lv.getNextPages();
		Assert.assertEquals(2, nextPages.size());
		
		Assert.assertEquals(75, nextPages.get(0).getStart());
		Assert.assertEquals(new Integer(6), nextPages.get(0).getNumber());
		
		Assert.assertEquals(90, nextPages.get(1).getStart());
		Assert.assertEquals(new Integer(7), nextPages.get(1).getNumber());

		lv.setTotalCount(90);
		nextPages = lv.getNextPages();		
		Assert.assertEquals(1, nextPages.size());
		
		Assert.assertEquals(75, nextPages.get(0).getStart());
		Assert.assertEquals(new Integer(6), nextPages.get(0).getNumber());
	}
}
