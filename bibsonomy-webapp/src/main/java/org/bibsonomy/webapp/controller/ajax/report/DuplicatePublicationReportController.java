package org.bibsonomy.webapp.controller.ajax.report;

import org.bibsonomy.webapp.command.ajax.ReportCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

public class DuplicatePublicationReportController extends AbstractReportController {

    private final static String SUBJECT_KEY = "report.error.duplicate.personPublication.mail.subject";
    private final static String BODY_KEY = "report.error.duplicate.personPublication.mail.body";

    @Override
    public View workOn(ReportCommand command) {
        String personId = command.getPersonId();
        String title = command.getTitle();
        String interhash = command.getInterhash();
        String personUrl = this.urlGenerator.getPersonUrl(personId);

        String referer = command.getReferer();
        String reporter = this.requestLogic.getLoginUser().getName();

        Object[] subjectParameters = {personId};
        Object[] bodyParameters = {title, personId, personUrl, referer, reporter};

        boolean result = report(SUBJECT_KEY, BODY_KEY, subjectParameters, bodyParameters);

        if (result) {
            return this.success(command, SUCCESS_KEY);
        } else {
            return this.error(command, ERROR_Key);
        }
    }

}
