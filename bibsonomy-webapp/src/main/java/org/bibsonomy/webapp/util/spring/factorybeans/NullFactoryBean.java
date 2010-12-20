package org.bibsonomy.webapp.util.spring.factorybeans;

import org.springframework.beans.factory.FactoryBean;

/**
 * To provide NULL beans for functionality one theme has (e.g., PUMA) and the
 * other doesn't (e.g., BibSonomy).
 * 
 * @author rja
 * @version $Id$
 */
public class NullFactoryBean implements FactoryBean<Void> {

    public Void getObject() throws Exception {
        return null;
    }

    public Class<? extends Void> getObjectType() {
        return null;
    }

    public boolean isSingleton() {
        return true;
    }
}

