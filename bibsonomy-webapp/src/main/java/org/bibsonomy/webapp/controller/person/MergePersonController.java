/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.webapp.controller.person;

import static org.bibsonomy.util.ValidationUtils.present;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonMergeFieldConflict;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.PersonMatchUtils;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MergePersonController {
    private static final Log log = LogFactory.getLog(EditPersonController.class);

    private final LogicInterface logic;

    public MergePersonController(LogicInterface logic) {
        this.logic = logic;
    }

    /*
     * performs the merge action for the selected match
     */
    protected View mergeAction(PersonPageCommand command) {
        int id = command.getFormMatchId();
        JSONObject jsonResponse = new JSONObject();

        PersonMatch match = this.logic.getPersonMergeRequest(id);
        boolean result = true;
        if (command.getUpdateOperation() == PersonUpdateOperation.MERGE_ACCEPT) {
            result = this.logic.acceptMerge(match);
        } else if (command.getUpdateOperation() == PersonUpdateOperation.MERGE_DENIED) {
            this.logic.denyPersonMerge(match);
        }
        jsonResponse.put("status", result);
        command.setResponseString(jsonResponse.toString());
        return Views.AJAX_JSON;
    }

    /**
     * FIXME: we DO NOT use database ids in the webapp!!!!!!!
     *
     * @param command
     * @return
     */
    protected View getConflicts(final PersonPageCommand command) {
        final int formMatchId = command.getFormMatchId();
        final PersonMatch personMatch = this.logic.getPersonMergeRequest(formMatchId);

        final JSONArray array = new JSONArray();
        for (PersonMergeFieldConflict conflict : PersonMatchUtils.getPersonMergeConflicts(personMatch)) {
            final JSONObject jsonConflict = new JSONObject();
            jsonConflict.put("field", conflict.getFieldName());
            jsonConflict.put("person1Value", conflict.getPerson1Value());
            jsonConflict.put("person2Value", conflict.getPerson2Value());
            array.add(jsonConflict);
        }
        command.setResponseString(array.toJSONString());
        return Views.AJAX_JSON;
    }

    /**
     * @param command
     * @return
     */
    protected View conflictMerge(PersonPageCommand command) {
        final JSONObject jsonResponse = new JSONObject();

        try {
            final Map<String, String> map = new HashMap<>();
            final Person person = command.getPerson();
            if (present(person)) {
                for (final String fieldName : Person.fieldsWithResolvableMergeConflicts){
                    final PropertyDescriptor desc = new PropertyDescriptor(fieldName, Person.class);
                    final Object value = desc.getReadMethod().invoke(person);

                    if (value != null) {
                        map.put(fieldName, value.toString());
                    }
                }
            }
            final PersonName newName = command.getNewName();
            if (present(newName)) {
                map.put("mainName", PersonNameUtils.serializePersonName(newName));
            }

            jsonResponse.put("status", this.logic.mergePersonsWithConflicts(command.getFormMatchId(), map));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
            log.error("error while building cpm", e);
            jsonResponse.put("status", false);
        }

        command.setResponseString(jsonResponse.toString());
        return Views.AJAX_JSON;
    }
}