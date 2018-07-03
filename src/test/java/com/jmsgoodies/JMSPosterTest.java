package com.jmsgoodies;

import lombok.extern.slf4j.Slf4j;

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

            Properties connectionProperties = MessageUtils.getActiveMQConnectionProperties(queueName,localBroker);
            MessageUtils.createPropertiesFile(connectionProperties,connectionFile);

            Properties headerProperties = MessageUtils.getHeaderProperties();
            MessageUtils.createPropertiesFile(headerProperties,headerFile);

            try {
                MessageUtils.saveFile(payloadFile,payloadMsg);

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



    @org.junit.Test
    public void loadProperties() {
        File propertiesFile = null;
        String fileName = "target/test-classes/connection.properties";
        Properties injectProperties = MessageUtils.getActiveMQConnectionProperties(queueName,localBroker);

        Properties extractedProperties = null;
        propertiesFile = new File(fileName);
        if (propertiesFile.exists())
            propertiesFile.delete();

        MessageUtils.createPropertiesFile(injectProperties,fileName);
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
    public void postMesssage() {
        String msg = ActivemqMsgReader.readMsg(queueName);
        assertEquals(msg,payloadMsg);
    }






}