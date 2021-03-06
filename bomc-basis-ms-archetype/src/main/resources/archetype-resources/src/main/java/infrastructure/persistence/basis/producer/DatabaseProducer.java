#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
/**
 * Project: bomc-onion-architecture
 * <pre>
 *
 * Last change:
 *
 *  by: ${symbol_dollar}Author: bomc ${symbol_dollar}
 *
 *  date: ${symbol_dollar}Date: ${symbol_dollar}
 *
 *  revision: ${symbol_dollar}Revision: ${symbol_dollar}
 *
 * </pre>
 */
package ${package}.infrastructure.persistence.basis.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.bomc.poc.logging.qualifier.LoggerQualifier;
import org.apache.log4j.Logger;
/**
 * A producer for different <code>EntityManager</code> stages.
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 * @since 08.11.2018
 */
@ApplicationScoped
public class DatabaseProducer {

    @Inject
    @LoggerQualifier
    private Logger logger;
    // Persistence unit names.
    // NOTE@MVN: will be changed during maven project creation.
    public static final String UNIT_H2_NAME = "bomc-h2-pu";
    @Produces
    @PersistenceContext(unitName = DatabaseProducer.UNIT_H2_NAME)
    private EntityManager entityManagerH2;
}
