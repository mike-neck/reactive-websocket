package com.example.tyrus;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ClientEndpoint
public class TyrusClient {

  private static final Logger log = LoggerFactory.getLogger(TyrusClient.class);

  @OnOpen
  public void onOpen(final Session session) {
    log.info("open session to server: {}", session.getId());
  }

  @OnMessage
  public void onMessage(final Session session, final String message) {
    log.info("message coming from server : {}", message);
  }
}
