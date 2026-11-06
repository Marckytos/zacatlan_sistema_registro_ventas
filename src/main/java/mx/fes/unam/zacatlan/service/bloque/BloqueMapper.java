package mx.fes.unam.zacatlan.service.bloque;

import mx.fes.unam.zacatlan.model.entities.bloque.BloqueDTO;
import mx.fes.unam.zacatlan.model.entities.bloque.BloqueEntity;
import org.springframework.stereotype.Component;

@Component
public class BloqueMapper {

    public BloqueEntity toEntity(BloqueDTO dto) {
        BloqueEntity entity = new BloqueEntity();
        if (dto != null) {
            entity.setId(dto.getId());
            entity.setNombre(dto.getNombre());
        }
        return entity;
    }

    public BloqueDTO toDTO(BloqueEntity entity) {
        BloqueDTO dto = null;
        if (entity != null) {
            dto = BloqueDTO.builder()
                    .id(entity.getId())
                    .nombre(entity.getNombre())
                    .build();
        }
        return dto;
    }
}