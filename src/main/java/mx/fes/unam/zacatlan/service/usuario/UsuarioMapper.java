package mx.fes.unam.zacatlan.service.usuario;

import mx.fes.unam.zacatlan.model.entities.usuario.UsuarioDTO;
import mx.fes.unam.zacatlan.model.entities.usuario.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioEntity toEntity(UsuarioDTO dto) {
        UsuarioEntity entity = new UsuarioEntity();
        if (dto != null) {
            entity.setId(dto.getId());
            entity.setUsername(dto.getUsername());
            entity.setPassword(dto.getPassword());
            entity.setRol(dto.getRol());
        }
        return entity;
    }

    public UsuarioDTO toDTO(UsuarioEntity entity) {
        UsuarioDTO dto = null;
        if (entity != null) {
            dto = UsuarioDTO.builder()
                    .id(entity.getId())
                    .username(entity.getUsername())
                    .password(entity.getPassword())
                    .rol(entity.getRol())
                    .build();
        }
        return dto;
    }
}