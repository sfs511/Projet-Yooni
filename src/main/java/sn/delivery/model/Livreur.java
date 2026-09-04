package sn.delivery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "livreurs")
public class Livreur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    private String vehicule;

    private String plaqueVehicule;

    private String zone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutLivreur statut = StatutLivreur.DISPONIBLE;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Livreur() {
        this.createdAt = LocalDateTime.now();
    }

    public Livreur(User user, String nom, String prenom) {
        this();
        this.user = user;
        this.nom = nom;
        this.prenom = prenom;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getVehicule() { return vehicule; }
    public void setVehicule(String vehicule) { this.vehicule = vehicule; }

    public String getPlaqueVehicule() { return plaqueVehicule; }
    public void setPlaqueVehicule(String plaqueVehicule) { this.plaqueVehicule = plaqueVehicule; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public StatutLivreur getStatut() { return statut; }
    public void setStatut(StatutLivreur statut) { this.statut = statut; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
