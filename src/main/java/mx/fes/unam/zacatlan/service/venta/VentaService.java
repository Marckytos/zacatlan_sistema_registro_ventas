package mx.fes.unam.zacatlan.service.venta;

import mx.fes.unam.zacatlan.model.entities.producto.ProductoEntity;
import mx.fes.unam.zacatlan.model.entities.venta.VentaDTO;
import mx.fes.unam.zacatlan.model.entities.venta.VentaEntity;
import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorEntity;
import mx.fes.unam.zacatlan.repository.ProductoRepository;
import mx.fes.unam.zacatlan.repository.VendedorRepository;
import mx.fes.unam.zacatlan.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    private final VentaMapper ventaMapper;
    private final VentaRepository ventaRepository;
    private final VendedorRepository vendedorRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaMapper ventaMapper, VentaRepository ventaRepository,
                        VendedorRepository vendedorRepository, ProductoRepository productoRepository) {
        this.ventaMapper = ventaMapper;
        this.ventaRepository = ventaRepository;
        this.vendedorRepository = vendedorRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Registra una venta. Solo para VENDEDORES activos.
     * BLINDAJE: Valida vendedor activo, producto aprobado y calcula el total en backend.
     */
    @Transactional
    public VentaDTO guardarVenta(VentaDTO ventaDTO) {
        // 1. Validar que el vendedor existe y está activo
        VendedorEntity vendedor = vendedorRepository.findById(ventaDTO.getVendedor().getId())
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        if (!vendedor.getActivo()) {
            throw new RuntimeException("El vendedor está inactivo. No puede realizar ventas.");
        }

        // 2. Validar que el producto existe y está aprobado
        ProductoEntity producto = productoRepository.findById(ventaDTO.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        if (!producto.getAprobado()) {
            throw new RuntimeException("El producto no está aprobado para la venta.");
        }

        // 3. Calcular el total en BACKEND (no confiar en el frontend)
        BigDecimal totalCalculado = producto.getPrecio().multiply(BigDecimal.valueOf(ventaDTO.getCantidad()));

        // 4. Preparar entidad
        VentaEntity venta = ventaMapper.toEntity(ventaDTO);
        venta.setTotal(totalCalculado);
        venta.setFecha(LocalDateTime.now()); // Fecha del servidor, no del cliente
        venta.setVendedor(vendedor);
        venta.setProducto(producto);

        VentaEntity guardada = ventaRepository.save(venta);

        // 5. Actualizar la fecha de última venta del vendedor
        vendedor.setUltimaVenta(guardada.getFecha());
        vendedorRepository.save(vendedor);

        return ventaMapper.toDTO(guardada);
    }

    /**
     * Modifica una venta existente. SOLO para ADMIN.
     * BLINDAJE: No permite cambiar vendedor, producto ni fecha; solo cantidad y recalcula total.
     */
    @Transactional
    public VentaDTO modificarVenta(VentaDTO ventaDTO) {
        VentaEntity existente = ventaRepository.findById(ventaDTO.getId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        // Solo se permite modificar cantidad
        if (ventaDTO.getCantidad() != null && ventaDTO.getCantidad() > 0) {
            existente.setCantidad(ventaDTO.getCantidad());
            // Recalcular total con el precio actual del producto
            BigDecimal nuevoTotal = existente.getProducto().getPrecio()
                    .multiply(BigDecimal.valueOf(ventaDTO.getCantidad()));
            existente.setTotal(nuevoTotal);
        }

        VentaEntity actualizada = ventaRepository.save(existente);
        return ventaMapper.toDTO(actualizada);
    }

    public VentaDTO obtenerVenta(Long id) {
        return ventaMapper.toDTO(ventaRepository.findById(id).orElse(null));
    }

    /**
     * NO existe método eliminarVenta() por requerimiento del cliente.
     * El administrador no puede borrar ventas, solo modificar.
     */

    public List<VentaDTO> listarVentas() {
        return ventaRepository.findAll().stream().map(ventaMapper::toDTO).toList();
    }

    public List<VentaDTO> listarVentasPorVendedor(Long vendedorId) {
        return ventaRepository.findByVendedorIdOrderByFechaDesc(vendedorId)
                .stream().map(ventaMapper::toDTO).toList();
    }
}