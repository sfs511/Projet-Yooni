package sn.delivery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "colis")
public class Colis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private double poids;

    private String dimensions;

    private double valeurDeclaree;

    private boolean fragile = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutColis statut = StatutColis.EN_ATTENTE;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Colis() {
        this.createdAt = LocalDateTime.now();
    }

    public Colis(String description, double poids) {
        this();
        this.description = description;
        this.poids = poids;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPoids() { return poids; }
    public void setPoids(double poids) { this.poids = poids; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public double getValeurDeclaree() { return valeurDeclaree; }
    public void setValeurDeclaree(double valeurDeclaree) { this.valeurDeclaree = valeurDeclaree; }

    public boolean isFragile() { return fragile; }
    public void setFragile(boolean fragile) { this.fragile = fragile; }

    public StatutColis getStatut() { return statut; }
    public void setStatut(StatutColis statut) { this.statut = statut; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
