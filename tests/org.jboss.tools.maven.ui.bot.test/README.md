# The JBoss Tools Integration Tests - Maven Tests project

## Summary

Maven Tests project contains configuration for integration testing of Maven pligin in DevStudio/JBoss Tools. 

## How to run

DevStudio:
(0. Install RedDeer in devstudio https://github.com/jboss-reddeer/reddeer/wiki/Installation )
1. Import project and all required packages in workspace.
2. Edit file org.jboss.tools.maven.ui.bot.test/resources/servers/wildfly-8.xml and set instead of ${jbosstools.test.wildfly8.home} path to WildFly 8 installation directory.
2. Right click on suite class(MavenAllBotTests.java or SmokeSuite.java) and Run As -> RedDeer Test.
3. Check Arguments tab for all parameters, example:
-Dusage_reporting_enabled=false -Drd.config=resources/servers -Djbosstools.test.seam.2.1.0.home=${path to jboss-seam-2.1.2} -Djbosstools.test.seam.2.3.0.home=${path to jboss-seam-2.3.0.Final} -Djbosstools.test.seam.2.2.0.home=${path to jboss-seam-2.2.0.GA} -Djbosstools.test.wildfly8.home=${path to wildfly8} -Dmaven.settings.path=${path_to_jbosstools-integration-tests}/tests/org.jboss.tools.maven.ui.bot.test/target/classes/usersettings/settings.xml

Maven:
1. Run maven in project directory: mvn clean verify -Dmaven.test.failure.ignore=true -Dtest.installBase=${path to installed DevStudio} -DskipTests=false

