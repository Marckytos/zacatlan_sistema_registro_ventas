package mx.fes.unam.zacatlan.service.vendedor;

import mx.fes.unam.zacatlan.model.entities.usuario.UsuarioEntity;
import mx.fes.unam.zacatlan.model.entities.venta.VentaEntity;
import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorDTO;
import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorEntity;
import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorRegistroDTO;
import mx.fes.unam.zacatlan.repository.UsuarioRepository;
import mx.fes.unam.zacatlan.repository.VendedorRepository;
import mx.fes.unam.zacatlan.repository.VentaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VendedorService {

    private final VendedorMapper vendedorMapper;
    private final VendedorRepository vendedorRepository;
    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public VendedorService(VendedorMapper vendedorMapper,
                           VendedorRepository vendedorRepository,
                           VentaRepository ventaRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.vendedorMapper = vendedorMapper;
        this.vendedorRepository = vendedorRepository;
        this.ventaRepository = ventaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un vendedor completo (Usuario + Vendedor).
     * SOLO debe ser llamado por el ADMIN.
     *
     * @Transactional asegura que si falla uno, no se guarda ninguno.
     */
    @Transactional
    public VendedorDTO registrarVendedor(VendedorRegistroDTO registroDTO) {

        if (registroDTO.getPassword() == null || registroDTO.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }
        if (registroDTO.getPassword().length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        // 1. Verificar que el username no exista
        if (usuarioRepository.findByUsername(registroDTO.getUsername()).isPresent()) {
            throw new RuntimeException("El username ya está en uso. Elija otro.");
        }

        // 2. Crear el UsuarioEntity
        UsuarioEntity usuario = UsuarioEntity.builder()
                .username(registroDTO.getUsername())
                .password(passwordEncoder.encode(registroDTO.getPassword()))
                .rol("ROLE_VENDEDOR")
                .build();
        usuario = usuarioRepository.save(usuario);

        // 3. Crear el VendedorEntity asociado
        VendedorEntity vendedor = VendedorEntity.builder()
                .nombre(registroDTO.getNombre())
                .activo(true)
                .usuario(usuario)
                .build();
        vendedor = vendedorRepository.save(vendedor);

        return vendedorMapper.toDTO(vendedor);
    }

    /**
     * Actualiza un vendedor existente (nombre, username, password opcional).
     * solo el admin puede actualizar los datos.
     */

    @Transactional
    public VendedorDTO actualizarVendedor(Long id, VendedorRegistroDTO registroDTO) {
        VendedorEntity vendedor = vendedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

        vendedor.setNombre(registroDTO.getNombre());

        // Actualizar username si cambió
        UsuarioEntity usuario = vendedor.getUsuario();
        if (!usuario.getUsername().equals(registroDTO.getUsername())) {
            if (usuarioRepository.findByUsername(registroDTO.getUsername()).isPresent()) {
                throw new RuntimeException("El username ya está en uso. Elija otro.");
            }
            usuario.setUsername(registroDTO.getUsername());
        }

        // Actualizar password solo si se proporcionó una nueva
        if (registroDTO.getPassword() != null && !registroDTO.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        }

        usuarioRepository.save(usuario);
        vendedorRepository.save(vendedor);

        return vendedorMapper.toDTO(vendedor);
    }

    public VendedorDTO guardarVendedor(VendedorDTO vendedorDTO) {
        VendedorEntity entity = vendedorRepository.save(vendedorMapper.toEntity(vendedorDTO));
        return vendedorMapper.toDTO(entity);
    }

    public VendedorDTO obtenerVendedor(Long id) {
        return vendedorMapper.toDTO(vendedorRepository.findById(id).orElse(null));
    }

    public void eliminarVendedor(Long id) {
        vendedorRepository.deleteById(id);
    }

    public List<VendedorDTO> listarVendedores() {
        return vendedorRepository.findAll().stream().map(vendedorMapper::toDTO).toList();
    }


    public VendedorDTO obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .flatMap(u -> vendedorRepository.findByUsuarioId(u.getId()))
                .map(vendedorMapper::toDTO)
                .orElse(null);
    }

    public VendedorRegistroDTO obtenerVendedorParaEditar(Long id) {
        VendedorEntity v = vendedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        return VendedorRegistroDTO.builder()
                .id(v.getId())
                .nombre(v.getNombre())
                .username(v.getUsuario().getUsername())
                .password("") // No se muestra
                .activo(v.getActivo())
                .build();
    }

    /**
     * Desactiva un vendedor y guarda la fecha de su última venta.
     */
    public VendedorDTO desactivarVendedor(Long id) {
        VendedorEntity vendedor = vendedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

        if (!vendedor.getActivo()) {
            return vendedorMapper.toDTO(vendedor); // Ya está inactivo
        }

        // Buscar la última venta del vendedor antes de desactivar
        Optional<VentaEntity> ultimaVenta = ventaRepository.findFirstByVendedorIdOrderByFechaDesc(id);
        vendedor.setUltimaVenta(ultimaVenta.map(VentaEntity::getFecha).orElse(null));
        vendedor.setFechaInactivacion(LocalDateTime.now());
        vendedor.setActivo(false);

        VendedorEntity actualizado = vendedorRepository.save(vendedor);
        return vendedorMapper.toDTO(actualizado);
    }

    public VendedorDTO activarVendedor(Long id) {
        VendedorEntity vendedor = vendedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        vendedor.setActivo(true);
        vendedor.setFechaInactivacion(null);
        // Nota: La ultimaVenta histórica se mantiene por trazabilidad
        VendedorEntity actualizado = vendedorRepository.save(vendedor);
        return vendedorMapper.toDTO(actualizado);
    }

    public VendedorDTO obtenerPorUsuario(Long usuarioId) {
        return vendedorMapper.toDTO(vendedorRepository.findByUsuarioId(usuarioId).orElse(null));
    }
}
