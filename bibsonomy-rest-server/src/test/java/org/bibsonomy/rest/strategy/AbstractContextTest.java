/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.database.TestDBLogic;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.bibsonomy.util.StringUtils;
import org.junit.Before;

/**
 * @author Christian Schenk
 */
public abstract class AbstractContextTest {

	protected LogicInterface db;
	protected UrlRenderer urlRenderer;
	protected Reader is;

	/**
	 * sets up the logic
	 * @throws UnsupportedEncodingException 
	 */
	@Before
	public final void setUp() throws UnsupportedEncodingException {
		this.db = TestDBLogic.factory.getLogicAccess(this.getClass().getSimpleName(), "apiKey");
		this.urlRenderer = new UrlRenderer("http://www.bibsonomy.org/api/");
		this.is = new InputStreamReader(new ByteArrayInputStream("".getBytes()), StringUtils.CHARSET_UTF_8);
	}
}