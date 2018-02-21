package com.example.tyrus;

import static com.example.tyrus.ExceptionalConsumer.consumer;
import static com.example.tyrus.ExceptionalSupplier.supplier;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.websocket.DeploymentException;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;

public class TyrusApp {

  private static final Logger log = LoggerFactory.getLogger(TyrusClient.class);

  private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

  public static void main(String[] args) throws DeploymentException {
    final ClientManager clientManager = ClientManager.createClient();
    final Future<Session> futureSession =
        clientManager.asyncConnectToServer(
            TyrusClient.class, URI.create("ws://localhost:8080/ws/hello"));
    log.info("start application");
    Mono.fromFuture(CompletableFuture.supplyAsync(supplier(futureSession::get), EXECUTOR))
        .delaySubscription(Duration.ofSeconds(2L)) // 接続後 2秒遅らせる
        .doOnNext(consumer(session -> session.getBasicRemote().sendText("tyrus"))) // サーバーに tyrus というメッセージを送信する
        .delayElement(Duration.ofSeconds(4L)) // 4秒間隔にする(=4秒後に接続を切断)
        .doOnNext(consumer(Session::close)) // 接続を切断
        .doOnTerminate(() -> log.info("close connection"))
        .doOnTerminate(EXECUTOR::shutdown)
        .subscribe(); // イベントを開始する
  }
}
