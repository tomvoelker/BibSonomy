/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.tags;

import java.util.Locale;
import java.util.ResourceBundle;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;

import tags.Functions;

/**
 * Class that implements a jsp el function to render TitleHovers for BibTex.
 */
public class BibTexListTitleHoverFormatter {
	
	private static final String LS = " ";
	private static final String LB = "\n";
	
	private final BibTex publication;
	private Locale locale;
	
	private StringBuilder output;
	private ResourceBundle rb;
	
	private BibTexListTitleHoverFormatter(final BibTex publication, final Locale locale) {
		if (publication == null) {
			throw new IllegalArgumentException("publication can't be null!");
		}
		if (locale == null) {
			throw new IllegalArgumentException("locale can't be null!");
		}
		this.publication = publication;
		this.locale = locale;
		this.output = new StringBuilder();
		  
		if (locale.equals(Locale.ENGLISH)) {
			this.rb = ResourceBundle.getBundle("messages", Locale.ROOT);
		} else {
			this.rb = ResourceBundle.getBundle("messages", locale);
		}
	}
	
	/**
	 * Used to render the Hover of a BibTex
	 * @param post 
	 * @param resource
	 * @param locale
	 * @return String
	 */
	public static String renderHover(final Post<?> post, final Locale locale) {
		
		final BibTex publication = (BibTex)post.getResource();
		
		BibTexListTitleHoverFormatter renderer = new BibTexListTitleHoverFormatter(publication, locale);
		
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
			output.append(BibTexUtils.cleanBibTex(publication.getJournal())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter volumeNumberPages() {
		if (present(publication.getVolume()) && present(publication.getNumber()) && present(publication.getPages())) {
			output.append(BibTexUtils.cleanBibTex(publication.getVolume())).append("(").append(
					BibTexUtils.cleanBibTex(publication.getNumber())).append("):").append(
					BibTexUtils.cleanBibTex(publication.getPages())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter year(boolean braces) {
		String date = Functions.getDate(publication.getDay(), publication.getMonth(), publication.getYear(), this.locale);
		if (braces) {
			output.append("(").append(date).append(")");
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
			output.append(BibTexUtils.cleanBibTex(publication.getSeries())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter publisher() {
		if (present(publication.getPublisher())) {
			output.append(BibTexUtils.cleanBibTex(publication.getPublisher())).append(", ");
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter address() {
		if (present(publication.getAddress())) {
			output.append(BibTexUtils.cleanBibTex(publication.getAddress())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter edition() {
		if (present(publication.getEdition())) {
			output.append(BibTexUtils.cleanBibTex(publication.getEdition())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter volumeOfSeries() {
		if (present(publication.getVolume())) {
			if (present(publication.getSeries())) {
				output.append(rb.getString("bibtex.volume")).append(LS).append(BibTexUtils.cleanBibTex(publication.getVolume())).append(
						LS).append(rb.getString("bibtex.volumeOf")).append(LS).append(BibTexUtils.cleanBibTex(publication.getSeries())).append(LS);
			} else {
				output.append(LS).append(BibTexUtils.cleanBibTex(publication.getVolume())).append(LS);
			}
		}
		else if (present(publication.getNumber())) {
			output.append(LS).append(BibTexUtils.cleanBibTex(publication.getNumber())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter chapter() {
		if (present(publication.getChapter())) {
			output.append(rb.getString("bibtex.chapter")).append(LS).append(BibTexUtils.cleanBibTex(publication.getChapter())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter page() {
		if (present(publication.getPages())) {
			output.append(rb.getString("bibtex.pages")).append(LS).append(BibTexUtils.cleanBibTex(publication.getPages())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter howpublished() {
		if (present(publication.getHowpublished())) {
			output.append(BibTexUtils.cleanBibTex(publication.getHowpublished())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter booktitle() {
		if (present(publication.getBooktitle())) {
			output.append(BibTexUtils.cleanBibTex(publication.getBooktitle())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter organization() {
		if (present(publication.getOrganization())) {
			output.append(BibTexUtils.cleanBibTex(publication.getOrganization())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter school() {
		if (present(publication.getSchool())) {
			output.append(BibTexUtils.cleanBibTex(publication.getSchool())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter type() {
		if (present(publication.getType())) {
			output.append(BibTexUtils.cleanBibTex(publication.getType())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter institution() {
		if (present(publication.getInstitution())) {
			output.append(BibTexUtils.cleanBibTex(publication.getInstitution())).append(LS);
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter volume() {
		if (present(publication.getVolume())) {
			output.append(rb.getString("bibtex.volume")).append(LS).append(BibTexUtils.cleanBibTex(publication.getVolume())).append(LS);
		}
		else if (present(publication.getNumber())) {
			output.append(BibTexUtils.cleanBibTex(publication.getNumber())).append(". ");
		}
		return this;
	}
	
	private BibTexListTitleHoverFormatter title() {
		if (present(publication.getTitle())) {
			output.append(BibTexUtils.cleanBibTex(publication.getTitle())).append(LS);
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
