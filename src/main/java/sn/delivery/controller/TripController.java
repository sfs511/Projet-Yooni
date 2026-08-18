//appelle GraphService.java pour calculer les itinéraires avec les algorithmes 
//djickstra, bellmanford et tsp

package sn.tripplanner.controller;
// Déclaration du package

import org.springframework.web.bind.annotation.*;
// Importation des outils REST
import sn.tripplanner.model.Region;
// Importation du modèle de données Region
import sn.tripplanner.service.GraphService;
// Importation du service contenant la logique métier
import java.util.*;
// Importation des collections Java

@RestController
// Déclare cette classe comme contrôleur API
@RequestMapping("/api/trip")
// Définit le chemin racine /api/trip
@CrossOrigin(origins = "*")
// Permet l'accès depuis n'importe quel client (ex: React, Vue, Mobile)



public class TripController {
// Déclaration de la classe

    private final GraphService graphService;
    // Déclaration du service qui gère le graphe des régions

    public TripController(GraphService graphService) {
    // Constructeur pour l'injection de dépendances de GraphService
        this.graphService = graphService;
    }

    @GetMapping("/regions")
    // Route pour obtenir la liste de toutes les régions
    public Collection<Region> getRegions() {
    // Méthode retournant une collection de régions
        return graphService.getAllRegions();
        // Appelle le service pour récupérer les données
    }

    //framework pour calculer Djickstra
    @GetMapping("/path")
    // Route pour calculer le chemin le plus court (Dijkstra)
    public Map<String, Object> getShortestPath(@RequestParam String from, @RequestParam String to) {
    // Prend deux paramètres : 'from' (départ) et 'to' (arrivée)
        List<Region> chemin = graphService.calculerDijkstra(from, to);
        // Exécute l'algorithme de Dijkstra via le service
        return buildResponse(chemin, "Dijkstra");
        // Formate et retourne la réponse
    }


    //framework pour calculer bellmanford
    @GetMapping("/path/bellmanford")
    // Route pour l'algorithme de Bellman-Ford
    public Map<String, Object> getBellmanFordPath(@RequestParam String from, @RequestParam String to) {
    // Prend les paramètres de départ et d'arrivée
        List<Region> chemin = graphService.calculerBellmanFord(from, to);
        // Exécute l'algorithme de Bellman-Ford
        return buildResponse(chemin, "Bellman-Ford");
        // Formate et retourne la réponse
    }


    //framework pour calculer tsp
    @GetMapping("/path/tsp")
    // Route pour le problème du voyageur de commerce (visiter tout le Sénégal)
    public Map<String, Object> getTSPPath(@RequestParam(defaultValue = "DAKAR") String from) {
    // Prend un point de départ, par défaut Dakar
        List<Region> chemin = graphService.calculerTSP(from);
        // Exécute l'algorithme TSP
        return buildResponse(chemin, "TSP");
        // Formate et retourne la réponse
    }

    private Map<String, Object> buildResponse(List<Region> chemin, String algorithme) {
    // Méthode utilitaire pour construire un objet JSON structuré
        Map<String, Object> response = new LinkedHashMap<>();
        // Utilise une LinkedHashMap pour conserver l'ordre d'insertion des clés
        response.put("algorithme", algorithme);
        // Ajoute le nom de l'algorithme utilisé
        response.put("chemin", chemin);
        // Ajoute la liste ordonnée des régions à traverser
        response.put("distanceTotaleKm", graphService.calculerDistanceTotale(chemin));
        // Calcule et ajoute la distance totale du trajet
        response.put("tempsEstimeMinutes", graphService.calculerTempsTotalMinutes(chemin));
        // Calcule et ajoute le temps de trajet estimé
        response.put("nombreEtapes", chemin.size());
        // Ajoute le nombre d'arrêts prévus
        return response;
        // Retourne la map prête à être convertie en JSON
    }
}