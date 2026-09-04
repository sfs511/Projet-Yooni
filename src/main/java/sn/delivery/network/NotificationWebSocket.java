package sn.delivery.network;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationWebSocket {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationWebSocket(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void envoyerStatut(String message) {
        System.out.println("[ALGO] " + message);
        messagingTemplate.convertAndSend("/topic/status", message);
    }

    public void envoyerNotification(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }

    public void envoyerTracking(Long livraisonId, Object position) {
        messagingTemplate.convertAndSend("/topic/tracking/" + livraisonId, position);
    }
}
