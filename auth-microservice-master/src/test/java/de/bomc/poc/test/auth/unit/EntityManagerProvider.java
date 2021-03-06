/**
 * Project: MY_POC
 * <p/>
 * <pre>
 *
 * Last change:
 *
 *  by: $Author: $
 *
 *  date: $Date: $
 *
 *  revision: $Revision: $
 *
 * </pre>
 * <p/>
 * Copyright (c): BOMC, 2016
 */
package de.bomc.poc.test.auth.unit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnitUtil;

/**
 * A class that provides database functionality.
 * 
 * @author <a href="mailto:bomc@bomc.org">bomc</a>
 */
public class EntityManagerProvider implements TestRule {

	private EntityManager entityManager;
	private EntityTransaction tx;
	private PersistenceUnitUtil persistenceUnitUtil;

	private EntityManagerProvider(String unitName) {
		final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(unitName);
		this.entityManager = entityManagerFactory.createEntityManager();
		this.persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
		this.tx = this.entityManager.getTransaction();
	}

	public final static EntityManagerProvider persistenceUnit(String unitName) {
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
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				base.evaluate();
				entityManager.clear();
			}
		};
	}
}
