package sn.delivery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "livraisons")
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livreur_id")
    private Livreur livreur;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "colis_id", nullable = false)
    private Colis colis;

    @Column(nullable = false)
    private String adresseDepart;

    @Column(nullable = false)
    private String adresseArrivee;

    private String villeDepart;

    private String villeArrivee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutLivraison statut = StatutLivraison.EN_ATTENTE;

    private double prix;

    private double distanceKm;

    private int dureeEstimeeMinutes;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime datePriseEnCharge;

    private LocalDateTime dateLivraison;

    public Livraison() {
        this.dateCreation = LocalDateTime.now();
    }

    public Livraison(Client client, Colis colis, String adresseDepart, String adresseArrivee) {
        this();
        this.client = client;
        this.colis = colis;
        this.adresseDepart = adresseDepart;
        this.adresseArrivee = adresseArrivee;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Livreur getLivreur() { return livreur; }
    public void setLivreur(Livreur livreur) { this.livreur = livreur; }

    public Colis getColis() { return colis; }
    public void setColis(Colis colis) { this.colis = colis; }

    public String getAdresseDepart() { return adresseDepart; }
    public void setAdresseDepart(String adresseDepart) { this.adresseDepart = adresseDepart; }

    public String getAdresseArrivee() { return adresseArrivee; }
    public void setAdresseArrivee(String adresseArrivee) { this.adresseArrivee = adresseArrivee; }

    public String getVilleDepart() { return villeDepart; }
    public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }

    public String getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }

    public StatutLivraison getStatut() { return statut; }
    public void setStatut(StatutLivraison statut) { this.statut = statut; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public int getDureeEstimeeMinutes() { return dureeEstimeeMinutes; }
    public void setDureeEstimeeMinutes(int dureeEstimeeMinutes) { this.dureeEstimeeMinutes = dureeEstimeeMinutes; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDatePriseEnCharge() { return datePriseEnCharge; }
    public void setDatePriseEnCharge(LocalDateTime datePriseEnCharge) { this.datePriseEnCharge = datePriseEnCharge; }

    public LocalDateTime getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(LocalDateTime dateLivraison) { this.dateLivraison = dateLivraison; }
}
