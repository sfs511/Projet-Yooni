package sn.delivery.util;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationUtil {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationUtil(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void envoyerNotification(String destination, String message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    public void envoyerATous(String message) {
        messagingTemplate.convertAndSend("/topic/status", message);
    }
}