package org.bibsonomy.events.model;

/**
 * 
 * @author mat
 *
 */

public class ParticipantDetails {

    private String address;
    private String badgename;
    private String badgeInstitutionName;
    private boolean isPresenter;
    private boolean hasPoster;
    private boolean vegetarian;
    private String subEvent;

    private String facebook;
    private String flickr;
    private String linkedIn;
    private String researchGate;
    private String twitter;
    private String xing;

    private String icq;
    private String jabber;
    private String msn;
    private String skype;

    public ParticipantDetails() {
	super();
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    public String getBadgename() {
	return badgename;
    }

    public void setBadgename(String badgename) {
	this.badgename = badgename;
    }

    public String getBadgeInstitutionName() {
	return badgeInstitutionName;
    }

    public void setBadgeInstitutionName(String badgeInstitutionName) {
	this.badgeInstitutionName = badgeInstitutionName;
    }

    public boolean getIsPresenter() {
	return isPresenter;
    }

    public void setIsPresenter(boolean isPresenter) {
	this.isPresenter = isPresenter;
    }

    public boolean getHasPoster() {
	return hasPoster;
    }

    public void setHasPoster(boolean hasPoster) {
	this.hasPoster = hasPoster;
    }

    public String getFacebook() {
	return facebook;
    }

    public void setFacebook(String facebook) {
	this.facebook = facebook;
    }

    public String getFlickr() {
	return flickr;
    }

    public void setFlickr(String flickr) {
	this.flickr = flickr;
    }

    public String getLinkedIn() {
	return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
	this.linkedIn = linkedIn;
    }

    public String getResearchGate() {
	return researchGate;
    }

    public void setResearchGate(String researchGate) {
	this.researchGate = researchGate;
    }

    public String getTwitter() {
	return twitter;
    }

    public void setTwitter(String twitter) {
	this.twitter = twitter;
    }

    public String getXing() {
	return xing;
    }

    public void setXing(String xing) {
	this.xing = xing;
    }

    public String getIcq() {
	return icq;
    }

    public void setIcq(String icq) {
	this.icq = icq;
    }

    public String getJabber() {
	return jabber;
    }

    public void setJabber(String jabber) {
	this.jabber = jabber;
    }

    public String getMsn() {
	return msn;
    }

    public void setMsn(String msn) {
	this.msn = msn;
    }

    public String getSkype() {
	return skype;
    }

    public void setSkype(String skype) {
	this.skype = skype;
    }

    public String getSubEvent() {
        return subEvent;
    }

    public void setSubEvent(String subEvent) {
        this.subEvent = subEvent;
    }

	public boolean getVegetarian() {
		return vegetarian;
	}

	public void setVegetarian(boolean vegetarian) {
		this.vegetarian = vegetarian;
	}
}
