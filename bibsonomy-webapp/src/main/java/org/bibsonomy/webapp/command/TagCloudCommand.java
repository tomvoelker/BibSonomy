/*
 * Created on 14.10.2007
 */
package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.TagCloudSort;
import org.bibsonomy.common.enums.TagCloudStyle;
import org.bibsonomy.model.Tag;

/**
 * bean for displaying a tag cloud
 * 
 * @author Dominik Benz
 */
public class TagCloudCommand extends BaseCommand {
	private List<Tag> tags = new ArrayList<Tag>();
	private int minFreq = 0; // threshold which tags to display
	private int maxFreq = 100; // maximum occurrence frequency of all tags
	private TagCloudStyle style = TagCloudStyle.CLOUD;
	private TagCloudSort sort = TagCloudSort.ALPHA;
	private int maxTagCount;
	
	public int getMaxUserTagCount() {
		return this.maxUserTagCount;
	}


	private int maxUserTagCount;
	
	
	/**
	 * default bean constructor
	 */
	public TagCloudCommand() {
	}
	
	/**
	 * @param tags a list of tags
	 */
	public TagCloudCommand(List<Tag> tags) {
		this.tags = tags;
		
		calculateMaxTagCount();
	}	


	/**
	 * find the max Tag Count
	 */
	private void calculateMaxTagCount() {
		maxTagCount = Integer.MIN_VALUE;
		maxUserTagCount = Integer.MIN_VALUE;
		for (Tag tag : tags) {
			if (tag.getGlobalcount() > maxTagCount) {
				maxTagCount = tag.getGlobalcount();
			}
			if (tag.getUsercount() > maxUserTagCount) {
				maxUserTagCount = tag.getUsercount();
			}
		}
	}

	public int getMaxTagCount() {
		return this.maxTagCount;
	}

	/**
	 * @return the list of contained tags
	 */
	public List<Tag> getTags() {
		return this.tags;
	}


	/**
	 * @param tags a list of tags
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
		calculateMaxTagCount();
	}


	/**
	 * @return minimum occurrence frequency
	 */
	public int getMinFreq() {
		return this.minFreq;
	}


	/**
	 * @param minFreq minimum occurrence frequency
	 */
	public void setMinFreq(int minFreq) {
		this.minFreq = minFreq;
	}


	/**
	 * @return maximum occurrence frequency
	 */
	public int getMaxFreq() {
		return this.maxFreq;
	}


	/**
	 * @param maxFreq the maximum occurrence frequency
	 */
	public void setMaxFreq(int maxFreq) {
		this.maxFreq = maxFreq;
	}


	/**
	 * @return the display mode
	 */
	public TagCloudStyle getStyle() {
		return this.style;
	}


	/**
	 * @param mode the display mode
	 */
	public void setStyle(TagCloudStyle mode) {
		this.style = mode;
	}


	/**
	 * @return the sorting mode
	 */
	public TagCloudSort getSort() {
		return this.sort;
	}


	/**
	 * @param sort the sorting mode
	 */
	public void setSort(TagCloudSort sort) {
		this.sort = sort;
	}
		
}
