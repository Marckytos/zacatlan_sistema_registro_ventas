package mx.fes.unam.zacatlan.model.entities.producto;

import jakarta.persistence.*;
import lombok.*;
import mx.fes.unam.zacatlan.model.entities.bloque.BloqueEntity;
import mx.fes.unam.zacatlan.model.entities.venta.VentaEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Boolean aprobado = false; // Solo el admin lo pone en true

    // Relación con Bloque
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloque_id", nullable = false)
    private BloqueEntity bloque;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VentaEntity> ventas = new ArrayList<>();
}