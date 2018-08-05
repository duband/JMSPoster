package com.github.duband.jmsgoodies;


import javax.jms.Message;
/**
 * MIT Copyright
 * Code produced by Dub Andrei
 * http://javagoogleappspot.blogspot.com
 */

public interface MessageHandler {

    public String handleMessage(Message message);
}
