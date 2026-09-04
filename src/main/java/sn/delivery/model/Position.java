package sn.delivery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livreur_id", nullable = false)
    private Livreur livreur;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    private double vitesse;

    private double direction;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Position() {
        this.timestamp = LocalDateTime.now();
    }

    public Position(Livreur livreur, double latitude, double longitude) {
        this();
        this.livreur = livreur;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Livreur getLivreur() { return livreur; }
    public void setLivreur(Livreur livreur) { this.livreur = livreur; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getVitesse() { return vitesse; }
    public void setVitesse(double vitesse) { this.vitesse = vitesse; }

    public double getDirection() { return direction; }
    public void setDirection(double direction) { this.direction = direction; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
