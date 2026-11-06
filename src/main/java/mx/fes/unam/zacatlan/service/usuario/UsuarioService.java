package mx.fes.unam.zacatlan.service.usuario;

import mx.fes.unam.zacatlan.model.entities.usuario.UsuarioDTO;
import mx.fes.unam.zacatlan.model.entities.usuario.UsuarioEntity;
import mx.fes.unam.zacatlan.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioMapper usuarioMapper, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioMapper = usuarioMapper;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioDTO guardarUsuario(UsuarioDTO usuarioDTO) {
        // Encriptar contraseña antes de guardar
        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().startsWith("$2a$")) {
            usuarioDTO.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }
        UsuarioEntity entity = usuarioRepository.save(usuarioMapper.toEntity(usuarioDTO));
        return usuarioMapper.toDTO(entity);
    }

    public UsuarioDTO obtenerUsuario(Long id) {
        return usuarioMapper.toDTO(usuarioRepository.findById(id).orElse(null));
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(usuarioMapper::toDTO).toList();
    }
}