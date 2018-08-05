package com.github.duband.jmsgoodies.example.spring;


import com.github.duband.jmsgoodies.JMSPoster;
import com.github.duband.jmsgoodies.MessageUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Properties;
/**
 * MIT Copyright
 * Code produced by Dub Andrei
 * http://javagoogleappspot.blogspot.com
 */

public class SpringIntegrationExample {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

        ctx.register(AppConfig.class);
        ctx.refresh();

        String queueName = "jms/queue";
        String localBroker = "tcp://localhost:61616";
        JMSPoster jmsPoster = (JMSPoster) ctx.getBean("jmsPoster");
        sendMessage(jmsPoster, localBroker, queueName);
        readMessage(jmsPoster, localBroker, queueName);
    }

    public static void sendMessage(JMSPoster jmsPoster, String localBroker, String queueName) {

        String payload = "hello";

        Properties connectionProperties = MessageUtils.recreateActiveMQConnectionProperties(queueName, localBroker);
        Properties headerProperties = MessageUtils.recreateExampleHeaderProperties();
        jmsPoster.postMesssage(connectionProperties, headerProperties, payload);

    }


    public static void readMessage(JMSPoster jmsPoster, String localBroker, String queueName) {
        Properties connectionProperties = MessageUtils.recreateActiveMQConnectionProperties(queueName, localBroker);
        String msg = jmsPoster.readMsg(connectionProperties);
    }

}