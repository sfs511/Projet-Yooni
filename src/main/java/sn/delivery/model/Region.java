package sn.tripplanner.model;
// Déclaration du package des modèles

public class Region {
// Déclaration de la classe Region
    private String id;
    // Identifiant unique (ex: DAKAR)
    private String nom;
    // Nom d'affichage (ex: Dakar)
    private double latitude;
    // Coordonnée géographique latitude
    private double longitude;
    // Coordonnée géographique longitude
    private String description;
    // Texte descriptif de la région
    private String conseils;
    // Champ pour stocker des conseils de voyage
    private String hebergement;
    // Champ pour stocker des informations d'hébergement

    public Region(String id, String nom, double latitude, double longitude, String description) {
    // Constructeur pour initialiser une région
        this.id = id;
        // Assigne l'id
        this.nom = nom;
        // Assigne le nom
        this.latitude = latitude;
        // Assigne la latitude
        this.longitude = longitude;
        // Assigne la longitude
        this.description = description;
        // Assigne la description
        this.conseils = "";
        // Initialise les conseils à vide
        this.hebergement = "";
        // Initialise l'hébergement à vide
    }

    public String getId() { return id; }
    // Getter pour l'id
    public String getNom() { return nom; }
    // Getter pour le nom
    public double getLatitude() { return latitude; }
    // Getter pour la latitude
    public double getLongitude() { return longitude; }
    // Getter pour la longitude
    public String getDescription() { return description; }
    // Getter pour la description
    public String getConseils() { return conseils; }
    // Getter pour les conseils
    public String getHebergement() { return hebergement; }
    // Getter pour l'hébergement

    public void setConseils(String conseils) { this.conseils = conseils; }
    // Setter pour modifier les conseils
    public void setHebergement(String hebergement) { this.hebergement = hebergement; }
    // Setter pour modifier l'hébergement
}