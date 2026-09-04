package sn.delivery.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class NotificationWebSocketHandler {

    @MessageMapping("/notification.send")
    @SendTo("/topic/notifications")
    public Map<String, Object> handleNotification(@Payload Map<String, Object> payload) {
        System.out.println("Notification recue: " + payload);
        return payload;
    }
}