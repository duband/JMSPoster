package com.github.duband.jmsgoodies;

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
public class LogMessage  implements MessageHandler {
    public String handleMessage(Message message) {
        String msg = null;
        try{
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                textMessage.acknowledge();
                msg = textMessage.getText();
                log.info("Received: " + msg);
            } else {
                log.info("Received: " + message);
            }
        } catch (JMSException e) {
            log.error(e.getMessage());
        }
        return msg;

    }
}
