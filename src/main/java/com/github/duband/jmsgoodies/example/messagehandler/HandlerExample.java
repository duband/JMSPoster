package com.github.duband.jmsgoodies.example.messagehandler;

import com.github.duband.jmsgoodies.MessageHandler;
import lombok.extern.slf4j.Slf4j;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * MIT Copyright
 * Code produced by Dub Andrei
 * http://javagoogleappspot.blogspot.com
 */

@Slf4j
public class HandlerExample implements MessageHandler {
    public String handleMessage(Message message) {
        String msg = null;
        try{
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                textMessage.acknowledge();
                msg = textMessage.getText();
                log.info("Example handler: Received: " + msg);
            } else {
                log.info("Example handler: Received: " + message);
            }
        } catch (JMSException e) {
            log.error(e.getMessage());
        }
        return msg;

    }
}
