package org.bibsonomy.events.model;

/**
 *
 * @author mat
 *
 */

public class ParticipantDetails {

	private static final String[] badgeTitles = {"", "Mr.", "Ms.", "Mrs.", "Miss", "Dr.", "1sgt.", "1st Lt.", "2nd Lt.", "Adm.", "AIM", "Baron", "Baroness", "Bishop", "Brig. Gen.", "Brother", "Cantor", "Capt.", "Cardinal", "Cmdr.", "Cmst.", "Col.", "Count", "Countess", "Cpl.", "Cpo.", "Dean", "DM", "Duchess", "Duke", "Elder", "Ens.", "Father", "Fleet Adm.", "General", "Governor", "Gysgt.", "Hon.", "Imam", "Judge", "Lady", "Lcpl.", "Lord", "Lt.", "Lt. Cmdr.", "Lt. Col.", "Lt. Gen.", "Lt. Jg.", "Ma.", "Major", "Major Gen.", "Mcpo.", "Mgysgt.", "Minister", "Monsignor", "Most Rev.", "Mother", "Msgt.", "Mstr.", "Pastor", "Petty Off.", "Pfc.", "Po1", "Po2", "Po3", "President", "Prince", "Prof.", "Pvt.", "Rabbi", "Rear Adm.", "Rev.", "Right Rev.", "Scpo.", "Senator", "Sfc.", "Sgt.", "Sgtmaj.", "Sir", "Sister", "Smn.", "Smn1", "Smst.", "Sp4", "Sp5", "Sp6", "Sr.", "Sra.", "Srta.", "Ssgt.", "Swami", "Tech Sgt.", "The Rev.", "The Rev. Dr.", "Very Rev.", "Vice Adm."};

	private String address = "<required>";
	private String badgeTitle;
	private String badgeName;
	private String badgeInstitutionName = "<required>";
	private boolean presenter;
	private boolean poster;
	private boolean vegetarian;
	private boolean dinner;
	private boolean socialEvent;
	private String subEvent;

	private String facebook;
	private String flickr;
	private String linkedIn;
	private String researchGate;
	private String twitter;
	private String xing;
	private String email;

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

	public String getBadgeInstitutionName() {
		return badgeInstitutionName;
	}

	public void setBadgeInstitutionName(String badgeInstitutionName) {
		this.badgeInstitutionName = badgeInstitutionName;
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

	public boolean getPresenter() {
		return presenter;
	}

	public void setPresenter(boolean presenter) {
		this.presenter = presenter;
	}

	public boolean getPoster() {
		return poster;
	}

	public void setPoster(boolean poster) {
		this.poster = poster;
	}

	public boolean getDinner() {
		return dinner;
	}

	public void setDinner(boolean dinner) {
		this.dinner = dinner;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBadgeTitle() {
		return badgeTitle;
	}

	public void setBadgeTitle(String badgeTitle) {
		this.badgeTitle = badgeTitle;
	}

	public String getBadgeName() {
		return badgeName;
	}

	public void setBadgeName(String badgeName) {
		this.badgeName = badgeName;
	}

	public boolean getSocialEvent() {
		return socialEvent;
	}

	public void setSocialEvent(boolean socialEvent) {
		this.socialEvent = socialEvent;
	}

	public String[] getBadgeTitles() {
		return badgeTitles;
	}
}
