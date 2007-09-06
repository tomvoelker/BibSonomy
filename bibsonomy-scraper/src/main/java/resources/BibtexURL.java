package resources;

import java.io.Serializable;
import java.util.Date;

public class BibtexURL implements Serializable {

	private static final long serialVersionUID = 9154829685417433301L;
	private String url;
	private String text;
	private Date date;
	
	public String toString () {
		return text + " <" + url + ">";
	}
	
	public BibtexURL(String url, String text) {
		super();
		this.url = url;
		this.text = text;
	}
	public BibtexURL(String url, String text, Date date) {
		super();
		this.url = url;
		this.text = text;
		this.date = date;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
