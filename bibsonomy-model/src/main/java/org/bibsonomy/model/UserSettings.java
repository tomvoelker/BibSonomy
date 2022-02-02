/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.TagCloudSort;
import org.bibsonomy.common.enums.TagCloudStyle;
import org.bibsonomy.model.enums.PersonPostsStyle;
import org.bibsonomy.model.user.settings.FavouriteLayout;
import org.bibsonomy.model.user.settings.LayoutSettings;

/**
 * Holds settings for a user.
 * 
 */
@Getter
@Setter
public class UserSettings implements Serializable {
	private static final long serialVersionUID = 501200873739971813L;

	/**
	 * the profile privacy level
	 */
	private ProfilePrivlevel profilePrivlevel = ProfilePrivlevel.PRIVATE;
	
	/**
	 * TODO: use {@link TagCloudStyle} as type
	 * tagbox style; 0 = cloud, 1 = list
	 */
	private int tagboxStyle = 0;

	/**
	 * TODO: use {@link TagCloudSort} as type
	 * sorting of tag box; 0 = alph, 1 = freq
	 */
	private int tagboxSort = 0;

	/**
	 * minimum frequency for tags to be displayed in tag box
	 */
	private int tagboxMinfreq = 0;

	/**
	 * top x posts shown in the tag box
	 */
	private int tagboxMaxCount = 50;
	
	/**
	 * TODO: change to boolean
	 * Show the tooltips for tags in the tag cloud? 0 = don't show, 1 = show 
	 */
	private int tagboxTooltip = 0;

	/**
	 * number of list items per page; how many posts to show in post lists
	 */
	private int listItemcount = 20;
	
	/**
	 * the layouts to be shown on publications and citations
	 * Reihenfolge ist egal. Wird vor jedem speichern in der Datenbank sortiert.
	 */
	private List<FavouriteLayout> favouriteLayouts = new LinkedList<FavouriteLayout>(Arrays.asList(new FavouriteLayout("SIMPLE", "BIBTEX"), new FavouriteLayout("SIMPLE", "ENDNOTE"), new FavouriteLayout("JABREF", "APA_HTML"),new FavouriteLayout("JABREF", "CHICAGO"),new FavouriteLayout("JABREF", "DIN1505"),new FavouriteLayout("JABREF", "HARVARDHTML"), new FavouriteLayout("JABREF", "MSOFFICEXML")));
		
	private boolean showBookmark = true;
	
	// TODO: rename to showPublication
	private boolean showBibtex = true;
	
	private LayoutSettings layoutSettings = new LayoutSettings();
	
	/**
	 * the default language for i18n
	 */
	private String defaultLanguage;

	/**
	 * style for person's page posts
	 */
	private PersonPostsStyle personPostsStyle = PersonPostsStyle.GOLDSTANDARD;

	/**
	 * layout for person's page posts
	 */
	private String personPostsLayout = "";

	/**
	 * The timeZone the user lives in. Used for rendering posts in the HTML 
	 * output. 
	 * FIXME: let user choose on the /settings page. FIXME: then we must store
	 * UTC times in the database!
	 * 
	 * FIXME: what to do with non-logged in users? They must have a valid
	 * time zone, too! Otherwise, we will get NPEs
	 */
	private final TimeZone timeZone = TimeZone.getDefault();
	
	/**
	 * TODO: change type to boolean
	 * How much data about the user behavior (clicking, etc.) is logged.
	 * 
	 * 0 = yes (log clicks to external pages)
	 * 1 = no  (don't log clicks to external pages)
	 */
	private int logLevel;

	/**
	 * Shall the web interface ask the user before it really deletes something?
	 */
	private boolean confirmDelete = true;
	
	/**
	 * User wants maxCount (true) or maxFreq (false)
	 */
	private boolean isMaxCount = true;

	/**
	 * @return isMaxCount
	 */
	public boolean getIsMaxCount() {
		return isMaxCount;
	}

	/**
	 * @return isMaxCount
	 */
	public boolean isMaxCount() {
		return isMaxCount;
	}

	/**
	 * @param isMaxCount
	 */
	public void setIsMaxCount(final boolean isMaxCount) {
		this.isMaxCount = isMaxCount;
	}

}