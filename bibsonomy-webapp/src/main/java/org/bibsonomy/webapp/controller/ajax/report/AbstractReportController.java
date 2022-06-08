package org.bibsonomy.webapp.controller.ajax.report;

import java.util.Locale;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.webapp.command.actions.ReportCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;

@Getter
@Setter
public abstract class AbstractReportController extends AjaxController implements MinimalisticController<ReportCommand> {

    protected MailUtils mailUtils;
    protected URLGenerator urlGenerator;

    @Override
    public abstract View workOn(ReportCommand command);

    protected boolean report(final String subjectKey, final String bodyKey, final Object[] subjectParameters, final Object[] bodyParameters) {
        final Locale locale = this.requestLogic.getLocale();
        mailUtils.sendReportMail(subjectKey, bodyKey, subjectParameters, bodyParameters, locale);

        return false;
    }

    @Override
    public ReportCommand instantiateCommand() {
        return new ReportCommand();
    }

}
