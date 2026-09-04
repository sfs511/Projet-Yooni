package sn.delivery.dto;

import java.time.LocalDateTime;

public class TrackingDTO {
    private Long livreurId;
    private String livreurNom;
    private double latitude;
    private double longitude;
    private double vitesse;
    private double direction;
    private LocalDateTime timestamp;

    public TrackingDTO() {}

    public Long getLivreurId() { return livreurId; }
    public void setLivreurId(Long livreurId) { this.livreurId = livreurId; }

    public String getLivreurNom() { return livreurNom; }
    public void setLivreurNom(String livreurNom) { this.livreurNom = livreurNom; }

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
