package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.TagCloudSort;
import org.bibsonomy.common.enums.TagCloudStyle;
import org.bibsonomy.model.Author;

import com.sun.media.jai.opimage.MaxCRIF;

/**
 * @author Christian Claus
 * @version $Id$
 */
public class AuthorsCommand extends BaseCommand {
	List<Author> authorList = new ArrayList<Author>();
	
	private TagCloudStyle style = TagCloudStyle.CLOUD;
	private TagCloudSort sort = TagCloudSort.ALPHA;
	private int minFreq	 = 0;
	private int maxFreq	 = 100;
	private int maxCount = 0;
	
	public AuthorsCommand() {
		
	}
	
	public AuthorsCommand(List<Author> authorList) {
		this.authorList = authorList;
		calculateMaxAuthorCount();
	}

	public List<Author> getAuthorList() {
		return this.authorList;
	}
	
	private void calculateMaxAuthorCount() {
		for(Author a : authorList) {
			if(a.getCtr() > maxCount) {
				maxCount = a.getCtr();
			}
		}
	}

	public void setAuthorList(List<Author> authorList) {
		this.authorList = authorList;
		calculateMaxAuthorCount();
	}

	public TagCloudStyle getStyle() {
		return this.style;
	}

	public void setStyle(TagCloudStyle style) {
		this.style = style;
	}

	public TagCloudSort getSort() {
		return this.sort;
	}

	public void setSort(TagCloudSort sort) {
		this.sort = sort;
	}

	public int getMinFreq() {
		return this.minFreq;
	}

	public void setMinFreq(int minFreq) {
		this.minFreq = minFreq;
	}

	public int getMaxFreq() {
		return this.maxFreq;
	}

	public void setMaxFreq(int maxFreq) {
		this.maxFreq = maxFreq;
	}

	public int getMaxCount() {
		return this.maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

}
