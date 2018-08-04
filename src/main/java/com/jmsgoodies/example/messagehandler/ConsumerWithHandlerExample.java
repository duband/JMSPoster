package com.jmsgoodies.example.messagehandler;

import com.jmsgoodies.JMSPoster;
import com.jmsgoodies.MessageHandler;
import com.jmsgoodies.MessageUtils;
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
