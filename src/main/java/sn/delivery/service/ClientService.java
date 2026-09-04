package sn.delivery.service;

import sn.delivery.dto.ClientDTO;
import sn.delivery.model.Client;
import sn.delivery.model.User;
import sn.delivery.model.UserRole;
import sn.delivery.repository.ClientRepository;
import sn.delivery.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientService(ClientRepository clientRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ClientDTO createClient(String email, String password, String telephone, String nom, String prenom, String adresse, String ville) {
        User user = new User(email, passwordEncoder.encode(password), telephone, UserRole.CLIENT);
        user = userRepository.save(user);

        Client client = new Client(user, nom, prenom);
        client.setAdresse(adresse);
        client.setVille(ville);
        client = clientRepository.save(client);

        return toDTO(client);
    }

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ClientDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouve"));
        return toDTO(client);
    }

    public ClientDTO getClientByUserId(Long userId) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client non trouve"));
        return toDTO(client);
    }

    public ClientDTO updateClient(Long id, String nom, String prenom, String adresse, String ville) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouve"));
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setAdresse(adresse);
        client.setVille(ville);
        client = clientRepository.save(client);
        return toDTO(client);
    }

    private ClientDTO toDTO(Client client) {
        return new ClientDTO(
                client.getId(),
                client.getUser().getId(),
                client.getUser().getEmail(),
                client.getNom(),
                client.getPrenom(),
                client.getUser().getTelephone(),
                client.getAdresse(),
                client.getVille(),
                client.getCreatedAt()
        );
    }
}