package com.example.client;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableWebFlux
public class ClientApp {

    private static final Logger log = LoggerFactory.getLogger(ClientApp.class);

    public static void main(String[] args) {
        SpringApplication.run(ClientApp.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction() {
        return route(GET("/message"), this::handleRequest);
    }

    Mono<ServerResponse> handleRequest(final ServerRequest request) {
        log.info("coming http request: {}", request.path());
        final ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        final URI uri = URI.create("ws://localhost:8080/ws/hello");
        final List<String> messages = new ArrayList<>();
        return client.execute(uri,
                session -> session.send(createMessage(session))
                        .then(session.receive()
                                .doOnNext(wsm -> messages.add(this.onComing(wsm)))
                                .then()))
                .then(Mono.just(messages))
                .flatMap(list -> Mono.justOrEmpty(list.stream().findFirst()))
                .flatMap(text -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(Mono.just(text), String.class));
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        log.info("command line runner created.");
        return args -> {
            final ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
            final URI uri = URI.create("ws://localhost:8080/ws/hello");
            client.execute(uri, session ->
                    session.send(createMessage(session))
                            .then(session.receive().doOnNext(this::onComing).then())
            );
        };
    }

    private String onComing(final WebSocketMessage message) {
        final String text = message.getPayloadAsText();
        log.info("coming message: {}", text);
        return text;
    }

    private Mono<WebSocketMessage> createMessage(final WebSocketSession session) {
        log.info("client sending message");
        return Mono.fromSupplier(() -> session.textMessage("client"));
    }
}
