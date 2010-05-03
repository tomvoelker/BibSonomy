package org.bibsonomy.email;

import java.util.List;
import java.util.Set;

import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.TagUtils;

/**
 * The parsed email.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class Email {

	private String from;
	private ToField to;
	private Set<Tag> tags;
	private List<String> urls;

	@Override
	public String toString() {
		return 
		"From: " + from + "\n" +
		"To: " + to + "\n" +
		"Subject: " + TagUtils.toTagString(tags, " ") + "\n" +
		"\n" +
		urls + 
		"\n";
	}

	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	
	public ToField getTo() {
		return to;
	}
	public void setTo(ToField to) {
		this.to = to;
	}
	
	public List<String> getUrls() {
		return urls;
	}
	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public Set<Tag> getTags() {
		return tags;
	}
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
}
