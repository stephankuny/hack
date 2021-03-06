package de.bomc.poc.model.test;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnitUtil;

/**
 * This class <code>Person</code> is a sample entity.
 * @author <a href="mailto:bomc@bomc.org">Michael Boerner</a>
 * @version $Revision: $ $Author: $ $Date: $
 * @since 10.08.2016
 */
public class EntityManagerProvider implements TestRule {
    private static final String LOG_PREFIX = "EntityManagerProvider#";
    
	private final EntityManager entityManager;
	private final EntityTransaction tx;
	private final PersistenceUnitUtil persistenceUnitUtil;

	private EntityManagerProvider(final String unitName) {
		System.out.println(LOG_PREFIX + "co [PU-name=" + unitName + "]");

		final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(unitName);
		this.entityManager = entityManagerFactory.createEntityManager();
		this.persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
		this.tx = this.entityManager.getTransaction();
	}

	public static EntityManagerProvider persistenceUnit(final String unitName) {
		return new EntityManagerProvider(unitName);
	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public PersistenceUnitUtil getPersistenceUnitUtil() {
		return this.persistenceUnitUtil;
	}

	public EntityTransaction tx() {
		return this.tx;
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				base.evaluate();
				EntityManagerProvider.this.entityManager.clear();
			}
		};
	}
}
