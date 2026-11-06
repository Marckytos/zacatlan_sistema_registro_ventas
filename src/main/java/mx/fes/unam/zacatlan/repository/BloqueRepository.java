package mx.fes.unam.zacatlan.repository;

import mx.fes.unam.zacatlan.model.entities.bloque.BloqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloqueRepository extends JpaRepository<BloqueEntity, Long> {
}
