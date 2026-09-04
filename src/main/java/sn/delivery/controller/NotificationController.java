package sn.delivery.controller;

import sn.delivery.dto.NotificationDTO;
import sn.delivery.model.TypeNotification;
import sn.delivery.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    @GetMapping("/user/{userId}/non-lues")
    public ResponseEntity<List<NotificationDTO>> getNonLues(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNonLues(userId));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countNonLues(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.countNonLues(userId));
    }

    @PutMapping("/{id}/lue")
    public ResponseEntity<Void> marquerCommeLue(@PathVariable Long id) {
        notificationService.marquerCommeLue(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody java.util.Map<String, Object> request) {
        return ResponseEntity.ok(notificationService.createNotification(
                ((Number) request.get("userId")).longValue(),
                (String) request.get("titre"),
                (String) request.get("message"),
                TypeNotification.valueOf((String) request.get("type"))
        ));
    }
}