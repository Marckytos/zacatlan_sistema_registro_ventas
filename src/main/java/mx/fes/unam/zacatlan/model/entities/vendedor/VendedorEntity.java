package mx.fes.unam.zacatlan.model.entities.vendedor;

import jakarta.persistence.*;
import lombok.*;
import mx.fes.unam.zacatlan.model.entities.usuario.UsuarioEntity;
import mx.fes.unam.zacatlan.model.entities.venta.VentaEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendedorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Boolean activo = true;

    private LocalDateTime fechaInactivacion;

    private LocalDateTime ultimaVenta; // Se actualiza al desactivar

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VentaEntity> ventas = new ArrayList<>();
}

