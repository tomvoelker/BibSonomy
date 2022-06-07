package org.bibsonomy.webapp.controller.actions.report;

import java.util.Locale;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.webapp.command.actions.ReportCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.springframework.validation.Errors;

@Getter
@Setter
public abstract class AbstractReportController implements MinimalisticController<ReportCommand>, ErrorAware {

    protected RequestLogic requestLogic;
    protected Errors errors;
    protected MailUtils mailUtils;

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

    @Override
    public Errors getErrors() {
        return this.errors;
    }

    @Override
    public void setErrors(Errors errors) {
        this.errors = errors;
    }
}
