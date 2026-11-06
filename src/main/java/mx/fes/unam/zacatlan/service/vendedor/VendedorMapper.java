package mx.fes.unam.zacatlan.service.vendedor;

import mx.fes.unam.zacatlan.model.entities.usuario.UsuarioEntity;
import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorDTO;
import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorEntity;
import org.springframework.stereotype.Component;

@Component
public class VendedorMapper {

    public VendedorEntity toEntity(VendedorDTO dto) {
        VendedorEntity entity = new VendedorEntity();
        if (dto != null) {
            entity.setId(dto.getId());
            entity.setNombre(dto.getNombre());
            entity.setActivo(dto.getActivo());
            entity.setFechaInactivacion(dto.getFechaInactivacion());
            entity.setUltimaVenta(dto.getUltimaVenta());
            if (dto.getUsuarioId() != null) {
                UsuarioEntity usuario = new UsuarioEntity();
                usuario.setId(dto.getUsuarioId());
                entity.setUsuario(usuario);
            }
        }
        return entity;
    }

    public VendedorDTO toDTO(VendedorEntity entity) {
        VendedorDTO dto = null;
        if (entity != null) {
            dto = VendedorDTO.builder()
                    .id(entity.getId())
                    .nombre(entity.getNombre())
                    .activo(entity.getActivo())
                    .fechaInactivacion(entity.getFechaInactivacion())
                    .ultimaVenta(entity.getUltimaVenta())
                    .usuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null)
                    .username(entity.getUsuario() != null ? entity.getUsuario().getUsername() : null)
                    .build();
        }
        return dto;
    }
}