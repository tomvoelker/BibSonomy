package org.bibsonomy.webapp.util.tags;

import java.util.Locale;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;

import tags.Functions;

/**
 * TODO: add documentation to this class
 *
 */
public class BibTexListHoverRendererTag {
	
	private BibTexListHoverRendererTag(final BibTex publication, final Locale locale) {
		this.publication = publication;
		this.locale = locale;
		this.output = "";
	}
	
	private final BibTex publication;
	private Locale locale;
	
	private String output;
	
	/**
	 * Used to render the Hover of a BibTex
	 * @param resource
	 * @param locale
	 * @param authors
	 * @return String
	 */
	public static String renderHover(final Post post, final Locale locale) {
		
		final BibTex publication = (BibTex)post.getResource();
		
		BibTexListHoverRendererTag renderer = new BibTexListHoverRendererTag(publication,locale);
		
		renderer.title().add("\n");
		
		if (renderer.publication.getEntrytype().equals("article")) {
			renderer.journal().volumeNumberPages().year(true).note();
		}
		else if (renderer.publication.getEntrytype().equals("book")) {
			renderer.series().publisher().address().edition().year(true).note();
		}
		else if (renderer.publication.getEntrytype().equals("inbook")) {
			renderer.volumeOfSeries().chapter().page().publisher().address().edition().year(true).note();
		}
		else if (renderer.publication.getEntrytype().equals("booklet")) {
			renderer.howpublished().address().year(true).note();
		}
		else if (renderer.publication.getEntrytype().equals("incollection")) {
			renderer.booktitle().volumeOfSeries().chapter().publisher().address().edition().note().year(true);
		}
		else if (renderer.publication.getEntrytype().equals("inproceedings")) {
			renderer.booktitle().volumeOfSeries().page().address().organization().publisher().year(true).note();
		}
		else if (renderer.publication.getEntrytype().equals("manual")) {
			renderer.organization().address().edition().year(true).note();
		}
		else if (renderer.publication.getEntrytype().equals("mastersthesis") ||
				renderer.publication.getEntrytype().equals("phdthesis")) {
			renderer.school().address().type().year(true).note();
		}
		else if (renderer.publication.getEntrytype().equals("proceedings")) {
			renderer.volumeOfSeries().address().organization().publisher().year(true).note();
		}
		else if (renderer.publication.getEntrytype().equals("techreport")) {
			renderer.type().volume().institution().address().year(true);
		}
		else if (renderer.publication.getEntrytype().equals("unpublished")) {
			renderer.year(true).note();
		}
		else {
			renderer.howpublished().year(true).note();
		}
		return renderer.getOutput();
		
	}
	
	private BibTexListHoverRendererTag add(String s) {
		this.output+=s;
		return this;
	}
	
	private BibTexListHoverRendererTag journal() {
		if (present(publication.getJournal())) {
			output+=BibTexUtils.cleanBibTex(publication.getJournal()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag volumeNumberPages() {
		if (present(publication.getVolume()) && present(publication.getNumber()) && present(publication.getPages())) {
			output+=BibTexUtils.cleanBibTex(publication.getVolume()) + "(" +
					BibTexUtils.cleanBibTex(publication.getNumber()) + "):" +
					BibTexUtils.cleanBibTex(publication.getPages()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag year(boolean braces) {
		
		if(this.locale == null) {
			this.locale = Locale.ENGLISH;
		}
		
		String date = Functions.getDate(publication.getDay(), publication.getMonth(), publication.getYear(), this.locale);
		if (braces) {
			output+="(" + date + ")";
		} else {
			output+=date;
		}
		output+= " ";
		return this;
	}
	
	private BibTexListHoverRendererTag note() {
		if (present(publication.getNote())) {
			output+=BibTexUtils.cleanBibTex(publication.getNote());
		}
		return this;
	}
	
	private BibTexListHoverRendererTag series() {
		if (present(publication.getSeries())) {
			output+=BibTexUtils.cleanBibTex(publication.getSeries()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag publisher() {
		if (present(publication.getPublisher())) {
			output+=BibTexUtils.cleanBibTex(publication.getPublisher()) + ", ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag address() {
		if (present(publication.getAddress())) {
			output+=BibTexUtils.cleanBibTex(publication.getAddress()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag edition() {
		if (present(publication.getEdition())) {
			output+=BibTexUtils.cleanBibTex(publication.getEdition()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag volumeOfSeries() {
		if (present(publication.getVolume())) {
			if (present(publication.getSeries())) {
				//FIXME - use BundleResource-PropertieMessages "bibtex.volume" "bibtex.volumeOf"
				output+="volume " + BibTexUtils.cleanBibTex(publication.getVolume()) +
						" of " + BibTexUtils.cleanBibTex(publication.getSeries()) + " ";
			} else {
				output+= " " + BibTexUtils.cleanBibTex(publication.getVolume()) + " ";
			}
		}
		else if (present(publication.getNumber())) {
			output+= " " + BibTexUtils.cleanBibTex(publication.getNumber()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag chapter() {
		if (present(publication.getChapter())) {
			//FIXME - use BundleResource-PropertieMessages "bibtex.chapter"
			output+="chapter " + BibTexUtils.cleanBibTex(publication.getChapter()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag page() {
		if (present(publication.getPages())) {
			//FIXME - use BundleResource-PropertieMessages "bibtex.pages"
			output+="page " + BibTexUtils.cleanBibTex(publication.getPages()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag howpublished() {
		if (present(publication.getHowpublished())) {
			output+=BibTexUtils.cleanBibTex(publication.getHowpublished()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag booktitle() {
		if (present(publication.getBooktitle())) {
			output+=BibTexUtils.cleanBibTex(publication.getBooktitle()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag organization() {
		if (present(publication.getOrganization())) {
			output+=BibTexUtils.cleanBibTex(publication.getOrganization()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag school() {
		if (present(publication.getSchool())) {
			output+=BibTexUtils.cleanBibTex(publication.getSchool()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag type() {
		if (present(publication.getType())) {
			output+=BibTexUtils.cleanBibTex(publication.getType()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag institution() {
		if (present(publication.getInstitution())) {
			output+=BibTexUtils.cleanBibTex(publication.getInstitution()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag volume() {
		if (present(publication.getVolume())) {
			//FIXME - use BundleResource-PropertieMessages "bibtex.volume"
			output+="volume " + BibTexUtils.cleanBibTex(publication.getVolume()) + " ";
		}
		else if (present(publication.getNumber())) {
			output+=BibTexUtils.cleanBibTex(publication.getNumber()) + ". ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag title() {
		if (present(publication.getTitle())) {
			output+=BibTexUtils.cleanBibTex(publication.getTitle()) + " ";
		}
		return this;
	}
	
	private static boolean present(String s) {
		if (s != null && !s.equals("")) {
			return true;
		}
		return false;
	}
	
	private String getOutput() {
		return this.output;
	}
	
}
