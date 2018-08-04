package com.jmsgoodies;


import javax.jms.Message;

public interface MessageHandler {

    public String handleMessage(Message message);
}
