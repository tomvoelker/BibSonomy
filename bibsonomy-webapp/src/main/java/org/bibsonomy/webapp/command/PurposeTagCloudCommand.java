/*
 * Created on 14.10.2007
 */
package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.TagCloudStyle;
import org.bibsonomy.common.enums.TagCloudSort;
import org.bibsonomy.model.Tag;

/**
 * bean for displaying a purpose tag cloud
 * 
 * @author Christian Körner
 */
public class PurposeTagCloudCommand extends BaseCommand {
	private List<Tag> tags = new ArrayList<Tag>();
	private List<String> relations = new ArrayList<String>();
	private int minFreq = 0; // threshold which tags to display
	private int maxFreq = 100; // maximum occurrence frequency of all tags
	private TagCloudStyle style = TagCloudStyle.CLOUD;
	private TagCloudSort sort = TagCloudSort.ALPHA;
	private int maxTagCount;
	
	
	/**
	 * default bean constructor
	 */
	public PurposeTagCloudCommand() {
		System.out.println("XXXXXXXXXXXXX purpose tag cloud command created");
	}
	
	/**
	 * @param tags a list of tags
	 */
	public PurposeTagCloudCommand(List<Tag> tags) {
		System.out.println("XXXXXXXXXXXXXXX purpose tag cloud command created with tags");
		this.tags = tags;
		
		calculateMaxTagCount();
	}	


	/**
	 * find the max Tag Count
	 */
	private void calculateMaxTagCount() {
		maxTagCount = Integer.MIN_VALUE;
		for (Tag tag : tags) {
			if (tag.getGlobalcount() > maxTagCount) {
				maxTagCount = tag.getGlobalcount();
			}
		}
	}

	/**
	 * @return the maximum tag count
	 */
	public int getMaxTagCount() {
		return this.maxTagCount;
	}

	/**
	 * @return the list of contained tags
	 */
	public List<Tag> getTags() {
		for (Tag tag: tags) {
			System.out.println("purposetagcloud command: "+tag.getName());
		}
		return this.tags;
	}


	/**
	 * @param tags a list of tags
	 */
	public void setTags(List<Tag> tags) {
		System.out.println("Purpose tag command add tags: "+tags.size());
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
	
	/**
	 * @return the relations for the purpose tags
	 */
	public List<String> getRelations() {
		return this.relations;
	}
	
	/**
	 * @param relations the relations for the purpose tags
	 */
	public void setRelations(List<String> relations) {
		this.relations = relations;
	}
		
}
