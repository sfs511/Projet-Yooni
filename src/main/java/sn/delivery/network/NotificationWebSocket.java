package sn.tripplanner.network;

import org.springframework.stereotype.Component;

/**
 * Composant de notification utilise par GraphService.
 *
 * Dans cette version TCP-only, les notifications sont affichees
 * dans les logs du serveur (console Spring Boot).
 * Elles sont egalement visibles cote client via la console TCP
 * de l'interface HTML.
 *
 * Note : si vous souhaitez ajouter WebSocket plus tard,
 * il suffit d'injecter SimpMessagingTemplate ici.
 */
@Component
public class NotificationWebSocket {

    /**
     * Envoie un message de statut dans les logs du serveur.
     * Appele par GraphService a chaque etape des algorithmes.
     */
    public void envoyerStatut(String message) {
        System.out.println("[ALGO] " + message);
    }
}