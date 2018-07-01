package com.jmsgoodies;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.NamingException;
import java.io.*;
import java.util.Properties;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@Slf4j
public class JMSPosterTest {
    public static String queueName = "jms/queue";
    public static String localBroker = "vm://localhost?broker.persistent=false";
    public static String connectionFile = "target/test-classes/connection.properties";
    public static String headerFile = "target/test-classes/header.properties";
    public static String payloadFile = "target/test-classes/payload.txt";
    public static String payloadMsg = "hello";


    public static class PosterThread implements Runnable {
        public void run() {

            Properties connectionProperties = getConnectionProperties();
            createPropertiesFile(connectionProperties,connectionFile);

            Properties headerProperties = getHeaderProperties();
            createPropertiesFile(headerProperties,headerFile);

            try {
                saveFile(payloadFile,payloadMsg);

            } catch (IOException e) {
                log.error(e.getMessage());
            }

            try {
                JMSPoster.postMesssage(connectionFile,headerFile,payloadFile);
            } catch (NamingException e) {
                log.error(e.getMessage());
            } catch (JMSException e) {
                log.error(e.getMessage());
            } catch (PosterException e) {
                log.error(e.getMessage());
            }

        }
    }

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    static public void createPropertiesFile(Properties injectProperties,String fileName){
        File propertiesFile = null;
        FileOutputStream fileOut = null;

        try{
            propertiesFile = new File(fileName);
            fileOut = new FileOutputStream(propertiesFile);

            injectProperties.store(fileOut, "Connection properties");
        }
        catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        if (fileOut!=null) {
            try {
                fileOut.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

    }

    static public Properties getConnectionProperties(){
        Properties injectProperties = new Properties();
        injectProperties.setProperty("connectionFactory", "ConnectionFactory");
        injectProperties.setProperty("queue", queueName);
        injectProperties.setProperty("initialContextFactory", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");

        injectProperties.setProperty("url", localBroker);
        injectProperties.setProperty("user", "");
        injectProperties.setProperty("password", "");
        return injectProperties;
    }

    static public Properties getHeaderProperties(){
        Properties injectProperties = new Properties();
        injectProperties.setProperty("SOURCE", "mySource");
        injectProperties.setProperty("TARGET", "myHeader");
        return injectProperties;
    }

    static public void saveFile(String fileName,String content)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);
        writer.close();
    }

    @org.junit.Test
    public void loadProperties() {
        File propertiesFile = null;
        String fileName = "target/test-classes/connection.properties";
        Properties injectProperties = getConnectionProperties();

        Properties extractedProperties = null;
        propertiesFile = new File(fileName);
        if (propertiesFile.exists())
            propertiesFile.delete();

        createPropertiesFile(injectProperties,fileName);
        try {
            extractedProperties =  JMSPoster.loadProperties(propertiesFile.getPath());
        } catch (PosterException e) {
            e.printStackTrace();
        }

        if (propertiesFile.exists())
            propertiesFile.delete();


        assertThat(injectProperties,is(extractedProperties));
    }

    @org.junit.Test
    public void main() {
    }


    @org.junit.Test
    public void postMesssage() {
        String msg = ActivemqMsgReader.readMsg(queueName);
        assertEquals(msg,payloadMsg);
    }






}