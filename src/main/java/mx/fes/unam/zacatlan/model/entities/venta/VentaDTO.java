package mx.fes.unam.zacatlan.model.entities.venta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import mx.fes.unam.zacatlan.model.entities.producto.ProductoDTO;
import mx.fes.unam.zacatlan.model.entities.vendedor.VendedorDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VentaDTO {
    Long id;
    LocalDateTime fecha;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    Integer cantidad;

    BigDecimal total;

    @Valid
    VendedorDTO vendedor;

    @Valid
    @NotNull(message = "El producto es obligatorio")
    ProductoDTO producto;
}