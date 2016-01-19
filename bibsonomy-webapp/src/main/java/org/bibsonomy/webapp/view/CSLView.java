/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.view;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONSerializer;

import org.bibsonomy.layout.csl.CslModelConverter;
import org.bibsonomy.layout.csl.model.Record;
import org.bibsonomy.layout.csl.model.RecordList;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.springframework.web.servlet.mvc.BaseCommandController;
import org.springframework.web.servlet.view.AbstractView;

/**
 * View to export data in CSL-compatible JSON-Format
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 */
public class CSLView extends AbstractView {

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		/*
		 * get the data
		 */
		final Object object = model.get(BaseCommandController.DEFAULT_COMMAND_NAME);
		
		final List<? extends Post<? extends BibTex>> publicationList = getPublicationList(object);
		if (!present(publicationList)) {
			return;
		}

		/*
		 * set the content type headers
		 */
		response.setContentType("application/json");
		response.setCharacterEncoding(StringUtils.CHARSET_UTF_8);

		/*
		 * output stream
		 */
		final ServletOutputStream outputStream = response.getOutputStream();
		final OutputStreamWriter writer = new OutputStreamWriter(outputStream);
		final RecordList recList = new RecordList();
		for (final Post<? extends BibTex> post : publicationList) {
			final Record rec = CslModelConverter.convertPost(post);
			recList.add(rec);
		}
		writer.write(JSONSerializer.toJSON(recList, CslModelConverter.getJsonConfig()).toString());
		writer.close();
	}
	
	/**
	 * 
	 * @param commandObject
	 * @return List of publications if supported command is given, null otherwise
	 */
	private List<? extends Post<? extends BibTex>> getPublicationList (Object commandObject) {
		if (commandObject instanceof SimpleResourceViewCommand) {
			final SimpleResourceViewCommand command = (SimpleResourceViewCommand)commandObject;
			return command.getBibtex().getList();
		}
		return null;
	}
}
