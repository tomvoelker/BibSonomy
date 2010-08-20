package org.bibsonomy.events.model;

import java.util.LinkedList;
import java.util.List;

public class Event {

    private String id;
    private String name;
    private String description;
    private String year;
    private String location;
    private String url;
    private String userName;

    private List<String> subEvents = new LinkedList<String>();

    public String getId() {
	return id;
    }

    public void setId(String name) {
	this.id = name;
    }

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
	return id + ": " + description + " (" + location + ", " + year + ")";
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public String getUserName() {
	return userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
    }

}
