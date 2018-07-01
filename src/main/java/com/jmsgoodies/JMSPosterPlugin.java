package com.jmsgoodies;


import lombok.extern.slf4j.Slf4j;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;
import org.apache.maven.plugins.annotations.ResolutionScope;
import java.io.File;

@Slf4j
@Mojo(name = "install",requiresProject = false,requiresDependencyResolution = ResolutionScope.COMPILE)
public class JMSPosterPlugin extends AbstractMojo {
    @Component
    private MavenProject mavenProject;

    @Component
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;


    @Parameter(property = "installationDirectory",defaultValue = "JMSPoster.jar")
    private String installationDirectory;
    public void execute()
            throws MojoExecutionException {
        log.info("Creating environment in "+installationDirectory);
        log.info("Downloading JMSPoster library into environment");
        String jarFile = "JMSPoster.jar";
        if (installationDirectory!=null){
            File installation = new File(installationDirectory);
            if (!installation.exists()){
                installation.mkdirs();
            }
            jarFile = installationDirectory + "//"+jarFile;
        }


        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-dependency-plugin"),
                        version("2.5.1")
                ),
                goal("get"),
                configuration(
                        element(name("artifact"), "com.jmsgoodies:JMSPoster:1.0-SNAPSHOT"),
                        element(name("transitive"), "false"),
                        element(name("destination"), jarFile)

                ),
                executionEnvironment(
                        mavenProject,
                        mavenSession,
                        pluginManager
                )
        );


    }

    public void setInstallationDirectory(String installationDirectory) {
        this.installationDirectory = installationDirectory;
    }

    public String getInstallationDirectory() {

        return installationDirectory;
    }
}
