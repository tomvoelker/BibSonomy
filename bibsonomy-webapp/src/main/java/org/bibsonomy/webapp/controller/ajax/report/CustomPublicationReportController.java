package org.bibsonomy.webapp.controller.ajax.report;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.webapp.command.ajax.ReportCommand;
import org.bibsonomy.webapp.util.View;

public class CustomPublicationReportController extends AbstractReportController {

    private final static String SUBJECT_KEY = "report.error.person.publications.custom.mail.subject";
    private final static String BODY_KEY = "report.error.person.publications.custom.mail.body";

    @Override
    public View workOn(ReportCommand command) {
        final User loggedInUser = this.requestLogic.getLoginUser();

        // Check, if spammer
        if (loggedInUser.isSpammer()) {
            return this.error(command, ERROR_KEY);
        }

        // Set parameters for the custom messages
        String personId = command.getPersonId();
        String title = command.getTitle();
        String interhash = command.getInterhash();
        String message = command.getMessage();

        String username = loggedInUser.getName();
        String postUrl = this.urlGenerator.getPublicationUrlByInterHash(interhash);
        Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, personId);
        String personName = person.getMainName().toString();
        String personUrl = this.urlGenerator.getPersonUrl(personId);

        Object[] subjectParameters = {username};
        Object[] bodyParameters = {message, title, postUrl, personName, personUrl, username};

        // Send e-mail
        boolean result = report(SUBJECT_KEY, BODY_KEY, subjectParameters, bodyParameters);

        if (result) {
            return this.success(command, SUCCESS_KEY);
        } else {
            return this.error(command, ERROR_KEY);
        }
    }

}
