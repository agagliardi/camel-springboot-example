package com.redhat.reproducer;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;

@Configuration
public class Configurer {

    @Bean
    public ConnectionFactory connectionFactory(@Value("${brokerURL}") String brokerURL) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);
        factory.setUser("testuser");
        factory.setPassword("12345");
        return new CachingConnectionFactory((ConnectionFactory) factory);
    }
    
    @Bean
    public Queue queue(@Value("$destination") String destination){
        return new ActiveMQQueue(destination);
    }

    @Bean
    public MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory, MessageConsumer messageConsumer, Queue queue) {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setDestination(queue);
        container.setMessageListener(messageConsumer);
        return container;
    }
}
