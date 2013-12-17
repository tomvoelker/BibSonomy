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
