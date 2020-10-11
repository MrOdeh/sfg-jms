package com.zain.jo.demo.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zain.jo.demo.config.JmsConfig;
import com.zain.jo.demo.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 1000)
    public void sendMessage(){
        System.out.println("Im Sending a message! ");

        HelloWorldMessage message = HelloWorldMessage.builder()
                .id(UUID.randomUUID())
                .message("Hello-World" + new Date(System.currentTimeMillis()))
                .build();

        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);

        System.out.println("Message Sent! -- " + message.toString());
    }

    @Scheduled(fixedRate = 5000)
    public void sendAndReciveMessage() throws JMSException, JsonProcessingException {
        System.out.println("Im Sending a message! ");

        HelloWorldMessage message = HelloWorldMessage.builder()
                .id(UUID.randomUUID())
                .message("Hello-World" + new Date(System.currentTimeMillis()))
                .build();

        Message recivedMessage = jmsTemplate.sendAndReceive(JmsConfig.ANOTHER_QUEUUE, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message helloMessage = null;
                try {
                    helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                    helloMessage.setStringProperty("_type", "com.zain.jo.demo.model.HelloWorldMessage");

                    System.out.println("Sending Hello!");
                    return helloMessage;
                } catch (JsonProcessingException e) {
                    throw new JMSException("Error Happend During Mapping The Object And Binding...");
                }
            }
        });

        System.out.println("Message Sent! -- " + message.toString());
        // to read as Object
        //System.out.println("RecivedMessage -- " + objectMapper.readValue(recivedMessage.getBody(String.class), HelloWorldMessage.class));
        // to read as plan String test
        System.out.println("RecivedMessage -- " + recivedMessage.getBody(String.class));
    }
}
