package mx.fes.unam.zacatlan.model.entities.usuario;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {
    Long id;
    String username;
    String password;
    String rol;
}

