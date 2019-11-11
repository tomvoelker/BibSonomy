package org.bibsonomy.model.logic.query;

/**
 * A class for specifying queries that yield the posts of a person.
 *
 * @author kchoong
 */
public class PersonPostQuery extends BasicPaginatedQuery {

    /**
     * A builder for constructing queries.
     */
    public static class PersonPostQueryBuilder {

        private String personId;
        private boolean paginated;
        private int start;
        private int end;


        public PersonPostQuery build() {
            return new PersonPostQuery(start, end, personId);
        }

        /**
         * Retrieve only resources from [<code>start</code>; <code>end</code>).
         *
         * @param start index of the first item.
         * @param end index of the last item.
         *
         * @return the builder.
         */
        public PersonPostQueryBuilder fromTo(int start, int end) {
            if (start < 0 || end < 0) {
                throw new IllegalArgumentException(String.format("Indices must be >= 0. start=%d, end=%d", start, end));
            }

            if (start > end) {
                throw new IllegalArgumentException(String.format("start must be <= end: %d > %d", start, end));
            }

            this.paginated = true;
            this.start = start;
            this.end = end;

            return this;
        }

        public String getPersonId() {
            return personId;
        }

        public PersonPostQueryBuilder setPersonId(String personId) {
            this.personId = personId;
            return this;
        }

        public boolean isPaginated() {
            return paginated;
        }

        public PersonPostQueryBuilder setPaginated(boolean paginated) {
            this.paginated = paginated;
            return this;
        }

        public int getStart() {
            return start;
        }

        public PersonPostQueryBuilder setStart(int start) {
            this.start = start;
            return this;
        }

        public int getEnd() {
            return end;
        }

        public PersonPostQueryBuilder setEnd(int end) {
            this.end = end;
            return this;
        }
    }

    private String personId;

    public PersonPostQuery(int start, int end, String personId) {
        super(start, end);
        this.personId = personId;
    }

    public String getPersonId() {
        return personId;
    }
}
