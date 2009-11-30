/**
 *  
 *  BibSonomy-Rest-Client - The REST-client.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.rest.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.rest.client.queries.get.GetPostsQuery;
import org.bibsonomy.rest.client.queries.get.GetUserDetailsQuery;
import org.junit.Test;

/*
 * FIXME: please don't use "BibSonomy" as name for classes, variables, methods, etc.
 */

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class BibsonomyTest {

	@Test
	public void testInstantiation() {
		try {
			new Bibsonomy("", "test");
			fail("exception should have been thrown");
		} catch (final IllegalArgumentException e) {
			if (!"The given username is not valid.".equals(e.getMessage())) {
				fail("wrong exception was thrown");
			}
		}
		try {
			new Bibsonomy("test", "");
			fail("exception should have been thrown");
		} catch (final IllegalArgumentException e) {
			if (!"The given apiKey is not valid.".equals(e.getMessage())) {
				fail("wrong exception was thrown");
			}
		}
		assertNotNull("instantiation failed", new Bibsonomy("user", "pw"));
	}

	@Test
	public void testSetUsername() {
		final Bibsonomy bib = new Bibsonomy();
		try {
			bib.setUsername("");
			fail("exception should have been thrown");
		} catch (final IllegalArgumentException e) {
			if (!"The given username is not valid.".equals(e.getMessage())) {
				fail("wrong exception was thrown");
			}
		}
		bib.setUsername("foo");
	}

	@Test
	public void testSetPassword() {
		final Bibsonomy bib = new Bibsonomy();
		try {
			bib.setApiKey("");
			fail("exception should have been thrown");
		} catch (final IllegalArgumentException e) {
			if (!"The given apiKey is not valid.".equals(e.getMessage())) {
				fail("wrong exception was thrown");
			}
		}
		bib.setApiKey("foo");
	}

	@Test
	public void testSetApiURL() {
		final Bibsonomy bib = new Bibsonomy();
		try {
			bib.setApiURL("");
			fail("exception should have been thrown");
		} catch (final IllegalArgumentException e) {
			if (!"The given apiURL is not valid.".equals(e.getMessage())) {
				fail("wrong exception was thrown");
			}
		}
		try {
			bib.setApiURL("/");
			fail("exception should have been thrown");
		} catch (final IllegalArgumentException e) {
			if (!"The given apiURL is not valid.".equals(e.getMessage())) {
				fail("wrong exception was thrown");
			}
		}
		bib.setApiURL("foo");
	}

	@Test
	public void testExecuteQuery() throws Exception {
		final Bibsonomy bib = new Bibsonomy();
		try {
			bib.executeQuery(new GetUserDetailsQuery("foo"));
			fail("exception should have been thrown");
		} catch (final IllegalStateException e) {
		}
		bib.setUsername("foo");
		try {
			bib.executeQuery(new GetUserDetailsQuery("foo"));
			fail("exception should have been thrown");
		} catch (final IllegalStateException e) {
		}
	}
	
	@Test
	public void testGetPostsQuery() throws Exception {
		final Bibsonomy bib = new Bibsonomy();
		GetPostsQuery query = new GetPostsQuery();
		query.setOrder(Order.FOLKRANK);
		query.setSearch("java xml");
		query.setGrouping(GroupingEntity.ALL,"");
		try{
			bib.executeQuery(query);
			System.out.println(query.getHttpStatusCode());
		}catch (final Exception e){
				System.out.println("exception should have been thrown");	
		}
	}
}