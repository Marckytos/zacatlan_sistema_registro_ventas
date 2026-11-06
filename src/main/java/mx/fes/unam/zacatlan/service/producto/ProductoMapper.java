package mx.fes.unam.zacatlan.service.producto;

import mx.fes.unam.zacatlan.model.entities.bloque.BloqueDTO;
import mx.fes.unam.zacatlan.model.entities.bloque.BloqueEntity;
import mx.fes.unam.zacatlan.model.entities.producto.ProductoDTO;
import mx.fes.unam.zacatlan.model.entities.producto.ProductoEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoEntity toEntity(ProductoDTO dto) {
        ProductoEntity entity = new ProductoEntity();
        if (dto != null) {
            entity.setId(dto.getId());
            entity.setNombre(dto.getNombre());
            entity.setPrecio(dto.getPrecio());
            entity.setAprobado(dto.getAprobado());
            if (dto.getBloque() != null && dto.getBloque().getId() != null) {
                BloqueEntity bloque = new BloqueEntity();
                bloque.setId(dto.getBloque().getId());
                bloque.setNombre(dto.getBloque().getNombre());
                entity.setBloque(bloque);
            }
        }
        return entity;
    }

    public ProductoDTO toDTO(ProductoEntity entity) {
        ProductoDTO dto = null;
        if (entity != null) {
            dto = ProductoDTO.builder()
                    .id(entity.getId())
                    .nombre(entity.getNombre())
                    .precio(entity.getPrecio())
                    .aprobado(entity.getAprobado())
                    .bloque(entity.getBloque() != null ?
                            BloqueDTO.builder()
                                    .id(entity.getBloque().getId())
                                    .nombre(entity.getBloque().getNombre())
                                    .build()
                            : null)
                    .build();
        }
        return dto;
    }
}