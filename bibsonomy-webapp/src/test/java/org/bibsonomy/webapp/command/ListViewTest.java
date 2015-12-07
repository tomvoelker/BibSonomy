/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

/**
 * Tests some pageNumber and pageContext calculations
 * 
 * @author Jens Illig
 */
public class ListViewTest {

	/**
	 * tests the correctness of some
	 * (curPage.start,entriesPerpage)->curPage.number values
	 */
	@Test
	public void testCurPageNumber() {
		final ListCommand<Integer> lv = new ListCommand<Integer>(new BaseCommand());
		lv.setEntriesPerPage(15);
		lv.setStart(0);
		assertEquals(Integer.valueOf(1), lv.getCurPage().getNumber());
		lv.setStart(15);
		assertEquals(Integer.valueOf(2), lv.getCurPage().getNumber());
		lv.setStart(16);
		assertEquals(Integer.valueOf(3), lv.getCurPage().getNumber());
		lv.setStart(29);
		assertEquals(Integer.valueOf(3), lv.getCurPage().getNumber());
	}

	/**
	 * tests the correctness of the previous page context
	 */
	@Test
	public void testPreviousPages() {
		final ListCommand<Integer> lv = new ListCommand<Integer>(new BaseCommand());
		lv.setEntriesPerPage(15);
		lv.setStart(60);
		lv.setNumPreviousPages(2);
		List<PageCommand> prevPages = lv.getPreviousPages();
		assertEquals(2, prevPages.size());

		assertEquals(30, prevPages.get(0).getStart());
		assertEquals(Integer.valueOf(3), prevPages.get(0).getNumber());

		assertEquals(45, prevPages.get(1).getStart());
		assertEquals(Integer.valueOf(4), prevPages.get(1).getNumber());

		lv.setStart(15);
		prevPages = lv.getPreviousPages();
		assertEquals(1, prevPages.size());

		assertEquals(0, prevPages.get(0).getStart());
		assertEquals(Integer.valueOf(1), prevPages.get(0).getNumber());
	}

	/**
	 * tests the correctness of the next page context
	 */
	@Test
	public void testNextPages() {
		final ListCommand<Integer> lv = new ListCommand<Integer>(new BaseCommand());
		lv.setEntriesPerPage(15);
		lv.setTotalCount(91);
		lv.setStart(60);
		lv.setNumNextPages(2);
		List<PageCommand> nextPages = lv.getNextPages();
		assertEquals(2, nextPages.size());

		assertEquals(75, nextPages.get(0).getStart());
		assertEquals(Integer.valueOf(6), nextPages.get(0).getNumber());

		assertEquals(90, nextPages.get(1).getStart());
		assertEquals(Integer.valueOf(7), nextPages.get(1).getNumber());

		lv.setTotalCount(90);
		nextPages = lv.getNextPages();
		assertEquals(1, nextPages.size());

		assertEquals(75, nextPages.get(0).getStart());
		assertEquals(Integer.valueOf(6), nextPages.get(0).getNumber());
	}
}