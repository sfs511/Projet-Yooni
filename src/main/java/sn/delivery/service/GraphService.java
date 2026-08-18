
package sn.tripplanner.service;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import sn.tripplanner.model.Region;
import sn.tripplanner.model.Route;
import sn.tripplanner.network.NotificationWebSocket;
import jakarta.annotation.PostConstruct;
import java.util.*;


// @Service : indique a Spring que cette classe est un composant de service.
//class principale
@Service
public class GraphService {


    // Map collection qui stocke toutes les regions avec leur ID comme cle.
    // HashMap permet un acces en O(1) : retrouver une region par son ID est instantane
    private final Map<String, Region> regions = new HashMap<>();


   // Map une collection qui stocke toutes les route avec les liste adjacent
    //HashMap permet un acces en O(1) : retrouver une route par son ID est instantane
    private final Map<String, List<Route>> adjacence = new HashMap<>();

    // Reference vers le composant de notification.
    // "final" signifie qu'elle sera assignee une seule fois dans le constructeur
    private final NotificationWebSocket notif;


    // Constructeur de GraphService.
    // @Lazy sur NotificationWebSocket evite une dependance circulaire :
    // ce qui casse le cycle et permet a Spring de tout initialiser correctement.
    public GraphService(@Lazy NotificationWebSocket notif) {
        this.notif = notif;
    }



     // @PostConstruct : cette methode est appelee automatiquement par Spring
    // juste apres que le constructeur a ete execute et que toutes les
    // injections de dependances ont ete faites.
    // C'est ici qu'on remplit le graphe avec les donnees reelles du Senegal.
    @PostConstruct
    public void initGraph() {
        // CREATION DES 14 NOEUDS DU GRAPHE (les regions du Senegal)
        // Region 1 : Dakar - capitale et point de depart par defaut
        // --- Étape 1 : Création des régions (Nœuds) ---
    ajouterRegion(new Region("DAKAR", "Dakar", 14.7167, -17.4677, "La capitale."));
    ajouterRegion(new Region("THIES", "Thiès", 14.791, -16.935, "Le carrefour."));
    ajouterRegion(new Region("SAINT_LOUIS", "Saint-Louis", 16.024, -16.489, "La Venise africaine."));
    ajouterRegion(new Region("DIOURBEL", "Diourbel", 14.656, -16.236, "La ville de la Grande Mosquée."));
    ajouterRegion(new Region("LOUGA", "Louga", 15.618, -16.224, "Le Ndiambour."));
    ajouterRegion(new Region("MATAM", "Matam", 15.655, -13.255, "Le Fouta."));
    ajouterRegion(new Region("KAOLACK", "Kaolack", 14.144, -16.083, "Le bassin arachidier."));
    ajouterRegion(new Region("FATICK", "Fatick", 14.358, -16.413, "Le Sine."));
    ajouterRegion(new Region("KAFFRINE", "Kaffrine", 14.105, -15.550, "Le Ndoucoumane."));
    ajouterRegion(new Region("TAMBA", "Tambacounda", 13.770, -13.667, "Le Sénégal Oriental."));
    ajouterRegion(new Region("KEDOUGOU", "Kédougou", 12.557, -12.174, "Les cascades et montagnes."));
    ajouterRegion(new Region("KOLDA", "Kolda", 12.883, -14.950, "La Haute Casamance."));
    ajouterRegion(new Region("SEDHIOU", "Sédhiou", 12.708, -15.556, "Le Pakao."));
    ajouterRegion(new Region("ZIGUINCHOR", "Ziguinchor", 12.583, -16.267, "La Basse Casamance."));


        
        // CREATION DES ARETES DU GRAPHE (routes entre les regions)
        // Format : ajouterRoute("ID_DEPART", "ID_ARRIVEE", distanceKm, tempsMinutes)
        // Les distances sont les vraies valeurs Google Maps.
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


// --- Étape 3 : Connexions secondaires entre régions ---
    // (Optionnel : tu peux garder ces routes pour que Dijkstra trouve des chemins 
    // qui ne passent pas forcément par Dakar)
    ajouterRoute("THIES", "LOUGA", 135, 120);
    ajouterRoute("KAOLACK", "FATICK", 43, 45);
    ajouterRoute("KAOLACK", "KAFFRINE", 65, 60);
    ajouterRoute("TAMBA", "KEDOUGOU", 235, 225);
    ajouterRoute("KOLDA", "ZIGUINCHOR", 185, 195);
    ajouterRoute("KOLDA", "SEDHIOU", 92, 90);
    }
 
    /**
     * Methode privee utilitaire : ajoute une region au graphe.
     * @param r : l'objet Region a ajouter
     */
    //Declaration de la methide ajouterRegion
    private void ajouterRegion(Region r) {
        // Enregistre la region dans la map avec son ID comme cle
        regions.put(r.getId(), r); 
 
        // Initialise une liste vide de routes pour cette region
        // putIfAbsent : ne fait rien si la cle existe deja (securite)
        // Cette liste sera remplie par ajouterRoute()
        adjacence.putIfAbsent(r.getId(), new ArrayList<>());
    }
 
    /**
     * Methode privee utilitaire : ajoute une route bidirectionnelle entre deux regions.
     * les parametre dans la methode ajouterRoute()
     * @param idDep  : ID de la region de depart
     * @param idDest : ID de la region de destination
     * @param dist   : distance en kilometres (poids de l'arete)
     * @param temps  : temps de trajet en minutes
     */

    //Methode ajoutrRoute()
    private void ajouterRoute(String idDep, String idDest, double dist, int temps) {
        // Recupere l'objet Region de depart depuis la map
        Region dep  = regions.get(idDep);
 
        // Recupere l'objet Region de destination depuis la map
        Region dest = regions.get(idDest);
 
        // Verifie que les deux regions existent avant de creer le lien
        // Evite les NullPointerException si un ID est mal orthographie
        if (dep != null && dest != null) {
            // Ajoute la route de A vers B dans la liste d'adjacence de A
            adjacence.get(idDep).add(new Route(dep, dest, dist, temps));
 
            // Ajoute la route de B vers A dans la liste d'adjacence de B
            // Le graphe est non-oriente : on peut aller dans les deux sens
            adjacence.get(idDest).add(new Route(dest, dep, dist, temps));
        }
    }
 


  
    // ALGORITHME DE DIJKSTRA
    // But : trouver le chemin le plus court entre deux regions.
    // Complexite : O((V + E) log V) ou V = noeuds, E = aretes
    //methode calculerDjikstra() dans la collection liste<region>
    public List<Region> calculerDijkstra(String departId, String arriveeId) {
 
        // Envoie un message de statut dans les logs Spring Boot
        notif.envoyerStatut("Dijkstra demarre : " + departId + " -> " + arriveeId);
 
        // Map qui stocke la distance minimale connue depuis le depart
        // jusqu'a chaque region. Initialisee a l'infini pour toutes les regions.
        Map<String, Double> distances = new HashMap<>();
 
        // Map qui stocke le "parent" de chaque region dans le chemin optimal.
        // Permet de reconstruire le chemin complet a la fin.
        // Exemple : parents.get("KAOLACK") = "FATICK" signifie qu'on vient de Fatick
        Map<String, String> parents = new HashMap<>();
 
        // File de priorite qui trie les regions par distance croissante.
        // Comparator.comparing(distances::get) = trie selon la valeur dans la map distances.
        // La region avec la plus petite distance est toujours en tete de file.
        PriorityQueue<String> file = new PriorityQueue<>(Comparator.comparing(distances::get));
 
        // Initialisation : toutes les distances a l'infini (region non encore visitee)
        for (String id : regions.keySet()) distances.put(id, Double.MAX_VALUE);
 
        // Le depart est a distance 0 de lui-meme
        distances.put(departId, 0.0);
 
        // On commence l'exploration depuis la region de depart
        file.add(departId);
 
        // Boucle principale : tant qu'il reste des regions a explorer
        while (!file.isEmpty()) {
 
            // Extrait la region avec la plus petite distance (O(log n))
            String actuel = file.poll();
 
            // Optimisation : si on a atteint la destination, on arrete
            // Il est inutile de continuer car on a trouve le chemin optimal
            if (actuel.equals(arriveeId)) break;
 
            // Explore tous les voisins de la region actuelle
            for (Route r : adjacence.getOrDefault(actuel, new ArrayList<>())) {
 
                // Calcule la distance totale pour atteindre ce voisin via la region actuelle
                // = distance jusqu'a actuel + distance de la route vers le voisin
                double nouv = distances.get(actuel) + r.getDistance();
 
                // Si ce nouveau chemin est plus court que celui deja connu
                if (nouv < distances.get(r.getDestination().getId())) {
 
                    // Met a jour la distance minimale pour ce voisin
                    distances.put(r.getDestination().getId(), nouv);
 
                    // Enregistre qu'on vient de "actuel" pour atteindre ce voisin
                    parents.put(r.getDestination().getId(), actuel);
 
                    // Ajoute le voisin dans la file pour l'explorer plus tard
                    file.add(r.getDestination().getId());
                }
            }
        }
 
        // Reconstruit la liste ordonnee des regions du chemin optimal
        List<Region> chemin = reconstruireChemin(parents, arriveeId);
 
        // Envoie le statut de fin avec le nombre d'etapes trouvees
        notif.envoyerStatut("Dijkstra termine : " + chemin.size() + " etapes trouvees");
 
        // Retourne le chemin complet de depart a arrivee
        return chemin;
    }
 




    
    // ALGORITHME DE BELLMAN-FORD
    // But : trouver le chemin le plus court entre deux regions.
    // Principe : repete n-1 fois le relachement de toutes les aretes.
    // Complexite : O(V * E) - plus lent que Dijkstra mais plus robuste
    //Methode calculerBellmanFord() dans la collection list<region>
    public List<Region> calculerBellmanFord(String departId, String arriveeId) {
 
        // Envoie un message de statut dans les logs
        notif.envoyerStatut("Bellman-Ford demarre : " + departId + " -> " + arriveeId);
 
        // Map des distances minimales depuis le depart (toutes a l'infini au debut)
        Map<String, Double> distances = new HashMap<>();
 
        // Map des parents pour reconstruire le chemin a la fin
        Map<String, String> parents = new HashMap<>();
 
        // Initialise toutes les distances a l'infini
        for (String id : regions.keySet()) distances.put(id, Double.MAX_VALUE);
 
        // La region de depart est a distance 0 d'elle-meme
        distances.put(departId, 0.0);
 
        // n = nombre total de regions dans le graphe (14 ici)
        int n = regions.size();
 
        // Repete n-1 fois : garantit que tous les chemins possibles ont ete explores
        // Car le chemin le plus long sans cycle passe par au maximum n-1 aretes
        for (int i = 0; i < n - 1; i++) {
 
            // Indicateur : y a-t-il eu une mise a jour pendant cette iteration ?
            boolean maj = false;
 
            // Parcourt toutes les regions du graphe
            for (String dep : adjacence.keySet()) {
 
                // Ignore les regions encore inaccessibles (distance infinie)
                // car on ne peut pas ameliorer un chemin via un noeud inaccessible
                if (distances.get(dep) == Double.MAX_VALUE) continue;
 
                // Parcourt toutes les routes sortantes de cette region
                for (Route r : adjacence.get(dep)) {
 
                    // ID de la region de destination de cette route
                    String destId = r.getDestination().getId();
 
                    // Calcule la nouvelle distance potentielle via cette route
                    double nouv = distances.get(dep) + r.getDistance();
 
                    // Si ce chemin est meilleur que celui connu jusqu'ici
                    if (nouv < distances.get(destId)) {
 
                        // Met a jour la distance minimale
                        distances.put(destId, nouv);
 
                        // Enregistre le parent pour la reconstruction du chemin
                        parents.put(destId, dep);
 
                        // Signale qu'une amelioration a ete trouvee
                        maj = true;
                    }
                }
            }
 
            // Optimisation : si aucune amelioration, l'algorithme a converge
            // On peut arreter avant les n-1 iterations
            if (!maj) break;
        }
 
        // Reconstruit le chemin optimal de depart a arrivee
        List<Region> chemin = reconstruireChemin(parents, arriveeId);
 
        // Envoie le statut de fin
        notif.envoyerStatut("Bellman-Ford termine : " + chemin.size() + " etapes trouvees");
 
        return chemin;
    }
 



   
    // ALGORITHME TSP (Voyageur de Commerce)
    // But : trouver un itineraire qui visite TOUTES les 14 regions
    //       en partant de Dakar sans repasser deux fois par la meme.
    // Methode : heuristique du voisin le plus proche (Nearest Neighbor).
    //           A chaque etape, on choisit la region non visitee la plus proche.
    //           Ce n'est pas la solution optimale mais une bonne approximation.
    //methode calculerTSP() dans la collection list<Region>
    public List<Region> calculerTSP(String departId) {
 
        // Envoie le statut de debut
        notif.envoyerStatut("TSP demarre depuis " + departId + "...");
 
        // Liste de toutes les regions pas encore visitees
        // Au debut : contient les 14 regions
        List<String> nonVisites = new ArrayList<>(regions.keySet());
 
        // Liste du chemin final : contiendra toutes les regions dans l'ordre de visite
        List<Region> chemin = new ArrayList<>();
 
        // Positionne le voyageur au point de depart (Dakar)
        String actuel = departId;
 
        // Retire le depart de la liste des non visites
        nonVisites.remove(actuel);
 
        // Ajoute Dakar comme premiere etape du chemin
        chemin.add(regions.get(actuel));
 
        // Continue tant qu'il reste des regions non visitees
        while (!nonVisites.isEmpty()) {
 
            // Variable pour stocker la region la plus proche trouvee
            String plusProche = null;
 
            // Distance minimale initialisee a l'infini
            double distMin = Double.MAX_VALUE;
 
            // Teste chaque region non visitee pour trouver la plus proche
            for (String candidat : nonVisites) {
 
                // Calcule le chemin le plus court vers ce candidat via Dijkstra
                List<Region> st = calculerDijkstra(actuel, candidat);
 
                // Calcule la distance totale de ce sous-trajet
                double dist = calculerDistanceTotale(st);
 
                // Si ce candidat est plus proche que le meilleur trouve jusqu'ici
                if (dist < distMin) {
                    distMin    = dist;      // Met a jour la distance minimale
                    plusProche = candidat;  // Enregistre ce candidat comme le plus proche
                }
            }
 
            // Si aucun voisin accessible, on s'arrete (graphe non connexe)
            if (plusProche == null) break;
 
            // Recalcule le sous-trajet optimal vers la region la plus proche
            List<Region> st = calculerDijkstra(actuel, plusProche);
 
            // Ajoute toutes les etapes intermediaires au chemin final
            // On commence a l'index 1 pour ne pas dupliquer la region actuelle
            for (int i = 1; i < st.size(); i++) chemin.add(st.get(i));
 
            // Retire la region visitee de la liste des non visites
            nonVisites.remove(plusProche);
 
            // Avance le voyageur vers la region qu'on vient de visiter
            actuel = plusProche;
 
            // Envoie une notification de progression
            notif.envoyerStatut("TSP : " + chemin.size() + " regions visitees sur " + regions.size());
        }
 
        // Envoie la notification de fin avec le nombre total d'etapes
        notif.envoyerStatut("TSP termine ! " + chemin.size() + " etapes.");
 
        return chemin;
    }
 
    // ================================================================
    // METHODES UTILITAIRES
    // ================================================================
 
    /**
     * Reconstruit le chemin complet depuis la map des parents.
     * Remonte de l'arrivee jusqu'au depart en suivant la chaine des parents,
     * puis inverse la liste pour obtenir l'ordre depart -> arrivee.
     * @param parents   : map associant chaque region a son predecesseur optimal
     * @param arriveeId : ID de la region d'arrivee
     * @return : liste ordonnee des regions du chemin (depart en premier)
     */
    private List<Region> reconstruireChemin(Map<String, String> parents, String arriveeId) {
 
        // LinkedList est efficace pour les insertions en tete de liste (addFirst)
        LinkedList<Region> chemin = new LinkedList<>();
 
        // Commence depuis l'arrivee et remonte vers le depart
        String courant = arriveeId;
 
        // Continue tant qu'on a un parent (on s'arrete quand on atteint le depart
        // car le depart n'a pas de parent dans la map)
        while (courant != null) {
 
            // Si la region n'existe pas dans notre graphe, retourne liste vide
            // (securite contre les IDs invalides)
            if (regions.get(courant) == null) return new LinkedList<>();
 
            // Insere la region en TETE de liste (inverse l'ordre de remontee)
            chemin.addFirst(regions.get(courant));
 
            // Passe au parent de la region courante
            // Quand courant = departId, parents.get(departId) = null -> boucle s'arrete
            courant = parents.get(courant);
        }
 
        // Retourne le chemin dans le bon ordre : depart -> ... -> arrivee
        return chemin;
    }
 
    /**
     * Calcule la distance totale d'un chemin en kilometres.
     * Parcourt les paires de regions consecutives et somme leurs distances.
     * @param chemin : liste ordonnee des regions
     * @return : distance totale en km
     */
    public double calculerDistanceTotale(List<Region> chemin) {
        double total = 0;
 
        // Parcourt chaque paire consecutive : (0,1), (1,2), (2,3)...
        for (int i = 0; i < chemin.size() - 1; i++) {
 
            // ID de la region courante
            String dep  = chemin.get(i).getId();
 
            // ID de la region suivante
            String dest = chemin.get(i + 1).getId();
 
            // Cherche dans la liste d'adjacence la route qui relie dep a dest
            for (Route r : adjacence.getOrDefault(dep, new ArrayList<>())) {
 
                // Quand on trouve la bonne route
                if (r.getDestination().getId().equals(dest)) {
 
                    // Ajoute sa distance au total
                    total += r.getDistance();
 
                    // break : inutile de continuer a chercher pour cette paire
                    break;
                }
            }
        }
        return total;
    }
 
    /**
     * Calcule le temps de trajet total d'un chemin en minutes.
     * Meme logique que calculerDistanceTotale mais avec les temps.
     * @param chemin : liste ordonnee des regions
     * @return : temps total en minutes
     */
    public int calculerTempsTotalMinutes(List<Region> chemin) {
        int total = 0;
 
        // Parcourt chaque paire consecutive de regions
        for (int i = 0; i < chemin.size() - 1; i++) {
            String dep  = chemin.get(i).getId();
            String dest = chemin.get(i + 1).getId();
 
            // Cherche la route correspondante dans la liste d'adjacence
            for (Route r : adjacence.getOrDefault(dep, new ArrayList<>())) {
                if (r.getDestination().getId().equals(dest)) {
 
                    // Ajoute le temps de cette route au total
                    total += r.getTempsMinutes();
                    break;
                }
            }
        }
        return total;
    }
 
    /**
     * Retourne toutes les regions du graphe.
     * Appelee par TripController pour envoyer la liste au frontend (carte Leaflet).
     * @return : collection de toutes les regions (ordre non garanti avec HashMap)
     */
    public Collection<Region> getAllRegions() {
        // regions.values() retourne toutes les valeurs de la HashMap
        return regions.values();
    }
}
 