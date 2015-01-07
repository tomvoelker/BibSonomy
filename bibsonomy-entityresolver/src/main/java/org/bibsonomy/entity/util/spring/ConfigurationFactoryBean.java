/**
 * BibSonomy Entity Resolver - Username/author identiy resolving for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.entity.util.spring;

import no.priv.garshol.duke.ConfigLoader;
import no.priv.garshol.duke.Configuration;
import no.priv.garshol.duke.DataSource;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author dzo
 */
public class ConfigurationFactoryBean implements FactoryBean<Configuration> {
	
	/** path to the config */
	private String path;
	/** where to store the index */
	private String indexPath;
	private DataSource datasource;
	
	@Override
	public Configuration getObject() throws Exception {
		final Configuration configuration = ConfigLoader.load(this.path);
		configuration.setPath(this.indexPath);
		configuration.addDataSource(0, this.datasource);
		return configuration;
	}

	@Override
	public Class<?> getObjectType() {
		return Configuration.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(final String path) {
		this.path = path;
	}

	/**
	 * @param indexPath the indexPath to set
	 */
	public void setIndexPath(final String indexPath) {
		this.indexPath = indexPath;
	}

	/**
	 * @param datasource the datasource to set
	 */
	public void setDatasource(final DataSource datasource) {
		this.datasource = datasource;
	}
}
