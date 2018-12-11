package org.bibsonomy.database.params.group;


import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Parameters used to select a group based on its external id.
 */
public class GetByExternalId {

    private final String externalId;

    public GetByExternalId(String externalId) {
        present(externalId);
        this.externalId = externalId;
    }

    /**
     * Gives the external id of the group to be selected.
     *
     * @return an external id.
     */
    public String getExternalId() {
        return externalId;
    }
}
