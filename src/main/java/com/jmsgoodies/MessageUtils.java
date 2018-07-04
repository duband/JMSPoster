package com.jmsgoodies;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

@Slf4j
public class MessageUtils {
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

    static public Properties getActiveMQConnectionProperties(String queueName, String activeMQlocalBroker){
        Properties injectProperties = new Properties();
        injectProperties.setProperty("connectionFactory", "ConnectionFactory");
        injectProperties.setProperty("queue", queueName);
        injectProperties.setProperty("initialContextFactory", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");

        injectProperties.setProperty("url", activeMQlocalBroker);
        injectProperties.setProperty("user", "");
        injectProperties.setProperty("password", "");
        return injectProperties;
    }

    static public Properties getWeblogicConnectionProperties(String queueName, String localBroker){
        Properties injectProperties = new Properties();
        injectProperties.setProperty("connectionFactory", "ConnectionFactory");
        injectProperties.setProperty("queue", queueName);
        injectProperties.setProperty("initialContextFactory", "weblogic.jndi.WLInitialContextFactory");

        injectProperties.setProperty("url", "t3://localhost:7001");
        injectProperties.setProperty("user", "");
        injectProperties.setProperty("password", "");
        return injectProperties;
    }

    static public Properties getActiveMqJNDIProperties(){
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

}
