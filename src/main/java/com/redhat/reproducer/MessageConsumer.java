package com.redhat.reproducer;

import org.springframework.stereotype.Component;

import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@Component
public class MessageConsumer implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                System.out.println("📩 Received message: " + ((TextMessage) message).getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
