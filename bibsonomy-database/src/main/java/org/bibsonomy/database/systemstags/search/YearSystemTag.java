/**
 * BibSonomy-Database - Database for BibSonomy.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.systemstags.search;

import java.util.regex.Pattern;

import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Resource;

/**
 * @author dzo
 */
public class YearSystemTag extends AbstractSearchSystemTagImpl {

	/**
	 * the name of the year system tag
	 */
	public static final String NAME = "year";
	
	/** year explicitly given (eg. 2006) */
	private static final Pattern SINGLE_YEAR = Pattern.compile("[12]{1}[0-9]{3}");
	/** range (e.g. 2001-2006) */
	private static final Pattern START_END_YEAR = Pattern.compile("[12]{1}[0-9]{3}-[12]{1}[0-9]{3}");
	/** upper bound (e.g -2005) means all years before 2005  */
	private static final Pattern END_YEAR = Pattern.compile("-[12]{1}[0-9]{3}");
	/** lower bound (e.g 1998-) means all years since 1998  */
	private static final Pattern START_YEAR = Pattern.compile("[12]{1}[0-9]{3}-");

	/**
	 * It is necessary to distinguish between the 4 types of legal arguments for the YearSystemTag.
	 * Some examples illustrate those types
	 * 1. 2007 => all publications of 2007
	 * 2. 2005-2007 => all publications of 2005, 2006, and 2007
	 * 3. -2007 => all publications before and in 2007
	 * 4. 2004- => all publication in and after 2004
	 */
	private String year; 		// for cases 1, 3, 4
	private String firstYear;	// for case 2
	private String lastYear;	// for case 2

	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * @return the firstYear
	 */
	public String getFirstYear() {
		return this.firstYear;
	}

	/**
	 * @param firstYear the firstYear to set
	 */
	public void setFirstYear(final String firstYear) {
		this.firstYear = firstYear;
	}

	/**
	 * @return the lastYear
	 */
	public String getLastYear() {
		return this.lastYear;
	}

	/**
	 * @param lastYear the lastYear to set
	 */
	public void setLastYear(final String lastYear) {
		this.lastYear = lastYear;
	}

	/**
	 * @return the year
	 */
	public String getYear() {
		return this.year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(final String year) {
		this.year = year;
	}

	@Override
	public YearSystemTag newInstance() {
		return new YearSystemTag();
	}

	@Override
	public void handleParam(final GenericParam param) {
		if (param instanceof BibTexParam ) {
			param.addToSystemTags(this);

			/*
			 * extract first-, last- and year from the argument
			 */
			if (SINGLE_YEAR.matcher(this.getArgument()).matches()) {
				this.year = this.getArgument();
				log.debug("Set year to " + this.getArgument() + " after matching year system tag");
			} else if (START_END_YEAR.matcher(this.getArgument()).matches()) {
				final String[] years = this.getArgument().split("-");
				this.firstYear = years[0];
				this.lastYear = years[1];
				log.debug("Set firstyear/lastyear to " + this.firstYear + "/" + this.lastYear + "after matching year system tag");
			} else if (END_YEAR.matcher(this.getArgument()).matches()) {
				// cut off the "-" at the beginning
				this.lastYear = this.getArgument().substring(1);
				log.debug("Set lastyear to " + this.lastYear + "after matching year system tag");
			} else if (START_YEAR.matcher(this.getArgument()).matches()) {
				// cut off the "-" at the end
				this.firstYear = this.getArgument().substring(0, this.getArgument().length() - 1);
				log.debug("Set firstyear to " + this.firstYear + "after matching year system tag");
			}
		}
		/*
		 * for Bookmarks do nothing
		 */
	}

	@Override
	public boolean allowsResource(final Class<? extends Resource> resourceType) {
		return isPublicationClass(resourceType);
	}
}
