package sn.delivery.service;

import sn.delivery.model.Region;
import sn.delivery.model.Route;
import sn.delivery.network.NotificationWebSocket;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
public class GraphService {

    private final Map<String, Region> regions = new HashMap<>();
    private final Map<String, List<Route>> adjacence = new HashMap<>();
    private final NotificationWebSocket notif;

    public GraphService(NotificationWebSocket notif) {
        this.notif = notif;
    }

    @PostConstruct
    public void initGraph() {
        ajouterRegion(new Region("DAKAR", "Dakar", 14.7167, -17.4677, "La capitale."));
        ajouterRegion(new Region("THIES", "Thies", 14.791, -16.935, "Le carrefour."));
        ajouterRegion(new Region("SAINT_LOUIS", "Saint-Louis", 16.024, -16.489, "La Venise africaine."));
        ajouterRegion(new Region("DIOURBEL", "Diourbel", 14.656, -16.236, "La ville de la Grande Mosquee."));
        ajouterRegion(new Region("LOUGA", "Louga", 15.618, -16.224, "Le Ndiambour."));
        ajouterRegion(new Region("MATAM", "Matam", 15.655, -13.255, "Le Fouta."));
        ajouterRegion(new Region("KAOLACK", "Kaolack", 14.144, -16.083, "Le bassin arachidier."));
        ajouterRegion(new Region("FATICK", "Fatick", 14.358, -16.413, "Le Sine."));
        ajouterRegion(new Region("KAFFRINE", "Kaffrine", 14.105, -15.550, "Le Ndoucoumane."));
        ajouterRegion(new Region("TAMBA", "Tambacounda", 13.770, -13.667, "Le Senegal Oriental."));
        ajouterRegion(new Region("KEDOUGOU", "Kedougou", 12.557, -12.174, "Les cascades et montagnes."));
        ajouterRegion(new Region("KOLDA", "Kolda", 12.883, -14.950, "La Haute Casamance."));
        ajouterRegion(new Region("SEDHIOU", "Sedhiou", 12.708, -15.556, "Le Pakao."));
        ajouterRegion(new Region("ZIGUINCHOR", "Ziguinchor", 12.583, -16.267, "La Basse Casamance."));

        ajouterRoute("DAKAR", "LOUGA", 189, 160);
        ajouterRoute("DAKAR", "THIES", 70, 75);
        ajouterRoute("DAKAR", "ZIGUINCHOR", 442, 400);
        ajouterRoute("DAKAR", "KOLDA", 500, 450);
        ajouterRoute("DAKAR", "KAOLACK", 180, 160);
        ajouterRoute("DAKAR", "SAINT_LOUIS", 247, 220);
        ajouterRoute("DAKAR", "MATAM", 529, 480);
        ajouterRoute("DAKAR", "TAMBA", 465, 420);
        ajouterRoute("DAKAR", "KEDOUGOU", 687, 600);
        ajouterRoute("DAKAR", "DIOURBEL", 150, 130);
        ajouterRoute("DAKAR", "KAFFRINE", 260, 230);
        ajouterRoute("DAKAR", "SEDHIOU", 380, 340);
        ajouterRoute("DAKAR", "FATICK", 140, 125);
        ajouterRoute("THIES", "LOUGA", 135, 120);
        ajouterRoute("KAOLACK", "FATICK", 43, 45);
        ajouterRoute("KAOLACK", "KAFFRINE", 65, 60);
        ajouterRoute("TAMBA", "KEDOUGOU", 235, 225);
        ajouterRoute("KOLDA", "ZIGUINCHOR", 185, 195);
        ajouterRoute("KOLDA", "SEDHIOU", 92, 90);
    }

    private void ajouterRegion(Region r) {
        regions.put(r.getId(), r);
        adjacence.putIfAbsent(r.getId(), new ArrayList<>());
    }

    private void ajouterRoute(String idDep, String idDest, double dist, int temps) {
        Region dep = regions.get(idDep);
        Region dest = regions.get(idDest);
        if (dep != null && dest != null) {
            adjacence.get(idDep).add(new Route(dep, dest, dist, temps));
            adjacence.get(idDest).add(new Route(dest, dep, dist, temps));
        }
    }

    public List<Region> calculerDijkstra(String departId, String arriveeId) {
        notif.envoyerStatut("Dijkstra demarre : " + departId + " -> " + arriveeId);

        Map<String, Double> distances = new HashMap<>();
        Map<String, String> parents = new HashMap<>();
        PriorityQueue<String> file = new PriorityQueue<>(Comparator.comparing(distances::get));

        for (String id : regions.keySet()) distances.put(id, Double.MAX_VALUE);
        distances.put(departId, 0.0);
        file.add(departId);

        while (!file.isEmpty()) {
            String actuel = file.poll();
            if (actuel.equals(arriveeId)) break;

            for (Route r : adjacence.getOrDefault(actuel, new ArrayList<>())) {
                double nouv = distances.get(actuel) + r.getDistance();
                if (nouv < distances.get(r.getDestination().getId())) {
                    distances.put(r.getDestination().getId(), nouv);
                    parents.put(r.getDestination().getId(), actuel);
                    file.add(r.getDestination().getId());
                }
            }
        }

        List<Region> chemin = reconstruireChemin(parents, arriveeId);
        notif.envoyerStatut("Dijkstra termine : " + chemin.size() + " etapes trouvees");
        return chemin;
    }

    public List<Region> calculerBellmanFord(String departId, String arriveeId) {
        notif.envoyerStatut("Bellman-Ford demarre : " + departId + " -> " + arriveeId);

        Map<String, Double> distances = new HashMap<>();
        Map<String, String> parents = new HashMap<>();
        for (String id : regions.keySet()) distances.put(id, Double.MAX_VALUE);
        distances.put(departId, 0.0);

        int n = regions.size();
        for (int i = 0; i < n - 1; i++) {
            boolean maj = false;
            for (String dep : adjacence.keySet()) {
                if (distances.get(dep) == Double.MAX_VALUE) continue;
                for (Route r : adjacence.get(dep)) {
                    String destId = r.getDestination().getId();
                    double nouv = distances.get(dep) + r.getDistance();
                    if (nouv < distances.get(destId)) {
                        distances.put(destId, nouv);
                        parents.put(destId, dep);
                        maj = true;
                    }
                }
            }
            if (!maj) break;
        }

        List<Region> chemin = reconstruireChemin(parents, arriveeId);
        notif.envoyerStatut("Bellman-Ford termine : " + chemin.size() + " etapes trouvees");
        return chemin;
    }

    public List<Region> calculerTSP(String departId) {
        notif.envoyerStatut("TSP demarre depuis " + departId + "...");

        List<String> nonVisites = new ArrayList<>(regions.keySet());
        List<Region> chemin = new ArrayList<>();
        String actuel = departId;
        nonVisites.remove(actuel);
        chemin.add(regions.get(actuel));

        while (!nonVisites.isEmpty()) {
            String plusProche = null;
            double distMin = Double.MAX_VALUE;

            for (String candidat : nonVisites) {
                List<Region> st = calculerDijkstra(actuel, candidat);
                double dist = calculerDistanceTotale(st);
                if (dist < distMin) {
                    distMin = dist;
                    plusProche = candidat;
                }
            }

            if (plusProche == null) break;

            List<Region> st = calculerDijkstra(actuel, plusProche);
            for (int i = 1; i < st.size(); i++) chemin.add(st.get(i));
            nonVisites.remove(plusProche);
            actuel = plusProche;

            notif.envoyerStatut("TSP : " + chemin.size() + " regions visitees sur " + regions.size());
        }

        notif.envoyerStatut("TSP termine ! " + chemin.size() + " etapes.");
        return chemin;
    }

    private List<Region> reconstruireChemin(Map<String, String> parents, String arriveeId) {
        LinkedList<Region> chemin = new LinkedList<>();
        String courant = arriveeId;
        while (courant != null) {
            if (regions.get(courant) == null) return new LinkedList<>();
            chemin.addFirst(regions.get(courant));
            courant = parents.get(courant);
        }
        return chemin;
    }

    public double calculerDistanceTotale(List<Region> chemin) {
        double total = 0;
        for (int i = 0; i < chemin.size() - 1; i++) {
            String dep = chemin.get(i).getId();
            String dest = chemin.get(i + 1).getId();
            for (Route r : adjacence.getOrDefault(dep, new ArrayList<>())) {
                if (r.getDestination().getId().equals(dest)) {
                    total += r.getDistance();
                    break;
                }
            }
        }
        return total;
    }

    public int calculerTempsTotalMinutes(List<Region> chemin) {
        int total = 0;
        for (int i = 0; i < chemin.size() - 1; i++) {
            String dep = chemin.get(i).getId();
            String dest = chemin.get(i + 1).getId();
            for (Route r : adjacence.getOrDefault(dep, new ArrayList<>())) {
                if (r.getDestination().getId().equals(dest)) {
                    total += r.getTempsMinutes();
                    break;
                }
            }
        }
        return total;
    }

    public Collection<Region> getAllRegions() {
        return regions.values();
    }
}
