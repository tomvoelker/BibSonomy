package org.bibsonomy.database.managers.fixtures;

import org.bibsonomy.common.enums.Privlevel;


/**
 * A fixture providing the expected values for properties of the "group_extended" type. See GroupCommon.xml for details.
 */
public class ExtendedGroupFixture extends BasicGroupFixture {

    private final String realName;
    private final String homepage;

    public ExtendedGroupFixture(int id,
                                String name,
                                Privlevel privlevel,
                                boolean sharedDocuments,
                                boolean allowjoin,
                                String description,
                                String realName,
                                String homepage) {

        super(id, name, privlevel, sharedDocuments, allowjoin, description);

        this.realName = realName;
        this.homepage = homepage;
    }

    public String getRealName() {
        return realName;
    }

    public String getHomepage() {
        return homepage;
    }
}
