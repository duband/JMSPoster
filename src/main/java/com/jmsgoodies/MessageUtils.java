package com.jmsgoodies;

import lombok.extern.slf4j.Slf4j;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.*;
import java.util.Properties;

@Slf4j
public class MessageUtils {
    public static class ConnectionSuite{
        private String initialContextFactory = null;
        private Context context = null;
        private ConnectionFactory connectionFactory = null;
        private Queue destination = null;
        private Connection connection = null;

        public ConnectionSuite(String initialContextFactory, Context context, ConnectionFactory connectionFactory, Queue destination, Connection connection) {
            this.initialContextFactory = initialContextFactory;
            this.context = context;
            this.connectionFactory = connectionFactory;
            this.destination = destination;
            this.connection = connection;
        }

        public String getInitialContextFactory() {
            return initialContextFactory;
        }

        public Context getContext() {
            return context;
        }

        public ConnectionFactory getConnectionFactory() {
            return connectionFactory;
        }

        public Queue getDestination() {
            return destination;
        }

        public Connection getConnection() {
            return connection;
        }
    }
    static public void createPropertiesFile(Properties injectProperties, String fileName){
        File propertiesFile = null;
        FileOutputStream fileOut = null;

        try{
            propertiesFile = new File(fileName);
            fileOut = new FileOutputStream(propertiesFile);

            injectProperties.store(fileOut, "Properties file");
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

    static public Properties recreateActiveMQConnectionProperties(String queueName, String activeMQlocalBroker){
        Properties injectProperties = new Properties();
        injectProperties.setProperty("connectionFactory", "ConnectionFactory");
        injectProperties.setProperty("queue", queueName);
        injectProperties.setProperty("initialContextFactory", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");

        injectProperties.setProperty("url", activeMQlocalBroker);
        injectProperties.setProperty("user", "");
        injectProperties.setProperty("password", "");
        return injectProperties;
    }

    static public Properties recreateWeblogicConnectionProperties(String queueName, String localBroker){
        Properties injectProperties = new Properties();
        injectProperties.setProperty("connectionFactory", "ConnectionFactory");
        injectProperties.setProperty("queue", queueName);
        injectProperties.setProperty("initialContextFactory", "weblogic.jndi.WLInitialContextFactory");

        injectProperties.setProperty("url", "t3://localhost:7001");
        injectProperties.setProperty("user", "");
        injectProperties.setProperty("password", "");
        return injectProperties;
    }

    static public Properties recreateActiveMqJNDIProperties(){
        Properties injectProperties = new Properties();
        injectProperties.setProperty("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        injectProperties.setProperty("java.naming.provider.url", "tcp://localhost:61616");
        injectProperties.setProperty("connectionFactoryNames","ConnectionFactory");
        injectProperties.setProperty("queue.jms/queue","jms/queue");
        return injectProperties;
    }

    static public Properties getHeaderProperties(){
        Properties injectProperties = new Properties();
        injectProperties.setProperty("SOURCE", "mySource");
        injectProperties.setProperty("TARGET", "myHeader");
        return injectProperties;
    }


    static public String readPayloadFile(String path) {
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


    static public void saveFile(String fileName,String content)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);
        writer.close();
    }

    public static Properties loadProperties(String path) throws PosterException {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            log.info("loading properties file "+path);
            File file = new File(path);
            if (!file.exists()){
                log.error("File "+path+" does not exist");
                throw new PosterException("File "+path+" does not exist");
            }
            input = new FileInputStream(path);


            prop.load(input);
        } catch (IOException ex) {
            log.error(ex.getMessage());

            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }


            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return prop;
    }

    public static ConnectionSuite createConnectionSuit(Properties connectionProp) throws NamingException, JMSException,PosterException {

        String initialContextFactory = null;
        Context context = null;
        ConnectionFactory connectionFactory = null;
        Queue destination = null;
        Connection connection = null;

        AdjustActiveMQJNDIFile.pushJNDIFileTOClassLoader(connectionProp);

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

        ConnectionSuite connectionSuite = new ConnectionSuite(initialContextFactory,context,connectionFactory,destination,connection);
        return connectionSuite;
    }
}
