package io.alfabattle.amaksim.bankomat.stompclient;

import io.alfabattle.amaksim.bankomat.stompclient.model.StompInMsg;
import io.alfabattle.amaksim.bankomat.stompclient.model.StompOutMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
//@Component
public class StompClient {

    @Value("${stomp.connect-url}")
    private String connectUrl;

    @Value("${stomp.topic}")
    private String stompTopic;

    private Map<Integer, Integer> alfikCache = new HashMap<>();
    private ListenableFuture<StompSession> stompSession;

    ReadWriteLock rwLock = new ReentrantReadWriteLock();

    @PostConstruct
    private void init() {
        connect();
    }

    public Integer getAlfikAmountByDeviceId(Integer deviceId) {
        rwLock.readLock().lock();
        try {
            return alfikCache.getOrDefault(deviceId, 0);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void processAlfikByDeviceID(Integer deviceId) {
        rwLock.writeLock().lock();
        connect();
        try {
            this.stompSession.get().send("", new StompOutMsg(deviceId));
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
    }

    private void connect() {
        try {
            if (stompSession == null || !stompSession.get().isConnected()) {
                Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
                List<Transport> transports = Collections.singletonList(webSocketTransport);

                SockJsClient sockJsClient = new SockJsClient(transports);
                sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

                WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
                stompClient.setMessageConverter(new MappingJackson2MessageConverter());

                WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
                stompSession = stompClient.connect(connectUrl, headers, new MyHandler(), "localhost", 8100);
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
    }

    private void subscribeTopic() {
        try {
            stompSession.get().subscribe(stompTopic, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return StompInMsg.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    StompInMsg stompInMsg = (StompInMsg) payload;
                    alfikCache.put(stompInMsg.getDeviceId(), stompInMsg.getAlfik());
                    log.info("{}", (StompInMsg) payload);
                }
            });
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private class MyHandler extends StompSessionHandlerAdapter {
        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            rwLock.writeLock().unlock();
            log.error(exception.getMessage());
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            //super.handleException(session, command, headers, payload, exception);
            rwLock.writeLock().unlock();
            log.error(exception.getMessage());
        }

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            subscribeTopic();
            log.info("Connected");
        }

    }
}
