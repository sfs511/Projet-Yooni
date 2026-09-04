package sn.delivery.repository;

import sn.delivery.model.Livraison;
import sn.delivery.model.StatutLivraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Long> {
    List<Livraison> findByClientId(Long clientId);
    List<Livraison> findByLivreurId(Long livreurId);
    List<Livraison> findByStatut(StatutLivraison statut);
    List<Livraison> findByClientIdAndStatut(Long clientId, StatutLivraison statut);
    List<Livraison> findByLivreurIdAndStatut(Long livreurId, StatutLivraison statut);
}
