package mx.fes.unam.zacatlan.model.entities.usuario;

import jakarta.persistence.*;
import lombok.*;
import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorEntity;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol; // "ROLE_ADMIN" o "ROLE_VENDEDOR"

    // Relación 1 a 1 con Vendedor (si el rol es vendedor)
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private VendedorEntity vendedor;
}