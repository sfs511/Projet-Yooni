package sn.delivery.model;

public class Region {

    private String id;
    private String nom;
    private double latitude;
    private double longitude;
    private String description;
    private String conseils;
    private String hebergement;

    public Region() {}

    public Region(String id, String nom, double latitude, double longitude, String description) {
        this.id = id;
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.conseils = "";
        this.hebergement = "";
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getDescription() { return description; }
    public String getConseils() { return conseils; }
    public String getHebergement() { return hebergement; }

    public void setConseils(String conseils) { this.conseils = conseils; }
    public void setHebergement(String hebergement) { this.hebergement = hebergement; }
}