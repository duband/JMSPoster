package com.jmsgoodies;

import lombok.extern.slf4j.Slf4j;

import javax.jms.*;
import javax.naming.NamingException;
import java.io.File;
import java.util.Map;
import java.util.Properties;


/**
 * Copyright belongs to Dub Andrei
 */
@Slf4j
public class JMSPoster {

    public static String connectionFile = "connection.properties";
    public static String activeMQJNDIDirectory = "activemq-conf";
    public static String activeMQJNDIFile = activeMQJNDIDirectory+"/jndi.properties";
    public static String headerFile = "header.properties";
    public static String payloadFile = "payload.txt";

    private static JMSPoster jmsPoster;

    public static JMSPoster getInstance(){
        if (jmsPoster==null)
            jmsPoster = new JMSPoster();
        return jmsPoster;
    }


    public static void main(String[] args) throws NamingException, JMSException,PosterException {
        JMSPoster jmsPoster = JMSPoster.getInstance();
        jmsPoster.postMesssage(args[0], args[1], args[2]);
    }


    public void postMesssage(String connectionPropFile, String msgPropertiesFile, String payloadFile)
            throws NamingException, JMSException,PosterException {
        File cd = new File("").getAbsoluteFile();
        String currentDirectory = cd.getAbsolutePath();
        if (connectionPropFile!=null){
            Properties connectionProp = MessageUtils.loadProperties(currentDirectory+"//"+connectionPropFile);
            if (msgPropertiesFile!=null){
                Properties msgProp = MessageUtils.loadProperties(currentDirectory+"//"+msgPropertiesFile);
                if (payloadFile!=null){
                    String payload = MessageUtils.readPayloadFile(payloadFile);
                    postMesssage(connectionProp, msgProp, payload);
                }
                else{
                    log.error("Please provide a payload file");
                }
            }
            else{
                log.error("Please provide a header properties file");
            }
        }
        else
            log.error("Please provide a connection properties file");
    }


    public void postMesssage(Properties connectionProp, Properties msgProp, String payload){

        Session session = null;
        MessageProducer producer = null;
        TextMessage message = null;
        Connection connection = null;
        String initialContextFactory = null;
        Queue destination = null;

        try {

            MessageUtils.ConnectionSuite connectionSuite = MessageUtils.createConnectionSuit(connectionProp);
            connection = connectionSuite.getConnection();
            destination = connectionSuite.getDestination();
            initialContextFactory = connectionSuite.getInitialContextFactory();

            log.info("connectionFactory.createConnection success!");

            session = connection.createSession(false, 1);
            producer = session.createProducer(destination);

            log.info("session.createProducer success!");

            connection.start();


            message = session.createTextMessage(payload);

            for (Map.Entry<Object, Object> entry : msgProp.entrySet()) {
                message.setStringProperty((String) entry.getKey(), (String) entry.getValue());
            }

            producer.send(message);
            log.info("Working!");
            producer.close();

        }
        catch (NamingException e){
            if (initialContextFactory!=null && initialContextFactory.contains("activemq")){
                log.error("Lookup issue. Either queue or connection factory were not found..Make sure you duplicated your connection parameters in the properties file "+JMSPoster.activeMQJNDIDirectory+"/jndi.properties");
            }
            log.error(e.getMessage());
            for (StackTraceElement stackTraceElement:e.getStackTrace()){
                log.error(stackTraceElement.toString());
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
            for (StackTraceElement stackTraceElement:e.getStackTrace()){
                log.error(stackTraceElement.toString());
            }

        } finally {

            // Close the message producer
            try {
                if (producer != null) producer.close();
            } catch (JMSException e) {
                log.error("Closing producer error: " + e);
            }

            // Close the session
            try {
                if (session != null) session.close();
            } catch (JMSException e) {
                log.error("Closing session error: " + e);
            }

            // Close the connection
            try {
                if (connection != null)
                    connection.close();
            } catch (JMSException e) {
                log.error("Closing connection error: " + e);
            }
        }
    }

    public  String readMsg(Properties connectionProp){
        MessageHandler messageHandler = new LogMessage();
        String msg = readMsg(connectionProp,messageHandler);
        return msg;
    }

    public String readMsg(Properties connectionProp,MessageHandler messageHandler){
        String msg = null;
        Session session = null;
        Connection connection = null;
        String initialContextFactory = null;
        Queue destination = null;
        try {

            MessageUtils.ConnectionSuite connectionSuite = MessageUtils.createConnectionSuit(connectionProp);
            connection = connectionSuite.getConnection();
            destination = connectionSuite.getDestination();
            initialContextFactory = connectionSuite.getInitialContextFactory();

            // Create a Connection
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            String queueName = connectionProp.getProperty("queue");

            // Create the destination (Topic or Queue)
            destination = session.createQueue(queueName);

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            // receive message
            msg =   consumeMessage(consumer,messageHandler);

            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            log.error("Caught: " + e);
        }
        return msg;
    }

    private static String consumeMessage(MessageConsumer consumer,MessageHandler messageHandler) throws JMSException{
        String msg = null;
        // Wait for a message
        Message message = consumer.receive();
        MessageHandler messageListener = null;
        msg = messageHandler.handleMessage(message);
        return msg;

    }

}
