package sn.tripplanner.model;
// On reste dans le package model car c'est une entité de notre domaine métier.

/**
 * La classe Route représente une arête pondérée dans notre graphe.
 * Elle connecte deux objets Region.
 */
public class Route {
// Déclaration de la classe Route.

    private Region depart;
    // La région d'origine de ce segment de route (le point A).

    private Region destination;
    // La région d'arrivée de ce segment (le point B).

    private double distance;
    // La distance en kilomètres (km). C'est le "poids" principal pour l'algorithme de Dijkstra.

    private int tempsMinutes;
    // Le temps de trajet estimé en minutes. Utile pour proposer des variantes d'itinéraires (le plus rapide vs le plus court).

    // --- CONSTRUCTEUR ---

    public Route(Region depart, Region destination, double distance, int tempsMinutes) {
    // Initialise une nouvelle connexion entre deux régions avec ses métriques.
        this.depart = depart;
        // Définit la région de départ.
        this.destination = destination;
        // Définit la région d'arrivée.
        this.distance = distance;
        // Stocke la distance physique entre les deux.
        this.tempsMinutes = tempsMinutes;
        // Stocke la durée estimée du voyage.
    }

    // --- GETTERS ---

    public Region getDepart() {
    // Récupère l'objet Region source.
        return depart;
    }

    public Region getDestination() {
    // Récupère l'objet Region cible.
        return destination;
    }

    public double getDistance() {
    // Récupère la distance pour les calculs algorithmiques.
        return distance;
    }

    public int getTempsMinutes() {
    // Récupère le temps pour l'affichage ou les calculs de durée totale.
        return tempsMinutes;
    }
}
// Fin de la classe Route.