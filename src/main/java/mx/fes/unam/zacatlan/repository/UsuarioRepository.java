package mx.fes.unam.zacatlan.repository;

import mx.fes.unam.zacatlan.model.entities.usuario.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByUsername(String username);
}
