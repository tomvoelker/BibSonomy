package org.bibsonomy.events.model;

import java.util.LinkedList;
import java.util.List;

public class Event {

	private String name;
	private String description;
	private String year;
	private String location;
	private List<String> subEvents = new LinkedList<String>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public List<String> getSubEvents() {
		return subEvents;
	}
	public void setSubEvents(List<String> subEvents) {
		this.subEvents = subEvents;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	@Override
	public String toString() {
		return name + ": " + description + " (" + location + ", " + year + ")";
	}
	
}
