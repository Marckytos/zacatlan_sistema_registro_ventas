package mx.fes.unam.zacatlan.model.entities.producto;

import lombok.*;
import mx.fes.unam.zacatlan.model.entities.bloque.BloqueDTO;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoDTO {
    Long id;
    String nombre;
    BigDecimal precio;
    Boolean aprobado;
    BloqueDTO bloque;
}

