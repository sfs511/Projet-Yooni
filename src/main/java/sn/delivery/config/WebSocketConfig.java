//c’est le fichier configurateur du tunnel de communication bidirectionnel 
// entre le navigateur et le serveur Java.

package sn.tripplanner.config;
// Déclaration du package organisant les classes de configuration

import org.springframework.context.annotation.Configuration;
// Importation de l'annotation pour marquer cette classe comme source de configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
// Importation pour configurer le gestionnaire de messages (broker)
import org.springframework.web.socket.config.annotation.*;
// Importation des outils nécessaires à la configuration des WebSockets



@Configuration
// Indique à Spring que cette classe contient des réglages de configuration
@EnableWebSocketMessageBroker
// Active la gestion des messages WebSocket via un "broker" (courtier)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
// Définition de la classe qui implémente l'interface de configuration WebSocket



    @Override
    // Indique que l'on surcharge une méthode de l'interface parente

    public void configureMessageBroker(MessageBrokerRegistry config) {
    // Méthode pour configurer les destinations des messages
        config.enableSimpleBroker("/topic");
        // Active un broker simple en mémoire pour envoyer des messages du serveur vers les clients sur '/topic'
        config.setApplicationDestinationPrefixes("/app");
        // Définit le préfixe que le client doit utiliser pour envoyer des messages au serveur
    }


    
    @Override
    // Indique la surcharge de la méthode d'enregistrement des points d'accès
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    // Méthode pour enregistrer les points de connexion (endpoints)
        registry.addEndpoint("/ws-trip")
        // Crée le point de connexion WebSocket nommé '/ws-trip'
                .setAllowedOriginPatterns("*")
                // Autorise toutes les origines (domaines) à se connecter
                .withSockJS(); 
                // Active SockJS pour offrir une solution de secours si le navigateur ne supporte pas les WebSockets
    }
}