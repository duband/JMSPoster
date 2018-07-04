package com.jmsgoodies;

import lombok.extern.slf4j.Slf4j;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.*;
import java.util.Map;
import java.util.Properties;


/**
 * Copyright belongs to Dub Andrei
 */
@Slf4j
public class JMSPoster {

    public static String connectionFile = "connection.properties";
    public static String activeMQJNDIFile = "lib/jndi.properties";
    public static String headerFile = "header.properties";
    public static String payloadFile = "payload.txt";


    private static String readPayloadFile(String path) {
        StringBuilder sb = new StringBuilder();
        BufferedReader in = null;
        try {
            File fileDir = new File(path);

            in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String str;

            while ((str = in.readLine()) != null) {
                sb.append(str);
            }

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws NamingException, JMSException,PosterException {
        postMesssage(args[0], args[1], args[2]);
    }


    public static void postMesssage(String connectionPropFile, String msgPropertiesFile, String payloadFile)
            throws NamingException, JMSException,PosterException {
        File cd = new File("").getAbsoluteFile();
        String currentDirectory = cd.getAbsolutePath();
        if (connectionPropFile!=null){
            Properties connectionProp = MessageUtils.loadProperties(currentDirectory+"//"+connectionPropFile);
            if (msgPropertiesFile!=null){
                Properties msgProp = MessageUtils.loadProperties(currentDirectory+"//"+msgPropertiesFile);
                if (payloadFile!=null){
                    postMesssage(connectionProp, msgProp, currentDirectory+"//"+payloadFile);
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


    private static void postMesssage(Properties connectionProp, Properties msgProp, String payloadFile)
            throws NamingException, JMSException {
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        Queue destination = null;
        TextMessage message = null;
        Context context = null;
        String initialContextFactory = null;
        try {
            String user = connectionProp.getProperty("user");
            String password = connectionProp.getProperty("password");
            String connectionFactoryName = connectionProp.getProperty("connectionFactory");
            if (connectionFactoryName==null)
                throw new PosterException("Connection factory name is missing from properties files");
            String queueName = connectionProp.getProperty("queue");
            if (queueName==null)
                throw new PosterException("Queue name is missing from properties files");

            initialContextFactory = connectionProp.getProperty("initialContextFactory");
            if (initialContextFactory==null)
                throw new PosterException("InitialContextFactory name is missing from properties files");

            String url = connectionProp.getProperty("url");
            if (url==null)
                throw new PosterException("URL name is missing from properties files");



            Properties env = new Properties();

            env.put("java.naming.factory.initial", initialContextFactory);
            env.put("java.naming.provider.url", url);

            env.put("java.naming.security.principal", user);
            env.put("java.naming.security.credentials", password);

            context = new InitialContext(env);

            connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryName);

            log.info("lookup: " + connectionFactoryName + " success!");

            destination = (Queue) context.lookup(queueName);

            log.info("lookup: " + queueName + " success!");


            log.info("connectionFactory.createContext success!");

            connection = connectionFactory.createConnection(user, password);

            log.info("connectionFactory.createConnection success!");

            session = connection.createSession(false, 1);
            producer = session.createProducer(destination);

            log.info("session.createProducer success!");

            connection.start();

            String payload = readPayloadFile(payloadFile);


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
                log.error("Could be an client ActiveMQ configuration issue.Make sure you duplicated your connection parameters in the properties file /lib/jndi.properties");
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

}
