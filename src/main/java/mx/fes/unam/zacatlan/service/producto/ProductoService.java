package mx.fes.unam.zacatlan.service.producto;

import mx.fes.unam.zacatlan.model.entities.producto.ProductoDTO;
import mx.fes.unam.zacatlan.model.entities.producto.ProductoEntity;
import mx.fes.unam.zacatlan.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoMapper productoMapper;
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoMapper productoMapper, ProductoRepository productoRepository) {
        this.productoMapper = productoMapper;
        this.productoRepository = productoRepository;
    }

    public ProductoDTO guardarProducto(ProductoDTO productoDTO) {
        ProductoEntity entity = productoRepository.save(productoMapper.toEntity(productoDTO));
        return productoMapper.toDTO(entity);
    }

    public ProductoDTO obtenerProducto(Long id) {
        return productoMapper.toDTO(productoRepository.findById(id).orElse(null));
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public List<ProductoDTO> listarProductos() {
        return productoRepository.findAll().stream().map(productoMapper::toDTO).toList();
    }

    public List<ProductoDTO> listarProductosAprobados() {
        return productoRepository.findByAprobadoTrue().stream().map(productoMapper::toDTO).toList();
    }

    public List<ProductoDTO> listarProductosAprobadosPorBloque(Long bloqueId) {
        return productoRepository.findByBloqueIdAndAprobadoTrue(bloqueId)
                .stream().map(productoMapper::toDTO).toList();
    }

    /**
     * Aprueba un producto para que pueda ser vendido. Solo el ADMIN debe llamar esto.
     */
    public ProductoDTO aprobarProducto(Long id) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setAprobado(true);
        return productoMapper.toDTO(productoRepository.save(producto));
    }
}