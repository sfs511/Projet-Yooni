package sn.delivery.repository;

import sn.delivery.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndLueFalseOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndLueFalse(Long userId);
}
