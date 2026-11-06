package mx.fes.unam.zacatlan.util;

import mx.fes.unam.zacatlan.model.entities.bloque.BloqueEntity;
import mx.fes.unam.zacatlan.model.entities.producto.ProductoEntity;
import mx.fes.unam.zacatlan.model.entities.usuario.UsuarioEntity;
import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorEntity;
import mx.fes.unam.zacatlan.repository.BloqueRepository;
import mx.fes.unam.zacatlan.repository.ProductoRepository;
import mx.fes.unam.zacatlan.repository.UsuarioRepository;
import mx.fes.unam.zacatlan.repository.VendedorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final VendedorRepository vendedorRepository;
    private final BloqueRepository bloqueRepository;
    private final ProductoRepository productoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           VendedorRepository vendedorRepository,
                           BloqueRepository bloqueRepository,
                           ProductoRepository productoRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.vendedorRepository = vendedorRepository;
        this.bloqueRepository = bloqueRepository;
        this.productoRepository = productoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        crearUsuarios();
        crearBloquesYProductos();
    }


    // USUARIOS + VENDEDORES (solo si la tabla está vacía)

    private void crearUsuarios() {
        if (usuarioRepository.count() > 0) return;

        // Admin
        UsuarioEntity admin = UsuarioEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .rol("ROLE_ADMIN")
                .build();
        usuarioRepository.save(admin);

        // Vendedor 1
        UsuarioEntity userV1 = UsuarioEntity.builder()
                .username("vendedor1")
                .password(passwordEncoder.encode("vendedor123"))
                .rol("ROLE_VENDEDOR")
                .build();
        usuarioRepository.save(userV1);
        vendedorRepository.save(VendedorEntity.builder()
                .nombre("Juan Pérez")
                .activo(true)
                .usuario(userV1)
                .build());

        // Vendedor 2
        UsuarioEntity userV2 = UsuarioEntity.builder()
                .username("vendedor2")
                .password(passwordEncoder.encode("vendedor123"))
                .rol("ROLE_VENDEDOR")
                .build();
        usuarioRepository.save(userV2);
        vendedorRepository.save(VendedorEntity.builder()
                .nombre("María Gómez")
                .activo(true)
                .usuario(userV2)
                .build());
    }


    // BLOQUES + PRODUCTOS (verificación individual por nombre)

    private void crearBloquesYProductos() {

        // ===== BLOQUES =====
        BloqueEntity comida = obtenerOCrearBloque("Comida");
        BloqueEntity textiles = obtenerOCrearBloque("Textiles");
        BloqueEntity manualidades = obtenerOCrearBloque("Manualidades");

        // ===== PRODUCTOS (solo si no existen por nombre) =====
        crearProductoSiNoExiste("Tacos de Guisado", new BigDecimal("35.00"), true, comida);
        crearProductoSiNoExiste("Quesadillas",        new BigDecimal("25.00"), true, comida);
        crearProductoSiNoExiste("Blusa Bordada",      new BigDecimal("250.00"), true, textiles);
        crearProductoSiNoExiste("Rebozo",             new BigDecimal("400.00"), true, textiles);
        crearProductoSiNoExiste("Muñeca de Trapo",    new BigDecimal("80.00"), true, manualidades);
        // Producto NO aprobado (para probar el blindaje del profe)
        crearProductoSiNoExiste("Pulsera de Chaquira", new BigDecimal("50.00"), false, manualidades);
    }

    /**
     * Busca un bloque por nombre. Si no existe, lo crea.
     * (Usa findFirst porque no tenemos findByNombre en el repo, así que iteramos)
     */
    private BloqueEntity obtenerOCrearBloque(String nombre) {
        Optional<BloqueEntity> existente = bloqueRepository.findAll().stream()
                .filter(b -> b.getNombre().equalsIgnoreCase(nombre))
                .findFirst();

        return existente.orElseGet(() ->
                bloqueRepository.save(BloqueEntity.builder().nombre(nombre).build())
        );
    }

    /**
     * Crea un producto solo si no existe ya uno con el mismo nombre.
     */
    private void crearProductoSiNoExiste(String nombre, BigDecimal precio,
                                         Boolean aprobado, BloqueEntity bloque) {
        boolean existe = productoRepository.findAll().stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre));

        if (!existe) {
            productoRepository.save(ProductoEntity.builder()
                    .nombre(nombre)
                    .precio(precio)
                    .aprobado(aprobado)
                    .bloque(bloque)
                    .build());
        }
    }
}