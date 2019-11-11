package org.bibsonomy.database.params.person;

import org.bibsonomy.common.enums.RatingAverage;
import org.bibsonomy.util.ValidationUtils;

/**
 * An object holding the parameters for querying posts for a single person.
 *
 * @author kchoong
 */
public class GetPersonPostsParam {

    private String personId;
    private Integer limit;
    private Integer offset;
    private RatingAverage ratingAverage;

    /**
     * Configures a query that returns <code>limit</code> relations beginning at <code>offset</code>.
     * If a <code>limit</code> and <code>offset</code> are <code>null</code> all relations will be returned.
     *
     * <code>limit</code> and <code>offset</code> must either both be set, or both be absent.
     *
     * @param personId      a person id.
     * @param limit         the number of relations that will be retrieved.
     * @param offset        the index of the first relation in the result set that will be retrieved.
     * @param ratingAverage the algorithm used to determine the average rating.
     */
    public GetPersonPostsParam(String personId, Integer limit, Integer offset, RatingAverage ratingAverage) {
        ValidationUtils.assertNotNull(personId);

        if ((limit == null && offset != null) || (limit != null && offset == null)) {
            throw new IllegalArgumentException("limit and offset must both be set or both be absent.");
        }

        this.personId = personId;
        this.limit = limit;
        this.offset = offset;
        this.ratingAverage = ratingAverage;
    }


    /**
     * Configures a query that returns <code>limit</code> relations beginning at <code>offset</code>.
     * If a <code>limit</code> and <code>offset</code> are <code>null</code> all relations will be returned.
     * <p>
     * Sets <code>ratingAverage</code> to the default value {@link RatingAverage#ARITHMETIC_MEAN}.
     *
     * <code>limit</code> and <code>offset</code> must either both be set, or both be absent.
     *
     * @param personId a person id.
     * @param limit    the number of relations that will be retrieved.
     * @param offset   the index of the first relation in the result set that will be retrieved.
     */
    public GetPersonPostsParam(String personId, Integer limit, Integer offset) {
        this(personId, limit, offset, RatingAverage.ARITHMETIC_MEAN);
    }


    /**
     * Configures a query that returns all relations for a given <code>personId</code>.
     * <p>
     * Sets <code>ratingAverage</code> to the default value {@link RatingAverage#ARITHMETIC_MEAN}.
     *
     * @param personId a person id.
     */
    public GetPersonPostsParam(String personId) {
        this(personId, null, null);
    }

    public String getPersonId() {
        return personId;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }
}
