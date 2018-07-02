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

    static public Properties getConnectionProperties(String queueName,String localBroker){
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
}
