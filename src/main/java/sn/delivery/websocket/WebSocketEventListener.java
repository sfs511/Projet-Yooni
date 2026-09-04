package sn.delivery.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        System.out.println("WebSocket connecte: " + event.getUser());
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        System.out.println("WebSocket deconnecte: " + event.getSessionId());
        messagingTemplate.convertAndSend("/topic/status",
                "Utilisateur deconnecte: " + event.getSessionId());
    }
}