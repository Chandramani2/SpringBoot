package com.rideapps.driver.service;

import com.rideapps.driver.config.DriverStompSessionHandler;
import com.rideapps.driver.model.Driver;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.*;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DriverMatchingClientService {

    private StompSession stompSession;

    @Value("${app.matching.websocket.url}")
    private String URL;

    @PostConstruct
    public void connect() {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());

        // Add MappingJackson2MessageConverter to handle POJO to JSON conversion
        stompClient.setMessageConverter(new CompositeMessageConverter(List.of(
                new MappingJackson2MessageConverter(), // Required for JSON/POJO support
                new StringMessageConverter(),
                new ByteArrayMessageConverter()
        )));

        try {
            this.stompSession = stompClient.connectAsync(URL, new DriverStompSessionHandler()).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLocationUpdate(Map<String, Object> payload) {
        if (stompSession != null && stompSession.isConnected()) {
            // Sends the Driver object; the infrastructure handles the JSON conversion
            stompSession.send("/app/driver.updateLocation", payload);
        }
    }
}