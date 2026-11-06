package mx.fes.unam.zacatlan.model.entities.vendedor;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendedorDTO {
    Long id;
    String nombre;
    Boolean activo;
    LocalDateTime fechaInactivacion;
    LocalDateTime ultimaVenta;
    Long usuarioId;
    String username;
}