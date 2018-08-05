package com.github.duband.jmsgoodies;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Properties;
/**
 * MIT Copyright
 * Code produced by Dub Andrei
 * http://javagoogleappspot.blogspot.com
 */

@Slf4j
public class AdjustActiveMQJNDIFile {
    public static void main(String[] args) {
        String connectionPropFile = args[0];

        File cd = new File("").getAbsoluteFile();
        String currentDirectory = cd.getAbsolutePath();
        log.info("Current directory set to:" + currentDirectory);
        log.info("Moving properties from " + connectionPropFile + " to " + currentDirectory + "//" + JMSPoster.activeMQJNDIFile);
        if (connectionPropFile != null) {
            try {
                Properties connectionProp = MessageUtils.loadProperties(currentDirectory + "//" + connectionPropFile);
            } catch (PosterException e) {
                log.error(e.getMessage());
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    log.error(stackTraceElement.toString());
                }

            }
        }
    }

    public static String createActiveMQJNDIFile(Properties connectionProp) {
        File cd = new File("").getAbsoluteFile();
        String currentDirectory = cd.getAbsolutePath();

        Properties jndiProperties = MessageUtils.recreateActiveMqJNDIProperties();

        String url = connectionProp.getProperty("url");
        jndiProperties.put("java.naming.provider.url", url);

        String connectionFactory = connectionProp.getProperty("connectionFactory");
        jndiProperties.put("connectionFactoryNames", connectionFactory);

        removeQueueProperties(jndiProperties);

        String queueName = connectionProp.getProperty("queue");
        jndiProperties.put("queue." + queueName, queueName);
        File directory = new File(currentDirectory + "/" + JMSPoster.activeMQJNDIDirectory);
        if (!directory.exists()){
            directory.mkdirs();
        }

        MessageUtils.createPropertiesFile(jndiProperties, currentDirectory + "/" + JMSPoster.activeMQJNDIFile);
        String holdingDirectory = currentDirectory+"/"+JMSPoster.activeMQJNDIDirectory;
        log.info("Created Activemq JNDI properties file in directory "+holdingDirectory);
        return holdingDirectory;
    }


    public static void addFileTOClassLoader(String directoryName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, MalformedURLException {
        File directory = new File(directoryName);
        URL url = directory.toURI().toURL();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(urlClassLoader, new Object[]{url});
    }

    private static void removeQueueProperties(Properties properties) {
        Properties clonedProperties = new Properties();
        clonedProperties.putAll(properties);
        for (Map.Entry property : clonedProperties.entrySet()) {
            if (((String) property.getKey()).contains("queue.")) {
                properties.remove(property.getKey());
            }
        }
    }


    public static void pushJNDIFileTOClassLoader(Properties connectionProperties) {
            String jndiDirectory = AdjustActiveMQJNDIFile.createActiveMQJNDIFile(connectionProperties);
            try {
                AdjustActiveMQJNDIFile.addFileTOClassLoader(jndiDirectory);
            } catch (MalformedURLException e) {
                log.error(e.getMessage());
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            } catch (InvocationTargetException e) {
                log.error(e.getMessage());
            } catch (NoSuchMethodException e) {
                log.error(e.getMessage());
            }

        }
}
