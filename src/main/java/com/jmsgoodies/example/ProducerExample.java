package com.jmsgoodies.example;

import com.jmsgoodies.JMSPoster;
import com.jmsgoodies.MessageUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
public class ProducerExample {
    /**
     * We assume you have a broker running on tcp://localhost:61616, if not change it to the appropiate address.
     *
     * @param args
     */
    public static void main(String[] args){
        String queueName = "jms/queue";
        String localBroker = "tcp://localhost:61616";
        String payload = "hello";
        JMSPoster jmsPoster = JMSPoster.getInstance();
        Properties connectionProperties = MessageUtils.recreateActiveMQConnectionProperties(queueName,localBroker);
        Properties headerProperties = MessageUtils.getHeaderProperties();
        jmsPoster.postMesssage(connectionProperties,headerProperties,payload);
    }
}
