package sn.delivery.repository;

import sn.delivery.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByLivreurIdOrderByTimestampDesc(Long livreurId);
    Optional<Position> findFirstByLivreurIdOrderByTimestampDesc(Long livreurId);
}
