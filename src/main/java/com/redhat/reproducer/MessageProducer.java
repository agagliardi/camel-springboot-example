package com.redhat.reproducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;

@Service
public class MessageProducer {
    

    @Value("greeting")
    private String greeting;

    @Autowired
    private Queue queue;

    private JmsTemplate jmsTemplate;

    public MessageProducer(ConnectionFactory cf){

        jmsTemplate = new JmsTemplate(cf);
    }

    @Scheduled(fixedRateString = "${timer.period}")
    public void sendMessage() {
        jmsTemplate.convertAndSend(queue, greeting);
        System.out.println("✅ Sent message: " + greeting);
    }



}
