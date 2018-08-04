package com.jmsgoodies.example.spring;


import com.jmsgoodies.JMSPoster;
import com.jmsgoodies.MessageUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Properties;

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
        Properties headerProperties = MessageUtils.getHeaderProperties();
        jmsPoster.postMesssage(connectionProperties, headerProperties, payload);

    }


    public static void readMessage(JMSPoster jmsPoster, String localBroker, String queueName) {
        Properties connectionProperties = MessageUtils.recreateActiveMQConnectionProperties(queueName, localBroker);
        String msg = jmsPoster.readMsg(connectionProperties);
    }

}