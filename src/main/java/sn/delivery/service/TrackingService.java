package sn.delivery.service;

import sn.delivery.dto.TrackingDTO;
import sn.delivery.model.Position;
import sn.delivery.model.Livreur;
import sn.delivery.repository.PositionRepository;
import sn.delivery.repository.LivreurRepository;
import sn.delivery.network.NotificationWebSocket;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrackingService {

    private final PositionRepository positionRepository;
    private final LivreurRepository livreurRepository;
    private final NotificationWebSocket notificationWebSocket;

    public TrackingService(PositionRepository positionRepository, LivreurRepository livreurRepository,
                           NotificationWebSocket notificationWebSocket) {
        this.positionRepository = positionRepository;
        this.livreurRepository = livreurRepository;
        this.notificationWebSocket = notificationWebSocket;
    }

    public TrackingDTO updatePosition(Long livreurId, double latitude, double longitude, double vitesse, double direction) {
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur non trouve"));

        Position position = new Position(livreur, latitude, longitude);
        position.setVitesse(vitesse);
        position.setDirection(direction);
        position = positionRepository.save(position);

        TrackingDTO dto = new TrackingDTO();
        dto.setLivreurId(livreurId);
        dto.setLivreurNom(livreur.getNom() + " " + livreur.getPrenom());
        dto.setLatitude(latitude);
        dto.setLongitude(longitude);
        dto.setVitesse(vitesse);
        dto.setDirection(direction);
        dto.setTimestamp(position.getTimestamp());

        notificationWebSocket.envoyerTracking(livreurId, dto);

        return dto;
    }

    public Optional<TrackingDTO> getDernierePosition(Long livreurId) {
        return positionRepository.findFirstByLivreurIdOrderByTimestampDesc(livreurId)
                .map(position -> {
                    Livreur livreur = position.getLivreur();
                    TrackingDTO dto = new TrackingDTO();
                    dto.setLivreurId(livreurId);
                    dto.setLivreurNom(livreur.getNom() + " " + livreur.getPrenom());
                    dto.setLatitude(position.getLatitude());
                    dto.setLongitude(position.getLongitude());
                    dto.setVitesse(position.getVitesse());
                    dto.setDirection(position.getDirection());
                    dto.setTimestamp(position.getTimestamp());
                    return dto;
                });
    }
}
