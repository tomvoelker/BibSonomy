/**
 * BibSonomy-Rest-Server - The REST-server.
 * <p>
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 * University of Kassel, Germany
 * http://www.kde.cs.uni-kassel.de/
 * Data Mining and Information Retrieval Group,
 * University of Würzburg, Germany
 * http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 * L3S Research Center,
 * Leibniz University Hannover, Germany
 * http://www.l3s.de/
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bibsonomy.rest.strategy.persons;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ObjectMovedException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.Person;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

import java.io.ByteArrayOutputStream;

import static org.bibsonomy.util.ValidationUtils.present;


/**
 * strategy to get a person by an additional key
 *
 * @author kchoong
 */
public class GetPersonByAdditionalKeyStrategy extends Strategy {

	private final String keyName;
	private final String keyValue;

	/**
	 * @param context
	 */
	public GetPersonByAdditionalKeyStrategy(Context context, String keyName, String keyValue) {
		super(context);
		this.keyName = keyName;
		this.keyValue = keyValue;
	}

	@Override
	public void perform(ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ObjectMovedException, ObjectNotFoundException {
		final Person person = this.getLogic().getPersonByAdditionalKey(this.keyName, this.keyValue);
		if (!present(person)) {
			throw new NoSuchResourceException("The requested person with " + this.keyName + " '" + this.keyValue + "' does not exist.");
		}
		this.getRenderer().serializePerson(this.writer, person, new ViewModel());
	}
}
