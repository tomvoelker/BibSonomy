/*
 * Created on 19.01.2006
 */
package recommender.db.backend;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetHandler<T> {
	public T handle(final ResultSet rs) throws SQLException;
}
