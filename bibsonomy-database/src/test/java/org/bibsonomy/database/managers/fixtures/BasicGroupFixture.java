package org.bibsonomy.database.managers.fixtures;

import org.bibsonomy.common.enums.Privlevel;

/**
 * A fixture providing the expected values for properties of the "group_basic" type. See GroupCommon.xml for details.
 */
public class BasicGroupFixture {

    private int groupId;
    private String name;
    private Privlevel privlevel;
    private boolean sharedDocuments;
    private boolean allowjoin;
    private String description;
    private boolean organization;

    public BasicGroupFixture(int groupId,
                             String name,
                             Privlevel privlevel,
                             boolean sharedDocuments,
                             boolean allowjoin,
                             String description,
                             boolean organization) {
        this.groupId = groupId;
        this.name = name;
        this.privlevel = privlevel;
        this.sharedDocuments = sharedDocuments;
        this.allowjoin = allowjoin;
        this.description = description;
        this.organization = organization;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public Privlevel getPrivlevel() {
        return privlevel;
    }

    public boolean isSharedDocuments() {
        return sharedDocuments;
    }

    public boolean isAllowjoin() {
        return allowjoin;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOrganization() {
        return organization;
    }
}
