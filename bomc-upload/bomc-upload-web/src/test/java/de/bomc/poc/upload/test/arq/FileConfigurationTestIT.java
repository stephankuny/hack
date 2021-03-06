/**
 * Project: Poc-upload
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: bomc $
 *
 *  date: $Date: 2016-12-23 14:20:47 +0100 (Fr, 23 Dez 2016) $
 *
 *  revision: $Revision: 9598 $
 *
 * </pre>
 */
package de.bomc.poc.upload.test.arq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.ejb.EJB;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bomc.poc.upload.configuration.ConfigKeys;
import de.bomc.poc.upload.configuration.ConfigSingletonEJB;
import de.bomc.poc.upload.configuration.producer.ConfigProducer;
import de.bomc.poc.upload.configuration.qualifier.ConfigQualifier;

/**
 * Tests the reading configuration from configuration.properties file.
 * 
 * <pre>
 *  mvn clean install -Parq-wildfly-remote -Dtest=FileConfigurationTestIT
 * </pre>
 * 
 * @author <a href="mailto:bomc@bomc.org">Michael B&ouml;rner</a>
 * @since 14.03.2016
 */
@RunWith(Arquillian.class)
public class FileConfigurationTestIT extends ArquillianBase {

	// _______________________________________________
	// Test parameters
	//
	// Constants
	private static final String LOG_PREFIX = "FileConfigurationTestIT#";
	private static final String WEB_ARCHIVE_NAME = "configuration-file-service";
	private static final Logger LOGGER = Logger.getLogger(DefaultConfigurationTestIT.class);

	@Deployment
	public static Archive<?> createDeployment() {
		final WebArchive webArchive = createTestArchive(WEB_ARCHIVE_NAME);
		webArchive.addClass(DefaultConfigurationTestIT.class);
		webArchive.addClasses(ConfigKeys.class, ConfigSingletonEJB.class, ConfigProducer.class, ConfigQualifier.class);
		webArchive.addAsWebInfResource(getBeansXml(), "beans.xml");
		webArchive.addAsResource("configuration.properties");

		// Add dependencies
		final MavenResolverSystem resolver = Maven.resolver();

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:logging-lib:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("commons-lang:commons-lang:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());

		webArchive.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("de.bomc.poc:exception-lib:jar:?")
				.withMavenCentralRepo(false).withTransitivity().asFile());
		
		System.out.println(LOG_PREFIX + "createDeployment: " + webArchive.toString(true));

		return webArchive;
	}

	@EJB
	ConfigSingletonEJB configSingletonEJB;

	/**
	 * <pre>
	 *  mvn clean install -Parq-wildfly-remote -Dtest=FileConfigurationTestIT#test010_v1_readDefaultPropertiesFromEJB_Pass
	 *
	 * <b><code>test010_v1_readDefaultPropertiesFromEJB_Pass</code>:</b><br>
	 *  Tests reading configuration properties from file.
	 *  
	 * <b>Preconditions:</b><br>
	 *  - Artifact must be successful deployed in Wildfly.
	 *  - Artifact consists the configuration.properties file.
	 *
	 * <b>Scenario:</b><br>
	 *  The following steps are executed:
	 *  - During deployment process, the EJB tries to read configuration from properties file.
	 *
	 * <b>Postconditions:</b><br>
	 * - Deployment finished successful.
	 * </pre>
	 */
	@Test
	@InSequence(10)
	public void test010_v1_readDefaultPropertiesFromEJB_Pass() {
		LOGGER.debug(LOG_PREFIX + "test010_v1_readDefaultPropertiesFromEJB_Pass");

		// Get the reading properties from configuration.properties.
		assertThat(this.configSingletonEJB.getConnectionRequestTimeout(),
				equalTo(7500));
		assertThat(this.configSingletonEJB.getConnectTimeout(), equalTo(4500));
		assertThat(this.configSingletonEJB.getSoTimeout(), equalTo(35000));
	}
}
