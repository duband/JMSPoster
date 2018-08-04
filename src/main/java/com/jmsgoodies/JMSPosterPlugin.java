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
import org.apache.commons.lang3.SystemUtils;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Slf4j
@Mojo(name = "install",requiresProject = false,requiresDependencyResolution = ResolutionScope.COMPILE)
public class JMSPosterPlugin extends AbstractMojo {

    public static String queueName = "jms/queue";
    public static String localBroker = "vm://localhost?broker.persistent=false";
    public static String payloadMsg = "hello";

    @Component
    private MavenProject mavenProject;

    @Component
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;


    @Parameter(property = "installationDirectory")
    private String installationDirectory;

    @Parameter(property = "targetBrokerType")
    private String targetBrokerType;

    private void downloadJar(String artifactId,String jarFile) throws MojoExecutionException {
        log.info("Creating environment in "+installationDirectory);
        log.info("Downloading JMSPoster library into environment");
        if (installationDirectory==null){
            File cd = new File("").getAbsoluteFile();
            installationDirectory = cd.getAbsolutePath();
        }
        File installation = new File(installationDirectory);
        if (!installation.exists()){
                installation.mkdirs();
        }
        jarFile = installationDirectory + "//"+jarFile;


        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-dependency-plugin"),
                        version("2.5.1")
                ),
                goal("get"),
                configuration(
                        element(name("artifact"), artifactId),
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

    public void createBatchFile() {
        String fileName = null;
        if(SystemUtils.IS_OS_WINDOWS){
            fileName  =  installationDirectory+"//postMsg.bat";
        }
        else
        if(SystemUtils.IS_OS_LINUX){
            fileName  = installationDirectory+"//postMsg.sh";
        }
        else{
            fileName  = installationDirectory+"//postMsg.sh";
        }

        StringBuilder sb = new StringBuilder();


        sb.append("java ");
        sb.append("-cp \".\\;");

        if (targetBrokerType != null) {
            if (targetBrokerType.toUpperCase().equals("WEBLOGIC")) {
                sb.append(".\\lib\\wlthint3client.jar");
            } else if (targetBrokerType.toUpperCase().equals("ACTIVEMQ")) {
                sb.append(".\\lib;.\\lib\\Activemq.jar");
            }
            else if (targetBrokerType.toUpperCase().equals("HORNETQ-JBOSS7.1")) {
                sb.append(".\\lib\\jboss-client-7.1.0.Final.jar");
            }
        }

        sb.append(";.\\lib\\JMSPoster.jar;.\\lib\\jms-api.jar");
        sb.append(";.\\lib\\slf4j.jar;.\\lib\\logback-core.jar");
        sb.append(";.\\lib\\logback-classic.jar");

        sb.append("\" ");
        sb.append("com.jmsgoodies.JMSPoster connection.properties header.properties payload.txt");

        try {
            MessageUtils.saveFile(fileName,sb.toString());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void downloadBrokerJMSLibary() throws MojoExecutionException {
        if (targetBrokerType != null) {
            if (targetBrokerType.toUpperCase().equals("WEBLOGIC")) {

            } else if (targetBrokerType.toUpperCase().equals("ACTIVEMQ")) {
                downloadJar("org.apache.activemq:activemq-all:5.15.4","lib/Activemq.jar");
            }

        }
    }

    private void createActivemqProperties() {
        Properties jndiProperties = MessageUtils.recreateActiveMqJNDIProperties();
        MessageUtils.createPropertiesFile(jndiProperties,installationDirectory+"//"+JMSPoster.activeMQJNDIFile);

    }

    private void createPostingFiles(){
        Properties connectionProperties = null;
        if (targetBrokerType != null) {
            if (targetBrokerType.toUpperCase().equals("WEBLOGIC")) {
                connectionProperties = MessageUtils.recreateWeblogicConnectionProperties (queueName,localBroker);

            } else if (targetBrokerType.toUpperCase().equals("ACTIVEMQ")) {
                connectionProperties = MessageUtils.recreateActiveMQConnectionProperties(queueName,localBroker);
                connectionProperties.setProperty("url","tcp://localhost:61616");
            }
            else if (targetBrokerType.toUpperCase().equals("HORNETQ-JBOSS7.1")) {
                connectionProperties = MessageUtils.recreateHornet21JBoss71(queueName,localBroker);
            }
        }
        else{
            connectionProperties = MessageUtils.recreateActiveMQConnectionProperties(queueName,localBroker);
        }

        MessageUtils.createPropertiesFile(connectionProperties,installationDirectory+"//"+JMSPoster.connectionFile);

        Properties headerProperties = MessageUtils.getHeaderProperties();
        MessageUtils.createPropertiesFile(headerProperties,installationDirectory+"//"+ JMSPoster.headerFile);


        try {
            MessageUtils.saveFile(installationDirectory+"//"+JMSPoster.payloadFile,payloadMsg);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void execute()
            throws MojoExecutionException {
        downloadJar("com.jmsgoodies:JMSPoster:1.0-SNAPSHOT","lib/JMSPoster.jar");
        downloadBrokerJMSLibary();
        downloadJar("javax.jms:javax.jms-api:2.0.1","lib/jms-api.jar");
        downloadJar("org.slf4j:slf4j-api:1.7.21","lib/slf4j.jar");
        downloadJar("ch.qos.logback:logback-classic:1.1.2","lib/logback-classic.jar");
        downloadJar("ch.qos.logback:logback-core:1.1.2","lib/logback-core.jar");


        createBatchFile();
        createPostingFiles();
        if (targetBrokerType != null) {
            if (targetBrokerType.toUpperCase().equals("ACTIVEMQ")) {
                createActivemqProperties();
            }
        }

    }


    public void setInstallationDirectory(String installationDirectory) {
        this.installationDirectory = installationDirectory;
    }

    public String getInstallationDirectory() {

        return installationDirectory;
    }

    public String getTargetBrokerType() {
        return targetBrokerType;
    }

    public void setTargetBrokerType(String targetBrokerType) {
        this.targetBrokerType = targetBrokerType;
    }
}
