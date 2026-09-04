package sn.delivery.service;

import sn.delivery.dto.LivraisonDTO;
import sn.delivery.model.*;
import sn.delivery.repository.*;
import sn.delivery.network.NotificationWebSocket;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final ClientRepository clientRepository;
    private final LivreurRepository livreurRepository;
    private final ColisRepository colisRepository;
    private final NotificationWebSocket notificationWebSocket;

    public LivraisonService(LivraisonRepository livraisonRepository, ClientRepository clientRepository,
                            LivreurRepository livreurRepository, ColisRepository colisRepository,
                            NotificationWebSocket notificationWebSocket) {
        this.livraisonRepository = livraisonRepository;
        this.clientRepository = clientRepository;
        this.livreurRepository = livreurRepository;
        this.colisRepository = colisRepository;
        this.notificationWebSocket = notificationWebSocket;
    }

    @Transactional
    public LivraisonDTO createLivraison(Long clientId, String descriptionColis, double poids,
                                         String adresseDepart, String adresseArrivee,
                                         String villeDepart, String villeArrivee) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouve"));

        Colis colis = new Colis(descriptionColis, poids);
        colis = colisRepository.save(colis);

        Livraison livraison = new Livraison(client, colis, adresseDepart, adresseArrivee);
        livraison.setVilleDepart(villeDepart);
        livraison.setVilleArrivee(villeArrivee);
        livraison.setPrix(calculerPrix(poids, 0));
        livraison = livraisonRepository.save(livraison);

        notificationWebSocket.envoyerNotification("/topic/livraisons",
                "Nouvelle livraison #" + livraison.getId() + " creee");

        return toDTO(livraison);
    }

    public List<LivraisonDTO> getAllLivraisons() {
        return livraisonRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LivraisonDTO> getLivraisonsByClient(Long clientId) {
        return livraisonRepository.findByClientId(clientId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LivraisonDTO> getLivraisonsByLivreur(Long livreurId) {
        return livraisonRepository.findByLivreurId(livreurId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public LivraisonDTO getLivraisonById(Long id) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvee"));
        return toDTO(livraison);
    }

    @Transactional
    public LivraisonDTO assignerLivreur(Long livraisonId, Long livreurId) {
        Livraison livraison = livraisonRepository.findById(livraisonId)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvee"));
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur non trouve"));

        livraison.setLivreur(livreur);
        livraison.setStatut(StatutLivraison.PRISE_EN_CHARGE);
        livraison.setDatePriseEnCharge(LocalDateTime.now());
        livreur.setStatut(StatutLivreur.EN_LIVRAISON);

        livraisonRepository.save(livraison);
        livreurRepository.save(livreur);

        notificationWebSocket.envoyerNotification("/topic/livraisons",
                "Livraison #" + livraisonId + " assignee au livreur " + livreur.getNom());

        return toDTO(livraison);
    }

    @Transactional
    public LivraisonDTO updateStatut(Long livraisonId, StatutLivraison statut) {
        Livraison livraison = livraisonRepository.findById(livraisonId)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvee"));

        livraison.setStatut(statut);

        if (statut == StatutLivraison.LIVREE) {
            livraison.setDateLivraison(LocalDateTime.now());
            if (livraison.getLivreur() != null) {
                livraison.getLivreur().setStatut(StatutLivreur.DISPONIBLE);
                livreurRepository.save(livraison.getLivreur());
            }
        }

        livraison = livraisonRepository.save(livraison);

        notificationWebSocket.envoyerNotification("/topic/tracking/" + livraisonId,
                "Statut mis a jour: " + statut.name());

        return toDTO(livraison);
    }

    public LivraisonDTO getLivraisonEnCoursByLivreur(Long livreurId) {
        List<Livraison> livraisons = livraisonRepository.findByLivreurIdAndStatut(livreurId, StatutLivraison.EN_COURS);
        if (livraisons.isEmpty()) {
            livraisons = livraisonRepository.findByLivreurIdAndStatut(livreurId, StatutLivraison.PRISE_EN_CHARGE);
        }
        if (livraisons.isEmpty()) {
            throw new RuntimeException("Aucune livraison en cours pour ce livreur");
        }
        return toDTO(livraisons.get(0));
    }

    private double calculerPrix(double poids, double distanceKm) {
        double prixBase = 500;
        double prixParKg = 100;
        double prixParKm = 50;
        return prixBase + (poids * prixParKg) + (distanceKm * prixParKm);
    }

    private LivraisonDTO toDTO(Livraison livraison) {
        LivraisonDTO dto = new LivraisonDTO();
        dto.setId(livraison.getId());
        dto.setClientId(livraison.getClient().getId());
        dto.setClientNom(livraison.getClient().getNom() + " " + livraison.getClient().getPrenom());
        dto.setAdresseDepart(livraison.getAdresseDepart());
        dto.setAdresseArrivee(livraison.getAdresseArrivee());
        dto.setVilleDepart(livraison.getVilleDepart());
        dto.setVilleArrivee(livraison.getVilleArrivee());
        dto.setStatut(livraison.getStatut().name());
        dto.setPrix(livraison.getPrix());
        dto.setDistanceKm(livraison.getDistanceKm());
        dto.setDureeEstimeeMinutes(livraison.getDureeEstimeeMinutes());
        dto.setColisDescription(livraison.getColis().getDescription());
        dto.setColisPoids(livraison.getColis().getPoids());
        dto.setDateCreation(livraison.getDateCreation());
        dto.setDateLivraison(livraison.getDateLivraison());

        if (livraison.getLivreur() != null) {
            dto.setLivreurId(livraison.getLivreur().getId());
            dto.setLivreurNom(livraison.getLivreur().getNom() + " " + livraison.getLivreur().getPrenom());
        }

        return dto;
    }
}
