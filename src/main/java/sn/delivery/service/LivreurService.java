package sn.delivery.service;

import sn.delivery.dto.LivreurDTO;
import sn.delivery.model.Livreur;
import sn.delivery.model.User;
import sn.delivery.model.UserRole;
import sn.delivery.model.StatutLivreur;
import sn.delivery.model.Position;
import sn.delivery.repository.LivreurRepository;
import sn.delivery.repository.PositionRepository;
import sn.delivery.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LivreurService {

    private final LivreurRepository livreurRepository;
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;

    public LivreurService(LivreurRepository livreurRepository, UserRepository userRepository,
                          PositionRepository positionRepository, PasswordEncoder passwordEncoder) {
        this.livreurRepository = livreurRepository;
        this.userRepository = userRepository;
        this.positionRepository = positionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LivreurDTO createLivreur(String email, String password, String telephone, String nom, String prenom,
                                     String vehicule, String plaque, String zone) {
        User user = new User(email, passwordEncoder.encode(password), telephone, UserRole.LIVREUR);
        user = userRepository.save(user);

        Livreur livreur = new Livreur(user, nom, prenom);
        livreur.setVehicule(vehicule);
        livreur.setPlaqueVehicule(plaque);
        livreur.setZone(zone);
        livreur = livreurRepository.save(livreur);

        return toDTO(livreur);
    }

    public List<LivreurDTO> getAllLivreurs() {
        return livreurRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LivreurDTO> getLivreursDisponibles() {
        return livreurRepository.findByStatut(StatutLivreur.DISPONIBLE).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public LivreurDTO getLivreurById(Long id) {
        Livreur livreur = livreurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livreur non trouve"));
        return toDTO(livreur);
    }

    public LivreurDTO updateStatut(Long id, StatutLivreur statut) {
        Livreur livreur = livreurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livreur non trouve"));
        livreur.setStatut(statut);
        livreur = livreurRepository.save(livreur);
        return toDTO(livreur);
    }

    public LivreurDTO updatePosition(Long livreurId, double latitude, double longitude, double vitesse) {
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur non trouve"));

        Position position = new Position(livreur, latitude, longitude);
        position.setVitesse(vitesse);
        positionRepository.save(position);

        return toDTO(livreur);
    }

    private LivreurDTO toDTO(Livreur livreur) {
        LivreurDTO dto = new LivreurDTO();
        dto.setId(livreur.getId());
        dto.setUserId(livreur.getUser().getId());
        dto.setEmail(livreur.getUser().getEmail());
        dto.setNom(livreur.getNom());
        dto.setPrenom(livreur.getPrenom());
        dto.setTelephone(livreur.getUser().getTelephone());
        dto.setVehicule(livreur.getVehicule());
        dto.setPlaqueVehicule(livreur.getPlaqueVehicule());
        dto.setZone(livreur.getZone());
        dto.setStatut(livreur.getStatut().name());
        dto.setCreatedAt(livreur.getCreatedAt());

        positionRepository.findFirstByLivreurIdOrderByTimestampDesc(livreur.getId())
                .ifPresent(pos -> {
                    dto.setLatitude(pos.getLatitude());
                    dto.setLongitude(pos.getLongitude());
                });

        return dto;
    }
}