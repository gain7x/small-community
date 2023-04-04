package com.practice.smallcommunity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            .simpSubscribeDestMatchers("/user/topic/inquiry").authenticated()
            .simpSubscribeDestMatchers("/topic/**", "/queue/**").hasRole("ADMIN")
            .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.DISCONNECT).permitAll()
            .simpDestMatchers("/app/**").authenticated()
            .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
