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
public class BibTexListTitleHoverFormatter {
	
	private static final String LS = " ";
	private static final String LB = "\n";
	
	private final BibTex publication;
	private Locale locale;
	
	private StringBuilder output;
	
	private BibTexListTitleHoverFormatter(final BibTex publication, final Locale locale) {
		this.publication = publication;
		this.locale = locale;
		this.output = new StringBuilder();
	}
	
	/**
	 * Used to render the Hover of a BibTex
	 * @param resource
	 * @param locale
	 * @param authors
	 * @return String
	 */
	public static String renderHover(final Post post, final Locale locale) {
		
		final BibTex publication = (BibTex)post.getResource();
		
		BibTexListTitleHoverFormatter renderer = new BibTexListTitleHoverFormatter(publication,locale);
		
		renderer.title().add(LB);
		
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
	
	private BibTexListTitleHoverFormatter add(String s) {
		output.append(s);
		return this;
	}
	
	private BibTexListTitleHoverFormatter journal() {
		if (present(publication.getJournal())) {
			output.append(BibTexUtils.cleanBibTex(publication.getJournal()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter volumeNumberPages() {
		if (present(publication.getVolume()) && present(publication.getNumber()) && present(publication.getPages())) {
			output.append(BibTexUtils.cleanBibTex(publication.getVolume()) + "(" +
					BibTexUtils.cleanBibTex(publication.getNumber()) + "):" +
					BibTexUtils.cleanBibTex(publication.getPages()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter year(boolean braces) {
		
		if(this.locale == null) {
			this.locale = Locale.ENGLISH;
		}
		
		String date = Functions.getDate(publication.getDay(), publication.getMonth(), publication.getYear(), this.locale);
		if (braces) {
			output.append("(" + date + ")");
		} else {
			output.append(date);
		}
		output.append(LS);
		return this;
	}
	
	private BibTexListTitleHoverFormatter note() {
		if (present(publication.getNote())) {
			output.append(BibTexUtils.cleanBibTex(publication.getNote()));
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter series() {
		if (present(publication.getSeries())) {
			output.append(BibTexUtils.cleanBibTex(publication.getSeries()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter publisher() {
		if (present(publication.getPublisher())) {
			output.append(BibTexUtils.cleanBibTex(publication.getPublisher()) + ", ");
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter address() {
		if (present(publication.getAddress())) {
			output.append(BibTexUtils.cleanBibTex(publication.getAddress()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter edition() {
		if (present(publication.getEdition())) {
			output.append(BibTexUtils.cleanBibTex(publication.getEdition()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter volumeOfSeries() {
		if (present(publication.getVolume())) {
			if (present(publication.getSeries())) {
				//FIXME - use BundleResource-PropertieMessages "bibtex.volume" "bibtex.volumeOf"
				output.append("volume " + BibTexUtils.cleanBibTex(publication.getVolume()) +
						" of " + BibTexUtils.cleanBibTex(publication.getSeries()) + LS);
			} else {
				output.append(LS + BibTexUtils.cleanBibTex(publication.getVolume()) + LS);
			}
		}
		else if (present(publication.getNumber())) {
			output.append(LS + BibTexUtils.cleanBibTex(publication.getNumber()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter chapter() {
		if (present(publication.getChapter())) {
			//FIXME - use BundleResource-PropertieMessages "bibtex.chapter"
			output.append("chapter " + BibTexUtils.cleanBibTex(publication.getChapter()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter page() {
		if (present(publication.getPages())) {
			//FIXME - use BundleResource-PropertieMessages "bibtex.pages"
			output.append("page " + BibTexUtils.cleanBibTex(publication.getPages()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter howpublished() {
		if (present(publication.getHowpublished())) {
			output.append(BibTexUtils.cleanBibTex(publication.getHowpublished()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter booktitle() {
		if (present(publication.getBooktitle())) {
			output.append(BibTexUtils.cleanBibTex(publication.getBooktitle()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter organization() {
		if (present(publication.getOrganization())) {
			output.append(BibTexUtils.cleanBibTex(publication.getOrganization()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter school() {
		if (present(publication.getSchool())) {
			output.append(BibTexUtils.cleanBibTex(publication.getSchool()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter type() {
		if (present(publication.getType())) {
			output.append(BibTexUtils.cleanBibTex(publication.getType()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter institution() {
		if (present(publication.getInstitution())) {
			output.append(BibTexUtils.cleanBibTex(publication.getInstitution()) + LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter volume() {
		if (present(publication.getVolume())) {
			//FIXME - use BundleResource-PropertieMessages "bibtex.volume"
			output.append("volume " + BibTexUtils.cleanBibTex(publication.getVolume()) + LS);
		}
		else if (present(publication.getNumber())) {
			output.append(BibTexUtils.cleanBibTex(publication.getNumber()) + ". ");
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter title() {
		if (present(publication.getTitle())) {
			output.append(BibTexUtils.cleanBibTex(publication.getTitle()) + LS);
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
		return output.toString();
	}
	
}
