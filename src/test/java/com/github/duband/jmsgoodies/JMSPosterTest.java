package com.github.duband.jmsgoodies;

import lombok.extern.slf4j.Slf4j;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
/**
 * MIT Copyright
 * Code produced by Dub Andrei
 * http://javagoogleappspot.blogspot.com
 */

@Slf4j
public class JMSPosterTest {
    public static String queueName = "jms/queue";
    public static String localBroker = "vm://localhost?broker.persistent=false";
    public static String connectionFile = "target/test-classes/connection.properties";
    public static String headerFile = "target/test-classes/header.properties";
    public static String payloadFile = "target/test-classes/payload.txt";
    public static String payloadMsg = "hello";


    public static class PosterByUsingFilesThread implements Runnable {
        public void run() {
            JMSPoster jmsPoster = JMSPoster.getInstance();
            Properties connectionProperties = MessageUtils.recreateActiveMQConnectionProperties(queueName,localBroker);
            MessageUtils.createPropertiesFile(connectionProperties,connectionFile);

            Properties headerProperties = MessageUtils.recreateExampleHeaderProperties();
            MessageUtils.createPropertiesFile(headerProperties,headerFile);

            try {
                MessageUtils.saveFile(payloadFile,payloadMsg);

            } catch (IOException e) {
                log.error(e.getMessage());
            }

            try {
                jmsPoster.postMesssage(connectionFile,headerFile,payloadFile);
            } catch (NamingException e) {
                log.error(e.getMessage());
            } catch (JMSException e) {
                log.error(e.getMessage());
            } catch (PosterException e) {
                log.error(e.getMessage());
            }

        }
    }


    public static class PosterThread implements Runnable {
        public void run() {
            JMSPoster jmsPoster = JMSPoster.getInstance();
            Properties connectionProperties = MessageUtils.recreateActiveMQConnectionProperties(queueName,localBroker);
            Properties headerProperties = MessageUtils.recreateExampleHeaderProperties();
            jmsPoster.postMesssage(connectionProperties,headerProperties,payloadMsg);

        }
    }

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }



    @org.junit.Test
    public void loadProperties() {
        File propertiesFile = null;
        String fileName = "target/test-classes/connection.properties";
        Properties injectProperties = MessageUtils.recreateActiveMQConnectionProperties(queueName,localBroker);

        Properties extractedProperties = null;
        propertiesFile = new File(fileName);
        if (propertiesFile.exists())
            propertiesFile.delete();

        MessageUtils.createPropertiesFile(injectProperties,fileName);
        try {
            extractedProperties =  MessageUtils.loadProperties(propertiesFile.getPath());
        } catch (PosterException e) {
            e.printStackTrace();
        }

        if (propertiesFile.exists())
            propertiesFile.delete();


        assertThat(injectProperties,is(extractedProperties));
    }



    @org.junit.Test
    public void postMesssageByUsingFiles() {
        Thread thread = new Thread(new PosterByUsingFilesThread());
        postMesssage(thread);

    }



    @org.junit.Test
    public void postMesssage() {
        Thread thread = new Thread(new PosterThread());
        postMesssage(thread);
    }

    public void postMesssage(Thread thread) {
        JMSPoster jmsPoster = JMSPoster.getInstance();
        Properties connectionProperties = MessageUtils.recreateActiveMQConnectionProperties(queueName,localBroker);
        thread.start();
        String msg = jmsPoster.readMsg(connectionProperties);
        assertEquals(msg,payloadMsg);
    }


}