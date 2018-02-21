package com.example.app;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class HelloWebSocketHandler implements WebSocketHandler {

  @Override
  public Mono<Void> handle(final WebSocketSession session) {
    log.info("new connection: {}", session.getId());
    final Flux<WebSocketMessage> result =
        session
            .receive() // メッセージ受信後の動作を定義
            .delaySubscription(Duration.ofSeconds(2L)) // 受信後2秒待つ
            .map(WebSocketMessage::getPayloadAsText) // メッセージをテキストで取り出す
            .flatMap(text -> this.handle(text, session.bufferFactory()));
    return session.send(result);
  }

  private Mono<WebSocketMessage> handle(final String input, final DataBufferFactory factory) {
    final String message = "Hello, " + input;
    log.info("coming: {}, going: {}", input, message);
    final DataBuffer buffer = factory.wrap(message.getBytes(StandardCharsets.UTF_8));
    final WebSocketMessage webSocketMessage =
        new WebSocketMessage(WebSocketMessage.Type.TEXT, buffer);
    return Mono.just(webSocketMessage);
  }
}
