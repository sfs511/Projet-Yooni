package sn.delivery.repository;

import sn.delivery.model.Livreur;
import sn.delivery.model.StatutLivreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LivreurRepository extends JpaRepository<Livreur, Long> {
    Optional<Livreur> findByUserId(Long userId);
    List<Livreur> findByStatut(StatutLivreur statut);
    List<Livreur> findByZone(String zone);
    Optional<Livreur> findByUserEmail(String email);
}