/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.forge2.ui.bot.wizard.test;

import static org.junit.Assert.assertFalse;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jboss.ide.eclipse.as.reddeer.server.requirement.ServerReqType;
import org.jboss.ide.eclipse.as.reddeer.server.requirement.ServerRequirement.JBossServer;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.eclipse.core.resources.Project;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.jface.wizard.WizardDialog;
import org.jboss.reddeer.requirements.server.ServerReqState;
import org.jboss.reddeer.swt.api.TableItem;
import org.jboss.reddeer.swt.impl.menu.ContextMenu;
import org.jboss.tools.forge.reddeer.ui.wizard.ConnectionProfileWizardPage;
import org.jboss.tools.forge.reddeer.ui.wizard.EntitiesFromTablesWizardFirstPage;
import org.jboss.tools.forge.reddeer.ui.wizard.EntitiesFromTablesWizardSecondPage;
import org.jboss.tools.forge.ui.bot.test.util.DatabaseUtils;
import org.jboss.tools.forge.ui.bot.test.util.ScaffoldType;
import org.junit.Test;

import org.jboss.tools.hibernate.reddeer.wizard.JBossDatasourceWizard;
import org.jboss.tools.hibernate.reddeer.wizard.NewJBossDatasourceWizardPage;

/**
 * 
 * 
 * @author jkopriva
 *
 */
@JBossServer(state = ServerReqState.RUNNING, type = ServerReqType.WILDFLY10x)
public class DeployScaffoldDBTest extends WizardTestBase {

	private String dbFolder = System.getProperty("database.path");
	private List<String> tableNames = new ArrayList<String>();

	private static final String PROJECT_NAME = "test-sakila-scaffold";

	private static final String DATASOURCE_NAME = "sakila";

	private static final String H2_DIALECT = "H2 Database : org.hibernate.dialect.H2Dialect";
	private static final String PACKAGE = GROUPID + ".model";
	private static final String PROFILE_NAME = "sakila";
	private static final String SAKILA_URL = "jdbc:h2:tcp://localhost/sakila";
	private static final String SAKILA_USERNAME = "sa";
	private static final String SAKILA_H2_DRIVER = "h2-1.3.161.jar";

	@Test
	public void testDeployScaffoldDB() {
		createNewProject();
		createConnectionProfile();
		createJBOSSDatasource();
		jpaSetup();
		jpaGenerateEntities();
		generateScaffold();
		deployOnServer();
	}

	@Override
	public void cleanup() {
		super.cleanup();
		DatabaseUtils.stopSakilaDB();
	}

	public void createNewProject() {
		newProject(PROJECT_NAME);
	}

	public void createConnectionProfile() {
		assertNotNull(dbFolder);
		DatabaseUtils.runSakilaDB(dbFolder);
		newProject(PROJECT_NAME);
		persistenceSetup(PROJECT_NAME);

		WizardDialog dialog = getWizardDialog("Connection: Create Profile", "(Connection: Create Profile).*");
		ConnectionProfileWizardPage page = new ConnectionProfileWizardPage();
		page.setConnectionName(PROFILE_NAME);
		page.setJdbcUrl(SAKILA_URL);
		page.setUserName(SAKILA_USERNAME);
		page.setDriverLocation(dbFolder + File.separator + SAKILA_H2_DRIVER);
		page.setHibernateDialect(H2_DIALECT);
		dialog.finish(TimePeriod.LONG);
	}

	public void createJBOSSDatasource() {
		JBossDatasourceWizard wizard = new JBossDatasourceWizard();
		wizard.open();
		NewJBossDatasourceWizardPage page = new NewJBossDatasourceWizardPage();
		page.setConnectionProfile(DATASOURCE_NAME);
		page.setParentFolder("/" + PROJECT_NAME + "/src/main/resources");
		page.finish();

	}

	public void jpaSetup() {
		persistenceSetup(PROJECT_NAME);
	}

	public void jpaGenerateEntities() {
		new ProjectExplorer().selectProjects(PROJECT_NAME);
		WizardDialog dialog = getWizardDialog("JPA: Generate Entities From Tables",
				"(JPA: Generate Entities From Tables).*");
		EntitiesFromTablesWizardFirstPage firstPage = new EntitiesFromTablesWizardFirstPage();
		firstPage.setPackage(PACKAGE);
		assertTrue("Missing connection profile selection", firstPage.getAllProfiles().contains(PROFILE_NAME));
		firstPage.setConnectionProfile(PROFILE_NAME);
		dialog.next();

		EntitiesFromTablesWizardSecondPage secondPage = new EntitiesFromTablesWizardSecondPage();
		List<TableItem> tables = secondPage.getAllTables();
		assertFalse("No database tables found", tables.isEmpty());
		for (TableItem item : tables) {
			tableNames.add(item.getText());
		}
		secondPage.selectAll();
		dialog.finish();
	}

	public void generateScaffold() {
		ScaffoldType angular = ScaffoldType.ANGULARJS;
		scaffoldSetup(PROJECT_NAME, angular);
		createScaffold(PROJECT_NAME, angular);
	}

	public void deployOnServer() {
		ProjectExplorer explorer = new ProjectExplorer();
		explorer.activate();
		Project project = explorer.getProject(PROJECT_NAME);
		project.select();
		new ContextMenu("Run As", "1 Run on Server").select();
		new WizardDialog().finish();
		// TODO check deploy of project
	}

}