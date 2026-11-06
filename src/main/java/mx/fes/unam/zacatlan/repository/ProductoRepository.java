package mx.fes.unam.zacatlan.repository;

import mx.fes.unam.zacatlan.model.entities.producto.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    List<ProductoEntity> findByAprobadoTrue();
    List<ProductoEntity> findByBloqueIdAndAprobadoTrue(Long bloqueId);
}

