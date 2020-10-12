package com.zain.jo.demo.listener;

import com.zain.jo.demo.config.JmsConfig;
import com.zain.jo.demo.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloMessageListener {

    private final JmsTemplate jmsTemplate;
    @JmsListener(destination = JmsConfig.MY_QUEUE)
    public void listen(@Payload HelloWorldMessage helloWorldMessage,
                       @Headers MessageHeaders headers, Message message){

        System.out.println("I Got a Message!!!!!");
        System.out.println("HeaderInfo...");

        System.out.println(helloWorldMessage);


        // uncomment and view to see retry count in debugger
        // throw new RuntimeException("foo");

    }

    @JmsListener(destination = JmsConfig.ANOTHER_QUEUUE)
    public void listenForSecondQueuue(@Payload HelloWorldMessage helloWorldMessage,
                       @Headers MessageHeaders headers, Message message) throws JMSException {

        HelloWorldMessage messageToSerialize = HelloWorldMessage.builder()
                .id(UUID.randomUUID())
                .message("Listener Recive and Sent"  + new Date(System.currentTimeMillis()))
                .build();

//        both of these will work :
//        jmsTemplate.convertAndSend((Destination) message.getJMSReplyTo(), messageToSerialize);
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), messageToSerialize);
    }


}
