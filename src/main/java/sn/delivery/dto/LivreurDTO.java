package sn.delivery.dto;

import java.time.LocalDateTime;

public class LivreurDTO {
    private Long id;
    private Long userId;
    private String email;
    private String nom;
    private String prenom;
    private String telephone;
    private String vehicule;
    private String plaqueVehicule;
    private String zone;
    private String statut;
    private double latitude;
    private double longitude;
    private LocalDateTime createdAt;

    public LivreurDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getVehicule() { return vehicule; }
    public void setVehicule(String vehicule) { this.vehicule = vehicule; }

    public String getPlaqueVehicule() { return plaqueVehicule; }
    public void setPlaqueVehicule(String plaqueVehicule) { this.plaqueVehicule = plaqueVehicule; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}