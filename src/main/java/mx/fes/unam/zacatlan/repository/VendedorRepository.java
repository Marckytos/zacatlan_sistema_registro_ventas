package mx.fes.unam.zacatlan.repository;

import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VendedorRepository extends JpaRepository<VendedorEntity, Long> {
    Optional<VendedorEntity> findByUsuarioId(Long usuarioId);
}
