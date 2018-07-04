package com.jmsgoodies;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class AdjustActiveMQJNDIFile {
    public static void main(String[] args) {
        String connectionPropFile = args[0];

        File cd = new File("").getAbsoluteFile();
        String currentDirectory = cd.getAbsolutePath();
        log.info("Current directory set to:"+currentDirectory);
        log.info("Moving properties from "+connectionPropFile+" to "+currentDirectory+"//"+JMSPoster.activeMQJNDIFile);

        if (connectionPropFile != null) {
            try {
                Properties connectionProp = MessageUtils.loadProperties(currentDirectory + "//" + connectionPropFile);
                Properties jndiProperties = MessageUtils.getActiveMqJNDIProperties();

                String url = connectionProp.getProperty("url");
                jndiProperties.put("java.naming.provider.url",url);

                String connectionFactory = connectionProp.getProperty("connectionFactory");
                jndiProperties.put("connectionFactoryNames",connectionFactory);

                removeQueueProperties(jndiProperties);

                String queueName = connectionProp.getProperty("queue");
                jndiProperties.put("queue."+queueName,queueName);

                MessageUtils.createPropertiesFile(jndiProperties,currentDirectory+"//"+JMSPoster.activeMQJNDIFile);



            } catch (PosterException e) {
                log.error(e.getMessage());
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    log.error(stackTraceElement.toString());
                }

            }

        }

    }

    private static void removeQueueProperties(Properties properties){
        Properties clonedProperties = new Properties();
        clonedProperties.putAll(properties);
        for (Map.Entry property : clonedProperties.entrySet()){
            if (((String)property.getKey()).contains("queue.")){
                properties.remove(property.getKey());
            }
        }
    }

}
