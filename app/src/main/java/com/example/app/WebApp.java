package com.example.app;

import java.util.Map;

import org.eclipse.collections.impl.factory.Maps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;

@SpringBootApplication
@EnableWebFlux
public class WebApp {

  public static void main(String[] args) {
    SpringApplication.run(WebApp.class, args);
  }

  @Bean
  HandlerMapping handlerMapping() {
    final Map<String, WebSocketHandler> handlerMappings =
        Maps.immutable
            .<String, WebSocketHandler>of("/ws/hello", new HelloWebSocketHandler())
            .castToMap();
    final SimpleUrlHandlerMapping urlHandlerMapping = new SimpleUrlHandlerMapping();
    urlHandlerMapping.setUrlMap(handlerMappings);
    urlHandlerMapping.setOrder(-1);
    return urlHandlerMapping;
  }

  @Bean
  WebSocketHandlerAdapter handlerAdapter() {
    return new WebSocketHandlerAdapter(webSocketService());
  }

  @Bean
  WebSocketService webSocketService() {
    return new HandshakeWebSocketService(new ReactorNettyRequestUpgradeStrategy());
  }
}
