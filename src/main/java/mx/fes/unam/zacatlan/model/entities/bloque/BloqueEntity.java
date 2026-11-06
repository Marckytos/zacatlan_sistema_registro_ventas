package mx.fes.unam.zacatlan.model.entities.bloque;

import jakarta.persistence.*;
import lombok.*;
import mx.fes.unam.zacatlan.model.entities.producto.ProductoEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bloques")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloqueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre; // "Comida", "Textiles", "Manualidades", etc.

    @OneToMany(mappedBy = "bloque", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductoEntity> productos = new ArrayList<>();
}