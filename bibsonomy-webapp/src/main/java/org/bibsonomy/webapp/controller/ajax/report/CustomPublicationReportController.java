package org.bibsonomy.webapp.controller.ajax.report;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.webapp.command.ajax.ReportCommand;
import org.bibsonomy.webapp.util.View;

public class CustomPublicationReportController extends AbstractReportController {

    private static final Log log = LogFactory.getLog(CustomPublicationReportController.class);

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
            log.info("Successfully sent an e-mail for a custom publication report. " + message + ", " + title + ", " + personUrl);
            return this.success(command, SUCCESS_KEY);
        } else {
            log.error("Failed attempting to sent an e-mail for a custom publication report. " + message + ", " + title + ", " + personUrl+ ", " + username);
            return this.error(command, ERROR_KEY);
        }
    }

}
