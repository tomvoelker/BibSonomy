package org.bibsonomy.webapp.util.tags;

import java.util.Locale;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;

import tags.Functions;

/**
 * TODO: add documentation to this class
 *
 */
public class BibTexListHoverRendererTag {
	
	private BibTexListHoverRendererTag(final BibTex publication, final Locale locale, final boolean authors) {
		this.publication = publication;
		this.locale = locale;
		//this.authors = authors;
		this.output = "";
	}
	
	private final BibTex publication;
	//private final boolean authors;
	private Locale locale;
	
	private String output;
	
	/**
	 * Used to render the Hover of a BibTex
	 * @param resource
	 * @param locale
	 * @param authors
	 * @return String
	 */
	public static String renderHover() {//final Resource resource, final Locale locale, final boolean authors) {
		return "BlaBlaBlub";
		/*
		BibTexListHoverRendererTag renderer = new BibTexListHoverRendererTag((BibTex) resource,locale,authors);
		
		//renderer.title().author();
		
		if (renderer.publication.getEntrytype().equals("article")) {
			renderer.journal().add(" ").volumeNumberPages().add(" ").year(true).note();
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
		*/
	}
	
	private BibTexListHoverRendererTag add(String s) {
		this.output+=s;
		return this;
	}
	
	/*
	private BibTexListHoverRendererTag author() {
		if (authors) {
			if (!publication.getAuthor().isEmpty()) {
				
			}
		}
		return this;
	}
	*/
	
	private BibTexListHoverRendererTag journal() {
		if (present(publication.getJournal())) {
			output+=Functions.cleanBibtex(publication.getJournal());
		}
		return this;
	}
	
	private BibTexListHoverRendererTag volumeNumberPages() {
		if (present(publication.getVolume()) && present(publication.getNumber()) && present(publication.getPages())) {
			output+=Functions.cleanBibtex(publication.getVolume()) + "(" +
					Functions.cleanBibtex(publication.getNumber()) + "):" +
					Functions.cleanBibtex(publication.getPages());
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
		return this;
	}
	
	private BibTexListHoverRendererTag note() {
		if (present(publication.getNote())) {
			output+=Functions.cleanBibtex(publication.getNote());
		}
		return this;
	}
	
	private BibTexListHoverRendererTag series() {
		if (present(publication.getSeries())) {
			output+=Functions.cleanBibtex(publication.getSeries()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag publisher() {
		if (present(publication.getPublisher())) {
			output+=Functions.cleanBibtex(publication.getPublisher()) + ", ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag address() {
		if (present(publication.getAddress())) {
			output+=Functions.cleanBibtex(publication.getAddress()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag edition() {
		if (present(publication.getEdition())) {
			output+=Functions.cleanBibtex(publication.getEdition()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag volumeOfSeries() {
		if (present(publication.getVolume())) {
			if (present(publication.getSeries())) {
				//FIXME - use BundleResource-PropertieMessages "bibtex.volume" "bibtex.volumeOf"
				output+="volume " + Functions.cleanBibtex(publication.getVolume()) +
						" of " + Functions.cleanBibtex(publication.getSeries()) + " ";
			} else {
				output+= " " + Functions.cleanBibtex(publication.getVolume()) + " ";
			}
		}
		else if (present(publication.getNumber())) {
			output+= " " + Functions.cleanBibtex(publication.getNumber()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag chapter() {
		if (present(publication.getChapter())) {
			//FIXME - use BundleResource-PropertieMessages "bibtex.chapter"
			output+="chapter " + Functions.cleanBibtex(publication.getChapter()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag page() {
		if (present(publication.getPages())) {
			//FIXME - use BundleResource-PropertieMessages "bibtex.pages"
			output+="page " + Functions.cleanBibtex(publication.getPages()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag howpublished() {
		if (present(publication.getHowpublished())) {
			output+=Functions.cleanBibtex(publication.getHowpublished()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag booktitle() {
		if (present(publication.getBooktitle())) {
			output+=Functions.cleanBibtex(publication.getBooktitle()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag organization() {
		if (present(publication.getOrganization())) {
			output+=Functions.cleanBibtex(publication.getOrganization()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag school() {
		if (present(publication.getSchool())) {
			output+=Functions.cleanBibtex(publication.getSchool()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag type() {
		if (present(publication.getType())) {
			output+=Functions.cleanBibtex(publication.getType()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag institution() {
		if (present(publication.getInstitution())) {
			output+=Functions.cleanBibtex(publication.getInstitution()) + " ";
		}
		return this;
	}
	
	private BibTexListHoverRendererTag volume() {
		if (present(publication.getVolume())) {
			//FIXME - use BundleResource-PropertieMessages "bibtex.volume"
			output+="volume " + Functions.cleanBibtex(publication.getVolume()) + " ";
		}
		else if (present(publication.getNumber())) {
			output+=Functions.cleanBibtex(publication.getNumber()) + ". ";
		}
		return this;
	}
	
	/*
	private BibTexListHoverRendererTag title() {
		if (present(publication.getTitle())) {
			//FIXME escapeXML?
			output+=BibTexUtils.cleanBibTex(publication.getTitle()) + " ";
		}
		return this;
	}
	*/
	
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
