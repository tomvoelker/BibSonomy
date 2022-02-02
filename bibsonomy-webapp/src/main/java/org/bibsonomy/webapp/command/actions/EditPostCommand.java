/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
package org.bibsonomy.webapp.command.actions;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.JobResult;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.tag.model.RecommendedTag;
import org.bibsonomy.webapp.command.GroupingCommand;
import org.bibsonomy.webapp.command.PostCommand;

/**
 * @author fba
 * @param <RESOURCE>
 *            The type of resource this command handles.
 */
@Getter
@Setter
public class EditPostCommand<RESOURCE extends Resource> extends PostCommand implements GroupingCommand, CaptchaCommand {
	/**
	 * The tags of the copied post.
	 */
	private List<Tag> copytags;

	private Post<RESOURCE> post;

	private String tags;

	/** TODO: add documentation */
	private Post<RESOURCE> diffPost;

	/**
	 * If the user edits his own post, this field is used to identify the post.
	 */
	private String intraHashToUpdate;

	/**
	 * If the user wants to copy a post from another user, this field is used.
	 * NOTE: the name must be "hash" since this was the case in the old system.
	 * There might exist web pages which use parameter name!
	 *
	 * The intra hash of the post which should be copied. Must be used
	 *  together with the name of the user.
	 *
	 */
	private String hash;

	/**
	 * This is the user who owns the post which should be copied.
	 */
	private String user;

	/**
	 * When the tag field contains commas, it is only accepted, if this boolean
	 * is set to <code>true</code>
	 */
	private boolean acceptComma = false;

	private boolean containsComma = false;

	/**
	 * The abstract (or general) group of the post: public, private, or other
	 */
	private String abstractGrouping;
	
	private List<String> groups;

	private List<String> relevantGroups;

	private SortedSet<RecommendedTag> recommendedTags;

	private Map<String, Map<String, List<String>>> relevantTagSets;

	/**
	 * field for the friends or groups dropdowns
	 */
	private String friendsOrGroups;

	/**
	 * stores an id, e.g. for mapping recommendations to posts
	 * the post id is used to uniquely identify a post until it is stored in the
	 * database. The recommender service needs this to assign recommenders to
	 * posting processes.
	 */
	private int postID;

	/**
	 * captcha fields for spammers
	 */
	private String recaptcha_challenge_field;

	private String recaptcha_response_field;

	private String captchaHTML;

	/** should the edit view be shown before the post is stored into the db? */
	private boolean editBeforeSaving;

	/**
	 * The file names of the documents uploaded during editing a post.
	 */
	private List<String> fileName;

	/**
	 * whether to redirect to the rating page after editing a post
	 */
	private String saveAndRate;
	private boolean approved;

	private int compareVersion;
	private List<String> differentEntryKeys;

	private User groupUser;

	private List<JobResult> jobResults;
	private String redirectUrl;


	/**
	 * @return the groups
	 */
	@Override
	public List<String> getGroups() {
		return this.groups;
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	@Override
	public void setGroups(final List<String> groups) {
		this.groups = groups;
	}

	/**
	 * Sets the tags from the copied post. Needed for the (old) "copy" links.
	 *
	 * @param tags
	 */
	public void setCopytag(final String tags) {
		for (final String tagname : tags.split("\\s")) {
			this.copytags.add(new Tag(tagname));
		}
	}

	/**
	 * @return the abstractGrouping
	 */
	@Override
	public String getAbstractGrouping() {
		return this.abstractGrouping;
	}

	/**
	 * @param abstractGrouping
	 *            the abstractGrouping to set
	 */
	@Override
	public void setAbstractGrouping(final String abstractGrouping) {
		this.abstractGrouping = abstractGrouping;
	}

	/**
	 * @return the recaptcha_challenge_field
	 */
	@Override
	public String getRecaptcha_challenge_field() {
		return this.recaptcha_challenge_field;
	}

	/**
	 * @param recaptchaChallengeField
	 *            the recaptcha_challenge_field to set
	 */
	@Override
	public void setRecaptcha_challenge_field(final String recaptchaChallengeField) {
		this.recaptcha_challenge_field = recaptchaChallengeField;
	}

	/**
	 * @return the recaptcha_response_field
	 */
	@Override
	public String getRecaptcha_response_field() {
		return this.recaptcha_response_field;
	}

	/**
	 * @param recaptchaResponseField
	 *            the recaptcha_response_field to set
	 */
	@Override
	public void setRecaptcha_response_field(final String recaptchaResponseField) {
		this.recaptcha_response_field = recaptchaResponseField;
	}

	/**
	 * @return the captchaHTML
	 */
	@Override
	public String getCaptchaHTML() {
		return this.captchaHTML;
	}

	/**
	 * @param captchaHTML
	 *            the captchaHTML to set
	 */
	@Override
	public void setCaptchaHTML(final String captchaHTML) {
		this.captchaHTML = captchaHTML;
	}

}