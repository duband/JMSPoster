package com.github.duband.jmsgoodies.example.messagehandler;

import com.github.duband.jmsgoodies.JMSPoster;
import com.github.duband.jmsgoodies.MessageUtils;
import com.github.duband.jmsgoodies.MessageHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
public class ConsumerWithHandlerExample {

    public static void main(String[] args){
        String queueName = "jms/queue";
        String localBroker = "tcp://localhost:61616";

        JMSPoster jmsPoster = JMSPoster.getInstance();
        Properties connectionProperties = MessageUtils.recreateActiveMQConnectionProperties(queueName,localBroker);
        MessageHandler messageHandler = new HandlerExample();
        String msg = jmsPoster.readMsg(connectionProperties,messageHandler);
        log.info("Received message "+msg);
    }
}
