package sn.delivery.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class TrackingWebSocketHandler {

    @MessageMapping("/tracking.update")
    @SendTo("/topic/tracking")
    public Map<String, Object> handleTrackingUpdate(@Payload Map<String, Object> payload) {
        System.out.println("Tracking update recu: " + payload);
        return payload;
    }
}
