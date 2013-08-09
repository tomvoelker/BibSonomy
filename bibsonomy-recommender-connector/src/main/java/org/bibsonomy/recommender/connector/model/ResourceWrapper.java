package org.bibsonomy.recommender.connector.model;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.util.DateParser;
import org.apache.commons.httpclient.util.DateUtil;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.PersonNameParser;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.util.DateUtils;

import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendationTag;

public class ResourceWrapper implements RecommendationItem {
	
	private Resource resource;
	private String id;
	private List<RecommendationTag> tags;
	
	public ResourceWrapper(Resource resource) {
		this.resource = resource;
	}
	
	public Resource getResource() {
		return resource;
	}
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public List<RecommendationTag> getTags() {
		return tags;
	}

	@Override
	public void setTags(List<RecommendationTag> tags) {
		this.tags = tags;
	}
	
	@Override
	public String getTitle() {
		if(this.resource != null) {
			return this.resource.getTitle();
		}
		return "";
	}

	@Override
	public void setTitle(String title) {
		if(this.resource != null) {
			this.resource.setTitle(title);
		}
	}
}
