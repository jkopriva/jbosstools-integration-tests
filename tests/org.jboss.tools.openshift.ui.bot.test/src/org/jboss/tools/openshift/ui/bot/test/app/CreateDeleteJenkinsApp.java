package org.jboss.tools.openshift.ui.bot.test.app;

import java.util.Date;

import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.tools.openshift.ui.bot.test.OpenShiftBotTest;
import org.jboss.tools.openshift.ui.bot.util.OpenShiftLabel;
import org.jboss.tools.openshift.ui.bot.util.TestProperties;
import org.jboss.tools.openshift.ui.bot.util.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@CleanWorkspace
public class CreateDeleteJenkinsApp extends OpenShiftBotTest {

	private final String JENKINS_APP_NAME = TestProperties
			.get("openshift.jenkins.name") + new Date().getTime();
	
	@Before
	public void cleanUpProject() {
		TestUtils.cleanupGitFolder(TestProperties
				.get("openshift.jenkins.name"));
	}
	
	@Test
	public void canCreateJenkinsApp() {
		createOpenShiftApplication(JENKINS_APP_NAME, OpenShiftLabel.AppType.JENKINS);
	}
	
	@After
	public void canDeleteJenkinsApp() {
		deleteOpenShiftApplication(JENKINS_APP_NAME, OpenShiftLabel.AppType.JENKINS);
	}
}