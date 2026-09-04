package sn.delivery.controller;

import sn.delivery.model.Region;
import sn.delivery.service.GraphService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/trip")
@CrossOrigin(origins = "*")
public class TripController {

    private final GraphService graphService;

    public TripController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/regions")
    public Collection<Region> getRegions() {
        return graphService.getAllRegions();
    }

    @GetMapping("/path")
    public Map<String, Object> getShortestPath(@RequestParam String from, @RequestParam String to) {
        List<Region> chemin = graphService.calculerDijkstra(from, to);
        return buildResponse(chemin, "Dijkstra");
    }

    @GetMapping("/path/bellmanford")
    public Map<String, Object> getBellmanFordPath(@RequestParam String from, @RequestParam String to) {
        List<Region> chemin = graphService.calculerBellmanFord(from, to);
        return buildResponse(chemin, "Bellman-Ford");
    }

    @GetMapping("/path/tsp")
    public Map<String, Object> getTSPPath(@RequestParam(defaultValue = "DAKAR") String from) {
        List<Region> chemin = graphService.calculerTSP(from);
        return buildResponse(chemin, "TSP");
    }

    private Map<String, Object> buildResponse(List<Region> chemin, String algorithme) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("algorithme", algorithme);
        response.put("chemin", chemin);
        response.put("distanceTotaleKm", graphService.calculerDistanceTotale(chemin));
        response.put("tempsEstimeMinutes", graphService.calculerTempsTotalMinutes(chemin));
        response.put("nombreEtapes", chemin.size());
        return response;
    }
}
