package mx.fes.unam.zacatlan.service.venta;

import mx.fes.unam.zacatlan.model.entities.venta.VentaDTO;
import mx.fes.unam.zacatlan.model.entities.venta.VentaEntity;
import mx.fes.unam.zacatlan.service.producto.ProductoMapper;
import mx.fes.unam.zacatlan.service.vendedor.VendedorMapper;
import org.springframework.stereotype.Component;

@Component
public class VentaMapper {

    private final VendedorMapper vendedorMapper;
    private final ProductoMapper productoMapper;

    public VentaMapper(VendedorMapper vendedorMapper, ProductoMapper productoMapper) {
        this.vendedorMapper = vendedorMapper;
        this.productoMapper = productoMapper;
    }

    public VentaEntity toEntity(VentaDTO dto) {
        VentaEntity entity = new VentaEntity();
        if (dto != null) {
            entity.setId(dto.getId());
            entity.setFecha(dto.getFecha());
            entity.setCantidad(dto.getCantidad());
            entity.setTotal(dto.getTotal());
            if (dto.getVendedor() != null) {
                entity.setVendedor(vendedorMapper.toEntity(dto.getVendedor()));
            }
            if (dto.getProducto() != null) {
                entity.setProducto(productoMapper.toEntity(dto.getProducto()));
            }
        }
        return entity;
    }

    public VentaDTO toDTO(VentaEntity entity) {
        VentaDTO dto = null;
        if (entity != null) {
            dto = VentaDTO.builder()
                    .id(entity.getId())
                    .fecha(entity.getFecha())
                    .cantidad(entity.getCantidad())
                    .total(entity.getTotal())
                    .vendedor(entity.getVendedor() != null ? vendedorMapper.toDTO(entity.getVendedor()) : null)
                    .producto(entity.getProducto() != null ? productoMapper.toDTO(entity.getProducto()) : null)
                    .build();
        }
        return dto;
    }
}