package com.practice.smallcommunity.testutils.interfaces;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import java.security.Principal;
import java.util.HashMap;

public abstract class WebSocketUtils {

    public static <T> Message<T> createSubscribeMessage(String destination, Principal authentication, T payload) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setDestination(destination);
        headers.setSubscriptionId("0");
        headers.setSessionId("0");
        headers.setUser(authentication);
        headers.setSessionAttributes(new HashMap<>());
        return MessageBuilder.createMessage(payload, headers.getMessageHeaders());
    }

    public static <T> Message<T> createSendMessage(String destination, Principal authentication, T payload) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
        headers.setDestination(destination);
        headers.setSessionId("0");
        headers.setUser(authentication);
        headers.setSessionAttributes(new HashMap<>());
        return MessageBuilder.createMessage(payload, headers.getMessageHeaders());
    }
}
