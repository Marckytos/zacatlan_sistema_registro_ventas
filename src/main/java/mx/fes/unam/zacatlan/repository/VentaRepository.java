package mx.fes.unam.zacatlan.repository;

import mx.fes.unam.zacatlan.model.entities.venta.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<VentaEntity, Long> {
    List<VentaEntity> findByVendedorIdOrderByFechaDesc(Long vendedorId);
    Optional<VentaEntity> findFirstByVendedorIdOrderByFechaDesc(Long vendedorId);
}
