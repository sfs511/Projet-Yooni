package sn.delivery.service;

import sn.delivery.dto.NotificationDTO;
import sn.delivery.model.Notification;
import sn.delivery.model.TypeNotification;
import sn.delivery.model.User;
import sn.delivery.repository.NotificationRepository;
import sn.delivery.repository.UserRepository;
import sn.delivery.network.NotificationWebSocket;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationWebSocket notificationWebSocket;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository,
                               NotificationWebSocket notificationWebSocket) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationWebSocket = notificationWebSocket;
    }

    public NotificationDTO createNotification(Long userId, String titre, String message, TypeNotification type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve"));

        Notification notification = new Notification(user, titre, message, type);
        notification = notificationRepository.save(notification);

        notificationWebSocket.envoyerNotification("/topic/notifications/" + userId, toDTO(notification));

        return toDTO(notification);
    }

    public List<NotificationDTO> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getNonLues(Long userId) {
        return notificationRepository.findByUserIdAndLueFalseOrderByCreatedAtDesc(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public long countNonLues(Long userId) {
        return notificationRepository.countByUserIdAndLueFalse(userId);
    }

    public void marquerCommeLue(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvee"));
        notification.setLue(true);
        notificationRepository.save(notification);
    }

    private NotificationDTO toDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitre(notification.getTitre());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType().name());
        dto.setLue(notification.isLue());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
