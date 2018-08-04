package com.github.duband.jmsgoodies.example;

import com.github.duband.jmsgoodies.JMSPoster;
import com.github.duband.jmsgoodies.MessageUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
public class ConsumerExample {

    public static void main(String[] args){
        String queueName = "jms/queue";
        String localBroker = "tcp://localhost:61616";

        JMSPoster jmsPoster = JMSPoster.getInstance();
        Properties connectionProperties = MessageUtils.recreateActiveMQConnectionProperties(queueName,localBroker);
        String msg = jmsPoster.readMsg(connectionProperties);
        log.info("Received message "+msg);
    }
}
