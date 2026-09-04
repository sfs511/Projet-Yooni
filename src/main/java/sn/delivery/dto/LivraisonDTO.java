package sn.delivery.dto;

import java.time.LocalDateTime;

public class LivraisonDTO {
    private Long id;
    private Long clientId;
    private Long livreurId;
    private String livreurNom;
    private String clientNom;
    private String adresseDepart;
    private String adresseArrivee;
    private String villeDepart;
    private String villeArrivee;
    private String statut;
    private double prix;
    private double distanceKm;
    private int dureeEstimeeMinutes;
    private String colisDescription;
    private double colisPoids;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLivraison;

    public LivraisonDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getLivreurId() { return livreurId; }
    public void setLivreurId(Long livreurId) { this.livreurId = livreurId; }

    public String getLivreurNom() { return livreurNom; }
    public void setLivreurNom(String livreurNom) { this.livreurNom = livreurNom; }

    public String getClientNom() { return clientNom; }
    public void setClientNom(String clientNom) { this.clientNom = clientNom; }

    public String getAdresseDepart() { return adresseDepart; }
    public void setAdresseDepart(String adresseDepart) { this.adresseDepart = adresseDepart; }

    public String getAdresseArrivee() { return adresseArrivee; }
    public void setAdresseArrivee(String adresseArrivee) { this.adresseArrivee = adresseArrivee; }

    public String getVilleDepart() { return villeDepart; }
    public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }

    public String getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public int getDureeEstimeeMinutes() { return dureeEstimeeMinutes; }
    public void setDureeEstimeeMinutes(int dureeEstimeeMinutes) { this.dureeEstimeeMinutes = dureeEstimeeMinutes; }

    public String getColisDescription() { return colisDescription; }
    public void setColisDescription(String colisDescription) { this.colisDescription = colisDescription; }

    public double getColisPoids() { return colisPoids; }
    public void setColisPoids(double colisPoids) { this.colisPoids = colisPoids; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(LocalDateTime dateLivraison) { this.dateLivraison = dateLivraison; }
}